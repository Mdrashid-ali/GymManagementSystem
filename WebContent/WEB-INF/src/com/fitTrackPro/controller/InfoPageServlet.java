package com.fitTrackPro.controller;

import com.fitTrackPro.model.user;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({ "/about", "/services", "/contact" })
/**
 * Displays shared informational pages such as About, Services, and Contact.
 */
public class InfoPageServlet extends HttpServlet {
	@Override
	/** Handles HTTP GET requests for this servlet route. */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
	/** Handles HTTP POST form submissions for this servlet route. */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(
				request.getContextPath() + "/contact?success=Message received. Our team will follow up soon.");
	}
}
