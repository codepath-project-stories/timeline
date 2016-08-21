package com.codepath.timeline.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("TagParse")
public class TagParse extends ParseObject {
	// Ensure that your subclass has a public default constructor
	public TagParse() {
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
}