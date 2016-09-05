package com.codepath.timeline.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// remember to register in ParseApplication
// only fields of Moment class will be serialized
@ParseClassName("Moment")
public class Moment extends ParseObject implements Comparable<Moment>{
  private static final String TAG = Moment.class.getSimpleName();

  private String mockCreatedAt;     // database format: 2016-08-22T19:22:54.695Z
  private String mockDescription;
  private String mockMediaUrl;
  private String mockLocation;
  private ParseUser mockAuthor;

  private String tempPhotoUri;      // temporarily store the photo URI after taking a picture

  public Moment() {
  }

  public static List<Moment> fromJsonArray(JsonArray jsonArray) {
    Gson gson = new Gson();
    Type type = new TypeToken<List<Moment>>() {
    }.getType();
    ArrayList<Moment> list = gson.fromJson(jsonArray, type);

    return list;
  }

  // TEST: for generating mock response purposes
  public Moment(String createdAtRealDoNotUseThis,
                String description,
                String mediaUrl,
                ParseUser user,
                String location) {
    setCreatedAtReal(new Date());
    setDescription(description);
    setMediaUrl(mediaUrl);
    setAuthor(user);
    setLocation(location);
  }

  // the date added into Parse
  public String getCreatedAtString() {
    return getCreatedAt().toString();
  }

  // the date from photo or user
  public Date getCreatedAtReal() {
    return (Date) get("createdAtReal");
  }

  public void setCreatedAtReal(Date createdAtReal) {
    put("createdAtReal", createdAtReal);
  }

  public String getDescription() {
    return (String) get("description");
  }

  public void setDescription(String description) {
    put("description", description);
  }

  public String getMediaUrl() {
    return (String) get("mediaUrl");
  }

  public void setMediaUrl(String mediaUrl) {
    put("mediaUrl", mediaUrl);
  }

  public ParseFile getMediaFile() {
    return getParseFile("mediaFile");
  }

  public void setMediaFile(ParseFile file) {
    put("mediaFile", file);
  }

  public String getTempPhotoUri() {
    return tempPhotoUri;
  }

  public void setTempPhotoUri(String tempPhotoUri) {
    this.tempPhotoUri = tempPhotoUri;
  }

  public String getLocation() {
    return (String) get("location");
  }

  public void setLocation(String location) {
    put("location", location);
  }

  public List<Comment> getCommentList() {
    return (List<Comment>) get("commentList");
  }

  public void setCommentList(List<Comment> commentList) {
    put("commentList", commentList);
  }

  public boolean isChat() {
    Boolean isChat = (Boolean) get("isChat");
    if (isChat == null) {
      return false;
    }
    return isChat;
  }

  public void setChat(boolean isChat) {
    put("isChat", isChat);
  }

  public ParseUser getAuthor() {
    return (ParseUser) get("author");
  }

  public void setAuthor(ParseUser author) {
    put("author", author);
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("---------- Moment");
    str.append("\nobjectId=").append(getObjectId());
    str.append("\ncreatedAtReal=").append(getCreatedAtReal());
    str.append("\ndescription=").append(getDescription());
    str.append("\nmediaUrl=").append(getMediaUrl());
    str.append("\nlocation=").append(getLocation());

    ParseUser author = getAuthor();
    if (author != null) {
      str.append("\n------- Owner");
      str.append("\nobjectId=").append(author.getObjectId());
//      str.append("\nuserName=").append(author.getUsername());
      str.append("\nemail=").append(author.getEmail());
      str.append("\nprofileImageUrl=").append(UserClient.getProfileImageUrl(author));
    }

    return str.toString();
  }

  @Override
  public int compareTo(Moment otherMoment) {
    return otherMoment.getCreatedAtReal().compareTo(getCreatedAtReal());
  }
}
