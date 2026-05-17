package com.fitTrackPro.controller;

import com.fitTrackPro.model.trainer;
import com.fitTrackPro.model.user;
import com.fitTrackPro.model.workoutPlan;
import com.fitTrackPro.service.attendanceService;
import com.fitTrackPro.service.memberService;
import com.fitTrackPro.service.trainerService;
import com.fitTrackPro.service.workoutPlanService;
import com.fitTrackPro.util.dateUtil;
import com.fitTrackPro.util.validationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;

@WebServlet({ "/trainerDashboard", "/trainer/dashboard", "/trainer/workouts", "/trainer/members", "/trainer/attendance",
		"/trainer/create-workout" })
/**
 * Loads trainer dashboard, assigned member, workout, and attendance data.
 */
public class trainerDashboardServlet extends HttpServlet {
	private final trainerService trainers = new trainerService();
	private final memberService members = new memberService();
	private final workoutPlanService workouts = new workoutPlanService();
	private final attendanceService attendance = new attendanceService();

	@Override
	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		user currentUser = (user) request.getSession().getAttribute("currentUser");
		try {
			trainer trainer = trainers.findByUserId(currentUser.getUserId());
			request.setAttribute("trainer", trainer);
			if (trainer != null)
				request.setAttribute("workoutPlans", workouts.findByTrainerId(trainer.getTrainerId()));

			String path = request.getServletPath();
			if ("/trainer/create-workout".equals(path)) {
				request.setAttribute("members", members.findAll());
				request.getRequestDispatcher("/WEB-INF/pages/createWorkout.jsp").forward(request, response);
				return;
			}

			if ("/trainer/attendance".equals(path)) {
				if (trainer == null) {
					response.sendError(404, "Trainer profile not found.");
					return;
				}
				Integer selectedMemberId = parseInt(request.getParameter("memberId"));
				request.setAttribute("members", members.findByTrainerId(trainer.getTrainerId()));
				request.setAttribute("selectedMemberId", selectedMemberId);
				request.setAttribute("attendanceList",
						attendance.findByTrainerId(trainer.getTrainerId(), selectedMemberId));
				request.getRequestDispatcher("/WEB-INF/pages/attendance.jsp").forward(request, response);
				return;
			}

			if ("/trainer/members".equals(path)) {
				if (trainer == null) {
					response.sendError(404, "Trainer profile not found.");
					return;
				}
				request.setAttribute("members", members.findByTrainerId(trainer.getTrainerId()));
				request.getRequestDispatcher("/WEB-INF/pages/trainerMembers.jsp").forward(request, response);
				return;
			}

			request.getRequestDispatcher("/trainer/workouts".equals(path) ? "/WEB-INF/pages/workoutPlan.jsp"
					: "/WEB-INF/pages/trainerDashboard.jsp").forward(request, response);
		} catch (SQLException e) {
			log("Trainer page failed", e);
			request.setAttribute("error", "Database error: " + e.getMessage());
			request.getRequestDispatcher("/WEB-INF/pages/trainerDashboard.jsp").forward(request, response);
		}
	}

	@Override
	/** Handles HTTP POST form submissions for this servlet route. */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			if ("/trainer/attendance".equals(request.getServletPath())) {
				handleAttendancePost(request, response);
				return;
			}
			handleCreateWorkout(request, response);
		} catch (Exception e) {
			log("Trainer action failed", e);
			String target = "/trainer/attendance".equals(request.getServletPath()) ? "/trainer/attendance"
					: "/trainer/create-workout";
			response.sendRedirect(request.getContextPath() + target + "?error=" + url("Unable to complete action."));
		}
	}

	/** Handles handleAttendancePost logic. */
	private void handleAttendancePost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, SQLException {
		if (!"checkout".equalsIgnoreCase(request.getParameter("action"))) {
			response.sendRedirect(request.getContextPath() + "/trainer/attendance?error="
					+ url("Trainers can only check out active attendance sessions."));
			return;
		}

		user currentUser = (user) request.getSession().getAttribute("currentUser");
		trainer trainer = trainers.findByUserId(currentUser.getUserId());
		Integer attendanceId = parseInt(request.getParameter("attendanceId"));
		if (trainer == null || attendanceId == null
				|| !attendance.checkOutForTrainer(attendanceId, trainer.getTrainerId())) {
			response.sendRedirect(request.getContextPath() + "/trainer/attendance?error="
					+ url("Unable to check out attendance record."));
			return;
		}
		response.sendRedirect(request.getContextPath() + "/trainer/attendance?success=" + url("Member checked out."));
	}

	/** Handles handleCreateWorkout logic. */
	private void handleCreateWorkout(HttpServletRequest request, HttpServletResponse response)
			throws IOException, SQLException {
		user currentUser = (user) request.getSession().getAttribute("currentUser");
		trainer trainer = trainers.findByUserId(currentUser.getUserId());
		Integer memberId = parseInt(request.getParameter("memberId"));
		String planName = request.getParameter("planName");
		String exercises = request.getParameter("exercises");
		Date start = dateUtil.parseSqlDate(request.getParameter("startDate"));
		Date end = dateUtil.parseSqlDate(request.getParameter("endDate"));
		String validationMessage = validateWorkoutPlan(trainer, memberId, planName, start, end, exercises);

		if (validationMessage != null) {
			response.sendRedirect(
					request.getContextPath() + "/trainer/create-workout?error=" + url(validationMessage));
			return;
		}

		workoutPlan plan = new workoutPlan(trainer.getTrainerId(), memberId, planName.trim(), start, end);
		plan.setDescription(request.getParameter("description"));
		plan.setExercises(exercises.trim());
		plan.setNotes(request.getParameter("notes"));
		workouts.create(plan);
		response.sendRedirect(request.getContextPath() + "/trainer/workouts?success=" + url("Workout plan created."));
	}

	/** Validates required workout plan fields before database insert. */
	private String validateWorkoutPlan(trainer trainer, Integer memberId, String planName, Date start, Date end,
			String exercises) {
		if (trainer == null)
			return "Trainer profile was not found.";
		if (memberId == null)
			return "Please select a member before creating the plan.";
		if (validationUtil.isBlank(planName))
			return "Please enter the plan name.";
		if (start == null)
			return "Please choose a start date.";
		if (end == null)
			return "Please choose an end date.";
		if (end.before(start))
			return "End date cannot be before start date.";
		if (validationUtil.isBlank(exercises))
			return "Please enter the exercises for this plan.";
		return null;
	}
	/** Handles parseInt logic. */
	private Integer parseInt(String value) {
		try {
			return value == null || value.isBlank() ? null : Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** Handles url logic. */
	private String url(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
}
