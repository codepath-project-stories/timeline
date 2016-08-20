package com.codepath.timeline.models;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = {Moment.class})
public class Moment {
  private static final String TAG = Moment.class.getSimpleName();

  private int id;
  private String createdAt;
  private String description;
  private String mediaUrl;
  private User user;
  private String header;  // hack to show header
  private String location;

  public Moment() {}

  public static Moment fromJson(JsonObject jsonObject) {
    Gson gson = new Gson();
    Moment moment = gson.fromJson(jsonObject.toString(), Moment.class);
    return moment;
  }

  public static List<Moment> fromJsonArray(JsonArray jsonArray) {
    Gson gson = new Gson();
    Type type = new TypeToken<List<Moment>>(){}.getType();
    ArrayList<Moment> list = gson.fromJson(jsonArray, type);

    return list;
  }

  // TEST: for generating mock response purposes
  public Moment(int id, String createdAt, String description, String mediaUrl, User user, String location) {
    this.id = id;
    this.createdAt = createdAt;
    this.description = description;
    this.mediaUrl = mediaUrl;
    this.user = user;
    this.location = location;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getMediaUrl() {
    return mediaUrl;
  }

  public void setMediaUrl(String mediaUrl) {
    this.mediaUrl = mediaUrl;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
