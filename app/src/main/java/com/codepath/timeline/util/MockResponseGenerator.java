package com.codepath.timeline.util;

import android.util.Log;

import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.ParseApplication;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.network.UserClient;
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

    private void createMockStoryList() {
        mStoryList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String dummyDate = "January " + i + ", 2016";
            Story story = new Story(
                    "Story " + i,
                    "http://pbs.twimg.com/media/CpdUcQcWAAAwgwJ.jpg",
                    "January " + i + ", 2016"
            );
            mStoryList.add(story);
        }
        if (ParseApplication.TURN_ON_PARSE) {
            TimelineClient.getInstance().addStoryList(mStoryList, null);
        }
    }

    private void createMockMomentList() {
        mMomentList = new ArrayList<>();

        for (int i = 10; i < 21; i++) {
            String dummyDate = "2016-08-" + i + "T19:22:54.695Z";

            String mediaUrl = getRandomMedia();
            Moment moment = new Moment(dummyDate, "Moment " + i + ": something, something, something, something",
                    mediaUrl, UserClient.getCurrentUser(), "San Francisco, CA");

      /*
      if (i % 5 == 0) {
        Comment comment = new Comment();
        comment.setAuthor(UserClient.getCurrentUser());
        comment.setCreatedAtReal("2016-09-31T19:22:54.695Z");
        comment.setBody("Looking really, really good");
        comment.setId(8000 + i);
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        moment.setCommentList(commentList);
      }
*/

            mMomentList.add(moment);
        }
    }

    public List<Moment> getMomentList() {
        createMockMomentList();
        return mMomentList;
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
