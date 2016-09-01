package com.codepath.timeline.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import com.codepath.timeline.models.Comment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.view.BitmapScaler;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Network client for getting response from the server
 */
public class TimelineClient {
  private static final String TAG = "TimelineLog:" + TimelineClient.class.getSimpleName();

  private ParseQuery<Moment> mMomentDetailQuery;
  private ParseQuery<Story> mStoryListQuery;
  private ParseQuery<Story> mMomentListQuery;

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

  // TODO: CACHE_ELSE_NETWORK
  // TODO: use a callback class instead of the following
  // other class can implement XXXListener
  // or, simply new TimelineClient.XXXListener() as callback

  public interface TimelineClientAddStoryListener {
    void onAddStoryList();
  }

  public interface TimelineClientGetFriendListListener {
    void onGetFriendList(List<ParseUser> itemList);
  }

  public interface TimelineClientGetStoryListener {
    void onGetStoryList(List<Story> itemList);
  }

  public interface TimelineClientGetMomentListListener {
    void onGetMomentList(List<Moment> itemList);
  }

  public interface TimelineClientGetUserListener {
    void onGetUserList(List<ParseUser> itemList);
  }

  public interface TimelineClientGetMomentListener {
    void onGetMomentListener(Moment moment);
  }

  public interface TimelineClientUploadFileListener {
    void onUploadFileListener(ParseFile file);
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
            ParseUser currentUser = UserClient.getCurrentUser();
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

  // query User table
  public void getStoryList(ParseUser user,
                           final TimelineClientGetStoryListener timelineClientGetStoryListener) {
    ParseQuery<ParseUser> query = ParseUser.getQuery();
    // http://parseplatform.github.io/docs/android/guide
    // fetchifneeded() could be an alternative to include()
    query.include("stories");
    // TODO: debug if this API can get 'owner' and 'collaboratorList' in "stories"
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
    mStoryListQuery = ParseQuery.getQuery(Story.class);
    mStoryListQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    mStoryListQuery.whereEqualTo("owner", user);
    mStoryListQuery.include("owner"); // eagerly load the owner -- we need it for updating the story view
    mStoryListQuery.include("collaboratorList");
    mStoryListQuery.findInBackground(new FindCallback<Story>() {
      @Override
      public void done(List<Story> itemList, ParseException e) {
        if (e != null) {
          Log.e(TAG, "Exception from getStoryList2: " + e.getMessage());
          return;
        }

        if (itemList != null) {
          Log.d(TAG, "Success getStoryList2");
          if (timelineClientGetStoryListener != null) {
            timelineClientGetStoryListener.onGetStoryList(itemList); // use callback
          }
        }
      }
    });
  }

  public void addStory(final Story story, ParseFile file) {
    story.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e != null) {
          Log.e(TAG, "Exception from adding story: " + e.getMessage());
          return;
        }

