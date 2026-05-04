<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.trainer" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    trainer trainer = (trainer) request.getAttribute("trainer");
    if (trainer == null) {
        response.sendRedirect(request.getContextPath() + "/admin/trainers");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Trainer - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="navbar">
        <div class="d-flex justify-content-between align-items-center">
            <a href="#" class="navbar-brand">FitTrack Pro Admin</a>
            <div>
                <span>Welcome, <%= ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).getDisplayName() %></span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>

    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/adminDashboard" class="sidebar-link">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/members" class="sidebar-link">Manage Members</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/trainers" class="sidebar-link active">Manage Trainers</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/add-member" class="sidebar-link">Add Member</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/add-trainer" class="sidebar-link">Add Trainer</a>
            </li>
        </ul>
    </div>

    <div class="main-content">
        <h1>Edit Trainer</h1>

        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger"><%= request.getParameter("error") %></div>
        <% } %>

        <div class="card">
            <div class="card-header">
                <h3>Trainer Information</h3>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/admin/update-trainer" method="post">
                    <input type="hidden" name="trainerId" value="<%= trainer.getTrainerId() %>">

                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="firstName">First Name *</label>
                                <input type="text" id="firstName" name="firstName" class="form-control" value="<%= trainer.getFirstName() %>" required>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="lastName">Last Name *</label>
                                <input type="text" id="lastName" name="lastName" class="form-control" value="<%= trainer.getLastName() %>" required>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="email">Email Address</label>
                                <input type="email" id="email" class="form-control" value="<%= trainer.getEmail() %>" readonly disabled>
                                <small class="text-muted">Email cannot be changed</small>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="phone">Phone Number</label>
                                <input type="tel" id="phone" name="phone" class="form-control" value="<%= trainer.getPhone() == null ? "" : trainer.getPhone() %>">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="specialization">Specialization</label>
                                <select id="specialization" name="specialization" class="form-control">
                                    <option value="General Fitness" <%= "General Fitness".equals(trainer.getSpecialization()) ? "selected" : "" %>>General Fitness</option>
                                    <option value="Strength Training" <%= "Strength Training".equals(trainer.getSpecialization()) ? "selected" : "" %>>Strength Training</option>
                                    <option value="Cardio" <%= "Cardio".equals(trainer.getSpecialization()) ? "selected" : "" %>>Cardio</option>
                                    <option value="Yoga" <%= "Yoga".equals(trainer.getSpecialization()) ? "selected" : "" %>>Yoga</option>
                                    <option value="Pilates" <%= "Pilates".equals(trainer.getSpecialization()) ? "selected" : "" %>>Pilates</option>
                                    <option value="CrossFit" <%= "CrossFit".equals(trainer.getSpecialization()) ? "selected" : "" %>>CrossFit</option>
                                    <option value="Zumba" <%= "Zumba".equals(trainer.getSpecialization()) ? "selected" : "" %>>Zumba</option>
                                    <option value="Nutrition Coaching" <%= "Nutrition Coaching".equals(trainer.getSpecialization()) ? "selected" : "" %>>Nutrition Coaching</option>
                                    <option value="Rehabilitation Training" <%= "Rehabilitation Training".equals(trainer.getSpecialization()) ? "selected" : "" %>>Rehabilitation Training</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="experienceYears">Experience Years</label>
                                <select id="experienceYears" name="experienceYears" class="form-control">
                                    <% for (int i = 0; i <= 10; i++) { %>
                                        <option value="<%= i %>" <%= trainer.getExperienceYears() == i ? "selected" : "" %>><%= i == 10 ? "10+" : String.valueOf(i) %> <%= i == 1 ? "year" : "years" %></option>
                                    <% } %>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="certification">Certification</label>
                                <select id="certification" name="certification" class="form-control">
                                    <option value="Certified Personal Trainer" <%= "Certified Personal Trainer".equals(trainer.getCertification()) ? "selected" : "" %>>Certified Personal Trainer</option>
                                    <option value="ACE Certified Trainer" <%= "ACE Certified Trainer".equals(trainer.getCertification()) ? "selected" : "" %>>ACE Certified Trainer</option>
                                    <option value="NASM Certified Trainer" <%= "NASM Certified Trainer".equals(trainer.getCertification()) ? "selected" : "" %>>NASM Certified Trainer</option>
                                    <option value="ISSA Certified Trainer" <%= "ISSA Certified Trainer".equals(trainer.getCertification()) ? "selected" : "" %>>ISSA Certified Trainer</option>
                                    <option value="Yoga Alliance Certified" <%= "Yoga Alliance Certified".equals(trainer.getCertification()) ? "selected" : "" %>>Yoga Alliance Certified</option>
                                    <option value="CrossFit Level 1" <%= "CrossFit Level 1".equals(trainer.getCertification()) ? "selected" : "" %>>CrossFit Level 1</option>
                                    <option value="Sports Nutrition Certified" <%= "Sports Nutrition Certified".equals(trainer.getCertification()) ? "selected" : "" %>>Sports Nutrition Certified</option>
                                    <option value="Rehabilitation Specialist" <%= "Rehabilitation Specialist".equals(trainer.getCertification()) ? "selected" : "" %>>Rehabilitation Specialist</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="hireDate">Hire Date</label>
                                <input type="date" id="hireDate" name="hireDate" class="form-control" value="<%= trainer.getHireDate() == null ? "" : trainer.getHireDate() %>">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="status">Status</label>
                                <select id="status" name="status" class="form-control">
                                    <option value="ACTIVE" <%= "ACTIVE".equalsIgnoreCase(trainer.getStatus()) ? "selected" : "" %>>Active</option>
                                    <option value="INACTIVE" <%= "INACTIVE".equalsIgnoreCase(trainer.getStatus()) ? "selected" : "" %>>Inactive</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="availabilitySchedule">Availability Schedule</label>
                                <select id="availabilitySchedule" name="availabilitySchedule" class="form-control">
                                    <option value="Mon-Fri, 6 AM - 2 PM" <%= "Mon-Fri, 6 AM - 2 PM".equals(trainer.getAvailabilitySchedule()) ? "selected" : "" %>>Mon-Fri, 6 AM - 2 PM</option>
                                    <option value="Mon-Fri, 8 AM - 4 PM" <%= "Mon-Fri, 8 AM - 4 PM".equals(trainer.getAvailabilitySchedule()) ? "selected" : "" %>>Mon-Fri, 8 AM - 4 PM</option>
                                    <option value="Mon-Fri, 2 PM - 10 PM" <%= "Mon-Fri, 2 PM - 10 PM".equals(trainer.getAvailabilitySchedule()) ? "selected" : "" %>>Mon-Fri, 2 PM - 10 PM</option>
                                    <option value="Weekends, 8 AM - 6 PM" <%= "Weekends, 8 AM - 6 PM".equals(trainer.getAvailabilitySchedule()) ? "selected" : "" %>>Weekends, 8 AM - 6 PM</option>
                                    <option value="Morning Shift" <%= "Morning Shift".equals(trainer.getAvailabilitySchedule()) ? "selected" : "" %>>Morning Shift</option>
                                    <option value="Evening Shift" <%= "Evening Shift".equals(trainer.getAvailabilitySchedule()) ? "selected" : "" %>>Evening Shift</option>
                                    <option value="Flexible Schedule" <%= "Flexible Schedule".equals(trainer.getAvailabilitySchedule()) ? "selected" : "" %>>Flexible Schedule</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="bio">Bio</label>
                        <textarea id="bio" name="bio" class="form-control" rows="3"><%= trainer.getBio() == null ? "" : trainer.getBio() %></textarea>
                    </div>

                    <div class="d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/admin/trainers" class="btn btn-outline">Cancel</a>
                        <button type="submit" class="btn btn-primary">Update Trainer</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>