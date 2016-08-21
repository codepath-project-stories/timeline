package com.codepath.timeline.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

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

  private List<User> collaboratorList;
  private List<Moment> momentList;

  public Story() {
    super();
  }

  // TEST: for generating mock response purposes
  public Story(String title, String backgroundImageUrl, ParseUser owner) {
    super();
    put("title", title);
    put("backgroundImageUrl", backgroundImageUrl);
    put("owner", owner);
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
