<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.attendance" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="com.fitTrackPro.util.dateUtil" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    List<attendance> attendanceList = (List<attendance>) request.getAttribute("attendanceList");
    List<member> members = (List<member>) request.getAttribute("members");
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
            <a href="#" class="navbar-brand">FitTrack Pro <%= isTrainer ? "Trainer" : "" %></a>
            <div>
                <span>Welcome, <%= user.getDisplayName() %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>

    <% if (isMember) { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/memberDashboard" class="sidebar-link">Dashboard</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/profile" class="sidebar-link">My Profile</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/attendance" class="sidebar-link active">Attendance History</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/workout" class="sidebar-link">Workout Plans</a></li>
        </ul>
    </div>
    <% } else if (isTrainer) { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainerDashboard" class="sidebar-link">Dashboard</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/members" class="sidebar-link">My Members</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link">Workout Plans</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/create-workout" class="sidebar-link">Create Workout Plan</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/attendance" class="sidebar-link active">Attendance</a></li>
        </ul>
    </div>
    <% } %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center">
            <h1><%= isTrainer ? "Member Attendance" : "Attendance History" %></h1>
            <% if (isMember) { %>
                <a href="${pageContext.request.contextPath}/member/checkin" class="btn btn-primary">Check In Now</a>
            <% } %>
        </div>

        <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success"><%= request.getParameter("success") %></div>
        <% } %>
        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger"><%= request.getParameter("error") %></div>
        <% } %>
        <% if (session.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <%= session.getAttribute("success") %>
                <% session.removeAttribute("success"); %>
            </div>
        <% } %>

        <% if (isTrainer) { %>
        <div class="card" style="margin-bottom: 20px;">
            <div class="card-header">
                <h3>Record Member Check-in</h3>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/trainer/attendance" method="post" class="d-flex align-items-center">
                    <input type="hidden" name="action" value="checkin">
                    <select name="memberId" class="form-control" required style="max-width: 420px; margin-right: 12px;">
                        <option value="">Select Member</option>
                        <% if (members != null) { %>
                            <% for (member member : members) { %>
                                <option value="<%= member.getMemberId() %>"><%= member.getFullName() %> - <%= member.getEmail() %></option>
                            <% } %>
                        <% } %>
                    </select>
                    <button type="submit" class="btn btn-primary">Check In Member</button>
                </form>
            </div>
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
                                    <% if (isTrainer) { %><th>Member</th><% } %>
                                    <th>Date</th>
                                    <th>Check-in Time</th>
                                    <th>Check-out Time</th>
                                    <th>Duration</th>
                                    <th>Method</th>
                                    <th>Status</th>
                                    <% if (isTrainer) { %><th>Actions</th><% } %>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (attendance attendance : attendanceList) { %>
                                <tr>
                                    <% if (isTrainer) { %><td><%= attendance.getMemberName() %></td><% } %>
                                    <td><%= dateUtil.formatDisplayDate(new java.sql.Date(attendance.getCheckInTime().getTime())) %></td>
                                    <td><%= dateUtil.formatDisplayDateTime(attendance.getCheckInTime()) %></td>
                                    <td>
                                        <%= attendance.getCheckOutTime() != null ? dateUtil.formatDisplayDateTime(attendance.getCheckOutTime()) : "<span class='badge badge-warning'>Active</span>" %>
                                    </td>
                                    <td><%= attendance.getFormattedDuration() %></td>
                                    <td><%= attendance.getCheckInMethod() %></td>
                                    <td>
                                        <span class="badge <%= attendance.isCheckedOut() ? "badge-success" : "badge-warning" %>">
                                            <%= attendance.isCheckedOut() ? "Completed" : "In Progress" %>
                                        </span>
                                    </td>
                                    <% if (isTrainer) { %>
                                    <td>
                                        <% if (!attendance.isCheckedOut()) { %>
                                            <form action="${pageContext.request.contextPath}/trainer/attendance" method="post" style="display:inline;">
                                                <input type="hidden" name="action" value="checkout">
                                                <input type="hidden" name="attendanceId" value="<%= attendance.getAttendanceId() %>">
                                                <button type="submit" class="btn btn-outline btn-sm">Check Out</button>
                                            </form>
                                        <% } else { %>
                                            <span class="text-muted">Done</span>
                                        <% } %>
                                    </td>
                                    <% } %>
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
                                    for (attendance a : attendanceList) totalMinutes += a.getDurationMinutes();
                                %>
                                <%= totalMinutes / 60 %>
                            </div>
                            <div class="stat-label">Total Hours</div>
                        </div>
                    </div>
                    <div class="col-4">
                        <div class="stat-card">
                            <div class="stat-value">
                                <%
                                    int activeCount = 0;
                                    for (attendance a : attendanceList) if (!a.isCheckedOut()) activeCount++;
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