package com.fitTrackPro.controller;

import com.fitTrackPro.model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/dashboard")
/**
 * Routes authenticated users to the correct role-specific dashboard.
 */
public class DashboardServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		user user = (user) request.getSession().getAttribute("currentUser");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login");
		} else if (user.isAdmin()) {
			response.sendRedirect(request.getContextPath() + "/admin/dashboard");
		} else if (user.isTrainer()) {
			response.sendRedirect(request.getContextPath() + "/trainer/dashboard");
		} else {
			response.sendRedirect(request.getContextPath() + "/member/dashboard");
		}
	}
}
