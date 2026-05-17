package com.fitTrackPro.controller;

import com.fitTrackPro.model.member;
import com.fitTrackPro.model.user;
import com.fitTrackPro.service.attendanceService;
import com.fitTrackPro.service.memberService;
import com.fitTrackPro.service.workoutPlanService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet({ "/memberDashboard", "/member/dashboard" })
/**
 * Loads dashboard, profile, attendance, and workout data for members.
 */
public class memberDashboardServlet extends HttpServlet {
	@Override
	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		user currentUser = (user) request.getSession().getAttribute("currentUser");
		try {
			member member = new memberService().findByUserId(currentUser.getUserId());
			if (member == null) {
				response.sendError(404, "Member profile not found.");
				return;
			}
			request.setAttribute("member", member);
			request.setAttribute("workoutPlans", new workoutPlanService().findByMemberId(member.getMemberId()));
			request.setAttribute("attendanceList", new attendanceService().findByMemberId(member.getMemberId()));
		} catch (SQLException e) {
			log("Member dashboard failed", e);
			request.setAttribute("error", "Database error: " + e.getMessage());
		}
		request.getRequestDispatcher("/WEB-INF/pages/memberDashboard.jsp").forward(request, response);
	}
}
