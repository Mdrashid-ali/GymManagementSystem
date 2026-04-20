<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.workoutPlan" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<workoutPlan> workoutPlans = (List<workoutPlan>) request.getAttribute("workoutPlans");
    boolean isMember = user.isMember();
    boolean isTrainer = user.isTrainer();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workout Plans - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .workout-card {
            background: var(--white);
            border-radius: var(--border-radius);
            box-shadow: var(--box-shadow);
            margin-bottom: 20px;
            overflow: hidden;
        }
        
        .workout-header {
            background: var(--primary-color);
            color: white;
            padding: 15px 20px;
        }
        
        .workout-body {
            padding: 20px;
        }
        
        .exercise-list {
            white-space: pre-line;
            line-height: 1.8;
        }
        
        .workout-footer {
            background: var(--light-color);
            padding: 15px 20px;
            border-top: 1px solid var(--gray-light);
        }
    </style>
</head>
<body>
    <div class="navbar">
        <div class="d-flex justify-content-between align-items-center">
            <a href="#" class="navbar-brand">FitTrack Pro</a>
            <div>
                <span>Welcome, <%= user.getEmail() %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>
    
    <% if (isMember) { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="member-dashboard.jsp" class="sidebar-link">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/profile" class="sidebar-link">My Profile</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/attendance" class="sidebar-link">Attendance History</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/workout" class="sidebar-link active">Workout Plans</a>
            </li>
        </ul>
    </div>
    <% } else if (isTrainer) { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="trainer-dashboard.jsp" class="sidebar-link">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/members" class="sidebar-link">My Members</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link active">Workout Plans</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/create-workout" class="sidebar-link">Create Workout Plan</a>
            </li>
        </ul>
    </div>
    <% } %>
    
    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center">
            <h1>Workout Plans</h1>
            <% if (isTrainer) { %>
            <a href="${pageContext.request.contextPath}/trainer/create-workout" class="btn btn-primary">Create New Plan</a>
            <% } %>
        </div>
        
        <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getParameter("success") %>
            </div>
        <% } %>
        
        <% if (workoutPlans != null && !workoutPlans.isEmpty()) { %>
            <% for (workoutPlan plan : workoutPlans) { %>
            <div class="workout-card">
                <div class="workout-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <h3 style="margin: 0; color: white;"><%= plan.getPlanName() %></h3>
                        <span class="badge <%= plan.isActive() ? "badge-success" : "badge-secondary" %>" 
                              style="background: white; color: <%= plan.isActive() ? "#27ae60" : "#7f8c8d" %>;">
                            <%= plan.getStatus() %>
                        </span>
                    </div>
                </div>
                <div class="workout-body">
                    <div class="row">
                        <div class="col-6">
                            <p><strong>Trainer:</strong> <%= plan.getTrainerName() %></p>
                            <% if (isTrainer) { %>
                            <p><strong>Member:</strong> <%= plan.getMemberName() %></p>
                            <% } %>
                            <p><strong>Period:</strong> <%= plan.getStartDate() %> to <%= plan.getEndDate() %></p>
                        </div>
                        <div class="col-6">
                            <p><strong>Description:</strong></p>
                            <p><%= plan.getDescription() != null ? plan.getDescription() : "No description provided." %></p>
                        </div>
                    </div>
                    
                    <hr style="margin: 20px 0; border-color: #eee;">
                    
                    <h4>Exercises</h4>
                    <div class="exercise-list">
                        <%= plan.getExercises() != null ? plan.getExercises().replace("\n", "<br>") : "No exercises listed." %>
                    </div>
                    
                    <% if (plan.getNotes() != null && !plan.getNotes().isEmpty()) { %>
                    <hr style="margin: 20px 0; border-color: #eee;">
                    <h4>Notes</h4>
                    <p><%= plan.getNotes() %></p>
                    <% } %>
                </div>
                <div class="workout-footer">
                    <div class="d-flex justify-content-between">
                        <span class="text-muted">Created: <%= plan.getCreatedAt() %></span>
                        <% if (plan.isActive()) { %>
                        <span class="badge badge-success">Active Plan</span>
                        <% } else if (plan.isCompleted()) { %>
                        <span class="badge badge-secondary">Completed</span>
                        <% } %>
                    </div>
                </div>
            </div>
            <% } %>
        <% } else { %>
            <div class="card">
                <div class="card-body text-center" style="padding: 60px;">
                    <p class="text-muted">No workout plans found.</p>
                    <% if (isTrainer) { %>
                    <a href="${pageContext.request.contextPath}/trainer/create-workout" class="btn btn-primary" style="margin-top: 20px;">Create First Workout Plan</a>
                    <% } else { %>
                    <p>Your trainer will assign workout plans soon.</p>
                    <% } %>
                </div>
            </div>
        <% } %>
    </div>
</body>
</html>