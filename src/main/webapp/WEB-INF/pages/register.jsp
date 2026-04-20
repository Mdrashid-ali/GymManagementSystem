<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-container">
        <div class="auth-card" style="max-width: 500px;">
            <div class="auth-header">
                <h2>Create Account</h2>
                <p>Join FitTrack Pro Today</p>
            </div>
            
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <form action="${pageContext.request.contextPath}/register" method="post">
                <div class="row">
                    <div class="col-6">
                        <div class="form-group">
                            <label for="firstName">First Name</label>
                            <input type="text" id="firstName" name="firstName" class="form-control" 
                                   value="<%= request.getAttribute("firstName") != null ? request.getAttribute("firstName") : "" %>"
                                   required>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="form-group">
                            <label for="lastName">Last Name</label>
                            <input type="text" id="lastName" name="lastName" class="form-control"
                                   value="<%= request.getAttribute("lastName") != null ? request.getAttribute("lastName") : "" %>"
                                   required>
                        </div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control"
                           value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
                           required>
                </div>
                
                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="tel" id="phone" name="phone" class="form-control"
                           value="<%= request.getAttribute("phone") != null ? request.getAttribute("phone") : "" %>"
                           required>
                </div>
                
                <div class="form-group">
                    <label for="membershipType">Membership Type</label>
                    <select id="membershipType" name="membershipType" class="form-control" required>
                        <option value="BASIC">Basic - $29.99/month</option>
                        <option value="PREMIUM" selected>Premium - $49.99/month</option>
                        <option value="FAMILY">Family - $89.99/month</option>
                        <option value="STUDENT">Student - $19.99/month</option>
                    </select>
                </div>
                
                <div class="row">
                    <div class="col-6">
                        <div class="form-group">
                            <label for="password">Password</label>
                            <input type="password" id="password" name="password" class="form-control" required>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="form-group">
                            <label for="confirmPassword">Confirm Password</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required>
                        </div>
                    </div>
                </div>
                
                <small class="text-muted">Password must be at least 8 characters with uppercase, lowercase, digit, and special character.</small>
                
                <button type="submit" class="btn btn-primary btn-block" style="margin-top: 20px;">Register</button>
            </form>
            
            <div class="auth-footer">
                <p>Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Sign In</a></p>
            </div>
        </div>
    </div>
</body>
</html>