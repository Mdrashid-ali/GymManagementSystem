<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.attendance" %>
<%@ page import="com.fitTrackPro.util.dateUtil" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<attendance> attendanceList = (List<attendance>) request.getAttribute("attendanceList");
    boolean isMember = user.isMember();
    boolean isTrainer = user.isTrainer();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Attendance - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
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
                <a href="${pageContext.request.contextPath}/member/attendance" class="sidebar-link active">Attendance History</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/workout" class="sidebar-link">Workout Plans</a>
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
                <a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link">Workout Plans</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/attendance" class="sidebar-link active">Attendance</a>
            </li>
        </ul>
    </div>
    <% } %>
    
    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center">
            <h1>Attendance History</h1>
            <% if (isMember) { %>
            <a href="${pageContext.request.contextPath}/member/checkin" class="btn btn-primary">Check In Now</a>
            <% } %>
        </div>
        
        <% if (session.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <%= session.getAttribute("success") %>
                <% session.removeAttribute("success"); %>
            </div>
        <% } %>
        
        <div class="card">
            <div class="card-header">
                <h3>Check-in Records</h3>
            </div>
            <div class="card-body">
                <% if (attendanceList != null && !attendanceList.isEmpty()) { %>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Check-in Time</th>
                                    <th>Check-out Time</th>
                                    <th>Duration</th>
                                    <th>Method</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (attendance attendance : attendanceList) { %>
                                <tr>
                                    <td><%= dateUtil.formatDisplayDate(new java.sql.Date(attendance.getCheckInTime().getTime())) %></td>
                                    <td><%= dateUtil.formatDisplayDateTime(attendance.getCheckInTime()) %></td>
                                    <td>
                                        <%= attendance.getCheckOutTime() != null ? 
                                            dateUtil.formatDisplayDateTime(attendance.getCheckOutTime()) : 
                                            "<span class='badge badge-warning'>Active</span>" %>
                                    </td>
                                    <td><%= attendance.getFormattedDuration() %></td>
                                    <td><%= attendance.getCheckInMethod() %></td>
                                    <td>
                                        <span class="badge <%= attendance.isCheckedOut() ? "badge-success" : "badge-warning" %>">
                                            <%= attendance.isCheckedOut() ? "Completed" : "In Progress" %>
                                        </span>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="text-center" style="padding: 40px;">
                        <p class="text-muted">No attendance records found.</p>
                        <% if (isMember) { %>
                        <a href="${pageContext.request.contextPath}/member/checkin" class="btn btn-primary" style="margin-top: 20px;">Check In for First Time</a>
                        <% } %>
                    </div>
                <% } %>
            </div>
        </div>
        
        <% if (attendanceList != null && !attendanceList.isEmpty()) { %>
        <div class="card" style="margin-top: 20px;">
            <div class="card-header">
                <h3>Attendance Summary</h3>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-4">
                        <div class="stat-card">
                            <div class="stat-value"><%= attendanceList.size() %></div>
                            <div class="stat-label">Total Visits</div>
                        </div>
                    </div>
                    <div class="col-4">
                        <div class="stat-card">
                            <div class="stat-value">
                                <%
                                    long totalMinutes = 0;
                                    for (attendance a : attendanceList) {
                                        totalMinutes += a.getDurationMinutes();
                                    }
                                    long totalHours = totalMinutes / 60;
                                %>
                                <%= totalHours %>
                            </div>
                            <div class="stat-label">Total Hours</div>
                        </div>
                    </div>
                    <div class="col-4">
                        <div class="stat-card">
                            <div class="stat-value">
                                <%
                                    int activeCount = 0;
                                    for (attendance a : attendanceList) {
                                        if (!a.isCheckedOut()) activeCount++;
                                    }
                                %>
                                <%= activeCount %>
                            </div>
                            <div class="stat-label">Active Sessions</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <% } %>
    </div>
</body>
</html>