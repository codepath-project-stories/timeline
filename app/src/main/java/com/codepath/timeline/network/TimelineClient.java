package com.codepath.timeline.network;

import android.content.Context;

import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Network client for getting response from the server
 */
public class TimelineClient {
  private static final String TAG = TimelineClient.class.getSimpleName();

  private static TimelineClient instance;
  private TimelineClient() {

  }

  public static TimelineClient getInstance() {
    if (instance == null) {
      instance = new TimelineClient();
    }

    return instance;
  }

  public List<Story> getStoryList(Context context, int userId) {
    // TODO: Use userId to query db; may not need context param anymore

    JsonArray jsonArray = createMockJsonArray(context, "stories.json");
    if (jsonArray != null) {
      List<Story> storyList = Story.fromJsonArray(jsonArray);
      return storyList;
    } else {
      return null;
    }
  }

  public List<Moment> getMomentsList(Context context, int storyId) {
    // TODO: Use storyId to query db; may not need context param anymore
    JsonArray jsonArray = createMockJsonArray(context, "moments.json");
    if (jsonArray != null) {
      List<Moment> momentList = Moment.fromJsonArray(jsonArray);
      return momentList;
    } else {
      return null;
    }
  }

  // TEST: Create mock response
  protected JsonArray createMockJsonArray(Context context, String jsonFileName) {
    JsonArray jsonArray = null;

    try {
      InputStream is = context.getAssets().open(jsonFileName);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();

      String mockResponse = new String(buffer, "UTF-8");
      JsonParser jsonParser = new JsonParser();
      jsonArray = (JsonArray) jsonParser.parse(mockResponse);
    } catch (IOException ex) {
      ex.printStackTrace();
      jsonArray = null;
    }

    return jsonArray;
  }
}
