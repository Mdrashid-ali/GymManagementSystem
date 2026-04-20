<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fitTrackPro.model.member" %>
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
    <title>Admin Dashboard - FitTrack Pro</title>
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
                <a href="admin-dashboard.jsp" class="sidebar-link active">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/members" class="sidebar-link">Manage Members</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/trainers" class="sidebar-link">Manage Trainers</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/add-member" class="sidebar-link">Add Member</a>
            </li>
        </ul>
    </div>
    
    <div class="main-content">
        <h1>Admin Dashboard</h1>
        
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
        
        <div class="card" style="margin-top: 20px;">
            <div class="card-header">
                <h3>Recent Members</h3>
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
                            <%
                            List<member> members = (List<member>) request.getAttribute("recentMembers");
                            if (members != null) {
                                for (member m : members) {
                            %>
                            <tr>
                                <td><%= m.getMemberId() %></td>
                                <td><%= m.getFullName() %></td>
                                <td><%= m.getEmail() %></td>
                                <td><%= m.getMembershipType() %></td>
                                <td><%= m.getMembershipExpiryDate() %></td>
                                <td>
                                    <span class="badge <%= m.isMembershipActive() ? "badge-success" : "badge-danger" %>">
                                        <%= m.isMembershipActive() ? "Active" : "Expired" %>
                                    </span>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/edit-member?id=<%= m.getMemberId() %>" class="btn btn-sm btn-outline">Edit</a>
                                    <a href="${pageContext.request.contextPath}/admin/delete-member?id=<%= m.getMemberId() %>" 
                                       class="btn btn-sm btn-danger" onclick="return confirm('Are you sure?')">Delete</a>
                                </td>
                            </tr>
                            <%
                                }
                            }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</body>
</html>