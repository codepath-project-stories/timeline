package com.codepath.timeline.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.parceler.Parcel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = {Story.class})
public class Story {
  private int id;
  private String createdAt;
  private String title;
  private String backgroundImageUrl;
  private User owner;
  private List<User> collaboratorList;
  private List<Moment> momentList;

  public Story() {}

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

  // TEST: for generating mock response purposes
  public Story(int id, String createdAt, String title, String backgroundImageUrl, User owner) {
    this.id = id;
    this.createdAt = createdAt;
    this.title = title;
    this.backgroundImageUrl = backgroundImageUrl;
    this.owner = owner;
  }

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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBackgroundImageUrl() {
    return backgroundImageUrl;
  }

  public void setBackgroundImageUrl(String backgroundImageUrl) {
    this.backgroundImageUrl = backgroundImageUrl;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public List<User> getCollaboratorList() {
    return collaboratorList;
  }

  public void setCollaboratorList(List<User> collaboratorList) {
    this.collaboratorList = collaboratorList;
  }

  public List<Moment> getMomentList() {
    return momentList;
  }

  public void setMomentList(List<Moment> momentList) {
    this.momentList = momentList;
  }
}
