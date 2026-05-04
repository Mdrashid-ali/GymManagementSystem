<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="com.fitTrackPro.util.dateUtil" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    member member = (member) request.getAttribute("member");
    if (member == null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }

    boolean isAdmin = user.isAdmin();
    boolean isOwnProfile = user.isMember() && user.getUserId() == member.getUserId();

    if (!isAdmin && !isOwnProfile) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }

    boolean membershipActive = member.isMembershipActive();
    double bmi = 0;
    String bmiStatus = "Not available";
    if (member.getHeightCm() != null && member.getWeightKg() != null && member.getHeightCm() > 0) {
        double heightM = member.getHeightCm() / 100.0;
        bmi = member.getWeightKg() / (heightM * heightM);
        if (bmi < 18.5) bmiStatus = "Underweight";
        else if (bmi < 25) bmiStatus = "Healthy";
        else if (bmi < 30) bmiStatus = "Overweight";
        else bmiStatus = "Obese";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isAdmin ? "Update Member" : "My Profile" %> - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .profile-shell { max-width: 1500px; }
        .profile-hero {
            background: #ffffff;
            border-radius: 8px;
            box-shadow: var(--shadow);
            padding: 24px;
            margin-bottom: 24px;
            display: grid;
            grid-template-columns: auto 1fr auto;
            gap: 18px;
            align-items: center;
            border-left: 5px solid var(--primary-color);
        }
        .profile-avatar {
            width: 72px;
            height: 72px;
            border-radius: 50%;
            background: var(--primary-color);
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            font-weight: 700;
            text-transform: uppercase;
        }
        .profile-title h1 { margin: 0 0 6px; }
        .profile-title p { margin: 0; color: var(--gray-medium); }
        .profile-badges { display: flex; gap: 8px; flex-wrap: wrap; justify-content: flex-end; }
        .profile-metric-grid {
            display: grid;
            grid-template-columns: repeat(4, minmax(150px, 1fr));
            gap: 14px;
            margin-bottom: 24px;
        }
        .profile-metric {
            background: #fff;
            border-radius: 8px;
            box-shadow: var(--shadow-sm);
            padding: 16px;
            border-top: 4px solid var(--primary-color);
        }
        .profile-metric span { display: block; color: var(--gray-medium); font-size: 13px; text-transform: uppercase; letter-spacing: .04em; }
        .profile-metric strong { display: block; margin-top: 6px; font-size: 20px; color: var(--dark-color); }
        .profile-section-title {
            font-size: 18px;
            margin: 0 0 18px;
            color: var(--dark-color);
        }
        .profile-form-card { margin-bottom: 20px; }
        .profile-actions {
            position: sticky;
            bottom: 0;
            background: rgba(255,255,255,.96);
            border-top: 1px solid var(--gray-light);
            padding: 16px 0 0;
            margin-top: 8px;
        }
        @media (max-width: 900px) {
            .profile-hero { grid-template-columns: 1fr; text-align: left; }
            .profile-badges { justify-content: flex-start; }
            .profile-metric-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
        }
        @media (max-width: 600px) {
            .profile-metric-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
    <div class="navbar">
        <div class="d-flex justify-content-between align-items-center">
            <a href="#" class="navbar-brand">FitTrack Pro <%= isAdmin ? "Admin" : "" %></a>
            <div>
                <span>Welcome, <%= user.getDisplayName() %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>

    <% if (isAdmin) { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/adminDashboard" class="sidebar-link">Dashboard</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/members" class="sidebar-link active">Manage Members</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/trainers" class="sidebar-link">Manage Trainers</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/add-member" class="sidebar-link">Add Member</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/add-trainer" class="sidebar-link">Add Trainer</a></li>
        </ul>
    </div>
    <% } else { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/memberDashboard" class="sidebar-link">Dashboard</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/profile" class="sidebar-link active">My Profile</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/attendance" class="sidebar-link">Attendance History</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/workout" class="sidebar-link">Workout Plans</a></li>
        </ul>
    </div>
    <% } %>

    <div class="main-content profile-shell">
        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger"><%= request.getParameter("error") %></div>
        <% } %>
        <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success"><%= request.getParameter("success") %></div>
        <% } %>

        <div class="profile-hero">
            <div class="profile-avatar"><%= member.getFirstName() != null && !member.getFirstName().isBlank() ? member.getFirstName().substring(0,1) : "M" %></div>
            <div class="profile-title">
                <h1><%= isAdmin ? "Edit Member" : "My Profile" %></h1>
                <p><strong><%= member.getFullName() %></strong> · <%= member.getEmail() %></p>
            </div>
            <div class="profile-badges">
                <span class="badge <%= membershipActive ? "badge-success" : "badge-warning" %>"><%= membershipActive ? "Active Membership" : "Expired Membership" %></span>
                <span class="badge badge-info"><%= member.getMembershipType() == null ? "BASIC" : member.getMembershipType() %></span>
            </div>
        </div>

        <div class="profile-metric-grid">
            <div class="profile-metric">
                <span>Expiry Date</span>
                <strong><%= member.getMembershipExpiryDate() == null ? "Not set" : dateUtil.formatDisplayDate(member.getMembershipExpiryDate()) %></strong>
            </div>
            <div class="profile-metric">
                <span>Fitness Goal</span>
                <strong><%= member.getFitnessGoal() == null || member.getFitnessGoal().isBlank() ? "Not set" : member.getFitnessGoal() %></strong>
            </div>
            <div class="profile-metric">
                <span>BMI</span>
                <strong><%= bmi == 0 ? "Not available" : String.format("%.1f", bmi) + " " + bmiStatus %></strong>
            </div>
            <div class="profile-metric">
                <span>Phone</span>
                <strong><%= member.getPhone() == null ? "Not set" : member.getPhone() %></strong>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/<%= isAdmin ? "admin/update-member" : "member/update" %>" method="post">
            <input type="hidden" name="memberId" value="<%= member.getMemberId() %>">

            <div class="card profile-form-card">
                <div class="card-header"><h3>Account Details</h3></div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="firstName">First Name</label>
                                <input type="text" id="firstName" name="firstName" class="form-control" value="<%= member.getFirstName() %>" <%= isAdmin ? "" : "readonly" %>>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="lastName">Last Name</label>
                                <input type="text" id="lastName" name="lastName" class="form-control" value="<%= member.getLastName() %>" <%= isAdmin ? "" : "readonly" %>>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="email">Email Address</label>
                                <input type="email" id="email" class="form-control" value="<%= member.getEmail() %>" readonly disabled>
                                <small class="text-muted">Email cannot be changed</small>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="phone">Phone Number</label>
                                <input type="tel" id="phone" name="phone" class="form-control" value="<%= member.getPhone() != null ? member.getPhone() : "" %>" required>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card profile-form-card">
                <div class="card-header"><h3>Membership</h3></div>
                <div class="card-body">
                    <% if (isAdmin) { %>
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="membershipType">Membership Type</label>
                                <select id="membershipType" name="membershipType" class="form-control">
                                    <option value="BASIC" <%= "BASIC".equals(member.getMembershipType()) ? "selected" : "" %>>Basic</option>
                                    <option value="PREMIUM" <%= "PREMIUM".equals(member.getMembershipType()) ? "selected" : "" %>>Premium</option>
                                    <option value="FAMILY" <%= "FAMILY".equals(member.getMembershipType()) ? "selected" : "" %>>Family</option>
                                    <option value="STUDENT" <%= "STUDENT".equals(member.getMembershipType()) ? "selected" : "" %>>Student</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="membershipExpiryDate">Membership Expiry Date</label>
                                <input type="date" id="membershipExpiryDate" name="membershipExpiryDate" class="form-control" value="<%= member.getMembershipExpiryDate() %>">
                            </div>
                        </div>
                    </div>
                    <% } else { %>
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label>Membership Type</label>
                                <input type="text" class="form-control" value="<%= member.getMembershipType() %>" readonly disabled>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label>Membership Expiry Date</label>
                                <input type="text" class="form-control" value="<%= member.getMembershipExpiryDate() %>" readonly disabled>
                            </div>
                        </div>
                    </div>
                    <% } %>
                </div>
            </div>

            <div class="card profile-form-card">
                <div class="card-header"><h3>Personal Information</h3></div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="dateOfBirth">Date of Birth</label>
                                <input type="date" id="dateOfBirth" name="dateOfBirth" class="form-control" value="<%= member.getDateOfBirth() %>" <%= isAdmin ? "" : "readonly" %>>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="gender">Gender</label>
                                <select id="gender" name="gender" class="form-control" <%= isAdmin ? "" : "disabled" %>>
                                    <option value="">Select Gender</option>
                                    <option value="MALE" <%= "MALE".equals(member.getGender()) ? "selected" : "" %>>Male</option>
                                    <option value="FEMALE" <%= "FEMALE".equals(member.getGender()) ? "selected" : "" %>>Female</option>
                                    <option value="OTHER" <%= "OTHER".equals(member.getGender()) ? "selected" : "" %>>Other</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="address">Address</label>
                        <textarea id="address" name="address" class="form-control" rows="2"><%= member.getAddress() != null ? member.getAddress() : "" %></textarea>
                    </div>
                </div>
            </div>

            <div class="card profile-form-card">
                <div class="card-header"><h3>Emergency Contact</h3></div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="emergencyContactName">Contact Name</label>
                                <input type="text" id="emergencyContactName" name="emergencyContactName" class="form-control" value="<%= member.getEmergencyContactName() != null ? member.getEmergencyContactName() : "" %>">
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="emergencyContactPhone">Contact Phone</label>
                                <input type="tel" id="emergencyContactPhone" name="emergencyContactPhone" class="form-control" value="<%= member.getEmergencyContactPhone() != null ? member.getEmergencyContactPhone() : "" %>">
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card profile-form-card">
                <div class="card-header"><h3>Fitness Details</h3></div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="height">Height (cm)</label>
                                <input type="number" step="0.1" id="height" name="height" class="form-control" value="<%= member.getHeightCm() != null ? member.getHeightCm() : "" %>">
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="weight">Weight (kg)</label>
                                <input type="number" step="0.1" id="weight" name="weight" class="form-control" value="<%= member.getWeightKg() != null ? member.getWeightKg() : "" %>">
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="fitnessGoal">Fitness Goal</label>
                        <textarea id="fitnessGoal" name="fitnessGoal" class="form-control" rows="3"><%= member.getFitnessGoal() != null ? member.getFitnessGoal() : "" %></textarea>
                    </div>
                    <% if (isAdmin) { %>
                    <div class="form-group">
                        <label for="medicalNotes">Medical Notes</label>
                        <textarea id="medicalNotes" name="medicalNotes" class="form-control" rows="3"><%= member.getMedicalNotes() != null ? member.getMedicalNotes() : "" %></textarea>
                    </div>
                    <% } %>
                </div>
            </div>

            <div class="profile-actions">
                <div class="d-flex justify-content-between">
                    <a href="<%= isAdmin ? request.getContextPath() + "/admin/members" : request.getContextPath() + "/memberDashboard" %>" class="btn btn-outline">Cancel</a>
                    <button type="submit" class="btn btn-primary"><%= isAdmin ? "Update Member" : "Update Profile" %></button>
                </div>
            </div>
        </form>
    </div>
</body>
</html>