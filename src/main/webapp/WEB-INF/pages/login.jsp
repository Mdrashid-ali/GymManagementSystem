<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <h2>FitTrack Pro</h2>
                <p>Gym Management System</p>
            </div>
            
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <% if (request.getParameter("success") != null) { %>
                <div class="alert alert-success">
                    <%= request.getParameter("success") %>
                </div>
            <% } %>
            
            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" 
                           value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
                           placeholder="Enter your email" required autofocus>
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" class="form-control" 
                           placeholder="Enter your password" required>
                </div>
                
                <button type="submit" class="btn btn-primary btn-block">Sign In</button>
            </form>
            
            <div class="auth-footer">
                <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
            </div>
            
            <hr>
            
            <div style="font-size: 0.875rem; color: #7f8c8d; text-align: center;">
                <p>Demo Accounts:<br>
                Admin: admin@fittrackpro.com / Admin@123<br>
                Trainer: john.trainer@fittrackpro.com / Trainer@123<br>
                Member: jane.member@email.com / Member@123</p>
            </div>
        </div>
    </div>
</body>
</html>