package com.fitTrackPro.controller;


import com.fitTrackPro.model.member;
import com.fitTrackPro.model.trainer;
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
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * TrainerServlet - Handles trainer-related requests
 */
@WebServlet("/trainer/*")
public class trainerServlet extends HttpServlet {
    
    private trainerService trainerService;
    private memberService memberService;
    
    @Override
    public void init() throws ServletException {
        trainerService = new trainerService();
        memberService = new memberService();
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
            } else if (pathInfo.equals("/members")) {
                showMembers(request, response);
            } else if (pathInfo.equals("/workouts")) {
                showWorkoutPlans(request, response, session);
            } else if (pathInfo.equals("/create-workout")) {
                showCreateWorkoutForm(request, response);
            } else if (pathInfo.equals("/attendance")) {
                showAttendance(request, response);
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
        
        Integer trainerId = (Integer) session.getAttribute("trainerId");
        if (trainerId != null) {
            trainer trainer = trainerService.getTrainerById(trainerId);
            List<workoutPlan> workoutPlans = trainerService.getWorkoutPlansByTrainer(trainerId);
            List<member> members = memberService.getAllMembers();
            
            request.setAttribute("trainer", trainer);
            request.setAttribute("workoutPlans", workoutPlans);
            request.setAttribute("members", members);
        }
        
        request.getRequestDispatcher("/WEB-INF/pages/trainer-dashboard.jsp").forward(request, response);
    }
    
    private void showMembers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        List<member> members = memberService.getAllMembers();
        request.setAttribute("members", members);
        request.getRequestDispatcher("/WEB-INF/pages/members.jsp").forward(request, response);
    }
    
    private void showWorkoutPlans(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws ServletException, IOException, SQLException {
        
        Integer trainerId = (Integer) session.getAttribute("trainerId");
        if (trainerId != null) {
            List<workoutPlan> plans = trainerService.getWorkoutPlansByTrainer(trainerId);
            request.setAttribute("workoutPlans", plans);
        }
        
        request.getRequestDispatcher("/WEB-INF/pages/workout-plan.jsp").forward(request, response);
    }
    
    private void showCreateWorkoutForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        List<member> members = memberService.getAllMembers();
        request.setAttribute("members", members);
        request.getRequestDispatcher("/WEB-INF/pages/add-workout.jsp").forward(request, response);
    }
    
    private void showAttendance(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/attendance.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/create-workout".equals(pathInfo)) {
                createWorkoutPlan(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void createWorkoutPlan(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        HttpSession session = request.getSession(false);
        Integer trainerId = (Integer) session.getAttribute("trainerId");
        
        if (trainerId != null) {
            workoutPlan plan = new workoutPlan();
            plan.setTrainerId(trainerId);
            plan.setMemberId(Integer.parseInt(request.getParameter("memberId")));
            plan.setPlanName(request.getParameter("planName"));
            plan.setDescription(request.getParameter("description"));
            plan.setStartDate(Date.valueOf(request.getParameter("startDate")));
            plan.setEndDate(Date.valueOf(request.getParameter("endDate")));
            plan.setExercises(request.getParameter("exercises"));
            plan.setNotes(request.getParameter("notes"));
            
            int planId = trainerService.createWorkoutPlan(plan);
            
            if (planId > 0) {
                response.sendRedirect("workouts?success=Workout plan created successfully");
            } else {
                response.sendRedirect("create-workout?error=Failed to create workout plan");
            }
        }
    }
}