package com.codepath.timeline.models;

import com.codepath.timeline.network.UserClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// remember to register in ParseApplication
// only fields of Story class will be serialized
@ParseClassName("Story")
public class Story extends ParseObject {

  // Gson needs the following
  private String mockTitle;
  private String mockBackgroundImage;
  private String mockCreatedAt;

  // TODO
  private List<Moment> momentList;

  public Story() {
    super();
  }

  // TODO: not used
  public static Story fromJson(JsonObject jsonObject) {
    Gson gson = new Gson();
    Story story = gson.fromJson(jsonObject.toString(), Story.class);
    return story;
  }

  public Story(String title, String backgroundImageUrl, String createdAtReal) {
    super();
    setTitle(title);
    setBackgroundImageUrl(backgroundImageUrl);
    setCreatedAtReal(createdAtReal);
    setOwner(UserClient.getCurrentUser());
    List<ParseUser> collaboratorList = new ArrayList<>();
    collaboratorList.add(UserClient.getCurrentUser());
    setCollaboratorList(collaboratorList);
  }

  public static List<Story> fromJsonArray(JsonArray jsonArray) {
    Gson gson = new Gson();
    Type type = new TypeToken<List<Story>>() {
    }.getType();
    ArrayList<Story> list = gson.fromJson(jsonArray, type);
    Story.fromGsonToParse(list);
    return list;
  }

  public static void fromGsonToParse(List<Story> storyList) {
    for (Story theStory : storyList) {
      theStory.setTitle(theStory.mockTitle);
      theStory.setBackgroundImageUrl(theStory.mockBackgroundImage);
      theStory.setCreatedAtReal(theStory.mockCreatedAt);
      theStory.setOwner(UserClient.getCurrentUser());
      List<ParseUser> collaboratorList = new ArrayList<>();
      collaboratorList.add(UserClient.getCurrentUser());
      theStory.setCollaboratorList(collaboratorList);
    }
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

  public ParseFile getBackgroundImageMedia() {
    return getParseFile("backgroundImageMedia");
  }

  public void setBackgroundImageMedia(ParseFile file) {
    put("backgroundImageMedia", file);
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

  public ParseUser getOwner() {
    return (ParseUser) get("owner");
  }

  public void setOwner(ParseUser owner) {
    put("owner", owner);
  }

  public List<ParseUser> getCollaboratorList() {
    return (List<ParseUser>) get("collaboratorList");
  }

  public void setCollaboratorList(List<ParseUser> collaboratorList) {
    put("collaboratorList", collaboratorList);
  }

  // Ordered by descending date
  public List<Moment> getMomentList() {
    List<Moment> momentList = (List<Moment>) get("momentList");
    Collections.sort(momentList);
    return momentList;
  }

  public void setMomentList(List<Moment> momentList) {
    put("momentList", momentList);
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("--------- Story");
    str.append("\nobjectId=").append(getObjectId());
    //    str.append("\ncreatedAt=").append(getCreatedAt().toString());
    str.append("\ncreatedAtReal=").append(getCreatedAtReal());
    str.append("\ntitle=").append(getTitle());
    str.append("\nbackgroundImageUrl=").append(getBackgroundImageUrl());

    ParseUser user = getOwner();
    if (user != null) {
      str.append("\n------- Owner");
      str.append("\nobjectId=").append(user.getObjectId());
      str.append("\ncreatedAt=").append(user.getCreatedAt().toString());
      str.append("\nuserName=").append(user.getUsername());
      str.append("\nemail=").append(user.getEmail());
      str.append("\nprofileImageUrl=").append(user.get("profileImageUrl"));
    }
    return str.toString();
  }
}
