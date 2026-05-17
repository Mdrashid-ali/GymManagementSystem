package com.fitTrackPro.controller;

import com.fitTrackPro.model.member;
import com.fitTrackPro.model.user;
import com.fitTrackPro.service.attendanceService;
import com.fitTrackPro.model.fitnessDetail;
import com.fitTrackPro.service.fitnessDetailService;
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
			request.setAttribute("fitnessDetails", new fitnessDetailService().findByMemberId(member.getMemberId()));
		} catch (SQLException e) {
			log("Member dashboard failed", e);
			request.setAttribute("error", "Database error: " + e.getMessage());
		}
		request.getRequestDispatcher("/WEB-INF/pages/memberDashboard.jsp").forward(request, response);
	}

	@Override
	/** Handles member fitness detail submissions. */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		user currentUser = (user) request.getSession().getAttribute("currentUser");
		if (currentUser == null || !currentUser.isMember()) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		try {
			member member = new memberService().findByUserId(currentUser.getUserId());
			if (member == null) {
				response.sendError(404, "Member profile not found.");
				return;
			}

			fitnessDetail detail = new fitnessDetail();
			detail.setMemberId(member.getMemberId());
			detail.setHeightCm(parseDouble(request.getParameter("height")));
			detail.setWeightKg(parseDouble(request.getParameter("weight")));
			detail.setBodyFatPercent(parseDouble(request.getParameter("bodyFat")));
			detail.setMuscleMassKg(parseDouble(request.getParameter("muscleMass")));
			detail.setFitnessGoal(trim(request.getParameter("fitnessGoal")));
			detail.setNotes(trim(request.getParameter("notes")));

			if (detail.getHeightCm() == null && detail.getWeightKg() == null && detail.getBodyFatPercent() == null
					&& detail.getMuscleMassKg() == null && isBlank(detail.getFitnessGoal()) && isBlank(detail.getNotes())) {
				request.getSession().setAttribute("error", "Enter at least one fitness detail before saving.");
			} else {
				new fitnessDetailService().create(detail);
				request.getSession().setAttribute("success", "Fitness detail saved.");
			}
		} catch (SQLException e) {
			log("Saving fitness detail failed", e);
			request.getSession().setAttribute("error", "Database error: " + e.getMessage());
		}
		response.sendRedirect(request.getContextPath() + "/memberDashboard");
	}

	/** Converts a submitted decimal value. */
	private Double parseDouble(String value) {
		if (value == null || value.isBlank())
			return null;
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** Trims optional text input. */
	private String trim(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}

	/** Checks optional text input. */
	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
