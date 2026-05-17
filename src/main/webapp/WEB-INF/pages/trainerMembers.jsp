<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.trainer" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="com.fitTrackPro.util.dateUtil" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isTrainer()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    trainer trainer = (trainer) request.getAttribute("trainer");
    List<member> members = (List<member>) request.getAttribute("members");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Members - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=brand-dashboard-16">
    <style>
        .member-profile-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px; }
        .member-profile-card { background: #fff; border-radius: 8px; box-shadow: var(--shadow); overflow: hidden; border-top: 4px solid var(--primary-color); }
        .member-profile-head { padding: 20px; display: flex; align-items: center; gap: 14px; border-bottom: 1px solid var(--gray-light); }
        .member-avatar { width: 54px; height: 54px; border-radius: 50%; background: var(--primary-color); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 22px; font-weight: 700; text-transform: uppercase; }
        .member-profile-head h3 { margin: 0 0 4px; font-size: 20px; }
        .member-profile-head p { margin: 0; color: var(--gray-medium); font-size: 14px; }
        .member-profile-body { padding: 18px 20px; }
        .member-facts { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 16px; }
        .member-fact { background: var(--light-color); border-radius: 8px; padding: 12px; }
        .member-fact span { display: block; color: var(--gray-medium); font-size: 12px; text-transform: uppercase; }
        .member-fact strong { display: block; margin-top: 4px; }
        .member-notes { border-top: 1px solid var(--gray-light); padding-top: 14px; }
        .member-notes p { margin-bottom: 8px; }
    </style>
</head>
<body>
        <div class="navbar">
        <div class="d-flex justify-content-between align-items-center" style="width:100%;display:flex;align-items:center;justify-content:space-between;">
            <a href="${pageContext.request.contextPath}<%= ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isAdmin() ? "/adminDashboard" : ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isTrainer() ? "/trainerDashboard" : "/memberDashboard" %>" class="navbar-brand">FitTrack Pro</a>
            <div class="navbar-actions" style="margin-left:auto;display:inline-flex;align-items:center;gap:12px;"><div class="navbar-user-card"><span class="navbar-user-name"><%= ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).getDisplayName() %></span><span class="navbar-user-role"><%= ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isAdmin() ? "Admin" : ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isTrainer() ? "Trainer" : "Member" %></span></div><a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm navbar-logout" title="Logout">
                <svg class="logout-icon" viewBox="0 0 24 24" aria-hidden="true"><path d="M4 4.5 11 2v20l-7-2.5v-15Z"></path><path d="M12.5 5H18v4h-2V7h-3.5V5Zm0 12H16v-2h2v4h-5.5v-2Z"></path><path d="M15 8.5 21 12l-6 3.5V13h-5v-2h5V8.5Z"></path><circle cx="8" cy="12" r="0.8" fill="#ffffff"></circle></svg>
                <span>Logout</span>
            </a></div>
        </div>
    </div>

    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainerDashboard" class="sidebar-link">Dashboard</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/members" class="sidebar-link active">My Members</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link">Workout Plans</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/create-workout" class="sidebar-link">Create Workout Plan</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/attendance" class="sidebar-link">Attendance</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/about" class="sidebar-link">About</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/services" class="sidebar-link">Services</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/contact" class="sidebar-link">Contact</a></li>
        </ul>
    </div>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center" style="width:100%;display:flex;align-items:center;justify-content:space-between;" style="margin-bottom: 18px;">
            <div>
                <h1>My Members</h1>
                <p class="text-muted">Members assigned through workout plans under <%= trainer != null ? trainer.getFullName() : "this trainer" %>.</p>
            </div>
            <a href="${pageContext.request.contextPath}/trainer/create-workout" class="btn btn-primary">Assign Workout Plan</a>
        </div>

        <% if (members != null && !members.isEmpty()) { %>
            <div class="member-profile-grid">
                <% for (member member : members) { 
                    boolean active = member.isMembershipActive();
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
                    <div class="member-profile-card">
                        <div class="member-profile-head">
                            <div class="member-avatar"><%= member.getFirstName() != null && !member.getFirstName().isBlank() ? member.getFirstName().substring(0,1) : "M" %></div>
                            <div>
                                <h3><%= member.getFullName() %></h3>
                                <p><%= member.getEmail() %></p>
                            </div>
                        </div>
                        <div class="member-profile-body">
                            <div class="d-flex justify-content-between align-items-center" style="width:100%;display:flex;align-items:center;justify-content:space-between;" style="margin-bottom: 14px;">
                                <span class="badge <%= active ? "badge-success" : "badge-warning" %>"><%= active ? "Active Membership" : "Expired Membership" %></span>
                                <span class="badge badge-info"><%= member.getMembershipType() %></span>
                            </div>
                            <div class="member-facts">
                                <div class="member-fact"><span>Phone</span><strong><%= member.getPhone() == null ? "Not set" : member.getPhone() %></strong></div>
                                <div class="member-fact"><span>Expiry</span><strong><%= member.getMembershipExpiryDate() == null ? "Not set" : dateUtil.formatDisplayDate(member.getMembershipExpiryDate()) %></strong></div>
                                <div class="member-fact"><span>Height</span><strong><%= member.getHeightCm() == null ? "Not set" : member.getHeightCm() + " cm" %></strong></div>
                                <div class="member-fact"><span>Weight</span><strong><%= member.getWeightKg() == null ? "Not set" : member.getWeightKg() + " kg" %></strong></div>
                                <div class="member-fact"><span>BMI</span><strong><%= bmi == 0 ? "Not available" : String.format("%.1f", bmi) + " - " + bmiStatus %></strong></div>
                                <div class="member-fact"><span>Gender</span><strong><%= member.getGender() == null ? "Not set" : member.getGender() %></strong></div>
                            </div>
                            <div class="member-notes">
                                <p><strong>Fitness Goal:</strong> <%= member.getFitnessGoal() == null || member.getFitnessGoal().isBlank() ? "Not set" : member.getFitnessGoal() %></p>
                                <p><strong>Medical Notes:</strong> <%= member.getMedicalNotes() == null || member.getMedicalNotes().isBlank() ? "None" : member.getMedicalNotes() %></p>
                                <p><strong>Emergency Contact:</strong> <%= member.getEmergencyContactName() == null || member.getEmergencyContactName().isBlank() ? "Not set" : member.getEmergencyContactName() %> <%= member.getEmergencyContactPhone() == null ? "" : "(" + member.getEmergencyContactPhone() + ")" %></p>
                            </div>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } else { %>
            <div class="card">
                <div class="card-body text-center" style="padding: 48px;">
                    <h3>No assigned members yet</h3>
                    <p class="text-muted">Create a workout plan for a member to make them appear under your My Members tab.</p>
                    <a href="${pageContext.request.contextPath}/trainer/create-workout" class="btn btn-primary">Create Workout Plan</a>
                </div>
            </div>
        <% } %>
    </div>
</body>
</html>