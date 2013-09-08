package com.socaldevs.glasstimelapse.web;

import java.util.Date;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Image {
	@Id
	public Long id;

	// Image 3 of 5000, for example
	@Index
	public int index;
	@Index
	public int eventId;
	public BlobKey imageKey;
	public Date time;

	@Ignore
	private Event e;

	private Image() {}

	public Image(int eventId, int index, BlobKey imageKey) {
		// this.event = Key.create(Event.class, eventId);
		// System.out.println("event is: " + event);
		this.eventId = eventId;
		this.index = index;
		this.imageKey = imageKey;
		this.time = new Date();
	}

	// public Event getEvent() {
	// if (e == null) {
	// e = ofy().load().type(Event.class).filter("id", eventId).first().now();
	// }
	//
	// return e;
	// }
}
