package com.fitTrackPro.controller;

import com.fitTrackPro.model.user;
import com.fitTrackPro.service.userService;
import com.fitTrackPro.util.validationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

@WebServlet("/login")
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL loginPage = getServletContext().getResource("/WEB-INF/pages/login.jsp");
		if (loginPage != null) {
			request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
			return;
		}
		renderFallbackLogin(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		request.setAttribute("email", email);

		if (!validationUtil.isValidEmail(email) || validationUtil.isBlank(password)) {
			request.setAttribute("error", "Enter a valid email address and password.");
			doGet(request, response);
			return;
		}

		try {
			user currentUser = new userService().authenticate(email.trim(), password);
			if (currentUser == null) {
				request.setAttribute("error", "Invalid email or password.");
				doGet(request, response);
				return;
			}

			HttpSession session = request.getSession(true);
			session.setAttribute("currentUser", currentUser);
			response.sendRedirect(request.getContextPath() + dashboardPath(currentUser));
		} catch (SQLException e) {
			log("Login failed", e);
			request.setAttribute("error", "Database is unavailable. Start XAMPP MySQL and refresh the page.");
			doGet(request, response);
		}
	}

	private String dashboardPath(user currentUser) {
		if (currentUser.isAdmin()) {
			return "/admin/dashboard";
		}
		if (currentUser.isTrainer()) {
			return "/trainer/dashboard";
		}
		return "/member/dashboard";
	}

	private void renderFallbackLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		String contextPath = request.getContextPath();
		Object error = request.getAttribute("error");
		Object email = request.getAttribute("email");
		response.getWriter().append("""
				<!DOCTYPE html>
				<html lang="en">
				<head>
				    <meta charset="UTF-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
				    <title>Login - FitTrack Pro</title>
				    <link rel="stylesheet" href="%s/css/style.css">
				</head>
				<body>
				    <div class="auth-container">
				        <div class="auth-card">
				            <div class="auth-header">
				                <h2>FitTrack Pro</h2>
				                <p>Gym Management System</p>
				            </div>
				            %s
				            <form action="%s/login" method="post">
				                <div class="form-group">
				                    <label for="email">Email Address</label>
				                    <input type="email" id="email" name="email" class="form-control" value="%s" required autofocus>
				                </div>
				                <div class="form-group">
				                    <label for="password">Password</label>
				                    <input type="password" id="password" name="password" class="form-control" required>
				                </div>
				                <button type="submit" class="btn btn-primary btn-block">Sign In</button>
				            </form>
				            <div class="auth-footer">
				                <p>Don't have an account? <a href="%s/register">Register here</a></p>
				            </div>
				        </div>
				    </div>
				</body>
				</html>
				""".formatted(
				contextPath,
				error == null ? "" : "<div class=\"alert alert-danger\">" + escapeHtml(error.toString()) + "</div>",
				contextPath,
				email == null ? "" : escapeHtml(email.toString()),
				contextPath));
	}

	private String escapeHtml(String value) {
		return value.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;");
	}
}
