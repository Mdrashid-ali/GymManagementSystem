package com.fitTrackPro.controller;

import com.fitTrackPro.model.*;
import com.fitTrackPro.service.*;
import com.fitTrackPro.util.validationUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;

@WebServlet({"/member/profile", "/member/attendance", "/member/workout", "/member/checkin", "/member/update"})
public class MemberPortalServlet extends HttpServlet {

    protected void doGet(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
        user u = (user) r.getSession().getAttribute("currentUser");

        try {
            member m = new memberService().findByUserId(u.getUserId());

            if (m == null) {
                p.sendError(404, "Member profile not found.");
                return;
            }

            String path = r.getServletPath();

            if ("/member/checkin".equals(path)) {
                new attendanceService().checkIn(m.getMemberId());
                r.getSession().setAttribute("success", "Checked in successfully.");
                p.sendRedirect(r.getContextPath() + "/member/attendance");
                return;
            }

            r.setAttribute("member", m);

            if ("/member/attendance".equals(path)) {
                r.setAttribute("attendanceList", new attendanceService().findByMemberId(m.getMemberId()));
                r.getRequestDispatcher("/WEB-INF/pages/attendance.jsp").forward(r, p);
            } else if ("/member/workout".equals(path)) {
                r.setAttribute("workoutPlans", new workoutPlanService().findByMemberId(m.getMemberId()));
                r.getRequestDispatcher("/WEB-INF/pages/workoutPlan.jsp").forward(r, p);
            } else {
                r.getRequestDispatcher("/WEB-INF/pages/updateMember.jsp").forward(r, p);
            }
        } catch (SQLException e) {
            log("Member page failed", e);
            r.setAttribute("error", "Database error: " + e.getMessage());
            r.getRequestDispatcher("/WEB-INF/pages/memberDashboard.jsp").forward(r, p);
        }
    }

    protected void doPost(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
        user u = (user) r.getSession().getAttribute("currentUser");

        if (!validationUtil.isValidPhone(r.getParameter("phone"))) {
            p.sendRedirect(r.getContextPath() + "/member/profile?error=Invalid phone.");
            return;
        }

        try {
            memberService svc = new memberService();
            member m = svc.findByUserId(u.getUserId());

            m.setPhone(r.getParameter("phone"));
            m.setAddress(r.getParameter("address"));
            m.setEmergencyContactName(r.getParameter("emergencyContactName"));
            m.setEmergencyContactPhone(r.getParameter("emergencyContactPhone"));
            m.setFitnessGoal(r.getParameter("fitnessGoal"));
            m.setHeightCm(parseD(r.getParameter("height")));
            m.setWeightKg(parseD(r.getParameter("weight")));

            svc.updateMember(m, false);
            p.sendRedirect(r.getContextPath() + "/member/profile?success=Profile updated.");
        } catch (Exception e) {
            log("Profile update failed", e);
            p.sendRedirect(r.getContextPath() + "/member/profile?error=Unable to update profile.");
        }
    }

    private Double parseD(String v) {
        return v == null || v.isBlank() ? null : Double.valueOf(v);
    }
}