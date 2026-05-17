package com.fitTrackPro.controller;

import com.fitTrackPro.model.user;
import com.fitTrackPro.service.userService;
import com.fitTrackPro.service.userService.AccountLockedException;
import com.fitTrackPro.util.validationUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;

@WebServlet("/login")
/**
 * Handles login page display, authentication, and role-based redirects.
 */
public class loginServlet extends HttpServlet {

	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
		user currentUser = (user) r.getSession().getAttribute("currentUser");
		if (currentUser != null && currentUser.isActive()) {
			redirectToDashboard(r, p, currentUser);
			return;
		}
		r.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(r, p);
	}

	/** Handles HTTP POST form submissions for this servlet route. */
	protected void doPost(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
		String email = r.getParameter("email"), pass = r.getParameter("password");
		r.setAttribute("email", email);

		if (validationUtil.isBlank(email) || validationUtil.isBlank(pass)) {
			r.setAttribute("error", "Enter email/username and password.");
			doGet(r, p);
			return;
		}

		try {
			user u = new userService().authenticate(email.trim(), pass);

			if (u == null) {
				r.setAttribute("error", "Invalid email or password.");
				doGet(r, p);
				return;
			}

			r.getSession(true).setAttribute("currentUser", u);
			redirectToDashboard(r, p, u);
		} catch (AccountLockedException e) {
			r.setAttribute("error", e.getMessage());
			doGet(r, p);
		} catch (SQLException e) {
			log("Login failed", e);
			r.setAttribute("error", "Database error: " + e.getMessage());
			doGet(r, p);
		}
	}
	/** Sends the user to the correct dashboard based on their role. */
	private void redirectToDashboard(HttpServletRequest r, HttpServletResponse p, user u) throws IOException {
		p.sendRedirect(r.getContextPath()
				+ (u.isAdmin() ? "/admin/dashboard" : u.isTrainer() ? "/trainer/dashboard" : "/member/dashboard"));
	}
}