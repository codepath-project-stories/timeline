package com.codepath.timeline.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.parceler.Parcel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = {User.class})
public class User {
  private int id;
  private String name;
  private String profileImageUrl;

  public User() {}

  public static User fromJson(JsonObject jsonObject) {
    Gson gson = new Gson();
    User user = gson.fromJson(jsonObject.toString(), User.class);

    return user;
  }

  public static ArrayList<User> fromJson(JsonArray jsonArray) {
    Gson gson = new Gson();
    Type type = new TypeToken<List<User>>(){}.getType();
    ArrayList<User> userList = gson.fromJson(jsonArray, type);

    return userList;
  }

  // TEST: for generating mock response purposes
  public User(int id, String name, String profileImageUrl) {
    this.id = id;
    this.name = name;
    this.profileImageUrl = profileImageUrl;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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
