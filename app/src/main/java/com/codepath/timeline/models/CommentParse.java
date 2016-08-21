package com.codepath.timeline.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

// This is only used for debug purpose
@ParseClassName("CommentParse")
public class CommentParse extends ParseObject {
	// Ensure that your subclass has a public default constructor
	public CommentParse() {
		super();
	}

	// Use getString and others to access fields
	public String getBody() {
		return getString("body");
	}

	// Use put to modify field values
	public void setBody(String value) {
		put("body", value);
	}

	// Associate each comment with a user
	public void setOwner(ParseUser user) {
		put("owner", user);
	}

	// Get the user for this comment
	public ParseUser getOwner()  {
		return getParseUser("owner");
	}

	// Associate each comment with a post
	public void setPost(UserParse post) {
		put("post", post);
	}

	// Get the post for this item
	public UserParse getPost()  {
		return (UserParse) getParseObject("post");
	}
}