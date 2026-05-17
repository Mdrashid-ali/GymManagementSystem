package com.fitTrackPro.controller;

import com.fitTrackPro.service.passwordResetService;
import com.fitTrackPro.util.validationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet({ "/forgot-password", "/reset-password" })
/**
 * Handles forgot-password token requests and password reset submissions.
 */
public class PasswordResetServlet extends HttpServlet {
	private final passwordResetService resets = new passwordResetService();

	@Override
	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if ("/reset-password".equals(request.getServletPath())) {
			String token = request.getParameter("token");
			try {
				if (!resets.isValidToken(token)) {
					request.setAttribute("error", "Reset link is invalid, expired, or already used. Generate a new link.");
					request.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(request, response);
					return;
				}
			} catch (SQLException e) {
				log("Reset token validation failed", e);
				request.setAttribute("error", "Database error: " + e.getMessage());
				request.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(request, response);
				return;
			}
			request.setAttribute("token", token);
			request.getRequestDispatcher("/WEB-INF/pages/resetPassword.jsp").forward(request, response);
			return;
		}
		request.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(request, response);
	}

	@Override
	/** Handles HTTP POST form submissions for this servlet route. */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			if ("/reset-password".equals(request.getServletPath())) {
				resetPassword(request, response);
				return;
			}
			requestReset(request, response);
		} catch (SQLException e) {
			log("Password reset failed", e);
			request.setAttribute("error", "Database error: " + e.getMessage());
			doGet(request, response);
		}
	}

	/** Handles requestReset logic. */
	private void requestReset(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ServletException, IOException {
		String email = request.getParameter("email");
		request.setAttribute("email", email);
		if (!validationUtil.isValidEmail(email)) {
			request.setAttribute("error", "Enter a valid registered email address.");
			request.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(request, response);
			return;
		}

		String token = resets.createResetToken(email.trim());
		if (token == null) {
			request.setAttribute("error", "No active account was found for that email.");
			request.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(request, response);
			return;
		}

		String resetLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + "/reset-password?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
		request.setAttribute("resetLink", resetLink);
		request.getRequestDispatcher("/WEB-INF/pages/forgotPassword.jsp").forward(request, response);
	}

	/** Updates the password for a valid reset token. */
	private void resetPassword(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ServletException, IOException {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		request.setAttribute("token", token);

		if (token == null || token.isBlank()) {
			request.setAttribute("error", "Reset token is missing.");
			request.getRequestDispatcher("/WEB-INF/pages/resetPassword.jsp").forward(request, response);
			return;
		}
		if (!validationUtil.isStrongPassword(password)) {
			request.setAttribute("error",
					"Password must include uppercase, lowercase, digit, special character, and be at least 8 characters.");
			request.getRequestDispatcher("/WEB-INF/pages/resetPassword.jsp").forward(request, response);
			return;
		}
		if (!password.equals(confirmPassword)) {
			request.setAttribute("error", "Password and confirmation do not match.");
			request.getRequestDispatcher("/WEB-INF/pages/resetPassword.jsp").forward(request, response);
			return;
		}

		if (!resets.resetPassword(token, password)) {
			request.setAttribute("error", "This reset link is invalid or expired.");
			request.getRequestDispatcher("/WEB-INF/pages/resetPassword.jsp").forward(request, response);
			return;
		}
		response.sendRedirect(request.getContextPath() + "/login?success="
				+ URLEncoder.encode("Password reset successful. Please sign in.", StandardCharsets.UTF_8));
	}
}
