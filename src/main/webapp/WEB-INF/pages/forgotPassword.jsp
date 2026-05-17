<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=forgot-fix-20">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <h2>Forgot Password</h2>
                <p>Reset access to your FitTrack Pro account</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
            <% } %>

            <% if (request.getAttribute("resetLink") != null) { %>
                <div class="alert alert-success">
                    Reset link generated. Use it within 30 minutes.
                    <div style="margin-top: 12px;">
                        <a class="btn btn-primary btn-sm" href="<%= request.getAttribute("resetLink") %>">Open Reset Page</a>
                    </div>
                    <small style="display:block; margin-top: 10px; word-break: break-all;"><%= request.getAttribute("resetLink") %></small>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                <div class="form-group">
                    <label for="email">Registered Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>" required autofocus>
                </div>
                <button type="submit" class="btn btn-primary btn-block">Generate Reset Link</button>
            </form>

            <div class="auth-footer">
                <p><a href="${pageContext.request.contextPath}/login">Back to Sign In</a></p>
            </div>
        </div>
    </div>
</body>
</html>
