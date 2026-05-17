<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%
    user currentUser = (user) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String pageType = (String) request.getAttribute("pageType");
    boolean about = "about".equals(pageType);
    boolean services = "services".equals(pageType);
    boolean contact = "contact".equals(pageType);
    String title = about ? "About FitTrack Pro" : services ? "Services" : "Contact Us";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= title %> - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=brand-dashboard-16">
    <style>
        .info-hero { background:#fff; border-left:5px solid var(--primary-color); border-radius:8px; box-shadow:var(--shadow); padding:28px; margin-bottom:24px; }
        .info-hero h1 { margin-bottom:8px; }
        .info-hero p { max-width:900px; color:var(--gray-medium); margin-bottom:0; }
        .info-grid { display:grid; grid-template-columns:repeat(3,minmax(0,1fr)); gap:20px; }
        .info-card { background:#fff; border-radius:8px; box-shadow:var(--shadow); padding:22px; border-top:4px solid var(--primary-color); }
        .info-card h3 { font-size:20px; margin-bottom:10px; }
        .info-card p { color:var(--gray-medium); margin-bottom:0; }
        .contact-layout { display:grid; grid-template-columns:1fr 1.2fr; gap:20px; }
        .contact-list p { margin-bottom:12px; }
        @media (max-width:900px){ .info-grid,.contact-layout{grid-template-columns:1fr;} }
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
            <% if (currentUser.isAdmin()) { %>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/adminDashboard" class="sidebar-link">Dashboard</a></li>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/members" class="sidebar-link">Manage Members</a></li>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/trainers" class="sidebar-link">Manage Trainers</a></li>
            <% } else if (currentUser.isTrainer()) { %>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainerDashboard" class="sidebar-link">Dashboard</a></li>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/members" class="sidebar-link">My Members</a></li>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link">Workout Plans</a></li>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/attendance" class="sidebar-link">Attendance</a></li>
            <% } else { %>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/memberDashboard" class="sidebar-link">Dashboard</a></li>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/profile" class="sidebar-link">My Profile</a></li>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/attendance" class="sidebar-link">Attendance History</a></li>
                <li class="sidebar-item"><a href="${pageContext.request.contextPath}/member/workout" class="sidebar-link">Workout Plans</a></li>
            <% } %>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/about" class="sidebar-link <%= about ? "active" : "" %>">About</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/services" class="sidebar-link <%= services ? "active" : "" %>">Services</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/contact" class="sidebar-link <%= contact ? "active" : "" %>">Contact</a></li>
        </ul>
    </div>

    <div class="main-content">
        <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success"><%= request.getParameter("success") %></div>
        <% } %>

        <% if (about) { %>
            <div class="info-hero">
                <h1>About FitTrack Pro</h1>
                <p>FitTrack Pro is a gym management platform built to connect administrators, trainers, and members in one clean workflow. It keeps member records, trainer assignments, attendance, and workout plans organized so the gym team can focus on coaching and member progress.</p>
            </div>
            <div class="info-grid">
                <div class="info-card"><h3>Our Mission</h3><p>Help fitness centers run smoother with practical tools for memberships, training, attendance, and progress tracking.</p></div>
                <div class="info-card"><h3>Role Based Access</h3><p>Admins manage the operation, trainers manage coaching activity, and members view their own progress and plans.</p></div>
                <div class="info-card"><h3>Built For Growth</h3><p>The system is structured around clean records, simple workflows, and reliable reporting as your gym expands.</p></div>
            </div>
        <% } else if (services) { %>
            <div class="info-hero">
                <h1>Services</h1>
                <p>FitTrack Pro supports the core services a modern fitness facility needs every day, from onboarding members to assigning workout plans and tracking attendance.</p>
            </div>
            <div class="info-grid">
                <div class="info-card"><h3>Membership Management</h3><p>Create, update, and monitor member accounts with status, expiry dates, health notes, and contact details.</p></div>
                <div class="info-card"><h3>Trainer Management</h3><p>Add trainers, maintain their specialties and profiles, and keep trainer records available to the admin team.</p></div>
                <div class="info-card"><h3>Workout Planning</h3><p>Trainers can create structured workout plans for members with dates, exercises, notes, and active status.</p></div>
                <div class="info-card"><h3>Attendance Tracking</h3><p>Record check-ins, track active sessions, and review attendance history for member engagement.</p></div>
                <div class="info-card"><h3>Member Dashboard</h3><p>Members can view membership status, fitness data, workout plans, and attendance history from one portal.</p></div>
                <div class="info-card"><h3>Admin Oversight</h3><p>Admins get a clear overview of members, trainers, active memberships, and operational activity.</p></div>
            </div>
        <% } else { %>
            <div class="info-hero">
                <h1>Contact Us</h1>
                <p>Need support, want to update gym information, or have a question about your account? Send a message to the FitTrack Pro team.</p>
            </div>
            <div class="contact-layout">
                <div class="info-card contact-list">
                    <h3>Gym Support</h3>
                    <p><strong>Email:</strong> support@fittrackpro.com</p>
                    <p><strong>Phone:</strong> +977 9800000000</p>
                    <p><strong>Hours:</strong> Sunday-Friday, 6:00 AM - 8:00 PM</p>
                    <p><strong>Location:</strong> FitTrack Pro Fitness Center</p>
                </div>
                <div class="info-card">
                    <h3>Send A Message</h3>
                    <form action="${pageContext.request.contextPath}/contact" method="post">
                        <div class="form-group"><label for="subject">Subject</label><input id="subject" name="subject" class="form-control" required></div>
                        <div class="form-group"><label for="message">Message</label><textarea id="message" name="message" class="form-control" rows="5" required></textarea></div>
                        <button type="submit" class="btn btn-primary">Submit Message</button>
                    </form>
                </div>
            </div>
        <% } %>
    </div>
</body>
</html>