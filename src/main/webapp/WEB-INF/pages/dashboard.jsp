<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Redirect to role-specific dashboard
    if (user.isAdmin()) {
        response.sendRedirect("admin-dashboard.jsp");
    } else if (user.isTrainer()) {
        response.sendRedirect("trainer-dashboard.jsp");
    } else {
        response.sendRedirect("member-dashboard.jsp");
    }
%>