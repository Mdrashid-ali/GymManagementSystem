package com.fitTrackPro.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
        HttpSession s = r.getSession(false);

        if (s != null) s.invalidate();

        p.sendRedirect(r.getContextPath() + "/login");
    }
}