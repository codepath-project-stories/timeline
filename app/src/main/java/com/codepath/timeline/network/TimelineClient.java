package com.codepath.timeline.network;

import android.content.Context;
import android.util.Log;

import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

  // other class can implement TimelineClientGetStoryListener
  // or, simply new TimelineClient.TimelineClientGetStoryListener() as callback
  public interface TimelineClientGetStoryListener {
    void onGetStoryList(List<Story> itemList); // this is a callback
  }

  public void getStoryList(Context context,
                           ParseUser user,
                           final TimelineClientGetStoryListener timelineClientGetStoryListener) {
    // Define the class we would like to query
    ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
    // Define our query conditions
    query.whereEqualTo("owner", user);
    // Execute the find asynchronously
    query.findInBackground(new FindCallback<Story>() {
      @Override
      public void done(List<Story> itemList, ParseException e) {
        if (e == null) {
          if (itemList != null && itemList.size() > 0) {
            Log.d("findInBackground", itemList.get(0).getObjectId());
            // Access the array of results here
            if (timelineClientGetStoryListener != null) {
              timelineClientGetStoryListener.onGetStoryList(itemList); // use callback
            }
          }
        } else {
          Log.d("findInBackground", "Error: " + e.getMessage());
        }
      }
    });
    return;
  }

  public List<Story> getMockStoryList(Context context) {

    JsonArray jsonArray = createMockJsonArray(context, "stories.json");
    if (jsonArray != null) {
      List<Story> storyList = Story.fromJsonArray(jsonArray);
      for (Story theStory : storyList) {
        theStory.setOwner(ParseUser.getCurrentUser());
        theStory.saveInBackground();
      }
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
