package com.socaldevs.glasstimelapse.web.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.socaldevs.glasstimelapse.web.User;

public class PairGlassServlet extends HttpServlet {
	/*
	 * Required: googleId - google user id & glassId - glass id
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String googleId = req.getParameter("googleId");
		System.out.println(req.getParameter("googleId"));
		String glassId  = req.getParameter("glassId");
		
		User user = ofy().load().type(User.class).filter("googleUserId", googleId).first().now();
		if (user != null) {
			user.glassId = glassId;
			ofy().save().entity(user).now();
		} else {
			resp.getWriter().println("User not found.");
		}
	}
}
