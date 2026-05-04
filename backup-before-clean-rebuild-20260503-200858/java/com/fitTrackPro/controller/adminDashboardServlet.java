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

@WebServlet({"/adminDashboard", "/admin/dashboard", "/admin/members", "/admin/trainers"})
public class adminDashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			memberService members = new memberService();
			request.setAttribute("totalMembers", members.countAll());
			request.setAttribute("activeMembers", members.countActive());
			request.setAttribute("expiredMembers", members.countExpired());
			request.setAttribute("recentMembers", members.findRecentMembers(10));
			request.setAttribute("totalTrainers", new trainerService().countAll());
		} catch (SQLException e) {
			log("Unable to load admin dashboard data", e);
			request.setAttribute("error", "Dashboard data is unavailable. Check the database connection.");
		}
		request.getRequestDispatcher("/WEB-INF/pages/adminDashboard.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
