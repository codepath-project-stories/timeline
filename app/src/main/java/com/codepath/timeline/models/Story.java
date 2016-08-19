package com.codepath.timeline.models;

import org.parceler.Parcel;

@Parcel(analyze = {Story.class})
public class Story {
  private String description;
  private String backgroundImageUrl;

  public Story() {}

  public Story(String description, String backgroundImageUrl) {
    this.description = description;
    this.backgroundImageUrl = backgroundImageUrl;
  }
}
