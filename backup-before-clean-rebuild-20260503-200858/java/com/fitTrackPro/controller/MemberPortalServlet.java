package com.fitTrackPro.controller;

import com.fitTrackPro.model.member;
import com.fitTrackPro.model.user;
import com.fitTrackPro.service.attendanceService;
import com.fitTrackPro.service.memberService;
import com.fitTrackPro.service.workoutPlanService;
import com.fitTrackPro.util.validationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet({"/member/profile", "/member/attendance", "/member/workout", "/member/checkin", "/member/update"})
public class MemberPortalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        user currentUser = (user) request.getSession().getAttribute("currentUser");
        String path = request.getServletPath();
        try {
            member currentMember = new memberService().findByUserId(currentUser.getUserId());
            if (currentMember == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Member profile not found.");
                return;
            }

            if ("/member/checkin".equals(path)) {
                new attendanceService().checkIn(currentMember.getMemberId());
                request.getSession().setAttribute("success", "Checked in successfully.");
                response.sendRedirect(request.getContextPath() + "/member/attendance");
                return;
            }

            request.setAttribute("member", currentMember);
            if ("/member/attendance".equals(path)) {
                request.setAttribute("attendanceList", new attendanceService().findByMemberId(currentMember.getMemberId()));
                request.getRequestDispatcher("/WEB-INF/pages/attendance.jsp").forward(request, response);
            } else if ("/member/workout".equals(path)) {
                request.setAttribute("workoutPlans", new workoutPlanService().findByMemberId(currentMember.getMemberId()));
                request.getRequestDispatcher("/WEB-INF/pages/workoutPlan.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/pages/updateMember.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            log("Unable to load member page", e);
            request.setAttribute("error", "This page is unavailable. Check the database connection.");
            request.getRequestDispatcher("/WEB-INF/pages/memberDashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        user currentUser = (user) request.getSession().getAttribute("currentUser");
        if (!"/member/update".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        if (!validationUtil.isValidPhone(request.getParameter("phone"))) {
            response.sendRedirect(request.getContextPath() + "/member/profile?error=Enter a valid phone number.");
            return;
        }
        try {
            member currentMember = new memberService().findByUserId(currentUser.getUserId());
            if (currentMember == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Member profile not found.");
                return;
            }
            currentMember.setPhone(request.getParameter("phone").trim());
            currentMember.setAddress(request.getParameter("address"));
            currentMember.setEmergencyContactName(request.getParameter("emergencyContactName"));
            currentMember.setEmergencyContactPhone(request.getParameter("emergencyContactPhone"));
            currentMember.setFitnessGoal(request.getParameter("fitnessGoal"));
            currentMember.setHeightCm(AdminMemberServlet.parseDouble(request.getParameter("height")));
            currentMember.setWeightKg(AdminMemberServlet.parseDouble(request.getParameter("weight")));
            new memberService().updateMember(currentMember, false);
            response.sendRedirect(request.getContextPath() + "/member/profile?success=Profile updated.");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/member/profile?error=Invalid number format.");
        } catch (SQLException e) {
            log("Unable to update member profile", e);
            response.sendRedirect(request.getContextPath() + "/member/profile?error=Unable to update profile.");
        }
    }
}
