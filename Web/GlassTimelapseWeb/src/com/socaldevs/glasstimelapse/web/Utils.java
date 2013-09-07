package com.socaldevs.glasstimelapse.web;

import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.servlet.http.HttpSession;

import com.googlecode.objectify.Key;

public class Utils {
	public static boolean isUserLoggedIn(HttpSession session) {
		return !(session == null || session.getAttribute("me") == null);
	}

	// CAN return null!
	public static Long getCurrentUserId(HttpSession session) {
		return (Long) session.getAttribute("me");
	}

	// CAN return null!
	public static User getCurrentUser(HttpSession session) {
		Long userId = getCurrentUserId(session);
		Key<User> userKey = Key.create(User.class, userId);
		return ofy().load().key(userKey).now();
	}
}
