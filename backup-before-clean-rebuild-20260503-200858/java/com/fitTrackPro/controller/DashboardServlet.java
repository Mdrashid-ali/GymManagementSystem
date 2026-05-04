package com.fitTrackPro.controller;

import com.fitTrackPro.model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        user currentUser = (user) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        } else if (currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if (currentUser.isTrainer()) {
            response.sendRedirect(request.getContextPath() + "/trainer/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/member/dashboard");
        }
    }
}
