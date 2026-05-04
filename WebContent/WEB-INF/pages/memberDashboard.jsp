<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="com.fitTrackPro.model.workoutPlan" %>
<%@ page import="com.fitTrackPro.model.attendance" %>
<%@ page import="com.fitTrackPro.util.dateUtil" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isMember()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    member member = (member) request.getAttribute("member");
    List<workoutPlan> workoutPlans = (List<workoutPlan>) request.getAttribute("workoutPlans");
    List<attendance> attendanceList = (List<attendance>) request.getAttribute("attendanceList");
    boolean membershipActive = member != null && member.isMembershipActive();
    int activePlans = 0;
    if (workoutPlans != null) {
        for (workoutPlan plan : workoutPlans) if (plan.isActive()) activePlans++;
    }
    int totalVisits = attendanceList == null ? 0 : attendanceList.size();
    double bmi = 0;
    String bmiStatus = "Not available";
    if (member != null && member.getHeightCm() != null && member.getWeightKg() != null && member.getHeightCm() > 0) {
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
    <title>Member Dashboard - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="navbar">
        <div class="d-flex justify-content-between align-items-center">
            <a href="#" class="navbar-brand">FitTrack Pro</a>
            <div>
                <span>Welcome, <%= user.getDisplayName() %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>

    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/memberDashboard" class="sidebar-link active">Dashboard</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/profile" class="sidebar-link">My Profile</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/attendance" class="sidebar-link">Attendance History</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/workout" class="sidebar-link">Workout Plans</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/about" class="sidebar-link">About</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/services" class="sidebar-link">Services</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/contact" class="sidebar-link">Contact</a></li>
        </ul>
    </div>

    <div class="main-content">
        <h1>Member Dashboard</h1>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (session.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <%= session.getAttribute("success") %>
                <% session.removeAttribute("success"); %>
            </div>
        <% } %>

        <div class="row">
            <div class="col-4">
                <div class="card">
                    <div class="card-header">
                        <h3>Membership Status</h3>
                    </div>
                    <div class="card-body">
                        <% if (member != null) { %>
                            <p><strong>Status:</strong> <span class="badge <%= membershipActive ? "badge-success" : "badge-warning" %>"><%= membershipActive ? "Active" : "Expired" %></span></p>
                            <p><strong>Type:</strong> <%= member.getMembershipType() %></p>
                            <p><strong>Joined:</strong> <%= member.getJoinDate() == null ? "" : dateUtil.formatDisplayDate(member.getJoinDate()) %></p>
                            <p><strong>Expires:</strong> <%= member.getMembershipExpiryDate() == null ? "" : dateUtil.formatDisplayDate(member.getMembershipExpiryDate()) %></p>
                        <% } %>
                    </div>
                </div>
            </div>

            <div class="col-4">
                <div class="card">
                    <div class="card-header">
                        <h3>Quick Check-In</h3>
                    </div>
                    <div class="card-body text-center">
                        <p>Ready for your workout?</p>
                        <a href="${pageContext.request.contextPath}/member/checkin" class="btn btn-primary btn-lg">Check In Now</a>
                    </div>
                </div>
            </div>

            <div class="col-4">
                <div class="card">
                    <div class="card-header">
                        <h3>Fitness Status</h3>
                    </div>
                    <div class="card-body">
                        <% if (member != null) { %>
                            <p><strong>Goal:</strong> <%= member.getFitnessGoal() == null || member.getFitnessGoal().isBlank() ? "Not set" : member.getFitnessGoal() %></p>
                            <p><strong>Height:</strong> <%= member.getHeightCm() == null ? "Not set" : member.getHeightCm() + " cm" %></p>
                            <p><strong>Weight:</strong> <%= member.getWeightKg() == null ? "Not set" : member.getWeightKg() + " kg" %></p>
                            <p><strong>BMI:</strong> <%= bmi == 0 ? "Not available" : String.format("%.1f", bmi) + " - " + bmiStatus %></p>
                            <p><strong>Visits:</strong> <%= totalVisits %></p>
                            <p><strong>Active Plans:</strong> <%= activePlans %></p>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>

        <div class="card" style="margin-top: 20px;">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h3>My Workout Plans</h3>
                <a href="${pageContext.request.contextPath}/member/workout" class="btn btn-outline btn-sm">View Details</a>
            </div>
            <div class="card-body">
                <% if (workoutPlans != null && !workoutPlans.isEmpty()) { %>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Plan Name</th>
                                    <th>Trainer</th>
                                    <th>Start Date</th>
                                    <th>End Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (workoutPlan plan : workoutPlans) { %>
                                    <tr>
                                        <td><%= plan.getPlanName() %></td>
                                        <td><%= plan.getTrainerName() %></td>
                                        <td><%= plan.getStartDate() %></td>
                                        <td><%= plan.getEndDate() %></td>
                                        <td>
                                            <span class="badge <%= plan.isActive() ? "badge-success" : "badge-secondary" %>">
                                                <%= plan.getStatus() %>
                                            </span>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="text-center" style="padding: 35px;">
                        <p class="text-muted">No workout plans assigned yet.</p>
                    </div>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>