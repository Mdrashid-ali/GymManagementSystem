<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isAdmin()) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Member - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="navbar">
        <div class="d-flex justify-content-between align-items-center">
            <a href="#" class="navbar-brand">FitTrack Pro Admin</a>
            <div>
                <span>Welcome, Admin</span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm" style="margin-left: 15px;">Logout</a>
            </div>
        </div>
    </div>
    
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="admin-dashboard.jsp" class="sidebar-link">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/members" class="sidebar-link">Manage Members</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/trainers" class="sidebar-link">Manage Trainers</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/add-member" class="sidebar-link active">Add Member</a>
            </li>
        </ul>
    </div>
    
    <div class="main-content">
        <h1>Add New Member</h1>
        
        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getParameter("error") %>
            </div>
        <% } %>
        
        <div class="card">
            <div class="card-header">
                <h3>Member Information</h3>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/admin/add-member" method="post">
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="firstName">First Name *</label>
                                <input type="text" id="firstName" name="firstName" class="form-control" required>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="lastName">Last Name *</label>
                                <input type="text" id="lastName" name="lastName" class="form-control" required>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="email">Email Address *</label>
                                <input type="email" id="email" name="email" class="form-control" required>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="phone">Phone Number *</label>
                                <input type="tel" id="phone" name="phone" class="form-control" required>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="password">Password *</label>
                                <input type="password" id="password" name="password" class="form-control" required>
                                <small class="text-muted">Min 8 chars with uppercase, lowercase, digit, special char</small>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="membershipType">Membership Type *</label>
                                <select id="membershipType" name="membershipType" class="form-control" required>
                                    <option value="BASIC">Basic - 1 Month</option>
                                    <option value="PREMIUM" selected>Premium - 3 Months</option>
                                    <option value="FAMILY">Family - 6 Months</option>
                                    <option value="STUDENT">Student - 1 Month</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="dateOfBirth">Date of Birth</label>
                                <input type="date" id="dateOfBirth" name="dateOfBirth" class="form-control">
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="gender">Gender</label>
                                <select id="gender" name="gender" class="form-control">
                                    <option value="">Select Gender</option>
                                    <option value="MALE">Male</option>
                                    <option value="FEMALE">Female</option>
                                    <option value="OTHER">Other</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="address">Address</label>
                        <textarea id="address" name="address" class="form-control" rows="2"></textarea>
                    </div>
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="emergencyContactName">Emergency Contact Name</label>
                                <input type="text" id="emergencyContactName" name="emergencyContactName" class="form-control">
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="emergencyContactPhone">Emergency Contact Phone</label>
                                <input type="tel" id="emergencyContactPhone" name="emergencyContactPhone" class="form-control">
                            </div>
                        </div>
                    </div>
                    
                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="height">Height (cm)</label>
                                <input type="number" step="0.1" id="height" name="height" class="form-control" placeholder="e.g., 175.5">
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="weight">Weight (kg)</label>
                                <input type="number" step="0.1" id="weight" name="weight" class="form-control" placeholder="e.g., 70.5">
                            </div>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="fitnessGoal">Fitness Goal</label>
                        <textarea id="fitnessGoal" name="fitnessGoal" class="form-control" rows="2" placeholder="e.g., Weight loss, Muscle gain, etc."></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label for="medicalNotes">Medical Notes</label>
                        <textarea id="medicalNotes" name="medicalNotes" class="form-control" rows="2" placeholder="Any medical conditions or notes"></textarea>
                    </div>
                    
                    <div class="d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/admin/members" class="btn btn-outline">Cancel</a>
                        <button type="submit" class="btn btn-primary">Add Member</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>