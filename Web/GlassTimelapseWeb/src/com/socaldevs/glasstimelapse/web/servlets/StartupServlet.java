package com.socaldevs.glasstimelapse.web.servlets;

import javax.servlet.http.HttpServlet;

import com.googlecode.objectify.ObjectifyService;
import com.socaldevs.glasstimelapse.web.Event;
import com.socaldevs.glasstimelapse.web.Image;
import com.socaldevs.glasstimelapse.web.User;

/**
 * Used to register entities with objectify.
 */
public class StartupServlet extends HttpServlet {
	static {
		ObjectifyService.register(User.class);
		ObjectifyService.register(Event.class);
		ObjectifyService.register(Image.class);
	}
}
