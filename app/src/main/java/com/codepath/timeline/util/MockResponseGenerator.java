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

public class MockResponseGenerator {
  private static final String TAG = MockResponseGenerator.class.getSimpleName();

  private static MockResponseGenerator instance;
  private Gson gson;
  private List<Moment> mMomentList;
  private List<Story> mStoryList;

  private MockResponseGenerator() {
    gson = new Gson();
    createMockStoryList();
    createMockMomentList();
  }

  public static MockResponseGenerator getInstance() {
    if (instance == null) {
      instance = new MockResponseGenerator();
    }

    return instance;
  }

  private void createMockStoryList() {
    mStoryList = new ArrayList<>();

    for (int i = 0; i < 20; i++) {
      Story story = new Story("Story " + i, "http://pbs.twimg.com/media/CpdUcQcWAAAwgwJ.jpg");
      mStoryList.add(story);
    }
  }

  private void createMockMomentList() {
    mMomentList = new ArrayList<>();
    User user = new User("Dianne Bautista", "http://pbs.twimg.com/profile_images/762174511118585856/dIYGQ-Wa_normal.jpg");
    for (int i = 0; i < 20; i++) {
      Moment moment;
      if (i % 10 == 0) {
        moment = new Moment();
        moment.setHeader("200" + i);
      } else {
        moment = new Moment("Moment " + i, "January " + i + ", 2016", "http://pbs.twimg.com/media/CqLfimAXgAAIqyD.jpg", user);
      }
      mMomentList.add(moment);
    }
  }

  public List<Story> getStoryList() {
    return mStoryList;
  }

  public String getStoryListAsString() {
    Type type = new TypeToken<List<Moment>>() {
    }.getType();
    String json = gson.toJson(mStoryList, type);
    Log.d(TAG, "stories json: " + json);

    return json;
  }

  public List<Moment> getMomentList() {
    return mMomentList;
  }

  public String getMomentListAsString() {
    Type type = new TypeToken<List<Moment>>() {
    }.getType();
    String json = gson.toJson(mMomentList, type);
    Log.d(TAG, "moments json: " + json);

    return json;
  }


}
