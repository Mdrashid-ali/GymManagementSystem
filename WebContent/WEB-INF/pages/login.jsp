<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login - FitTrack Pro</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css?v=logout-door-fixed-14">
<style>
.sign-in-icon {
	width: 18px;
	height: 18px;
	fill: currentColor;
	margin-right: 8px;
	vertical-align: -3px;
}

.auth-card .btn-primary.sign-in-button::before {
	display: none;
}

.password-field {
	position: relative;
}

.password-field .form-control {
	padding-right: 54px;
}

.password-eye {
	position: absolute;
	right: 12px;
	top: 50%;
	transform: translateY(-50%);
	width: 34px;
	height: 34px;
	display: inline-flex;
	align-items: center;
	justify-content: center;
	border: 0;
	background: transparent;
	color: #9aa4ad;
	cursor: pointer;
	border-radius: 6px;
	padding: 0;
}

.password-eye:hover, .password-eye:focus {
	color: #2563eb;
	background: #eff6ff;
	outline: none;
}

.password-eye svg {
	width: 21px;
	height: 21px;
	stroke: currentColor;
	fill: none;
	stroke-width: 2;
	stroke-linecap: round;
	stroke-linejoin: round;
}

.auth-links {
	display: flex;
	justify-content: space-between;
	gap: 12px;
	margin-top: 12px;
	font-size: 0.9rem;
}

.client-login-error {
	display: none;
}

.client-login-error.show {
	display: block;
}
</style>
</head>
<body>
	<main class="auth-container">
		<div class="auth-card">
			<div class="auth-header">
				<h2>FitTrack Pro</h2>
				<p>Gym Management System</p>
			</div>

			<%
			if (request.getAttribute("error") != null) {
			%>
			<div class="alert alert-danger">
				<%=request.getAttribute("error")%>
			</div>
			<%
			}
			%>

			<%
			if (request.getParameter("success") != null) {
			%>
			<div class="alert alert-success">
				<%=request.getParameter("success")%>
			</div>
			<%
			}
			%>

			<form id="loginForm"
				action="${pageContext.request.contextPath}/login" method="post"
				novalidate>
				<div id="loginValidationMessage"
					class="alert alert-danger client-login-error" role="alert"></div>
				<div class="form-group">
					<label for="email">Email Address</label> <input type="email"
						id="email" name="email" class="form-control"
						value="<%=request.getAttribute("email") != null ? request.getAttribute("email") : ""%>"
						placeholder="Enter your email" autofocus>
				</div>

				<div class="form-group">
					<label for="password">Password</label>
					<div class="password-field">
						<input type="password" id="password" name="password"
							class="form-control" placeholder="Enter your password">
						<button type="button" class="password-eye" data-target="password"
							aria-label="Show password" title="Show password">
							<svg class="eye-open" viewBox="0 0 24 24" aria-hidden="true">
                                <path
									d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z"></path>
                                <circle cx="12" cy="12" r="3"></circle>
                            </svg>
							<svg class="eye-closed" viewBox="0 0 24 24" aria-hidden="true"
								style="display: none;">
                                <path
									d="M17.94 17.94A10.94 10.94 0 0 1 12 19C5.5 19 2 12 2 12a20.28 20.28 0 0 1 5.06-5.94"></path>
                                <path
									d="M9.9 4.24A10.45 10.45 0 0 1 12 5c6.5 0 10 7 10 7a20.2 20.2 0 0 1-2.16 3.19"></path>
                                <path
									d="M14.12 14.12A3 3 0 0 1 9.88 9.88"></path>
                                <path d="M1 1l22 22"></path>
                            </svg>
						</button>
					</div>
				</div>

				<button type="submit"
					class="btn btn-primary btn-block sign-in-button">
					<svg class="sign-in-icon" viewBox="0 0 24 24" aria-hidden="true">
                        <path
							d="M17 9h-1V7a4 4 0 0 0-8 0v2H7a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-8a2 2 0 0 0-2-2Zm-7-2a2 2 0 0 1 4 0v2h-4V7Zm3 9.73V18h-2v-1.27a2 2 0 1 1 2 0Z"></path>
                    </svg>
					Sign In
				</button>
				<div class="auth-links">
					<a href="${pageContext.request.contextPath}/forgot-password">Forgot
						password?</a>
				</div>
			</form>

			<div class="auth-footer">
				<p>
					Don't have an account? <a
						href="${pageContext.request.contextPath}/register">Register
						here</a>
				</p>
			</div>

			<hr>

			<div class="auth-demo-box">
				<p>
					Demo Accounts:<br> Admin: admin@fittrackpro.com / Admin@123<br>
					Trainer: john.trainer@fittrackpro.com / Trainer@123<br>
					Member: jane.member@email.com / Member@123
				</p>
			</div>
		</div>
	</main>
	<script>
		var loginForm = document.getElementById('loginForm');
		var loginMessage = document.getElementById('loginValidationMessage');
		var emailInput = document.getElementById('email');
		var passwordInput = document.getElementById('password');

		loginForm
				.addEventListener(
						'submit',
						function(event) {
							var emailEmpty = emailInput.value.trim() === '';
							var passwordEmpty = passwordInput.value.trim() === '';

							if (emailEmpty || passwordEmpty) {
								event.preventDefault();
								loginMessage.textContent = emailEmpty
										&& passwordEmpty ? 'Please enter your email address and password.'
										: emailEmpty ? 'Please enter your email address.'
												: 'Please enter your password.';
								loginMessage.classList.add('show');
								(emailEmpty ? emailInput : passwordInput)
										.focus();
							}
						});

		[ emailInput, passwordInput ].forEach(function(input) {
			input.addEventListener('input', function() {
				loginMessage.classList.remove('show');
				loginMessage.textContent = '';
			});
		});
		document
				.querySelectorAll('.password-eye')
				.forEach(
						function(button) {
							button
									.addEventListener(
											'click',
											function() {
												var input = document
														.getElementById(button.dataset.target);
												var showing = input.type === 'text';
												input.type = showing ? 'password'
														: 'text';
												button
														.setAttribute(
																'aria-label',
																showing ? 'Show password'
																		: 'Hide password');
												button
														.setAttribute(
																'title',
																showing ? 'Show password'
																		: 'Hide password');
												button
														.querySelector('.eye-open').style.display = showing ? ''
														: 'none';
												button
														.querySelector('.eye-closed').style.display = showing ? 'none'
														: '';
											});
						});
	</script>
</body>
</html>
