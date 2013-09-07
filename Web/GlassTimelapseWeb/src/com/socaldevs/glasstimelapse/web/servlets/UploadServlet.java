package com.socaldevs.glasstimelapse.web.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.socaldevs.glasstimelapse.web.Image;

public class UploadServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	/*
	 * POST TO #getUploadUrl, NOT "/upload"
	 * 
	 * required: image, index, eventId
	 */

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		// get the first blob
		BlobKey blobKey = blobs.get("image").get(0);

		int eventId = Integer.valueOf(req.getParameter("eventId"));
		int index = Integer.valueOf(req.getParameter("index"));

		if (blobKey != null) {
			Image image = new Image(eventId, index, blobKey);
			ofy().save().entity(image).now();
		}
	}

	/*
	 * required: mode ("getUploadUrl")
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String mode = req.getParameter("mode");
		if (mode.equals("getUploadUrl")) {
			resp.getWriter().println(blobstoreService.createUploadUrl("/upload"));
		}
	}
}
