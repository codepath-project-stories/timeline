package com.codepath.timeline.models;

import org.parceler.Parcel;

@Parcel(analyze = {Story.class})
public class Story {
    private String storyTitle;
    private String backgroundImageUrl;

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public Story() {}

    public Story(String description, String backgroundImageUrl) {
        this.storyTitle = description;
        this.backgroundImageUrl = backgroundImageUrl;
    }
}
