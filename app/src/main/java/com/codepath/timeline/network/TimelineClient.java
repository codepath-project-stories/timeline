package com.codepath.timeline.network;

import android.content.Context;
import android.util.Log;

import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

  public List<Story> getMockStoryList(Context context) {
    JsonArray jsonArray = createMockJsonArray(context, "stories.json");
    if (jsonArray != null) {
      List<Story> storyList = Story.fromJsonArray(jsonArray);
      return storyList;
    } else {
      return null;
    }
  }

  // TODO: use a callback class instead of the following
  // other class can implement XXXListener
  // or, simply new TimelineClient.XXXListener() as callback

  public interface TimelineClientAddStoryListener {
    void onAddStoryList(); // this is a callback
  }

  public interface TimelineClientGetStoryListener {
    void onGetStoryList(List<Story> itemList); // this is a callback
  }

  public interface TimelineClientGetMomentListener {
    void onGetMomentList(List<Moment> itemList); // this is a callback
  }

  public interface TimelineClientGetUserListener {
    void onGetUserList(List<ParseUser> itemList); // this is a callback
  }

  public void addStoryList(final List<Story> storyList,
                           final TimelineClientAddStoryListener timelineClientAddStoryListener) {
    if (storyList.size() > 0) {
      Log.d("saveToParse", "storyList.size() > 0");
      ParseObject.saveAllInBackground(storyList, new SaveCallback() {
        @Override
        public void done(ParseException e) {
          Log.d("saveToParse", "saveAllInBackground done");
          if (e != null) {
            Log.d("saveToParse", e.toString());
          } else {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.addAll("stories", storyList);
            currentUser.saveInBackground(
                new SaveCallback() {
                  @Override
                  public void done(ParseException e) {
                    Log.d("saveToParse", "saveInBackground done");
                    if (e != null) {
                      Log.d("saveToParse", e.toString());
                    } else {
                      if (timelineClientAddStoryListener != null) {
                        timelineClientAddStoryListener.onAddStoryList(); // use callback
                      }
                    }
                  }
                }
            );
          }
        }
      });
    }
  }

  // Used for populating mock data
  public void addMomentList(Story story, List<Moment> momentList) {
    if (momentList != null && momentList.size() > 0) {
      story.addAll("momentList", momentList);
      story.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
          if (e != null) {
            Log.e(TAG, "addMomentList exception: " + e.toString());
          } else {
            Log.d(TAG, "addMomentList successful");
          }
        }
      });
    }
  }

  // query User_Temp table
  public void getStoryList(ParseUser user,
                           final TimelineClientGetStoryListener timelineClientGetStoryListener) {
    ParseQuery<ParseUser> query = ParseUser.getQuery();
    // http://parseplatform.github.io/docs/android/guide
    // fetchifneeded() could be an alternative to include()
    query.include("stories");
    query.getInBackground(
        user.getObjectId(),
        new GetCallback<ParseUser>() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (e == null) {
              if (user != null) {
                Log.d("findInBackground", user.getObjectId());
                if (timelineClientGetStoryListener != null) {
                  timelineClientGetStoryListener.onGetStoryList(
                      (ArrayList<Story>) user.get("stories")
                  ); // use callback
                }
              }
            } else {
              Log.d("findInBackground", "Error: " + e.getMessage());
            }
          }
        });
  }

  // DIANNE: Decided to use this API instead so I can include the 'owner' and 'collaboratorList'
  // for the story list in the LandingActivity
  public void getStoryList2(ParseUser user,
                            final TimelineClientGetStoryListener timelineClientGetStoryListener) {
    // Define the class we would like to query
    ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
    // Define our query conditions
    query.whereEqualTo("owner", user);
    query.include("owner"); // eagerly load the owner -- we need it for updating the story view
    query.include("collaboratorList");
    // Execute the find asynchronously
    query.findInBackground(new FindCallback<Story>() {
      @Override
      public void done(List<Story> itemList, ParseException e) {
        if (e == null) {
          if (itemList != null) {
            Log.d("findInBackground", Integer.toString(itemList.size()));
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
  }

  // Query the DB for moments associated with this story
  public void getMomentList(String storyObjectId, final TimelineClientGetMomentListener timelineClientGetMomentListener) {
    ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
    query.include("momentList");
    query.getInBackground(storyObjectId, new GetCallback<Story>() {
      @Override
      public void done(Story story, ParseException e) {
        if (e != null) {
          Log.e(TAG, "Exception from getMomentList: " + e.getMessage());
          return;
        }
        if (story != null && story.getMomentList() != null) {
          if (timelineClientGetMomentListener != null) {
            timelineClientGetMomentListener.onGetMomentList(story.getMomentList());
          }
        }
      }
    });
  }

  // query User_Temp table
  public void getUserList(Story story,
                          final TimelineClientGetUserListener timelineClientGetUserListener) {
    ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
    // http://parseplatform.github.io/docs/android/guide
    // fetchifneeded() could be an alternative to include()
    query.include("collaboratorList");
    query.getInBackground(
        story.getObjectId(),
        new GetCallback<Story>() {
          @Override
          public void done(Story story, ParseException e) {
            if (e == null) {
              if (story != null) {
                Log.d("findInBackground", story.getObjectId());
                if (timelineClientGetUserListener != null) {
                  timelineClientGetUserListener.onGetUserList(
                      (ArrayList<ParseUser>) story.get("collaboratorList")
                  ); // use callback
                }
              }
            } else {
              Log.d("findInBackground", "Error: " + e.getMessage());
            }
          }
        });
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
