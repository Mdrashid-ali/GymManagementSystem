<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    // Redirect to role-specific dashboard
    if (user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/adminDashboard");
    } else if (user.isTrainer()) {
        response.sendRedirect(request.getContextPath() + "/trainerDashboard");
    } else {
        response.sendRedirect(request.getContextPath() + "/memberDashboard");
    }
%>
