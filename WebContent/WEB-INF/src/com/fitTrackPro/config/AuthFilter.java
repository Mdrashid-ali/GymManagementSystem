package com.fitTrackPro.config;

import com.fitTrackPro.model.user;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = { "/admin/*", "/member/*", "/trainer/*", "/dashboard", "/adminDashboard", "/memberDashboard",
		"/trainerDashboard" })
/**
 * Filters protected requests and redirects unauthenticated users to the login
 * page.
 */
public class AuthFilter implements Filter {
	/** Applies authentication filtering before protected resources are reached. */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest r = (HttpServletRequest) req;
		HttpServletResponse p = (HttpServletResponse) res;
		HttpSession s = r.getSession(false);
		user u = s == null ? null : (user) s.getAttribute("currentUser");

		if (u == null || !u.isActive()) {
			p.sendRedirect(r.getContextPath() + "/login");
			return;
		}

		String path = r.getServletPath();

		if (path.startsWith("/admin") && !u.isAdmin()) {
			p.sendError(403);
			return;
		}

		if (path.startsWith("/trainer") && !u.isTrainer()) {
			p.sendError(403);
			return;
		}

		if (path.startsWith("/member") && !u.isMember()) {
			p.sendError(403);
			return;
		}

		chain.doFilter(req, res);
	}
}
