package com.codepath.timeline.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("UserParse")
public class UserParse extends ParseObject {
	// Ensure that your subclass has a public default constructor
	public UserParse() {
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

	// Get the user for this item
	public ParseUser getUser()  {
		return getParseUser("owner");
	}

	// Associate each item with a user
	public void setOwner(ParseUser user) {
		put("owner", user);
	}

	public ParseRelation<TagParse> getTagsRelation() {
		return getRelation("tags");
	}

	public void addTag(TagParse tagParse) {
		getTagsRelation().add(tagParse);
		saveInBackground();
	}

	public void removeTag(TagParse tagParse) {
		getTagsRelation().remove(tagParse);
		saveInBackground();
	}
}