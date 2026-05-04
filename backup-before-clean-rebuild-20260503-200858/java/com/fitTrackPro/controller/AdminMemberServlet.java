package com.fitTrackPro.controller;

import com.fitTrackPro.model.member;
import com.fitTrackPro.service.memberService;
import com.fitTrackPro.service.userService;
import com.fitTrackPro.util.dateUtil;
import com.fitTrackPro.util.validationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

@WebServlet({"/admin/add-member", "/admin/update-member"})
public class AdminMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/admin/update-member".equals(request.getServletPath())) {
            showUpdateForm(request, response);
            return;
        }
        request.getRequestDispatcher("/WEB-INF/pages/addMember.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/admin/update-member".equals(request.getServletPath())) {
            updateMember(request, response);
            return;
        }
        createMember(request, response);
    }

    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer memberId = parseInteger(request.getParameter("memberId"));
        if (memberId == null) {
            response.sendRedirect(request.getContextPath() + "/admin/members?error=Select a member to edit.");
            return;
        }
        try {
            member existingMember = new memberService().findById(memberId);
            if (existingMember == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Member not found.");
                return;
            }
            request.setAttribute("member", existingMember);
            request.getRequestDispatcher("/WEB-INF/pages/updateMember.jsp").forward(request, response);
        } catch (SQLException e) {
            log("Unable to load member", e);
            response.sendRedirect(request.getContextPath() + "/admin/members?error=Unable to load member.");
        }
    }

    private void createMember(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        if (!validationUtil.isValidEmail(email) || !validationUtil.isStrongPassword(password)
                || !validationUtil.isValidPhone(phone) || validationUtil.isBlank(request.getParameter("firstName"))
                || validationUtil.isBlank(request.getParameter("lastName"))) {
            response.sendRedirect(request.getContextPath() + "/admin/add-member?error=Invalid member data.");
            return;
        }

        userService users = new userService();
        memberService members = new memberService();
        try {
            if (users.emailExists(email.trim())) {
                response.sendRedirect(request.getContextPath() + "/admin/add-member?error=Email already exists.");
                return;
            }
            int userId = users.createUser(email.trim(), password, "MEMBER");
            Date joinDate = dateUtil.today();
            member newMember = new member(request.getParameter("firstName").trim(), request.getParameter("lastName").trim(),
                    phone.trim(), userId, cleanMembershipType(request.getParameter("membershipType")), joinDate,
                    members.calculateExpiry(request.getParameter("membershipType"), joinDate));
            bindEditableFields(request, newMember, true);
            members.createMember(newMember);
            response.sendRedirect(request.getContextPath() + "/adminDashboard?success=Member added.");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/admin/add-member?error=Invalid date or number format.");
        } catch (SQLException e) {
            log("Unable to add member", e);
            response.sendRedirect(request.getContextPath() + "/admin/add-member?error=Unable to save member.");
        }
    }

    private void updateMember(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Integer memberId = parseInteger(request.getParameter("memberId"));
        if (memberId == null || validationUtil.isBlank(request.getParameter("firstName"))
                || validationUtil.isBlank(request.getParameter("lastName"))
                || !validationUtil.isValidPhone(request.getParameter("phone"))) {
            response.sendRedirect(request.getContextPath() + "/admin/members?error=Invalid member data.");
            return;
        }

        try {
            member existingMember = new memberService().findById(memberId);
            if (existingMember == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Member not found.");
                return;
            }
            existingMember.setFirstName(request.getParameter("firstName").trim());
            existingMember.setLastName(request.getParameter("lastName").trim());
            existingMember.setPhone(request.getParameter("phone").trim());
            existingMember.setMembershipType(cleanMembershipType(request.getParameter("membershipType")));
            Date expiryDate = dateUtil.parseSqlDate(request.getParameter("membershipExpiryDate"));
            existingMember.setMembershipExpiryDate(expiryDate == null ? existingMember.getMembershipExpiryDate() : expiryDate);
            bindEditableFields(request, existingMember, true);
            new memberService().updateMember(existingMember, true);
            response.sendRedirect(request.getContextPath() + "/adminDashboard?success=Member updated.");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/admin/update-member?memberId=" + memberId + "&error=Invalid date or number format.");
        } catch (SQLException e) {
            log("Unable to update member", e);
            response.sendRedirect(request.getContextPath() + "/admin/update-member?memberId=" + memberId + "&error=Unable to update member.");
        }
    }

    static void bindEditableFields(HttpServletRequest request, member target, boolean includeAdminFields) {
        target.setAddress(request.getParameter("address"));
        target.setEmergencyContactName(request.getParameter("emergencyContactName"));
        target.setEmergencyContactPhone(request.getParameter("emergencyContactPhone"));
        target.setFitnessGoal(request.getParameter("fitnessGoal"));
        target.setHeightCm(parseDouble(request.getParameter("height")));
        target.setWeightKg(parseDouble(request.getParameter("weight")));
        if (includeAdminFields) {
            target.setDateOfBirth(dateUtil.parseSqlDate(request.getParameter("dateOfBirth")));
            target.setGender(request.getParameter("gender"));
            target.setMedicalNotes(request.getParameter("medicalNotes"));
        }
    }

    static Double parseDouble(String value) {
        return value == null || value.isBlank() ? null : Double.valueOf(value);
    }

    private String cleanMembershipType(String membershipType) {
        return validationUtil.isBlank(membershipType) ? "BASIC" : membershipType.trim().toUpperCase();
    }

    private Integer parseInteger(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
