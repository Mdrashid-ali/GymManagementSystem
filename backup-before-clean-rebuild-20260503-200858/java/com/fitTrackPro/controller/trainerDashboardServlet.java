package com.fitTrackPro.controller;

import com.fitTrackPro.model.trainer;
import com.fitTrackPro.model.user;
import com.fitTrackPro.model.workoutPlan;
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
import java.sql.Date;
import java.sql.SQLException;

@WebServlet({"/trainerDashboard", "/trainer/dashboard", "/trainer/workouts", "/trainer/members", "/trainer/attendance", "/trainer/create-workout"})
public class trainerDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        user currentUser = (user) request.getSession().getAttribute("currentUser");
        String path = request.getServletPath();
        try {
            trainer currentTrainer = new trainerService().findByUserId(currentUser.getUserId());
            request.setAttribute("trainer", currentTrainer);
            if (currentTrainer != null) {
                request.setAttribute("workoutPlans", new workoutPlanService().findByTrainerId(currentTrainer.getTrainerId()));
            }
            if ("/trainer/create-workout".equals(path)) {
                request.setAttribute("members", new memberService().findAll());
                request.getRequestDispatcher("/WEB-INF/pages/createWorkout.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            log("Unable to load trainer dashboard data", e);
            request.setAttribute("error", "Trainer data is unavailable. Check the database connection.");
        }
        if ("/trainer/workouts".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/pages/workoutPlan.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/pages/trainerDashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"/trainer/create-workout".equals(request.getServletPath())) {
            doGet(request, response);
            return;
        }
        user currentUser = (user) request.getSession().getAttribute("currentUser");
        try {
            trainer currentTrainer = new trainerService().findByUserId(currentUser.getUserId());
            if (currentTrainer == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Trainer profile not found.");
                return;
            }
            Integer memberId = parseInteger(request.getParameter("memberId"));
            Date startDate = dateUtil.parseSqlDate(request.getParameter("startDate"));
            Date endDate = dateUtil.parseSqlDate(request.getParameter("endDate"));
            if (memberId == null || validationUtil.isBlank(request.getParameter("planName"))
                    || startDate == null || endDate == null || endDate.before(startDate)) {
                response.sendRedirect(request.getContextPath() + "/trainer/create-workout?error=Enter valid workout plan details.");
                return;
            }
            workoutPlan plan = new workoutPlan(currentTrainer.getTrainerId(), memberId,
                    request.getParameter("planName").trim(), startDate, endDate);
            plan.setDescription(request.getParameter("description"));
            plan.setExercises(request.getParameter("exercises"));
            plan.setNotes(request.getParameter("notes"));
            new workoutPlanService().create(plan);
            response.sendRedirect(request.getContextPath() + "/trainer/workouts?success=Workout plan created.");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/trainer/create-workout?error=Invalid date format.");
        } catch (SQLException e) {
            log("Unable to create workout plan", e);
            response.sendRedirect(request.getContextPath() + "/trainer/create-workout?error=Unable to create workout plan.");
        }
    }

    private Integer parseInteger(String value) {
        try {
            return value == null || value.isBlank() ? null : Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
