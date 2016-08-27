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

  // Gson needs the following
  private String titleDoNotUseThis;
  private String backgroundImageUrlDoNotUseThis;
  private String createdAtRealDoNotUseThis;

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

  // TODO: not used
  public Story(String title, String backgroundImageUrl, String createdAtReal) {
    super();
    setTitle(title);
    setBackgroundImageUrl(backgroundImageUrl);
    setCreatedAtReal(createdAtReal);
    setOwner(ParseUser.getCurrentUser());
    List<ParseUser> collaboratorList = new ArrayList<>();
    collaboratorList.add(ParseUser.getCurrentUser());
    setCollaboratorList(collaboratorList);
  }

  public static List<Story> fromJsonArray(JsonArray jsonArray) {
    Gson gson = new Gson();
    Type type = new TypeToken<List<Story>>(){}.getType();
    ArrayList<Story> list = gson.fromJson(jsonArray, type);
    Story.fromGsonToParse(list);
    return list;
  }

  public static void fromGsonToParse(List<Story> storyList) {
    for (Story theStory : storyList) {
      theStory.setTitle(theStory.titleDoNotUseThis);
      theStory.setBackgroundImageUrl(theStory.backgroundImageUrlDoNotUseThis);
      theStory.setCreatedAtReal(theStory.createdAtRealDoNotUseThis);
      theStory.setOwner(ParseUser.getCurrentUser());
      List<ParseUser> collaboratorList = new ArrayList<>();
      collaboratorList.add(ParseUser.getCurrentUser());
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

  // TODO: work with Parse
  public List<Moment> getMomentList() {
    return momentList;
  }

  // TODO: work with Parse
  public void setMomentList(List<Moment> momentList) {
    this.momentList = momentList;
  }
}
