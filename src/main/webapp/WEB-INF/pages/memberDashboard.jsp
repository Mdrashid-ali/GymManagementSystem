<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="com.fitTrackPro.model.workoutPlan" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isMember()) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    member member = (member) request.getAttribute("member");
    List<workoutPlan> workoutPlans = (List<workoutPlan>) request.getAttribute("workoutPlans");
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
                <span>Welcome, <%= member != null ? member.getFirstName() : "Member" %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>
    
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="member-dashboard.jsp" class="sidebar-link active">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/profile" class="sidebar-link">My Profile</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/attendance" class="sidebar-link">Attendance History</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/workout" class="sidebar-link">Workout Plans</a>
            </li>
        </ul>
    </div>
    
    <div class="main-content">
        <h1>Member Dashboard</h1>
        
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
                            <p><strong>Type:</strong> <%= member.getMembershipType() %></p>
                            <p><strong>Join Date:</strong> <%= member.getJoinDate() %></p>
                            <p><strong>Expiry Date:</strong> <%= member.getMembershipExpiryDate() %></p>
                            <p>
                                <strong>Status:</strong> 
                                <span class="badge <%= member.isMembershipActive() ? "badge-success" : "badge-danger" %>">
                                    <%= member.isMembershipActive() ? "Active" : "Expired" %>
                                </span>
                            </p>
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
                        <h3>Fitness Stats</h3>
                    </div>
                    <div class="card-body">
                        <% if (member != null) { %>
                            <p><strong>Height:</strong> <%= member.getHeightCm() != null ? member.getHeightCm() + " cm" : "Not set" %></p>
                            <p><strong>Weight:</strong> <%= member.getWeightKg() != null ? member.getWeightKg() + " kg" : "Not set" %></p>
                            <p><strong>BMI:</strong> <%= member.calculateBMI() != null ? String.format("%.1f", member.calculateBMI()) : "Not calculated" %></p>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="card" style="margin-top: 20px;">
            <div class="card-header">
                <h3>My Workout Plans</h3>
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
                    <p>No workout plans assigned yet.</p>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>