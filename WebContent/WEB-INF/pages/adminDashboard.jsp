<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="com.fitTrackPro.model.trainer" %>
<%@ page import="java.util.List" %>
<%
    List<member> allMembers = (List<member>) request.getAttribute("allMembers");
    List<trainer> allTrainers = (List<trainer>) request.getAttribute("allTrainers");
    String view = (String) request.getAttribute("view");
    boolean dashboardView = view == null || "dashboard".equals(view);
    boolean memberView = "members".equals(view);
    boolean trainerView = "trainers".equals(view);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - FitTrack Pro</title>
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
                <a href="${pageContext.request.contextPath}/adminDashboard" class="sidebar-link <%= dashboardView ? "active" : "" %>">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/members" class="sidebar-link <%= memberView ? "active" : "" %>">Manage Members</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/trainers" class="sidebar-link <%= trainerView ? "active" : "" %>">Manage Trainers</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/add-member" class="sidebar-link">Add Member</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/add-trainer" class="sidebar-link">Add Trainer</a>
            </li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/about" class="sidebar-link">About</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/services" class="sidebar-link">Services</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/contact" class="sidebar-link">Contact</a></li>
        </ul>
    </div>

    <div class="main-content">
        <h1><%= trainerView ? "Manage Trainers" : memberView ? "Manage Members" : "Admin Dashboard" %></h1>
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
        <% } %>
        <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success"><%= request.getParameter("success") %></div>
        <% } %>

        <div class="row">
            <div class="col-3">
                <div class="stat-card">
                    <div class="stat-value"><%= request.getAttribute("totalMembers") != null ? request.getAttribute("totalMembers") : 0 %></div>
                    <div class="stat-label">Total Members</div>
                </div>
            </div>
            <div class="col-3">
                <div class="stat-card">
                    <div class="stat-value" style="color: #27ae60;"><%= request.getAttribute("activeMembers") != null ? request.getAttribute("activeMembers") : 0 %></div>
                    <div class="stat-label">Active Members</div>
                </div>
            </div>
            <div class="col-3">
                <div class="stat-card">
                    <div class="stat-value" style="color: #e74c3c;"><%= request.getAttribute("expiredMembers") != null ? request.getAttribute("expiredMembers") : 0 %></div>
                    <div class="stat-label">Expired Memberships</div>
                </div>
            </div>
            <div class="col-3">
                <div class="stat-card">
                    <div class="stat-value" style="color: #3498db;"><%= request.getAttribute("totalTrainers") != null ? request.getAttribute("totalTrainers") : 0 %></div>
                    <div class="stat-label">Total Trainers</div>
                </div>
            </div>
        </div>

        <% if (dashboardView || memberView) { %>
            <div class="card" style="margin-top: 20px;">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h3>Members</h3>
                    <a href="${pageContext.request.contextPath}/admin/add-member" class="btn btn-primary btn-sm">Add Member</a>
                </div>
                <div class="card-body">
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Membership</th>
                                    <th>Expiry Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (allMembers != null && !allMembers.isEmpty()) { %>
                                    <% for (member member : allMembers) { %>
                                        <tr>
                                            <td><%= member.getMemberId() %></td>
                                            <td><%= member.getFullName() %></td>
                                            <td><%= member.getEmail() %></td>
                                            <td><%= member.getMembershipType() %></td>
                                            <td><%= member.getMembershipExpiryDate() %></td>
                                            <td>
                                                <span class="badge <%= member.isMembershipActive() ? "badge-success" : "badge-warning" %>">
                                                    <%= member.isMembershipActive() ? "Active" : "Expired" %>
                                                </span>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/admin/update-member?memberId=<%= member.getMemberId() %>" class="btn btn-outline btn-sm">Edit</a>
                                                <form action="${pageContext.request.contextPath}/admin/delete-member" method="post" style="display:inline;" onsubmit="return confirm('Delete this member account permanently?');">
                                                    <input type="hidden" name="memberId" value="<%= member.getMemberId() %>">
                                                    <button type="submit" class="btn btn-danger btn-sm" style="margin-left: 6px;">Delete</button>
                                                </form>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } else { %>
                                    <tr>
                                        <td colspan="7" class="text-center">No members found.</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        <% } %>

        <% if (dashboardView || trainerView) { %>
            <div class="card" style="margin-top: 20px;">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h3>Trainers</h3>
                    <a href="${pageContext.request.contextPath}/admin/add-trainer" class="btn btn-primary btn-sm">Add Trainer</a>
                </div>
                <div class="card-body">
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Specialization</th>
                                    <th>Experience</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (allTrainers != null && !allTrainers.isEmpty()) { %>
                                    <% for (trainer trainer : allTrainers) { %>
                                        <tr>
                                            <td><%= trainer.getTrainerId() %></td>
                                            <td><%= trainer.getFullName() %></td>
                                            <td><%= trainer.getEmail() %></td>
                                            <td><%= trainer.getPhone() == null ? "" : trainer.getPhone() %></td>
                                            <td><%= trainer.getSpecialization() == null ? "" : trainer.getSpecialization() %></td>
                                            <td><%= trainer.getExperienceYears() %> years</td>
                                            <td><span class="badge badge-success"><%= trainer.getStatus() == null ? "ACTIVE" : trainer.getStatus() %></span></td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/admin/update-trainer?trainerId=<%= trainer.getTrainerId() %>" class="btn btn-outline btn-sm">Edit</a>
                                                <form action="${pageContext.request.contextPath}/admin/delete-trainer" method="post" style="display:inline;" onsubmit="return confirm('Delete this trainer account permanently?');">
                                                    <input type="hidden" name="trainerId" value="<%= trainer.getTrainerId() %>">
                                                    <button type="submit" class="btn btn-danger btn-sm" style="margin-left: 6px;">Delete</button>
                                                </form>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } else { %>
                                    <tr>
                                        <td colspan="8" class="text-center">No trainers found.</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        <% } %>
    </div>
</body>
</html>