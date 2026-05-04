<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String token = request.getParameter("token") != null ? request.getParameter("token") : (String) request.getAttribute("token");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .password-field { position: relative; }
        .password-field .form-control { padding-right: 54px; }
        .password-eye { position: absolute; right: 12px; top: 50%; transform: translateY(-50%); width: 34px; height: 34px; display: inline-flex; align-items: center; justify-content: center; border: 0; background: transparent; color: #7f8c8d; cursor: pointer; border-radius: 6px; padding: 0; }
        .password-eye:hover, .password-eye:focus { color: #2ecc71; background: #e8f8f5; outline: none; }
        .password-eye svg { width: 21px; height: 21px; stroke: currentColor; fill: none; stroke-width: 2; stroke-linecap: round; stroke-linejoin: round; }    </style>
</head>
<body>
    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <h2>Reset Password</h2>
                <p>Create a new secure password</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/reset-password" method="post">
                <input type="hidden" name="token" value="<%= token == null ? "" : token %>">
                <div class="form-group">
                    <label for="password">New Password</label>
                    <div class="password-field">
                        <input type="password" id="password" name="password" class="form-control" required>
                        <button type="button" class="password-eye" data-target="password" aria-label="Show password" title="Show password">
    <svg class="eye-open" viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"></path><circle cx="12" cy="12" r="3"></circle></svg>
    <svg class="eye-closed" viewBox="0 0 24 24" aria-hidden="true" style="display:none;"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19C5.5 19 2 12 2 12a20.28 20.28 0 0 1 5.06-5.94"></path><path d="M9.9 4.24A10.45 10.45 0 0 1 12 5c6.5 0 10 7 10 7a20.2 20.2 0 0 1-2.16 3.19"></path><path d="M14.12 14.12A3 3 0 0 1 9.88 9.88"></path><path d="M1 1l22 22"></path></svg>
</button>
                    </div>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password</label>
                    <div class="password-field">
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required>
                        <button type="button" class="password-eye" data-target="confirmPassword" aria-label="Show confirm password" title="Show confirm password">
    <svg class="eye-open" viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"></path><circle cx="12" cy="12" r="3"></circle></svg>
    <svg class="eye-closed" viewBox="0 0 24 24" aria-hidden="true" style="display:none;"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19C5.5 19 2 12 2 12a20.28 20.28 0 0 1 5.06-5.94"></path><path d="M9.9 4.24A10.45 10.45 0 0 1 12 5c6.5 0 10 7 10 7a20.2 20.2 0 0 1-2.16 3.19"></path><path d="M14.12 14.12A3 3 0 0 1 9.88 9.88"></path><path d="M1 1l22 22"></path></svg>
</button>
                    </div>
                </div>
                <small class="text-muted">Password must be at least 8 characters with uppercase, lowercase, digit, and special character.</small>
                <button type="submit" class="btn btn-primary btn-block" style="margin-top: 20px;">Reset Password</button>
            </form>

            <div class="auth-footer">
                <p><a href="${pageContext.request.contextPath}/login">Back to Sign In</a></p>
            </div>
        </div>
    </div>
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
    </script>
</body>
</html>