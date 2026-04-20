package com.fitTrackPro.config;

import com.fitTrackPro.model.user;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Authentication Filter - Handles access control and redirect management
 * Enforces role-based authorization for protected resources
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    
    // Public URLs that don't require authentication
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        "/login.jsp", "/register.jsp", "/forgot-password.jsp", 
        "/reset-password.jsp", "/about.jsp", "/contact.jsp",
        "/css/", "/js/", "/images/"
    );
    
    // URLs accessible by role
    private static final List<String> ADMIN_URLS = Arrays.asList(
        "/admin-dashboard.jsp", "/add-member.jsp", "/update-member.jsp"
    );
    
    private static final List<String> TRAINER_URLS = Arrays.asList(
        "/trainer-dashboard.jsp", "/workout-plan.jsp", "/attendance.jsp"
    );
    
    private static final List<String> MEMBER_URLS = Arrays.asList(
        "/member-dashboard.jsp"
    );
    
    // Public servlet paths
    private static final List<String> PUBLIC_SERVLETS = Arrays.asList(
        "/login", "/register", "/logout"
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Check if URL is public
        if (isPublicUrl(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is logged in
        if (session == null || session.getAttribute("currentUser") == null) {
            // Store requested URL for post-login redirect
            httpRequest.getSession().setAttribute("redirectUrl", path);
            httpResponse.sendRedirect(contextPath + "/login.jsp");
            return;
        }
        
        // Check role-based access
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser != null && !hasAccess(path, currentUser.getRole())) {
            // Redirect to appropriate dashboard
            String dashboard = getDashboardForRole(currentUser.getRole());
            httpResponse.sendRedirect(contextPath + dashboard);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    /**
     * Checks if a URL is publicly accessible
     */
    private boolean isPublicUrl(String path) {
        // Check exact matches
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
            return true;
        }
        
        if (PUBLIC_URLS.contains(path)) {
            return true;
        }
        
        // Check prefixes
        for (String publicUrl : PUBLIC_URLS) {
            if (path.startsWith(publicUrl)) {
                return true;
            }
        }
        
        // Check public servlets
        for (String servlet : PUBLIC_SERVLETS) {
            if (path.startsWith("/" + servlet)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if a role has access to a URL
     */
    private boolean hasAccess(String path, String role) {
        switch (role) {
            case "ADMIN":
                return true; // Admin has access to everything
            case "TRAINER":
                return !isAdminOnlyUrl(path);
            case "MEMBER":
                return isMemberUrl(path) || isPublicMemberUrl(path);
            default:
                return false;
        }
    }
    
    private boolean isAdminOnlyUrl(String path) {
        for (String url : ADMIN_URLS) {
            if (path.endsWith(url) || path.contains("/admin")) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isMemberUrl(String path) {
        for (String url : MEMBER_URLS) {
            if (path.endsWith(url)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isPublicMemberUrl(String path) {
        return path.endsWith("/dashboard.jsp") || 
               path.endsWith("/attendance.jsp") ||
               path.endsWith("/workout-plan.jsp");
    }
    
    /**
     * Gets the dashboard URL for a role
     */
    private String getDashboardForRole(String role) {
        switch (role) {
            case "ADMIN":
                return "/admin-dashboard.jsp";
            case "TRAINER":
                return "/trainer-dashboard.jsp";
            case "MEMBER":
                return "/member-dashboard.jsp";
            default:
                return "/login.jsp";
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Authentication Filter initialized.");
    }
    
    @Override
    public void destroy() {
        System.out.println("Authentication Filter destroyed.");
    }
}