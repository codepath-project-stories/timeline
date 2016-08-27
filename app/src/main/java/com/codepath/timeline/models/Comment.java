package com.codepath.timeline.models;

import org.parceler.Parcel;

@Parcel(analyze = {Comment.class})
public class Comment {
  private int id;
  private String createdAt;     // DB format: 2016-08-22T19:22:54.695Z
  private String mediaUrl;
  private User user;
  private String location;
  private String body;

  public Comment() {}


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getMediaUrl() {
    return mediaUrl;
  }

  public void setMediaUrl(String mediaUrl) {
    this.mediaUrl = mediaUrl;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
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
