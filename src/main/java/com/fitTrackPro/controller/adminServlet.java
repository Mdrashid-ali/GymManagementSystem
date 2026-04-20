package com.fitTrackPro.controller;


import com.fitTrackPro.model.member;
import com.fitTrackPro.model.trainer;
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
import java.time.LocalDate;
import java.util.List;

/**
 * AdminServlet - Handles admin-related requests
 */
@WebServlet("/admin/*")
public class adminServlet extends HttpServlet {
    
    private memberService memberService;
    private trainerService trainerService;
    
    @Override
    public void init() throws ServletException {
        memberService = new memberService();
        trainerService = new trainerService();
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
                showDashboard(request, response);
            } else if (pathInfo.equals("/members")) {
                showMembers(request, response);
            } else if (pathInfo.equals("/add-member")) {
                showAddMemberForm(request, response);
            } else if (pathInfo.equals("/edit-member")) {
                showEditMemberForm(request, response);
            } else if (pathInfo.equals("/trainers")) {
                showTrainers(request, response);
            } else if (pathInfo.equals("/delete-member")) {
                deleteMember(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        List<member> members = memberService.getAllMembers();
        List<trainer> trainers = trainerService.getAllTrainers();
        
        int activeMembers = 0;
        int expiredMembers = 0;
        
        for (member m : members) {
            if (m.isMembershipActive()) {
                activeMembers++;
            } else {
                expiredMembers++;
            }
        }
        
        request.setAttribute("totalMembers", members.size());
        request.setAttribute("activeMembers", activeMembers);
        request.setAttribute("expiredMembers", expiredMembers);
        request.setAttribute("totalTrainers", trainers.size());
        request.setAttribute("recentMembers", members.subList(0, Math.min(5, members.size())));
        
        request.getRequestDispatcher("/WEB-INF/pages/admin-dashboard.jsp").forward(request, response);
    }
    
    private void showMembers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        List<member> members = memberService.getAllMembers();
        request.setAttribute("members", members);
        request.getRequestDispatcher("/WEB-INF/pages/members.jsp").forward(request, response);
    }
    
    private void showAddMemberForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/add-member.jsp").forward(request, response);
    }
    
    private void showEditMemberForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String memberIdStr = request.getParameter("id");
        if (memberIdStr != null) {
            int memberId = Integer.parseInt(memberIdStr);
            member member = memberService.getMemberById(memberId);
            request.setAttribute("member", member);
        }
        
        request.getRequestDispatcher("/WEB-INF/pages/update-member.jsp").forward(request, response);
    }
    
    private void showTrainers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        List<trainer> trainers = trainerService.getAllTrainers();
        request.setAttribute("trainers", trainers);
        request.getRequestDispatcher("/WEB-INF/pages/trainers.jsp").forward(request, response);
    }
    
    private void deleteMember(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        String memberIdStr = request.getParameter("id");
        if (memberIdStr != null) {
            int memberId = Integer.parseInt(memberIdStr);
            memberService.deleteMember(memberId);
        }
        
        response.sendRedirect("members");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/add-member".equals(pathInfo)) {
                addMember(request, response);
            } else if ("/update-member".equals(pathInfo)) {
                updateMember(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void addMember(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        member member = new member();
        member.setFirstName(request.getParameter("firstName"));
        member.setLastName(request.getParameter("lastName"));
        member.setEmail(request.getParameter("email"));
        member.setPhone(request.getParameter("phone"));
        member.setMembershipType(request.getParameter("membershipType"));
        member.setJoinDate(Date.valueOf(LocalDate.now()));
        
        int months = getMembershipMonths(member.getMembershipType());
        member.setMembershipExpiryDate(Date.valueOf(LocalDate.now().plusMonths(months)));
        
        String password = request.getParameter("password");
        
        member registeredMember = memberService.registerMember(member, password);
        
        if (registeredMember != null) {
            response.sendRedirect("members?success=Member added successfully");
        } else {
            response.sendRedirect("add-member?error=Failed to add member");
        }
    }
    
    private void updateMember(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        int memberId = Integer.parseInt(request.getParameter("memberId"));
        member member = memberService.getMemberById(memberId);
        
        if (member != null) {
            member.setFirstName(request.getParameter("firstName"));
            member.setLastName(request.getParameter("lastName"));
            member.setPhone(request.getParameter("phone"));
            member.setMembershipType(request.getParameter("membershipType"));
            
            String expiryDateStr = request.getParameter("membershipExpiryDate");
            if (expiryDateStr != null && !expiryDateStr.isEmpty()) {
                member.setMembershipExpiryDate(Date.valueOf(expiryDateStr));
            }
            
            memberService.updateMember(member);
        }
        
        response.sendRedirect("members?success=Member updated successfully");
    }
    
    private int getMembershipMonths(String type) {
        switch (type) {
            case "BASIC": return 1;
            case "PREMIUM": return 3;
            case "FAMILY": return 6;
            case "STUDENT": return 1;
            default: return 1;
        }
    }
}