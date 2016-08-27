package com.codepath.timeline.models;

import android.util.Log;

import com.codepath.timeline.util.ParseApplication;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// @Parcel(analyze = {Story.class})
// only fields of Story class will be serialized
@ParseClassName("Story")
@Parcel(analyze = {Story.class})
public class Story extends ParseObject {
  // TODO: Story has an array of ParseUser
  // TODO: ParseUser has an array of  Story

  // Gson needs the following
  String title;
  String backgroundImageUrl;

  private List<User> collaboratorList;
  private List<Moment> momentList;

  public Story() {
    super();
  }

  // TODO: not used
  public Story(String title, String backgroundImageUrl) {
    super();
    this.title = title;
    this.backgroundImageUrl = backgroundImageUrl;
  }

  public void prepareSaveToParse() {
    if (ParseApplication.TURN_ON_PARSE) {
      setTitle(title);
      setBackgroundImageUrl(backgroundImageUrl);
      setOwner(ParseUser.getCurrentUser());
    }
  }

  public static void saveToParse(final List<Story> storyList) {
    if (ParseApplication.TURN_ON_PARSE) {
      for (Story theStory : storyList) {
        theStory.prepareSaveToParse();
      }
      ParseObject.saveAllInBackground(storyList, new SaveCallback() {
        @Override
        public void done(ParseException e) {
          ParseUser currentUser = ParseUser.getCurrentUser();
          currentUser.put("stories", storyList);
          currentUser.put("demoCreated2", "true");
          currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
              if (e != null) {
                Log.d("getMockStoryList", e.toString());
              }
            }
          });
        }
      });
    }
  }

  // TODO: not used
  public static Story fromJson(JsonObject jsonObject) {
    Gson gson = new Gson();
    Story story = gson.fromJson(jsonObject.toString(), Story.class);
    return story;
  }

  public static List<Story> fromJsonArray(JsonArray jsonArray) {
    Gson gson = new Gson();
    Type type = new TypeToken<List<Story>>(){}.getType();
    ArrayList<Story> list = gson.fromJson(jsonArray, type);
    return list;
  }

  // TODO
  public String getCreatedAt2() {
    return getCreatedAt().toString();
  }

  public String getTitle() {
    return (String) get("title");
  }

  public void setTitle(String title) {
    put("title", title);
  }

  public String getBackgroundImageUrl() {
    return (String) get("backgroundImageUrl");
  }

  public void setBackgroundImageUrl(String backgroundImageUrl) {
    put("backgroundImageUrl", backgroundImageUrl);
  }

  public ParseUser getOwner() {
    return (ParseUser) get("owner");
  }

  public void setOwner(ParseUser owner) {
    put("owner", owner);
  }

  // TODO: work with Parse
  public List<User> getCollaboratorList() {
    return collaboratorList;
  }

  // TODO: work with Parse
  public void setCollaboratorList(List<User> collaboratorList) {
    this.collaboratorList = collaboratorList;
  }

  // TODO: work with Parse
  public List<Moment> getMomentList() {
    return momentList;
  }

  // TODO: work with Parse
  public void setMomentList(List<Moment> momentList) {
    this.momentList = momentList;
  }
}
