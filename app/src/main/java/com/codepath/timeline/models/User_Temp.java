package com.codepath.timeline.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.parceler.Parcel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = {User_Temp.class})
public class User_Temp {
  private int id;
  private String name;
  private String profileImageUrl;

  public User_Temp() {}

  public static User_Temp fromJson(JsonObject jsonObject) {
    Gson gson = new Gson();
    User_Temp user = gson.fromJson(jsonObject.toString(), User_Temp.class);

    return user;
  }

  public static ArrayList<User_Temp> fromJson(JsonArray jsonArray) {
    Gson gson = new Gson();
    Type type = new TypeToken<List<User_Temp>>(){}.getType();
    ArrayList<User_Temp> userList = gson.fromJson(jsonArray, type);

    return userList;
  }

  // TEST: for generating mock response purposes
  public User_Temp(int id, String name, String profileImageUrl) {
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
