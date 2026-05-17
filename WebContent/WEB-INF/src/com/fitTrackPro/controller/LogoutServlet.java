package com.fitTrackPro.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;

@WebServlet("/logout")
/**
 * Invalidates the current session and returns the user to login.
 */
public class LogoutServlet extends HttpServlet {

	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
		HttpSession s = r.getSession(false);

		if (s != null)
			s.invalidate();

		p.sendRedirect(r.getContextPath() + "/login");
	}
}
