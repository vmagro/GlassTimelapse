package com.socaldevs.glasstimelapse.web.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.socaldevs.glasstimelapse.web.EndEventRequest;
import com.socaldevs.glasstimelapse.web.Event;
import com.socaldevs.glasstimelapse.web.Utils;

public class EventServlet extends HttpServlet {
	/*
	 * Required: mode ("new", "end")
	 * 
	 * new: returns created event ID.
	 * 
	 * end: ends the currently started event, and sends request to build video
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String mode = req.getParameter("mode");

		if (mode.equals("new")) {
			if (Utils.isUserLoggedIn(req.getSession())) {
				Event event = new Event(Utils.getCurrentUserId(req.getSession()), new Date());
				Key<Event> k = ofy().save().entity(event).now();
				resp.getWriter().println(k.getId());
			}
		} else if (mode.equals("end")) {
			if (Utils.isUserLoggedIn(req.getSession())) {
				Long userId = Utils.getCurrentUserId(req.getSession());
				Event event = ofy().load().type(Event.class)
						.filter("userId", userId)
						.order("-startTime")
						.first().now();

				// check if event has ended
				if(!event.hasEnded()) {
					// end this event
					event.end();
					EndEventRequest request = new EndEventRequest(event);
					Gson gson = new Gson();
					resp.getWriter().println(gson.toJson(request));
				} else {
					// error, no open events
				}
			}
		}

	}
}
