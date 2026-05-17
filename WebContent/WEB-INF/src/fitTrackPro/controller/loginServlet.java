package com.fitTrackPro.controller;

import com.fitTrackPro.model.user;
import com.fitTrackPro.service.userService;
import com.fitTrackPro.service.userService.AccountLockedException;
import com.fitTrackPro.util.validationUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.SQLException;

@WebServlet("/login")
public class loginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
        r.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(r, p);
    }

    protected void doPost(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
        String email = r.getParameter("email"), pass = r.getParameter("password");
        r.setAttribute("email", email);

        if (validationUtil.isBlank(email) || validationUtil.isBlank(pass)) {
            r.setAttribute("error", "Enter email/username and password.");
            doGet(r, p);
            return;
        }

        try {
            user u = new userService().authenticate(email.trim(), pass);

            if (u == null) {
                r.setAttribute("error", "Invalid email or password.");
                doGet(r, p);
                return;
            }

            r.getSession(true).setAttribute("currentUser", u);
            p.sendRedirect(r.getContextPath() + (u.isAdmin() ? "/admin/dashboard" : u.isTrainer() ? "/trainer/dashboard" : "/member/dashboard"));
        } catch (AccountLockedException e) {
            r.setAttribute("error", e.getMessage());
            doGet(r, p);
        } catch (SQLException e) {
            log("Login failed", e);
            r.setAttribute("error", "Database error: " + e.getMessage());
            doGet(r, p);
        }
    }
}