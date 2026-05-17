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

@WebServlet({"/admin/add-member", "/admin/update-member", "/admin/delete-member"})
public class AdminMemberServlet extends HttpServlet {
    private final memberService members = new memberService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireAdmin(request, response)) return;

        if ("/admin/delete-member".equals(request.getServletPath())) {
            response.sendRedirect(request.getContextPath() + "/adminDashboard");
            return;
        }

        if ("/admin/update-member".equals(request.getServletPath())) {
            try {
                member existing = members.findById(Integer.parseInt(request.getParameter("memberId")));
                if (existing == null) {
                    response.sendRedirect(request.getContextPath() + "/adminDashboard?error=" + url("Member not found."));
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireAdmin(request, response)) return;

        try {
            String path = request.getServletPath();
            if ("/admin/delete-member".equals(path)) {
                int memberId = Integer.parseInt(request.getParameter("memberId"));
                boolean deleted = members.deleteMember(memberId);
                response.sendRedirect(request.getContextPath() + "/adminDashboard?" + (deleted ? "success=" + url("Member deleted.") : "error=" + url("Member not found.")));
                return;
            }

            if ("/admin/update-member".equals(path)) {
                member existing = members.findById(Integer.parseInt(request.getParameter("memberId")));
                if (existing == null) {
                    response.sendRedirect(request.getContextPath() + "/adminDashboard?error=" + url("Member not found."));
                    return;
                }
                fillMember(request, existing);
                members.updateMember(existing, true);
                response.sendRedirect(request.getContextPath() + "/adminDashboard?success=" + url("Member updated."));
                return;
            }

            String email = trim(request.getParameter("email"));
            String password = request.getParameter("password");
            if (!validationUtil.isValidEmail(email) || !validationUtil.isStrongPassword(password)) {
                response.sendRedirect(request.getContextPath() + "/admin/add-member?error=" + url("Enter a valid email and a strong password."));
                return;
            }

            Date joinDate = dateUtil.today();
            member newMember = new member(
                    trim(request.getParameter("firstName")),
                    trim(request.getParameter("lastName")),
                    trim(request.getParameter("phone")),
                    0,
                    cleanMembership(request.getParameter("membershipType")),
                    joinDate,
                    members.calculateExpiry(request.getParameter("membershipType"), joinDate)
            );
            fillMember(request, newMember);
            members.registerMemberAccount(email, password, newMember);
            response.sendRedirect(request.getContextPath() + "/adminDashboard?success=" + url("Member added."));
        } catch (Exception e) {
            log("Admin member action failed", e);
            response.sendRedirect(request.getContextPath() + "/adminDashboard?error=" + url("Unable to complete member action: " + rootMessage(e)));
        }
    }

    private boolean requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        user currentUser = (user) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }

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
        if (expiry != null) target.setMembershipExpiryDate(expiry);

        target.setHeightCm(parseDouble(request.getParameter("height")));
        target.setWeightKg(parseDouble(request.getParameter("weight")));
        target.setFitnessGoal(trim(request.getParameter("fitnessGoal")));
        target.setMedicalNotes(trim(request.getParameter("medicalNotes")));
    }

    private String valueOrCurrent(HttpServletRequest request, String name, String current) {
        String value = trim(request.getParameter(name));
        return value == null ? current : value;
    }

    private String cleanMembership(String value) {
        return value == null || value.isBlank() ? "BASIC" : value.trim().toUpperCase();
    }

    private Double parseDouble(String value) {
        return value == null || value.isBlank() ? null : Double.valueOf(value);
    }

    private String trim(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String rootMessage(Exception e) {
        Throwable cause = e;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() == null ? e.getClass().getSimpleName() : cause.getMessage();
    }

    private String url(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}