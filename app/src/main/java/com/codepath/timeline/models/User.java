package com.codepath.timeline.models;

import org.parceler.Parcel;

@Parcel(analyze = {User.class})
public class User {
  private String name;
  private String profileImageUrl;

  public User() {}

  public User(String name, String profileImageUrl) {
    this.name = name;
    this.profileImageUrl = profileImageUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public void setProfileImageUrl(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }
}
