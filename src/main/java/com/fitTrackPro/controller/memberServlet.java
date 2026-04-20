package com.fitTrackPro.controller;


import com.fitTrackPro.model.attendance;
import com.fitTrackPro.model.member;
import com.fitTrackPro.model.workoutPlan;
import com.fitTrackPro.service.memberService;
import com.fitTrackPro.service.trainerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * MemberServlet - Handles member-related requests
 */
@WebServlet("/member/*")
public class memberServlet extends HttpServlet {
    
    private memberService memberService;
    private trainerService trainerService;
    
    @Override
    public void init() throws ServletException {
        memberService = new memberService();
        trainerService = new trainerService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                showDashboard(request, response, session);
            } else if (pathInfo.equals("/attendance")) {
                showAttendance(request, response, session);
            } else if (pathInfo.equals("/workout")) {
                showWorkoutPlans(request, response, session);
            } else if (pathInfo.equals("/profile")) {
                showProfile(request, response, session);
            } else if (pathInfo.equals("/checkin")) {
                handleCheckIn(request, response, session);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws ServletException, IOException, SQLException {
        
        Integer memberId = (Integer) session.getAttribute("memberId");
        if (memberId != null) {
            member member = memberService.getMemberById(memberId);
            List<attendance> recentAttendance = memberService.getMemberAttendance(memberId);
            List<workoutPlan> workoutPlans = trainerService.getWorkoutPlansByMember(memberId);
            
            request.setAttribute("member", member);
            request.setAttribute("recentAttendance", recentAttendance.subList(0, Math.min(5, recentAttendance.size())));
            request.setAttribute("workoutPlans", workoutPlans);
        }
        
        request.getRequestDispatcher("/WEB-INF/pages/memberDashboard.jsp").forward(request, response);
    }
    
    private void showAttendance(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws ServletException, IOException, SQLException {
        
        Integer memberId = (Integer) session.getAttribute("memberId");
        if (memberId != null) {
            List<attendance> attendanceList = memberService.getMemberAttendance(memberId);
            request.setAttribute("attendanceList", attendanceList);
        }
        
        request.getRequestDispatcher("/WEB-INF/pages/attendance.jsp").forward(request, response);
    }
    
    private void showWorkoutPlans(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws ServletException, IOException, SQLException {
        
        Integer memberId = (Integer) session.getAttribute("memberId");
        if (memberId != null) {
            List<workoutPlan> plans = trainerService.getWorkoutPlansByMember(memberId);
            request.setAttribute("workoutPlans", plans);
        }
        
        request.getRequestDispatcher("/WEB-INF/pages/workout-plan.jsp").forward(request, response);
    }
    
    private void showProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws ServletException, IOException, SQLException {
        
        Integer memberId = (Integer) session.getAttribute("memberId");
        if (memberId != null) {
            member member = memberService.getMemberById(memberId);
            request.setAttribute("member", member);
        }
        
        request.getRequestDispatcher("/WEB-INF/pages/update-member.jsp").forward(request, response);
    }
    
    private void handleCheckIn(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws IOException, SQLException {
        
        Integer memberId = (Integer) session.getAttribute("memberId");
        if (memberId != null) {
            memberService.checkInMember(memberId, "APP");
            session.setAttribute("success", "Check-in successful!");
        }
        
        response.sendRedirect("dashboard");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if ("/update".equals(pathInfo)) {
            try {
                updateProfile(request, response);
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
    
    private void updateProfile(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        HttpSession session = request.getSession(false);
        Integer memberId = (Integer) session.getAttribute("memberId");
        
        if (memberId != null) {
            member member = memberService.getMemberById(memberId);
            
            member.setPhone(request.getParameter("phone"));
            member.setAddress(request.getParameter("address"));
            member.setEmergencyContactName(request.getParameter("emergencyContactName"));
            member.setEmergencyContactPhone(request.getParameter("emergencyContactPhone"));
            member.setFitnessGoal(request.getParameter("fitnessGoal"));
            
            String heightStr = request.getParameter("height");
            String weightStr = request.getParameter("weight");
            
            if (heightStr != null && !heightStr.isEmpty()) {
                member.setHeightCm(Double.parseDouble(heightStr));
            }
            if (weightStr != null && !weightStr.isEmpty()) {
                member.setWeightKg(Double.parseDouble(weightStr));
            }
            
            if (memberService.updateMember(member)) {
                session.setAttribute("success", "Profile updated successfully!");
            } else {
                session.setAttribute("error", "Failed to update profile.");
            }
        }
        
        response.sendRedirect("profile");
    }
}