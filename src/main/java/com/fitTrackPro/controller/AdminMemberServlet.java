package com.fitTrackPro.controller;

import com.fitTrackPro.model.member;
import com.fitTrackPro.model.user;
import com.fitTrackPro.service.memberService;
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

@WebServlet({ "/admin/add-member", "/admin/update-member", "/admin/delete-member" })
/**
 * Handles admin member creation, update, and delete actions.
 */
public class AdminMemberServlet extends HttpServlet {
	private final memberService members = new memberService();

	@Override
	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!requireAdmin(request, response))
			return;

		if ("/admin/delete-member".equals(request.getServletPath())) {
			response.sendRedirect(request.getContextPath() + "/adminDashboard");
			return;
		}

		if ("/admin/update-member".equals(request.getServletPath())) {
			try {
				member existing = members.findById(Integer.parseInt(request.getParameter("memberId")));
				if (existing == null) {
					response.sendRedirect(
							request.getContextPath() + "/adminDashboard?error=" + url("Member not found."));
					return;
				}
				request.setAttribute("member", existing);
				request.getRequestDispatcher("/WEB-INF/pages/updateMember.jsp").forward(request, response);
			} catch (Exception e) {
				log("Unable to load member for update", e);
				response.sendRedirect(request.getContextPath() + "/adminDashboard?error=" + url("Member not found."));
			}
			return;
		}

		request.getRequestDispatcher("/WEB-INF/pages/addMember.jsp").forward(request, response);
	}

	@Override
	/** Handles HTTP POST form submissions for this servlet route. */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!requireAdmin(request, response))
			return;

		try {
			String path = request.getServletPath();
			if ("/admin/delete-member".equals(path)) {
				int memberId = Integer.parseInt(request.getParameter("memberId"));
				boolean deleted = members.deleteMember(memberId);
				response.sendRedirect(request.getContextPath() + "/adminDashboard?"
						+ (deleted ? "success=" + url("Member deleted.") : "error=" + url("Member not found.")));
				return;
			}

			if ("/admin/update-member".equals(path)) {
				member existing = members.findById(Integer.parseInt(request.getParameter("memberId")));
				if (existing == null) {
					response.sendRedirect(
							request.getContextPath() + "/adminDashboard?error=" + url("Member not found."));
					return;
				}
				fillMember(request, existing);
				members.updateMember(existing, true);
				response.sendRedirect(request.getContextPath() + "/adminDashboard?success=" + url("Member updated."));
				return;
			}

			String email = trim(request.getParameter("email"));
			String password = request.getParameter("password");
			Date joinDate = dateUtil.today();
			member newMember = new member(trim(request.getParameter("firstName")),
					trim(request.getParameter("lastName")), trim(request.getParameter("phone")), 0,
					cleanMembership(request.getParameter("membershipType")), joinDate,
					members.calculateExpiry(request.getParameter("membershipType"), joinDate));
			fillMember(request, newMember);
			String validationMessage = validateNewMember(newMember, email, password);

			if (validationMessage != null) {
				response.sendRedirect(request.getContextPath() + "/admin/add-member?error=" + url(validationMessage));
				return;
			}

			members.registerMemberAccount(email, password, newMember);
			response.sendRedirect(request.getContextPath() + "/adminDashboard?success=" + url("Member added."));
		} catch (Exception e) {
			log("Admin member action failed", e);
			response.sendRedirect(request.getContextPath() + "/adminDashboard?error="
					+ url("Unable to complete member action: " + rootMessage(e)));
		}
	}

	/** Validates required fields before creating a member account. */
	private String validateNewMember(member member, String email, String password) {
		if (validationUtil.isBlank(member.getFirstName()))
			return "Please enter the member first name.";
		if (validationUtil.isBlank(member.getLastName()))
			return "Please enter the member last name.";
		if (validationUtil.isBlank(email))
			return "Please enter the member email address.";
		if (!validationUtil.isValidEmail(email))
			return "Please enter a valid email address.";
		if (validationUtil.isBlank(member.getPhone()))
			return "Please enter the member phone number.";
		if (!validationUtil.isValidPhone(member.getPhone()))
			return "Please enter a valid phone number.";
		if (validationUtil.isBlank(password))
			return "Please enter a password.";
		if (!validationUtil.isStrongPassword(password))
			return "Password must be at least 8 characters with uppercase, lowercase, digit, and special character.";
		return null;
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

	/** Handles fillMember logic. */
	private void fillMember(HttpServletRequest request, member target) {
		target.setFirstName(valueOrCurrent(request, "firstName", target.getFirstName()));
		target.setLastName(valueOrCurrent(request, "lastName", target.getLastName()));
		target.setPhone(valueOrCurrent(request, "phone", target.getPhone()));
		target.setDateOfBirth(dateUtil.parseSqlDate(request.getParameter("dateOfBirth")));
		target.setGender(trim(request.getParameter("gender")));
		target.setAddress(trim(request.getParameter("address")));
		target.setEmergencyContactName(trim(request.getParameter("emergencyContactName")));
		target.setEmergencyContactPhone(trim(request.getParameter("emergencyContactPhone")));
		target.setMembershipType(cleanMembership(request.getParameter("membershipType")));

		Date expiry = dateUtil.parseSqlDate(request.getParameter("membershipExpiryDate"));
		if (expiry != null)
			target.setMembershipExpiryDate(expiry);

		target.setHeightCm(parseDouble(request.getParameter("height")));
		target.setWeightKg(parseDouble(request.getParameter("weight")));
		target.setFitnessGoal(trim(request.getParameter("fitnessGoal")));
		target.setMedicalNotes(trim(request.getParameter("medicalNotes")));
	}

	/** Handles valueOrCurrent logic. */
	private String valueOrCurrent(HttpServletRequest request, String name, String current) {
		String value = trim(request.getParameter(name));
		return value == null ? current : value;
	}

	/** Handles cleanMembership logic. */
	private String cleanMembership(String value) {
		return value == null || value.isBlank() ? "BASIC" : value.trim().toUpperCase();
	}

	/** Handles parseDouble logic. */
	private Double parseDouble(String value) {
		return value == null || value.isBlank() ? null : Double.valueOf(value);
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
