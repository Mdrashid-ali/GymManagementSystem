<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.trainer" %>
<%@ page import="com.fitTrackPro.model.workoutPlan" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isTrainer()) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    trainer trainer = (trainer) request.getAttribute("trainer");
    List<workoutPlan> workoutPlans = (List<workoutPlan>) request.getAttribute("workoutPlans");
    List<member> members = (List<member>) request.getAttribute("members");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trainer Dashboard - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="navbar">
        <div class="d-flex justify-content-between align-items-center">
            <a href="#" class="navbar-brand">FitTrack Pro Trainer</a>
            <div>
                <span>Welcome, <%= trainer != null ? trainer.getFirstName() : "Trainer" %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>
    
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="trainer-dashboard.jsp" class="sidebar-link active">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/members" class="sidebar-link">My Members</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link">Workout Plans</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/create-workout" class="sidebar-link">Create Workout Plan</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/attendance" class="sidebar-link">Attendance</a>
            </li>
        </ul>
    </div>
    
    <div class="main-content">
        <h1>Trainer Dashboard</h1>
        
        <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getParameter("success") %>
            </div>
        <% } %>
        
        <div class="row">
            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h3>Trainer Information</h3>
                    </div>
                    <div class="card-body">
                        <% if (trainer != null) { %>
                            <p><strong>Name:</strong> <%= trainer.getFullName() %></p>
                            <p><strong>Specialization:</strong> <%= trainer.getSpecialization() %></p>
                            <p><strong>Experience:</strong> <%= trainer.getExperienceYears() %> years</p>
                            <p><strong>Certification:</strong> <%= trainer.getCertification() %></p>
                        <% } %>
                    </div>
                </div>
            </div>
            
            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h3>Quick Actions</h3>
                    </div>
                    <div class="card-body">
                        <a href="${pageContext.request.contextPath}/trainer/create-workout" class="btn btn-primary btn-block">Create New Workout Plan</a>
                        <a href="${pageContext.request.contextPath}/trainer/members" class="btn btn-outline btn-block" style="margin-top: 10px;">View All Members</a>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="card" style="margin-top: 20px;">
            <div class="card-header">
                <h3>Recent Workout Plans</h3>
            </div>
            <div class="card-body">
                <% if (workoutPlans != null && !workoutPlans.isEmpty()) { %>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Plan Name</th>
                                    <th>Member</th>
                                    <th>Start Date</th>
                                    <th>End Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% 
                                int count = 0;
                                for (workoutPlan plan : workoutPlans) {
                                    if (count++ >= 5) break;
                                %>
                                <tr>
                                    <td><%= plan.getPlanName() %></td>
                                    <td><%= plan.getMemberName() %></td>
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
                    <p>No workout plans created yet.</p>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>