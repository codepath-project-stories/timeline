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

// remember to register in ParseApplication
// only fields of Moment class will be serialized
@ParseClassName("Moment")
@Parcel(analyze = {Moment.class})
public class Moment extends ParseObject {
  private static final String TAG = Moment.class.getSimpleName();

  private int id;
  private String createdAtRealDoNotUseThis;     // DB format: 2016-08-22T19:22:54.695Z
  private String description;
  private String mediaUrl;
  private String header;  // hack to show header
  private String location;
  private List<Comment> commentList;

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
  public Moment(int id,
                String createdAtRealDoNotUseThis,
                String description,
                String mediaUrl,
                ParseUser user,
                String location) {
    this.id = id;
    this.createdAtRealDoNotUseThis = createdAtRealDoNotUseThis;
    this.description = description;
    this.mediaUrl = mediaUrl;
    this.setUser(user);
    this.location = location;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public ParseUser getUser() {
    return (ParseUser) get("user");
  }

  public void setUser(ParseUser user) {
    put("user", user);
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

  public List<Comment> getCommentList() {
    return commentList;
  }

  public void setCommentList(List<Comment> commentList) {
    this.commentList = commentList;
  }
}
