package com.socaldevs.glasstimelapse.web.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.socaldevs.glasstimelapse.web.Event;
import com.socaldevs.glasstimelapse.web.User;

public class EventServlet extends HttpServlet {
	/*
	 * Required: mode ("new", "end"), access_token
	 * 
	 * new: returns created event ID.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String mode = req.getParameter("mode");

		if (mode.equals("new")) {
			User user = ofy().load().type(User.class).filter("googleAccessToken", req.getParameter("access_token"))
					.first().now();
			System.out.println("Token: " + req.getParameter("access_token"));
			System.out.println("User: " + user);
			if (user != null) {
				Event event = new Event(user.id, new Date());
				Key<Event> k = ofy().save().entity(event).now();
				resp.getWriter().println(k.getId());
			}
		} else if (mode.equals("end")) {

		}

	}
}
