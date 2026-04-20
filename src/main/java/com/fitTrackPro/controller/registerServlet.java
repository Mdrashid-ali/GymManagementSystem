package com.fitTrackPro.controller;


import com.fitTrackPro.model.member;
import com.fitTrackPro.service.memberService;
import com.fitTrackPro.util.validationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * RegisterServlet - Handles user registration
 */
@WebServlet("/register")
public class registerServlet extends HttpServlet {
    
    private memberService memberService;
    
    @Override
    public void init() throws ServletException {
        memberService = new memberService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String membershipType = request.getParameter("membershipType");
        
        // Validation
        StringBuilder errors = new StringBuilder();
        
        if (!validationUtil.isValidName(firstName)) {
            errors.append("Invalid first name. ");
        }
        if (!validationUtil.isValidName(lastName)) {
            errors.append("Invalid last name. ");
        }
        if (!validationUtil.isValidEmail(email)) {
            errors.append("Invalid email format. ");
        }
        if (!validationUtil.isValidPhone(phone)) {
            errors.append("Invalid phone number. ");
        }
        if (!validationUtil.isValidPassword(password)) {
            errors.append("Password must be at least 8 characters with uppercase, lowercase, digit, and special character. ");
        }
        if (!password.equals(confirmPassword)) {
            errors.append("Passwords do not match. ");
        }
        
        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString());
            request.setAttribute("firstName", firstName);
            request.setAttribute("lastName", lastName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("membershipType", membershipType);
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }
        
        try {
            member member = new member();
            member.setFirstName(firstName);
            member.setLastName(lastName);
            member.setEmail(email);
            member.setPhone(phone);
            member.setMembershipType(membershipType);
            member.setJoinDate(Date.valueOf(LocalDate.now()));
            
            // Set expiry date based on membership type
            int months = getMembershipMonths(membershipType);
            member.setMembershipExpiryDate(Date.valueOf(LocalDate.now().plusMonths(months)));
            
            member registeredMember = memberService.registerMember(member, password);
            
            if (registeredMember != null) {
                request.setAttribute("success", "Registration successful! Please login.");
                response.sendRedirect("login.jsp");
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            }
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "System error. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
        }
    }
    
    private int getMembershipMonths(String type) {
        switch (type) {
            case "BASIC": return 1;
            case "PREMIUM": return 3;
            case "FAMILY": return 6;
            case "STUDENT": return 1;
            default: return 1;
        }
    }
}