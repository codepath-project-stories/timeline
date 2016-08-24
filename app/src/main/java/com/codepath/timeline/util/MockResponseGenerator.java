package com.codepath.timeline.util;

import android.util.Log;

import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// This is only used for debug purpose
public class MockResponseGenerator {
  private static final String TAG = MockResponseGenerator.class.getSimpleName();

  private static MockResponseGenerator instance;
  private Gson gson;
  private List<Moment> mMomentList;
  private List<Story> mStoryList;
  private List<String> mMediaList;
  private Random mRandomGenerator;

  private MockResponseGenerator() {
    gson = new Gson();
    createMediaList();
//    createMockStoryList();
    createMockMomentList();
  }

  public static MockResponseGenerator getInstance() {
    if (instance == null) {
      instance = new MockResponseGenerator();
    }

    return instance;
  }

  private void createMediaList() {
    mMediaList = new ArrayList<>();
    mMediaList.add("http://pbs.twimg.com/media/CqLfimAXgAAIqyD.jpg");
    mMediaList.add("https://pbs.twimg.com/media/CqLmaWCXgAEbYCY.jpg");
    mMediaList.add("https://pbs.twimg.com/media/CqLOZTrWcAQOl0x.jpg");
    mMediaList.add("https://pbs.twimg.com/media/CqK2WIdWgAEZIFM.jpg");
    mMediaList.add("https://pbs.twimg.com/media/CqKUA_TXEAASfa5.jpg");
    mMediaList.add("https://pbs.twimg.com/media/CqJ_a9NWYAAfCMX.jpg");
    mMediaList.add("https://pbs.twimg.com/media/CqJxseQW8AA9bEa.jpg");
    mMediaList.add("https://pbs.twimg.com/media/CqJdGVLXgAAnLJl.jpg");
    mMediaList.add("https://pbs.twimg.com/media/CqJBn4BWEAALVNo.jpg");

    mRandomGenerator = new Random();
  }

  private User getUser() {
    return new User(5555, "Jane Smith", "https://pbs.twimg.com/profile_images/761636511238516736/k5XbteDD.jpg");
  }

  private void createMockStoryList() {
    mStoryList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      String dummyDate = "January " + i + ", 2016";
      Story story = new Story("Story " + i,
          "http://pbs.twimg.com/media/CpdUcQcWAAAwgwJ.jpg",
          null);
      mStoryList.add(story);
    }
  }

  private void createMockMomentList() {
    mMomentList = new ArrayList<>();

    for (int i = 0; i < 14; i++) {
      String dummyDate = "January " + i + ", 2016";
      String mediaUrl = getRandomMedia();
      Moment moment = new Moment(5000 + i, dummyDate, "Moment " + i + ": something, something, something, something",
          mediaUrl, getUser(), "San Francisco, CA");
      mMomentList.add(moment);
    }
  }

  private String getRandomMedia() {
    int index = mRandomGenerator.nextInt(mMediaList.size());
    return mMediaList.get(index);
  }

  public String getStoryListAsString() {
    Type type = new TypeToken<List<Moment>>() {
    }.getType();
    String json = gson.toJson(mStoryList, type);
    Log.d(TAG, "stories json: " + json);

    return json;
  }

  public String getMomentListAsString() {
    Type type = new TypeToken<List<Moment>>() {
    }.getType();
    String json = gson.toJson(mMomentList, type);
    Log.d(TAG, "moments json: " + json);

    return json;
  }


}
