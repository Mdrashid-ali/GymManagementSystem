<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Register - FitTrack Pro</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css?v=register-icon-clear-15">
<style>
.register-button-icon {
	width: 22px;
	height: 22px;
	fill: none;
	stroke: currentColor;
	stroke-width: 2.4;
	stroke-linecap: round;
	stroke-linejoin: round;
	margin-right: 8px;
	vertical-align: -5px;
}

.auth-card .btn-primary.register-button::before {
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

.register-validation-error {
	display: none;
	margin-bottom: 18px;
}

.register-validation-error.show {
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
</style>
</head>
<body>
	<main class="auth-container">
		<div class="auth-card auth-card-wide">
			<div class="auth-header">
				<h2>Create Account</h2>
				<p>Join FitTrack Pro Today</p>
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

			<form id="registerForm"
				action="${pageContext.request.contextPath}/register" method="post"
				novalidate>
				<div id="registerValidationMessage"
					class="alert alert-danger register-validation-error" role="alert"></div>
				<div class="row">
					<div class="col-6">
						<div class="form-group">
							<label for="firstName">First Name</label> <input type="text"
								id="firstName" name="firstName" class="form-control"
								value="<%=request.getAttribute("firstName") != null ? request.getAttribute("firstName") : ""%>"
								data-required-message="Please enter your first name.">
							<div class="field-error" data-error-for="firstName"></div>
						</div>
					</div>
					<div class="col-6">
						<div class="form-group">
							<label for="lastName">Last Name</label> <input type="text"
								id="lastName" name="lastName" class="form-control"
								value="<%=request.getAttribute("lastName") != null ? request.getAttribute("lastName") : ""%>"
								data-required-message="Please enter your last name.">
							<div class="field-error" data-error-for="lastName"></div>
						</div>
					</div>
				</div>

				<div class="form-group">
					<label for="email">Email Address</label> <input type="email"
						id="email" name="email" class="form-control"
						value="<%=request.getAttribute("email") != null ? request.getAttribute("email") : ""%>"
						data-required-message="Please enter your email address.">
					<div class="field-error" data-error-for="email"></div>
				</div>

				<div class="form-group">
					<label for="phone">Phone Number</label> <input type="tel"
						id="phone" name="phone" class="form-control"
						value="<%=request.getAttribute("phone") != null ? request.getAttribute("phone") : ""%>"
						data-required-message="Please enter your phone number.">
					<div class="field-error" data-error-for="phone"></div>
				</div>
				<div class="form-group">
					<label for="membershipType">Membership Type</label> <select
						id="membershipType" name="membershipType" class="form-control"
						required>
						<option value="BASIC">Basic - NPR 4,500/month</option>
						<option value="PREMIUM" selected>Premium - NPR
							7,500/month</option>
						<option value="FAMILY">Family - NPR 13,500/month</option>
						<option value="STUDENT">Student - NPR 3,000/month</option>
					</select>
				</div>

				<div class="row">
					<div class="col-6">
						<div class="form-group">
							<label for="password">Password</label>
							<div class="password-field">
								<input type="password" id="password" name="password"
									class="form-control"
									data-required-message="Please enter a password.">
								<button type="button" class="password-eye"
									data-target="password" aria-label="Show password"
									title="Show password">
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
							<div class="field-error" data-error-for="password"></div>
						</div>
					</div>
					<div class="col-6">
						<div class="form-group">
							<label for="confirmPassword">Confirm Password</label>
							<div class="password-field">
								<input type="password" id="confirmPassword"
									name="confirmPassword" class="form-control"
									data-required-message="Please confirm your password.">
								<button type="button" class="password-eye"
									data-target="confirmPassword"
									aria-label="Show confirm password"
									title="Show confirm password">
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
							<div class="field-error" data-error-for="confirmPassword"></div>
						</div>
					</div>
				</div>

				<small class="text-muted">Password must be at least 8
					characters with uppercase, lowercase, digit, and special character.</small>
				<button type="submit"
					class="btn btn-primary btn-block auth-submit-spaced register-button">
					<svg class="register-button-icon" viewBox="0 0 24 24"
						aria-hidden="true">
                        <circle cx="9" cy="7" r="4"></circle>
                        <path d="M3 21v-2a6 6 0 0 1 6-6h1.5"></path>
                        <path d="M18 14v6"></path>
                        <path d="M15 17h6"></path>
                    </svg>
					Register
				</button>
			</form>

			<div class="auth-footer">
				<p>
					Already have an account? <a
						href="${pageContext.request.contextPath}/login">Sign In</a>
				</p>
			</div>
		</div>
	</main>
	<script>
		var registerForm = document.getElementById('registerForm');
		var registerMessage = document
				.getElementById('registerValidationMessage');
		var requiredRegisterFields = [ 'firstName', 'lastName', 'email',
				'phone', 'password', 'confirmPassword' ];

		function showRegisterFieldError(input, message) {
			var error = document.querySelector('[data-error-for="' + input.id
					+ '"]');
			input.classList.add('input-error');
			if (error) {
				error.textContent = message;
				error.classList.add('show');
			}
		}

		function clearRegisterFieldError(input) {
			var error = document.querySelector('[data-error-for="' + input.id
					+ '"]');
			input.classList.remove('input-error');
			if (error) {
				error.textContent = '';
				error.classList.remove('show');
			}
		}

		var passwordRuleMessage = 'Password must be at least 8 characters with uppercase, lowercase, digit, and special character.';

		function isStrongPassword(value) {
			return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/
					.test(value);
		}
		registerForm
				.addEventListener(
						'submit',
						function(event) {
							var firstInvalid = null;
							var hasError = false;

							requiredRegisterFields.forEach(function(fieldId) {
								var input = document.getElementById(fieldId);
								clearRegisterFieldError(input);
								if (input.value.trim() === '') {
									hasError = true;
									showRegisterFieldError(input,
											input.dataset.requiredMessage);
									if (!firstInvalid) {
										firstInvalid = input;
									}
								}
							});

							var emailInput = document.getElementById('email');
							if (emailInput.value.trim() !== ''
									&& !emailInput.checkValidity()) {
								hasError = true;
								showRegisterFieldError(emailInput,
										'Please enter a valid email address.');
								if (!firstInvalid) {
									firstInvalid = emailInput;
								}
							}

							var passwordInput = document
									.getElementById('password');
							if (passwordInput.value.trim() !== ''
									&& !isStrongPassword(passwordInput.value)) {
								hasError = true;
								showRegisterFieldError(passwordInput,
										passwordRuleMessage);
								if (!firstInvalid) {
									firstInvalid = passwordInput;
								}
							}
							var confirmInput = document
									.getElementById('confirmPassword');
							if (passwordInput.value
									&& confirmInput.value
									&& passwordInput.value !== confirmInput.value) {
								hasError = true;
								showRegisterFieldError(confirmInput,
										'Password and confirmation do not match.');
								registerMessage.textContent = 'Password and confirmation do not match.';
								registerMessage.classList.add('show');
								if (!firstInvalid) {
									firstInvalid = confirmInput;
								}
							}

							if (hasError) {
								event.preventDefault();
								registerMessage.textContent = 'Please complete the required fields before registering.';
								registerMessage.classList.add('show');
								firstInvalid.focus();
							}
						});

		requiredRegisterFields.forEach(function(fieldId) {
			var input = document.getElementById(fieldId);
			input.addEventListener('input', function() {
				clearRegisterFieldError(input);
				registerMessage.classList.remove('show');
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
