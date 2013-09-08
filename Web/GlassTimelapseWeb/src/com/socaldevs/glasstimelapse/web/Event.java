package com.socaldevs.glasstimelapse.web;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Date;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Event {
	@Id
	public Long id;
	@Index
	public Long userId;
	@Index
	public String city;
	@Index
	public Date startTime;
	@Index
	public Date endTime;
	
	public int numImages;

	public String youtubeUrl;

	private Event() {}

	public Event(Long userId, Date startTime) {
		super();
		this.userId = userId;
		this.startTime = startTime;
		this.endTime = null;
	}

	public void end() {
		this.endTime = new Date();
		ofy().save().entity(this).now();
	}

	public boolean hasEnded() {
		return this.endTime != null;
	}

	public User getUser() {
		Key<User> userKey = Key.create(User.class, userId);
		return ofy().load().key(userKey).now();
	}

	public String getState() {
		if (endTime == null) {
			return "ongoing";
		} else if (youtubeUrl == null) {
			return "processing";
		} else {
			return "finished";
		}
	}
}
