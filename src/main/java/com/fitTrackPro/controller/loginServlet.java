package com.fitTrackPro.controller;


import com.fitTrackPro.model.member;
import com.fitTrackPro.model.trainer;
import com.fitTrackPro.model.user;
import com.fitTrackPro.service.memberService;
import com.fitTrackPro.service.trainerService;
import com.fitTrackPro.service.userService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * LoginServlet - Handles user authentication
 */
@WebServlet("/login")
public class loginServlet extends HttpServlet {
    
    private userService userService;
    private memberService memberService;
    private trainerService trainerService;
    
    @Override
    public void init() throws ServletException {
        userService = new userService();
        memberService = new memberService();
        trainerService = new trainerService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            user user = (user) session.getAttribute("currentUser");
            redirectToDashboard(response, user.getRole());
            return;
        }
        
        request.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String ipAddress = request.getRemoteAddr();
        
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Email and password are required");
            request.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(request, response);
            return;
        }
        
        try {
            user user = userService.authenticateUser(email, password, ipAddress);
            
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("userRole", user.getRole());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                
                // Set additional session info
                if (user.isMember()) {
                    member member = memberService.getMemberByUserId(user.getUserId());
                    if (member != null) {
                        session.setAttribute("memberId", member.getMemberId());
                    }
                } else if (user.isTrainer()) {
                    trainer trainer = trainerService.getTrainerByUserId(user.getUserId());
                    if (trainer != null) {
                        session.setAttribute("trainerId", trainer.getTrainerId());
                    }
                }
                
                // Redirect to stored URL or dashboard
                String redirectUrl = (String) session.getAttribute("redirectUrl");
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    session.removeAttribute("redirectUrl");
                    response.sendRedirect(request.getContextPath() + redirectUrl);
                } else {
                    redirectToDashboard(response, user.getRole());
                }
            } else {
                request.setAttribute("error", "Invalid email or password");
                request.setAttribute("email", email);
                request.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(request, response);
            }
            
        } catch (IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "System error. Please try again later.");
            request.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(request, response);
        }
    }
    
    private void redirectToDashboard(HttpServletResponse response, String role) throws IOException {
        switch (role) {
            case "ADMIN":
                response.sendRedirect("adminDashboard.jsp");
                break;
            case "TRAINER":
                response.sendRedirect("trainerDashboard.jsp");
                break;
            case "MEMBER":
                response.sendRedirect("memberDashboard.jsp");
                break;
            default:
                response.sendRedirect("login.jsp");
        }
    }
}