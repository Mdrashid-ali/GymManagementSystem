package com.fitTrackPro.controller;

import com.fitTrackPro.model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/about", "/services", "/contact"})
public class InfoPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        user currentUser = (user) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !currentUser.isActive()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();
        request.setAttribute("pageType", path.substring(1));
        request.getRequestDispatcher("/WEB-INF/pages/infoPage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/contact?success=Message received. Our team will follow up soon.");
    }
}