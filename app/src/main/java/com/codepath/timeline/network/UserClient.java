package com.codepath.timeline.network;

import com.parse.ParseUser;

/**
 * Network client for getting response from the server
 */
public class UserClient {
	public static ParseUser getCurrentUser() {
		return ParseUser.getCurrentUser();
	}
	public static String getName(ParseUser user) {
		return (String) user.get("name");
	}
	public static String getProfileImageUrl(ParseUser user) {
		return (String) user.get("profileImageUrl");
	}
}
