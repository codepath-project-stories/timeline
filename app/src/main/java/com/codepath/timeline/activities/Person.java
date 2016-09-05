package com.codepath.timeline.activities;

import java.io.Serializable;

public class Person implements Serializable {
	private String name;
	private String email;
	private String image;

	public Person(String n, String e, String i) { name = n; email = e; image = i; }

	public String getName() { return name; }
	public String getEmail() { return email; }
	public String getImage() { return image; }

	@Override
	public String toString() { return name; }
}