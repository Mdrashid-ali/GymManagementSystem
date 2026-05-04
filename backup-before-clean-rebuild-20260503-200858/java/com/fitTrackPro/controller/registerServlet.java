package com.fitTrackPro.controller;

import com.fitTrackPro.model.member;
import com.fitTrackPro.service.memberService;
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

@WebServlet({"/register", "/registerServlet"})
public class registerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String membershipType = cleanMembershipType(request.getParameter("membershipType"));

        keepFormValues(request, firstName, lastName, email, phone);
        String error = validateRegistration(firstName, lastName, email, phone, password, confirmPassword);
        if (error != null) {
            request.setAttribute("error", error);
            doGet(request, response);
            return;
        }

        memberService members = new memberService();
        try {
            Date joinDate = dateUtil.today();
            member newMember = new member(firstName.trim(), lastName.trim(), phone.trim(), 0,
                    membershipType, joinDate, members.calculateExpiry(membershipType, joinDate));
            members.registerMemberAccount(email.trim(), password, newMember);
            response.sendRedirect(request.getContextPath() + "/login?success=Registration successful. Please sign in.");
        } catch (SQLException e) {
            log("Registration failed", e);
            if ("23000".equals(e.getSQLState())) {
                request.setAttribute("error", "An account already exists for that email.");
            } else {
                request.setAttribute("error", "Unable to create the account. Database detail: " + e.getMessage());
            }
            doGet(request, response);
        }
    }

    private String cleanMembershipType(String membershipType) {
        return validationUtil.isBlank(membershipType) ? "BASIC" : membershipType.trim().toUpperCase();
    }

    private String validateRegistration(String firstName, String lastName, String email, String phone,
            String password, String confirmPassword) {
        if (validationUtil.isBlank(firstName) || validationUtil.isBlank(lastName)) {
            return "First name and last name are required.";
        }
        if (!validationUtil.isValidEmail(email)) {
            return "Enter a valid email address.";
        }
        if (!validationUtil.isValidPhone(phone)) {
            return "Enter a valid phone number.";
        }
        if (!validationUtil.isStrongPassword(password)) {
            return "Password must be at least 8 characters and include uppercase, lowercase, digit, and special character.";
        }
        if (!password.equals(confirmPassword)) {
            return "Password and confirmation do not match.";
        }
        return null;
    }

    private void keepFormValues(HttpServletRequest request, String firstName, String lastName, String email, String phone) {
        request.setAttribute("firstName", firstName);
        request.setAttribute("lastName", lastName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
    }
}