        ParseUser user = UserClient.getCurrentUser();
        if (user != null) {
          user.add("stories", story);
          user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
              if (e != null) {
                Log.e(TAG, "Exception from saving user story: " + e.getMessage());
                return;
              }

              if (mStoryListQuery.hasCachedResult()) {
                Log.d(TAG, "Clearing user story cache");
                mStoryListQuery.clearCachedResult();
              }
              Log.d(TAG, "Successfully saved story");
            }
          });
        }
      }
    });
  }

  public void uploadFile(String fileName, String photoUri, final TimelineClientUploadFileListener uploadFileListener) {
    try {
      Bitmap rawTakenImage = BitmapFactory.decodeFile(photoUri);
      Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 700);
      // Configure byte output stream
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      // Compress the image further
      resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
      // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
      String path = photoUri + "_resized";
      Log.d(TAG, "Photo path: " + path);
      File resizedFile = new File(path);

      resizedFile.createNewFile();
      FileOutputStream fos = new FileOutputStream(resizedFile);
// Write the bytes of the bitmap to file
      byte[] imageByte = bytes.toByteArray();

    /*
    // Convert it to byte
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    // Compress image to lower quality scale 1 - 100
    rawTakenImage.compress(Bitmap.CompressFormat.PNG, 40, stream);
    byte[] imageByte = stream.toByteArray();
*/

      final ParseFile file = new ParseFile(fileName, imageByte);
      file.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
          if (e != null) {
            Log.e(TAG, "Exception uploading file: " + e.getMessage());
            return;
          }

          Log.d(TAG, "Success uploadFile");
          if (uploadFileListener != null) {
            uploadFileListener.onUploadFileListener(file);
          }
        }
      });

      fos.write(imageByte);
      fos.close();
    } catch (IOException e) {
      Log.e(TAG, "uploadFile IOException" + e.getMessage());
    }
  }

  // Query the DB for moments associated with this story
  public void getMomentList(String storyObjectId, final TimelineClientGetMomentListListener timelineClientGetMomentListListener) {
    mMomentListQuery = ParseQuery.getQuery(Story.class);
    mMomentListQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    mMomentListQuery.include("momentList");
    mMomentListQuery.include("momentList.author");
    mMomentListQuery.getInBackground(storyObjectId, new GetCallback<Story>() {
      @Override
      public void done(Story story, ParseException e) {
        if (e != null) {
          Log.e(TAG, "Exception from getMomentList: " + e.getMessage());
          return;
        }

        if (story != null && story.getMomentList() != null) {
          Log.d(TAG, "Success getMomentList");
          if (timelineClientGetMomentListListener != null) {
            timelineClientGetMomentListListener.onGetMomentList(story.getMomentList());
          }
        }
      }
    });
  }

  public void getMoment(String momentObjectId, final TimelineClientGetMomentListener timelineClientGetMomentListener) {
    ParseQuery<Moment> query = ParseQuery.getQuery(Moment.class);
    query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    query.include("author");
    query.getInBackground(momentObjectId, new GetCallback<Moment>() {
      @Override
      public void done(Moment moment, ParseException e) {
        if (e != null) {
          Log.e(TAG, "Exception from getMoment: " + e.getMessage());
          return;
        }

        if (moment != null) {
          Log.d(TAG, "Success getMoment: " + moment);
          if (timelineClientGetMomentListener != null) {
            timelineClientGetMomentListener.onGetMomentListener(moment);
          }
        }
      }
    });
  }

  // Includes moment details + commentList
  public void getMomentDetails(String momentObjectId, final TimelineClientGetMomentListener timelineClientGetMomentListener) {
    mMomentDetailQuery = ParseQuery.getQuery(Moment.class);
    mMomentDetailQuery.include("author");
    mMomentDetailQuery.include("commentList");
    mMomentDetailQuery.include("commentList.author");
    mMomentDetailQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
    mMomentDetailQuery.getInBackground(momentObjectId, new GetCallback<Moment>() {
      @Override
      public void done(Moment moment, ParseException e) {
        if (e != null) {
          Log.e(TAG, "Exception from getMomentDetails: " + e.getMessage());
          return;
        }

        if (moment != null) {
          Log.d(TAG, "Success getMomentDetails: " + moment);
          if (timelineClientGetMomentListener != null) {
            timelineClientGetMomentListener.onGetMomentListener(moment);
          }
        }
      }
    });
  }

  public void addComment(final Moment moment, final Comment comment) {
    comment.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e != null) {
          Log.e(TAG, "Exception from saving comment: " + e.getMessage());
          return;
        }

        Log.d(TAG, "Success addComment");
        // Update the moment after saving the comment
        moment.add("commentList", comment);
        moment.saveInBackground(new SaveCallback() {
          @Override
          public void done(ParseException e) {
            if (e != null) {
              Log.e(TAG, "Exception from saving moment: " + e.getMessage());
              return;
            }

            // Clear cached result for the moment list
            if (mMomentDetailQuery.hasCachedResult()) {
              Log.d(TAG, "Clearing moment detail cache");
              mMomentDetailQuery.clearCachedResult();
            }
            Log.d(TAG, "Successfully saved moment");
          }
        });
      }
    });
  }

  public void addMoment(final Moment moment, final String storyObjectId) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      Log.d(TAG, "MAIN thread inside addMoment");
    }

    moment.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e != null) {
          Log.e(TAG, "Exception from adding moment: " + e.getMessage());
          return;
        }

        Log.d(TAG, "Successfully added moment");
        Log.d(TAG, "Adding moment to story: " + storyObjectId);
        ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
        query.getInBackground(storyObjectId, new GetCallback<Story>() {
          @Override
          public void done(Story story, ParseException e) {
            if (e != null) {
              Log.e(TAG, "Exception from fetching story: " + e.getMessage());
              return;
            }

            if (Looper.myLooper() == Looper.getMainLooper()) {
              Log.d(TAG, "MAIN thread inside getStory");
            }

            Log.d(TAG, "Successfully fetched story");
            story.add("momentList", moment);
            story.saveInBackground(new SaveCallback() {
              @Override
              public void done(ParseException e) {
                if (e != null) {
                  Log.e(TAG, "Exception from saving moment: " + e.getMessage());
                  return;
                }

                if (Looper.myLooper() == Looper.getMainLooper()) {
                  Log.d(TAG, "MAIN thread inside saveStory");
                }

                // Clear cached result for the moment list
                if (mMomentListQuery.hasCachedResult()) {
                  Log.d(TAG, "Clearing moment list query");
                  mMomentListQuery.clearCachedResult();
                }
                Log.d(TAG, "Successfully saved moment");
              }
            });
          }
        });
      }
    });
  }

  // query User table
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

  // TODO: now is get all users. need to change to get friend list.
  public void getFriendList(ParseUser user,
                            final TimelineClientGetFriendListListener onGetFriendList) {
    ParseQuery<ParseUser> query = ParseUser.getQuery();
    // http://parseplatform.github.io/docs/android/guide
    // fetchifneeded() could be an alternative to include()
    query.findInBackground(
        new FindCallback<ParseUser>() {
          @Override
          public void done(List<ParseUser> userList, ParseException e) {
            if (e == null) {
              if (userList != null) {
                Log.d("findInBackground", "!= null");
                if (onGetFriendList != null) {
                  onGetFriendList.onGetFriendList(userList);
                }
              }
            } else {
              Log.d("findInBackground", "Error: " + e.getMessage());
            }
          }
        });
  }

  public void getSharedStoryList(ParseUser user,
                                 final TimelineClientGetStoryListener timelineClientGetStoryListener) {
    ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
    // http://parseplatform.github.io/docs/android/guide
    // fetchifneeded() could be an alternative to include()
    query.whereEqualTo("collaboratorList", user);
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
