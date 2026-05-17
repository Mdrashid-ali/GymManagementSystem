package com.fitTrackPro.controller;

import com.fitTrackPro.model.trainer;
import com.fitTrackPro.model.user;
import com.fitTrackPro.service.trainerService;
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

@WebServlet({ "/admin/add-trainer", "/admin/update-trainer", "/admin/delete-trainer" })
/**
 * Handles admin trainer creation, update, and delete actions.
 */
public class AdminTrainerServlet extends HttpServlet {
	private final trainerService trainers = new trainerService();

	@Override
	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!requireAdmin(request, response))
			return;

		if ("/admin/update-trainer".equals(request.getServletPath())) {
			try {
				trainer existing = trainers.findById(Integer.parseInt(request.getParameter("trainerId")));
				if (existing == null) {
					response.sendRedirect(
							request.getContextPath() + "/admin/trainers?error=" + url("Trainer not found."));
					return;
				}
				request.setAttribute("trainer", existing);
				request.getRequestDispatcher("/WEB-INF/pages/updateTrainer.jsp").forward(request, response);
			} catch (Exception e) {
				log("Unable to load trainer for update", e);
				response.sendRedirect(request.getContextPath() + "/admin/trainers?error=" + url("Trainer not found."));
			}
			return;
		}

		if ("/admin/delete-trainer".equals(request.getServletPath())) {
			response.sendRedirect(request.getContextPath() + "/admin/trainers");
			return;
		}

		request.getRequestDispatcher("/WEB-INF/pages/addTrainer.jsp").forward(request, response);
	}

	@Override
	/** Handles HTTP POST form submissions for this servlet route. */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!requireAdmin(request, response))
			return;

		try {
			String path = request.getServletPath();
			if ("/admin/delete-trainer".equals(path)) {
				int trainerId = Integer.parseInt(request.getParameter("trainerId"));
				boolean deleted = trainers.deleteTrainer(trainerId);
				response.sendRedirect(request.getContextPath() + "/admin/trainers?"
						+ (deleted ? "success=" + url("Trainer deleted.") : "error=" + url("Trainer not found.")));
				return;
			}

			if ("/admin/update-trainer".equals(path)) {
				trainer existing = trainers.findById(Integer.parseInt(request.getParameter("trainerId")));
				if (existing == null) {
					response.sendRedirect(
							request.getContextPath() + "/admin/trainers?error=" + url("Trainer not found."));
					return;
				}
				fillTrainer(request, existing);
				trainers.updateTrainer(existing);
				response.sendRedirect(request.getContextPath() + "/admin/trainers?success=" + url("Trainer updated."));
				return;
			}

			String email = trim(request.getParameter("email"));
			String password = request.getParameter("password");
			trainer trainer = new trainer();
			fillTrainer(request, trainer);
			String validationMessage = validateNewTrainer(trainer, email, password);

			if (validationMessage != null) {
				response.sendRedirect(request.getContextPath() + "/admin/add-trainer?error=" + url(validationMessage));
				return;
			}

			trainer.setEmail(email);
			trainer.setStatus("ACTIVE");
			trainers.createTrainerAccount(email, password, trainer);
			response.sendRedirect(request.getContextPath() + "/admin/trainers?success=" + url("Trainer added."));
		} catch (Exception e) {
			log("Admin trainer action failed", e);
			String target = "/admin/add-trainer";
			if ("/admin/update-trainer".equals(request.getServletPath()))
				target = "/admin/trainers";
			response.sendRedirect(request.getContextPath() + target + "?error="
					+ url("Unable to complete trainer action: " + rootMessage(e)));
		}
	}

	/** Validates required fields before creating a trainer account. */
	private String validateNewTrainer(trainer trainer, String email, String password) {
		if (validationUtil.isBlank(trainer.getFirstName()))
			return "Please enter the trainer first name.";
		if (validationUtil.isBlank(trainer.getLastName()))
			return "Please enter the trainer last name.";
		if (validationUtil.isBlank(email))
			return "Please enter the trainer email address.";
		if (!validationUtil.isValidEmail(email))
			return "Please enter a valid email address.";
		if (validationUtil.isBlank(password))
			return "Please enter a password.";
		if (!validationUtil.isStrongPassword(password))
			return "Password must be at least 8 characters with uppercase, lowercase, digit, and special character.";
		return null;
	}
	/** Handles fillTrainer logic. */
	private void fillTrainer(HttpServletRequest request, trainer trainer) {
		trainer.setFirstName(trim(request.getParameter("firstName")));
		trainer.setLastName(trim(request.getParameter("lastName")));
		trainer.setPhone(trim(request.getParameter("phone")));
		trainer.setSpecialization(trim(request.getParameter("specialization")));
		trainer.setExperienceYears(parseInt(request.getParameter("experienceYears")));
		trainer.setCertification(trim(request.getParameter("certification")));
		trainer.setBio(trim(request.getParameter("bio")));
		trainer.setAvailabilitySchedule(trim(request.getParameter("availabilitySchedule")));
		trainer.setHireDate(dateUtil.parseSqlDate(request.getParameter("hireDate")));
		trainer.setStatus(cleanStatus(request.getParameter("status")));
	}

	/** Handles requireAdmin logic. */
	private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		user currentUser = (user) request.getSession().getAttribute("currentUser");
		if (currentUser == null || !currentUser.isAdmin()) {
			response.sendRedirect(request.getContextPath() + "/login");
			return false;
		}
		return true;
	}

	/** Handles parseInt logic. */
	private int parseInt(String value) {
		return value == null || value.isBlank() ? 0 : Integer.parseInt(value);
	}

	/** Handles cleanStatus logic. */
	private String cleanStatus(String value) {
		return value == null || value.isBlank() ? "ACTIVE" : value.trim().toUpperCase();
	}

	/** Handles trim logic. */
	private String trim(String value) {
		return value == null || value.trim().isEmpty() ? null : value.trim();
	}

	/** Handles rootMessage logic. */
	private String rootMessage(Exception e) {
		Throwable cause = e;
		while (cause.getCause() != null)
			cause = cause.getCause();
		return cause.getMessage() == null ? e.getClass().getSimpleName() : cause.getMessage();
	}

	/** Handles url logic. */
	private String url(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
}
