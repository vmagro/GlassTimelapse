package com.socaldevs.glasstimelapse.web.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.socaldevs.glasstimelapse.web.Event;

public class EventsServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String from = req.getParameter("from");
		String all = req.getParameter("all");
		Gson gson = new Gson();

		if (from != null) {
			Long fromId = Long.valueOf(from);
			List<Event> eventsList = ofy().load().type(Event.class).filter("userId", fromId).order("-startTime").list();
			resp.getWriter().println(gson.toJson(eventsList));
		} else if (all != null) {
			List<Event> eventsList = ofy().load().type(Event.class).limit(20).order("-startTime").list();
			resp.getWriter().println(gson.toJson(eventsList));
		}
	}
}