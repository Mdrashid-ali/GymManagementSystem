<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Trainer - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .password-field { position: relative; }
        .password-field .form-control { padding-right: 54px; }
        .password-eye { position: absolute; right: 12px; top: 50%; transform: translateY(-50%); width: 34px; height: 34px; display: inline-flex; align-items: center; justify-content: center; border: 0; background: transparent; color: #7f8c8d; cursor: pointer; border-radius: 6px; padding: 0; }
        .password-eye:hover, .password-eye:focus { color: #2ecc71; background: #e8f8f5; outline: none; }
        .password-eye svg { width: 21px; height: 21px; stroke: currentColor; fill: none; stroke-width: 2; stroke-linecap: round; stroke-linejoin: round; }    </style>
</head>
<body>
    <div class="navbar"><div class="d-flex justify-content-between align-items-center"><a href="#" class="navbar-brand">FitTrack Pro Admin</a><div><span>Welcome, <%= ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).getDisplayName() %></span><a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a></div></div></div>
    <div class="sidebar"><ul class="sidebar-menu"><li class="sidebar-item"><a href="${pageContext.request.contextPath}/adminDashboard" class="sidebar-link">Dashboard</a></li><li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/members" class="sidebar-link">Manage Members</a></li><li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/trainers" class="sidebar-link">Manage Trainers</a></li><li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/add-member" class="sidebar-link">Add Member</a></li><li class="sidebar-item"><a href="${pageContext.request.contextPath}/admin/add-trainer" class="sidebar-link active">Add Trainer</a></li></ul></div>
    <div class="main-content"><h1>Add New Trainer</h1><% if (request.getParameter("error") != null) { %><div class="alert alert-danger"><%= request.getParameter("error") %></div><% } %><div class="card"><div class="card-header"><h3>Trainer Information</h3></div><div class="card-body"><form action="${pageContext.request.contextPath}/admin/add-trainer" method="post">
        <div class="row"><div class="col-6"><div class="form-group"><label for="firstName">First Name *</label><input type="text" id="firstName" name="firstName" class="form-control" required></div></div><div class="col-6"><div class="form-group"><label for="lastName">Last Name *</label><input type="text" id="lastName" name="lastName" class="form-control" required></div></div></div>
        <div class="row"><div class="col-6"><div class="form-group"><label for="email">Email Address *</label><input type="email" id="email" name="email" class="form-control" required></div></div><div class="col-6"><div class="form-group"><label for="phone">Phone Number</label><input type="tel" id="phone" name="phone" class="form-control"></div></div></div>
        <div class="row"><div class="col-6"><div class="form-group"><label for="password">Password *</label><div class="password-field"><input type="password" id="password" name="password" class="form-control" required><button type="button" class="password-eye" data-target="password" aria-label="Show password" title="Show password">
    <svg class="eye-open" viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"></path><circle cx="12" cy="12" r="3"></circle></svg>
    <svg class="eye-closed" viewBox="0 0 24 24" aria-hidden="true" style="display:none;"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19C5.5 19 2 12 2 12a20.28 20.28 0 0 1 5.06-5.94"></path><path d="M9.9 4.24A10.45 10.45 0 0 1 12 5c6.5 0 10 7 10 7a20.2 20.2 0 0 1-2.16 3.19"></path><path d="M14.12 14.12A3 3 0 0 1 9.88 9.88"></path><path d="M1 1l22 22"></path></svg>
</button></div><small class="text-muted">Min 8 chars with uppercase, lowercase, digit, special char</small></div></div><div class="col-6"><div class="form-group"><label for="experienceYears">Experience Years</label><select id="experienceYears" name="experienceYears" class="form-control"><option value="0">0 years</option><option value="1">1 year</option><option value="2">2 years</option><option value="3">3 years</option><option value="4">4 years</option><option value="5">5 years</option><option value="6">6 years</option><option value="7">7 years</option><option value="8">8 years</option><option value="9">9 years</option><option value="10">10+ years</option></select></div></div></div>
        <div class="row"><div class="col-6"><div class="form-group"><label for="specialization">Specialization</label><select id="specialization" name="specialization" class="form-control"><option value="General Fitness">General Fitness</option><option value="Strength Training">Strength Training</option><option value="Cardio">Cardio</option><option value="Yoga">Yoga</option><option value="Pilates">Pilates</option><option value="CrossFit">CrossFit</option><option value="Zumba">Zumba</option><option value="Nutrition Coaching">Nutrition Coaching</option><option value="Rehabilitation Training">Rehabilitation Training</option></select></div></div><div class="col-6"><div class="form-group"><label for="certification">Certification</label><select id="certification" name="certification" class="form-control"><option value="Certified Personal Trainer">Certified Personal Trainer</option><option value="ACE Certified Trainer">ACE Certified Trainer</option><option value="NASM Certified Trainer">NASM Certified Trainer</option><option value="ISSA Certified Trainer">ISSA Certified Trainer</option><option value="Yoga Alliance Certified">Yoga Alliance Certified</option><option value="CrossFit Level 1">CrossFit Level 1</option><option value="Sports Nutrition Certified">Sports Nutrition Certified</option><option value="Rehabilitation Specialist">Rehabilitation Specialist</option></select></div></div></div>
        <div class="row"><div class="col-6"><div class="form-group"><label for="hireDate">Hire Date</label><input type="date" id="hireDate" name="hireDate" class="form-control"></div></div><div class="col-6"><div class="form-group"><label for="availabilitySchedule">Availability Schedule</label><select id="availabilitySchedule" name="availabilitySchedule" class="form-control"><option value="Mon-Fri, 6 AM - 2 PM">Mon-Fri, 6 AM - 2 PM</option><option value="Mon-Fri, 8 AM - 4 PM">Mon-Fri, 8 AM - 4 PM</option><option value="Mon-Fri, 2 PM - 10 PM">Mon-Fri, 2 PM - 10 PM</option><option value="Weekends, 8 AM - 6 PM">Weekends, 8 AM - 6 PM</option><option value="Morning Shift">Morning Shift</option><option value="Evening Shift">Evening Shift</option><option value="Flexible Schedule">Flexible Schedule</option></select></div></div></div>
        <div class="form-group"><label for="bio">Bio</label><textarea id="bio" name="bio" class="form-control" rows="3" placeholder="Trainer background and focus areas"></textarea></div><div class="d-flex justify-content-between"><a href="${pageContext.request.contextPath}/adminDashboard" class="btn btn-outline">Cancel</a><button type="submit" class="btn btn-primary">Add Trainer</button></div>
    </form></div></div></div>
    <script>
        document.querySelectorAll('.password-eye').forEach(function(button) {
            button.addEventListener('click', function() {
                var input = document.getElementById(button.dataset.target);
                var showing = input.type === 'text';
                input.type = showing ? 'password' : 'text';
                button.setAttribute('aria-label', showing ? 'Show password' : 'Hide password');
                button.setAttribute('title', showing ? 'Show password' : 'Hide password');
                button.querySelector('.eye-open').style.display = showing ? '' : 'none';
                button.querySelector('.eye-closed').style.display = showing ? 'none' : '';
            });
        });
    </script></body>
</html>