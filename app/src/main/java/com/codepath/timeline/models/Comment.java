package com.codepath.timeline.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

// remember to register in ParseApplication
// only fields of Comment class will be serialized
@ParseClassName("Comment")
@Parcel(analyze = {Comment.class})
public class Comment extends ParseObject {
  private int id;
  private String createdAtRealDoNotUseThis;     // DB format: 2016-08-22T19:22:54.695Z
  private String mediaUrl;
  private String location;
  private String body;

  public Comment() {}


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  // the date added into Parse
  public String getCreatedAtString() {
    return getCreatedAt().toString();
  }

  // the date from photo or user
  public String getCreatedAtReal() {
    return (String) get("createdAtReal");
  }

  public void setCreatedAtReal(String createdAtReal) {
    put("createdAtReal", createdAtReal);
  }

  public String getMediaUrl() {
    return mediaUrl;
  }

  public void setMediaUrl(String mediaUrl) {
    this.mediaUrl = mediaUrl;
  }

  public ParseUser getUser() {
    return (ParseUser) get("user");
  }

  public void setUser(ParseUser user) {
    put("user", user);
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
