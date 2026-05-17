package com.fitTrackPro.controller;

import com.fitTrackPro.service.memberService;
import com.fitTrackPro.service.trainerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet({ "/adminDashboard", "/admin/dashboard", "/admin/members", "/admin/trainers" })
/**
 * Loads dashboard, member, and trainer overview data for the admin area.
 */
public class adminDashboardServlet extends HttpServlet {
	@Override
	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			memberService members = new memberService();
			trainerService trainers = new trainerService();

			request.setAttribute("totalMembers", members.countAll());
			request.setAttribute("activeMembers", members.countActive());
			request.setAttribute("expiredMembers", members.countExpired());
			request.setAttribute("allMembers", members.findAll());
			request.setAttribute("totalTrainers", trainers.countAll());
			request.setAttribute("allTrainers", trainers.findAll());

			String path = request.getServletPath();
			if ("/admin/trainers".equals(path)) {
				request.setAttribute("view", "trainers");
			} else if ("/admin/members".equals(path)) {
				request.setAttribute("view", "members");
			} else {
				request.setAttribute("view", "dashboard");
			}
		} catch (SQLException e) {
			log("Admin dashboard failed", e);
			request.setAttribute("error", "Database error: " + e.getMessage());
		}
		request.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(request, response);
	}
}
