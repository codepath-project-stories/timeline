package com.codepath.timeline.network;

import android.util.Log;

import com.parse.ParseUser;

// TODO: try to extends ParseUser and move it to models
public class UserClient {
	private static final String TAG = UserClient.class.getSimpleName();

	public static ParseUser getCurrentUser() {
		return ParseUser.getCurrentUser();
	}
	public static String getName(ParseUser user) {
		return (String) user.get("name");
	}
	public static String getProfileImageUrl(ParseUser user) {
		return (String) user.get("profileImageUrl");
	}

	public static void printUser(ParseUser user) {
		StringBuilder str = new StringBuilder();
		str.append("\nobjectId=").append(user.getObjectId());
		str.append("\ncreatedAt=").append(user.getCreatedAt().toString());
		str.append("\nuserName=").append(user.getUsername());
		str.append("\nemail=").append(user.getEmail());
		Log.d(TAG, "CurrentUser=" + str);
	}
}
