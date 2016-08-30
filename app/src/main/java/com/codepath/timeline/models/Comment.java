package com.codepath.timeline.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

// remember to register in ParseApplication
// only fields of Comment class will be serialized
@ParseClassName("Comment")
public class Comment extends ParseObject {
  private String mediaUrl;                      // DB: Only used for displaying the moment detail
  private String location;                      // DB: Only used for displaying the moment detail

  public Comment() {
  }

  // the date from photo or user
  public String getCreatedAtReal() {
    return (String) get("createdAtReal");
  }

  public void setCreatedAtReal(String createdAtReal) {
    put("createdAtReal", createdAtReal);
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getMediaUrl() {
    return mediaUrl;
  }

  public void setMediaUrl(String mediaUrl) {
    this.mediaUrl = mediaUrl;
  }

  public ParseUser getAuthor() {
    return (ParseUser) get("author");
  }

  public void setAuthor(ParseUser author) {
    put("author", author);
  }

  public String getBody() {
    return getString("body");
  }

  public void setBody(String body) {
    put("body", body);
  }
}
