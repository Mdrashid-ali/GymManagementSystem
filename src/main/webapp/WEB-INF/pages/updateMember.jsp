<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    member member = (member) request.getAttribute("member");
    if (member == null) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
    
    boolean isAdmin = user.isAdmin();
    boolean isOwnProfile = user.isMember() && user.getUserId() == member.getUserId();
    
    if (!isAdmin && !isOwnProfile) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Member - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="navbar">
        <div class="d-flex justify-content-between align-items-center">
            <a href="#" class="navbar-brand">FitTrack Pro <%= isAdmin ? "Admin" : "" %></a>
            <div>
                <span>Welcome, <%= user.getEmail() %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>
    
    <% if (isAdmin) { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="admin-dashboard.jsp" class="sidebar-link">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/members" class="sidebar-link active">Manage Members</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/trainers" class="sidebar-link">Manage Trainers</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/add-member" class="sidebar-link">Add Member</a>
            </li>
        </ul>
    </div>
    <% } else { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="member-dashboard.jsp" class="sidebar-link">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/profile" class="sidebar-link active">My Profile</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/attendance" class="sidebar-link">Attendance History</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/workout" class="sidebar-link">Workout Plans</a>
            </li>
        </ul>
    </div>
    <% } %>
    
    <div class="main-content">
        <h1><%= isAdmin ? "Edit Member" : "My Profile" %></h1>
        
        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getParameter("error") %>
            </div>
        <% } %>
        
        <div class="card">
            <div class="card-header">
                <h3>Member Information</h3>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/<%= isAdmin ? "admin/update-member" : "member/update" %>" method="post">
                    <input type="hidden" name="memberId" value="<%= member.getMemberId() %>">
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="firstName">First Name</label>
                                <input type="text" id="firstName" name="firstName" class="form-control" 
                                       value="<%= member.getFirstName() %>" <%= isAdmin ? "" : "readonly" %>>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="lastName">Last Name</label>
                                <input type="text" id="lastName" name="lastName" class="form-control" 
                                       value="<%= member.getLastName() %>" <%= isAdmin ? "" : "readonly" %>>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="email">Email Address</label>
                                <input type="email" id="email" class="form-control" 
                                       value="<%= member.getEmail() %>" readonly disabled>
                                <small class="text-muted">Email cannot be changed</small>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="phone">Phone Number</label>
                                <input type="tel" id="phone" name="phone" class="form-control" 
                                       value="<%= member.getPhone() != null ? member.getPhone() : "" %>" required>
                            </div>
                        </div>
                    </div>
                    
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
                                <input type="date" id="membershipExpiryDate" name="membershipExpiryDate" class="form-control" 
                                       value="<%= member.getMembershipExpiryDate() %>">
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
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="dateOfBirth">Date of Birth</label>
                                <input type="date" id="dateOfBirth" name="dateOfBirth" class="form-control" 
                                       value="<%= member.getDateOfBirth() %>" <%= isAdmin ? "" : "readonly" %>>
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
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="emergencyContactName">Emergency Contact Name</label>
                                <input type="text" id="emergencyContactName" name="emergencyContactName" class="form-control" 
                                       value="<%= member.getEmergencyContactName() != null ? member.getEmergencyContactName() : "" %>">
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="emergencyContactPhone">Emergency Contact Phone</label>
                                <input type="tel" id="emergencyContactPhone" name="emergencyContactPhone" class="form-control" 
                                       value="<%= member.getEmergencyContactPhone() != null ? member.getEmergencyContactPhone() : "" %>">
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="height">Height (cm)</label>
                                <input type="number" step="0.1" id="height" name="height" class="form-control" 
                                       value="<%= member.getHeightCm() != null ? member.getHeightCm() : "" %>">
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="weight">Weight (kg)</label>
                                <input type="number" step="0.1" id="weight" name="weight" class="form-control" 
                                       value="<%= member.getWeightKg() != null ? member.getWeightKg() : "" %>">
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="fitnessGoal">Fitness Goal</label>
                        <textarea id="fitnessGoal" name="fitnessGoal" class="form-control" rows="2"><%= member.getFitnessGoal() != null ? member.getFitnessGoal() : "" %></textarea>
                    </div>
                    
                    <% if (isAdmin) { %>
                    <div class="form-group">
                        <label for="medicalNotes">Medical Notes</label>
                        <textarea id="medicalNotes" name="medicalNotes" class="form-control" rows="2"><%= member.getMedicalNotes() != null ? member.getMedicalNotes() : "" %></textarea>
                    </div>
                    <% } %>
                    
                    <div class="d-flex justify-content-between">
                        <a href="<%= isAdmin ? "${pageContext.request.contextPath}/admin/members" : "${pageContext.request.contextPath}/member-dashboard.jsp" %>" 
                           class="btn btn-outline">Cancel</a>
                        <button type="submit" class="btn btn-primary">Update Profile</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>