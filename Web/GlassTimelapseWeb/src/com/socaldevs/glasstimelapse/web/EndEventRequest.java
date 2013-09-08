package com.socaldevs.glasstimelapse.web;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Base64;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class EndEventRequest {

	public String gPlusId;
	public Long eventId;
	public String refreshToken;
	public List<String> images;

	public EndEventRequest(Event e) {
		this.eventId = e.id;

		User user = e.getUser();
		this.gPlusId = user.googleUserId;
		this.refreshToken = user.googleRefreshToken;

		this.images = new ArrayList<String>();
		System.out.println(this.eventId);
		List<Image> images = ofy().load().type(Image.class).filter("eventId", this.eventId).order("index").list();
		for(Image image : images) {
			// this.images.add(image.imageKey.toString());
			this.images.add(new String(Base64.encodeBase64(readBlobFully(image.imageKey))));
		}
	}

	public static byte[] readBlobFully(BlobKey blobKey) {

		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);

		if (blobInfo == null)
			return null;

		if (blobInfo.getSize() > Integer.MAX_VALUE)
			throw new RuntimeException("This method can only process blobs up to " + Integer.MAX_VALUE + " bytes");

		int blobSize = (int) blobInfo.getSize();
		int chunks = (int) Math.ceil(((double) blobSize / BlobstoreService.MAX_BLOB_FETCH_SIZE));
		int totalBytesRead = 0;
		int startPointer = 0;
		int endPointer;
		byte[] blobBytes = new byte[blobSize];

		for (int i = 0; i < chunks; i++) {

			endPointer = Math.min(blobSize - 1, startPointer + BlobstoreService.MAX_BLOB_FETCH_SIZE - 1);

			byte[] bytes = blobstoreService.fetchData(blobKey, startPointer, endPointer);

			for (int j = 0; j < bytes.length; j++)
				blobBytes[j + totalBytesRead] = bytes[j];

			startPointer = endPointer + 1;
			totalBytesRead += bytes.length;
		}

		return blobBytes;
	}

}
