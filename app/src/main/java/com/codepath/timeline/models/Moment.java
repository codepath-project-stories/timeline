package com.codepath.timeline.models;

import org.parceler.Parcel;

@Parcel(analyze = {Moment.class})
public class Moment {
  private String description;
  private String date;
  private String resourceUrl;
  private User user;
  private String header;  // hack to show header

  public Moment() {}

  public Moment(String description, String date, String resourceUrl, User user) {
    this.description = description;
    this.date = date;
    this.resourceUrl = resourceUrl;
    this.user = user;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getResourceUrl() {
    return resourceUrl;
  }

  public void setResourceUrl(String resourceUrl) {
    this.resourceUrl = resourceUrl;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }
}
