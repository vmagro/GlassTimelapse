package com.socaldevs.glasstimelapse.web.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.socaldevs.glasstimelapse.web.Event;

public class UploadedServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String youtubeUrl = req.getParameter("youtubeUrl");
		String city = req.getParameter("city");
		String eventId = req.getParameter("eventId");
		Event event = ofy().load().type(Event.class).id(Long.valueOf(eventId)).now();

		event.city = city;
		event.youtubeUrl = youtubeUrl;

		ofy().save().entity(event).now();

	}
}
