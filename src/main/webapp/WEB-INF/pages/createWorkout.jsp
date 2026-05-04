<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isTrainer()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<member> members = (List<member>) request.getAttribute("members");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Workout Plan - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="navbar">
        <div class="d-flex justify-content-between align-items-center">
            <a href="#" class="navbar-brand">FitTrack Pro Trainer</a>
            <div>
                <span>Welcome, <%= user.getDisplayName() %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>

    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainerDashboard" class="sidebar-link">Dashboard</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/members" class="sidebar-link">My Members</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link">Workout Plans</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/create-workout" class="sidebar-link active">Create Workout Plan</a></li>
        </ul>
    </div>

    <div class="main-content">
        <h1>Create Workout Plan</h1>
        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger"><%= request.getParameter("error") %></div>
        <% } %>

        <div class="card">
            <div class="card-header"><h3>Plan Details</h3></div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/trainer/create-workout" method="post">
                    <div class="form-group">
                        <label for="memberId">Member</label>
                        <select id="memberId" name="memberId" class="form-control" required>
                            <option value="">Select member</option>
                            <% if (members != null) { %>
                                <% for (member member : members) { %>
                                    <option value="<%= member.getMemberId() %>"><%= member.getFullName() %> (<%= member.getEmail() %>)</option>
                                <% } %>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="planName">Plan Name</label>
                        <input type="text" id="planName" name="planName" class="form-control" required>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="startDate">Start Date</label>
                                <input type="date" id="startDate" name="startDate" class="form-control" required>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="endDate">End Date</label>
                                <input type="date" id="endDate" name="endDate" class="form-control" required>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control" rows="3"></textarea>
                    </div>

                    <div class="form-group">
                        <label for="exercises">Exercises</label>
                        <textarea id="exercises" name="exercises" class="form-control" rows="8" required></textarea>
                    </div>

                    <div class="form-group">
                        <label for="notes">Notes</label>
                        <textarea id="notes" name="notes" class="form-control" rows="3"></textarea>
                    </div>

                    <div class="d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/trainer/workouts" class="btn btn-outline">Cancel</a>
                        <button type="submit" class="btn btn-primary">Create Plan</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
