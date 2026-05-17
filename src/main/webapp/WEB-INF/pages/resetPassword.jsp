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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=forgot-fix-20">
    <style>
        .password-field { position: relative; }
        .password-field .form-control { padding-right: 54px; }
        .password-eye { position: absolute; right: 12px; top: 50%; transform: translateY(-50%); width: 34px; height: 34px; display: inline-flex; align-items: center; justify-content: center; border: 0; background: transparent; color: #9aa4ad; cursor: pointer; border-radius: 6px; padding: 0; }
        .password-eye:hover, .password-eye:focus { color: #2563eb; background: #eff6ff; outline: none; }
        .password-eye svg { width: 21px; height: 21px; stroke: currentColor; fill: none; stroke-width: 2; stroke-linecap: round; stroke-linejoin: round; }
        .reset-validation-error {
            display: none;
            margin-bottom: 18px;
        }
        .reset-validation-error.show {
            display: block;
        }
        .field-error {
            display: none;
            color: #dc2626;
            font-size: 0.86rem;
            margin-top: 6px;
        }
        .field-error.show {
            display: block;
        }
        .form-control.input-error {
            border-color: #dc2626;
            box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.12);
        }    </style>
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

            <form id="resetPasswordForm" action="${pageContext.request.contextPath}/reset-password" method="post" novalidate>
                <div id="resetValidationMessage" class="alert alert-danger reset-validation-error" role="alert"></div>
                <input type="hidden" name="token" value="<%= token == null ? "" : token %>">
                <div class="form-group">
                    <label for="password">New Password</label>
                    <div class="password-field">
                        <input type="password" id="password" name="password" class="form-control" data-required-message="Please enter a new password.">
                        <button type="button" class="password-eye" data-target="password" aria-label="Show password" title="Show password">
    <svg class="eye-open" viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"></path><circle cx="12" cy="12" r="3"></circle></svg>
    <svg class="eye-closed" viewBox="0 0 24 24" aria-hidden="true" style="display:none;"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19C5.5 19 2 12 2 12a20.28 20.28 0 0 1 5.06-5.94"></path><path d="M9.9 4.24A10.45 10.45 0 0 1 12 5c6.5 0 10 7 10 7a20.2 20.2 0 0 1-2.16 3.19"></path><path d="M14.12 14.12A3 3 0 0 1 9.88 9.88"></path><path d="M1 1l22 22"></path></svg>
</button>
                    </div>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">Confirm Password</label>
                    <div class="password-field">
                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" data-required-message="Please confirm your new password.">
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
        var resetPasswordForm = document.getElementById('resetPasswordForm');
        var resetMessage = document.getElementById('resetValidationMessage');
        var passwordRuleMessage = 'Password must be at least 8 characters with uppercase, lowercase, digit, and special character.';

        function isStrongPassword(value) {
            return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/.test(value);
        }

        function showResetFieldError(input, message) {
            var error = document.querySelector('[data-error-for="' + input.id + '"]');
            input.classList.add('input-error');
            if (error) {
                error.textContent = message;
                error.classList.add('show');
            }
        }

        function clearResetFieldError(input) {
            var error = document.querySelector('[data-error-for="' + input.id + '"]');
            input.classList.remove('input-error');
            if (error) {
                error.textContent = '';
                error.classList.remove('show');
            }
        }

        resetPasswordForm.addEventListener('submit', function(event) {
            var passwordInput = document.getElementById('password');
            var confirmInput = document.getElementById('confirmPassword');
            var firstInvalid = null;
            var message = '';

            clearResetFieldError(passwordInput);
            clearResetFieldError(confirmInput);

            if (passwordInput.value.trim() === '') {
                message = passwordInput.dataset.requiredMessage;
                showResetFieldError(passwordInput, message);
                firstInvalid = passwordInput;
            } else if (!isStrongPassword(passwordInput.value)) {
                message = passwordRuleMessage;
                showResetFieldError(passwordInput, message);
                firstInvalid = passwordInput;
            } else if (confirmInput.value.trim() === '') {
                message = confirmInput.dataset.requiredMessage;
                showResetFieldError(confirmInput, message);
                firstInvalid = confirmInput;
            } else if (passwordInput.value !== confirmInput.value) {
                message = 'Password and confirmation do not match.';
                showResetFieldError(confirmInput, message);
                firstInvalid = confirmInput;
            }

            if (message !== '') {
                event.preventDefault();
                resetMessage.textContent = message;
                resetMessage.classList.add('show');
                firstInvalid.focus();
            }
        });

        ['password', 'confirmPassword'].forEach(function(fieldId) {
            var input = document.getElementById(fieldId);
            input.addEventListener('input', function() {
                clearResetFieldError(input);
                resetMessage.classList.remove('show');
            });
        });
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
