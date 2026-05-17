package com.fitTrackPro.config;

import com.fitTrackPro.model.user;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = { "/admin/*", "/member/*", "/trainer/*", "/dashboard", "/adminDashboard", "/memberDashboard",
		"/trainerDashboard" })
/**
 * Filters protected requests and redirects users to pages allowed for their
 * role.
 */
public class AuthFilter implements Filter {
	/**
	 * Applies authentication and role filtering before protected resources are
	 * reached.
	 */
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

		if (isAdminPath(path) && !u.isAdmin()) {
			p.sendRedirect(r.getContextPath() + dashboardPath(u));
			return;
		}

		if (isTrainerPath(path) && !u.isTrainer()) {
			p.sendRedirect(r.getContextPath() + dashboardPath(u));
			return;
		}

		if (isMemberPath(path) && !u.isMember()) {
			p.sendRedirect(r.getContextPath() + dashboardPath(u));
			return;
		}

		chain.doFilter(req, res);
	}

	/** Checks whether the request targets the admin area. */
	private boolean isAdminPath(String path) {
		return path.startsWith("/admin/") || "/adminDashboard".equals(path) || "/admin/dashboard".equals(path);
	}

	/** Checks whether the request targets the trainer area. */
	private boolean isTrainerPath(String path) {
		return path.startsWith("/trainer/") || "/trainerDashboard".equals(path) || "/trainer/dashboard".equals(path);
	}

	/** Checks whether the request targets the member area. */
	private boolean isMemberPath(String path) {
		return path.startsWith("/member/") || "/memberDashboard".equals(path) || "/member/dashboard".equals(path);
	}

	/** Returns the dashboard URL for the signed-in user's role. */
	private String dashboardPath(user u) {
		if (u.isAdmin())
			return "/admin/dashboard";
		if (u.isTrainer())
			return "/trainer/dashboard";
		return "/member/dashboard";
	}
}