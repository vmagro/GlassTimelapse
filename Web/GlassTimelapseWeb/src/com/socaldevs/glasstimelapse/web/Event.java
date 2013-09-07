package com.socaldevs.glasstimelapse.web;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Event {
	@Id
	public Long id;
	public Long userId;
	public Date startTime;
	public Date endTime;

	private Event() {}

	public Event(Long userId, Date startTime) {
		super();
		this.userId = userId;
		this.startTime = startTime;
		this.endTime = null;
	}

	public void endEvent(Date endTime) {
		this.endTime = endTime;
	}

}
