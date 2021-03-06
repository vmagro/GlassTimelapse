package com.socaldevs.glasstimelapse.web.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.socaldevs.glasstimelapse.web.EndEventRequest;
import com.socaldevs.glasstimelapse.web.Event;
import com.socaldevs.glasstimelapse.web.Image;
import com.socaldevs.glasstimelapse.web.User;

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
			// if (Utils.isUserLoggedIn(req.getSession())) {
			String glassId = req.getParameter("glassId");
			User user = ofy().load().type(User.class).filter("glassId", glassId).first().now();

			if (user != null) {
				Event event = new Event(user.id, new Date());
				Key<Event> k = ofy().save().entity(event).now();
				resp.getWriter().println(k.getId());
			} else {
				resp.getWriter().println("User not found.");
			}

		} else if (mode.equals("end")) {
			String glassId = req.getParameter("glassId");
			String eventId = req.getParameter("eventId");
			Event event = ofy().load().type(Event.class).id(Long.valueOf(eventId)).now();

			// check if event has ended
			if(!event.hasEnded()) {
				// end this event
				event.end();
				EndEventRequest request = new EndEventRequest(event);
				Gson gson = new Gson();
				resp.getWriter().println(gson.toJson(request));

				try {
					URL url = new URL("http://173.255.121.241/create");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoOutput(true);
					connection.setRequestMethod("POST");

					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(gson.toJson(request));
					writer.close();

					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// OK
					} else {
						// Server returned HTTP error code.
					}
				} catch (MalformedURLException e) {
					// ...
				} catch (IOException e) {
					// ...
				}
			} else {
				// error, no open events
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String mode = req.getParameter("mode");
		if (mode.equals("getImage")) {
			int eventId = Integer.valueOf(req.getParameter("eventId"));
			int imageIndex = Integer.valueOf(req.getParameter("i"));
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

			Image image = ofy().load().type(Image.class).filter("eventId", eventId).filter("index", imageIndex).first()
					.now();
			if (image != null) {
				blobstoreService.serve(image.imageKey, resp);
			}
		}
	}
}
