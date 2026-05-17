package com.fitTrackPro.controller;

import com.fitTrackPro.model.member;
import com.fitTrackPro.model.user;
import com.fitTrackPro.service.memberService;
import com.fitTrackPro.util.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet({ "/register", "/registerServlet" })
/**
 * Handles public member registration and account creation.
 */
public class registerServlet extends HttpServlet {

	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
		user currentUser = (user) r.getSession().getAttribute("currentUser");
		if (currentUser != null && currentUser.isActive()) {
			redirectToDashboard(r, p, currentUser);
			return;
		}

		r.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(r, p);
	}

	/** Handles HTTP POST form submissions for this servlet route. */
	protected void doPost(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
		user currentUser = (user) r.getSession().getAttribute("currentUser");
		if (currentUser != null && currentUser.isActive()) {
			redirectToDashboard(r, p, currentUser);
			return;
		}

		String fn = r.getParameter("firstName"), ln = r.getParameter("lastName"), email = r.getParameter("email"),
				phone = r.getParameter("phone"), pass = r.getParameter("password"),
				cp = r.getParameter("confirmPassword"), type = clean(r.getParameter("membershipType"));
		keep(r, fn, ln, email, phone);

		String err = validate(fn, ln, email, phone, pass, cp);

		if (err != null) {
			r.setAttribute("error", err);
			doGet(r, p);
			return;
		}

		try {
			memberService ms = new memberService();
			Date join = dateUtil.today();
			member m = new member(fn.trim(), ln.trim(), phone.trim(), 0, type, join, ms.calculateExpiry(type, join));

			ms.registerMemberAccount(email.trim(), pass, m);
			p.sendRedirect(r.getContextPath() + "/login?success=Registration successful. Please sign in.");
		} catch (SQLException e) {
			log("Registration failed", e);
			r.setAttribute("error", "23000".equals(e.getSQLState()) ? "An account already exists for that email."
					: "Unable to create account: " + e.getMessage());
			doGet(r, p);
		}
	}

	/** Handles clean logic. */
	private String clean(String v) {
		return validationUtil.isBlank(v) ? "BASIC" : v.trim().toUpperCase();
	}

	/** Handles validate logic. */
	private String validate(String fn, String ln, String e, String ph, String pw, String cp) {
		if (validationUtil.isBlank(fn) || validationUtil.isBlank(ln))
			return "First name and last name are required.";
		if (!validationUtil.isValidEmail(e))
			return "Enter a valid email address.";
		if (!validationUtil.isValidPhone(ph))
			return "Enter a valid phone number.";
		if (!validationUtil.isStrongPassword(pw))
			return "Password must be at least 8 characters and include uppercase, lowercase, digit, and special character.";
		if (!pw.equals(cp))
			return "Password and confirmation do not match.";
		return null;
	}

	/** Handles keep logic. */
	private void keep(HttpServletRequest r, String fn, String ln, String e, String ph) {
		r.setAttribute("firstName", fn);
		r.setAttribute("lastName", ln);
		r.setAttribute("email", e);
		r.setAttribute("phone", ph);
	}

	/** Sends logged-in users to their role dashboard. */
	private void redirectToDashboard(HttpServletRequest r, HttpServletResponse p, user u) throws IOException {
		p.sendRedirect(r.getContextPath()
				+ (u.isAdmin() ? "/admin/dashboard" : u.isTrainer() ? "/trainer/dashboard" : "/member/dashboard"));
	}
}
