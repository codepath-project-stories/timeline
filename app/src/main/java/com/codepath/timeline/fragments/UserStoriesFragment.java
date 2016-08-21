package com.codepath.timeline.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.TimelineClient;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class UserStoriesFragment extends BaseStoryModelFragment {
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment calls TimelineActivity

    // newInstance constructor for creating fragment with arguments
    public static UserStoriesFragment newInstance(int page) {
        UserStoriesFragment frag = new UserStoriesFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // TODO: production needs to remove demo things
    @Override
    protected void populateList() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String demoCreated = (String) currentUser.get("demoCreated");
        if (demoCreated != null && demoCreated.equals("true")) {
            // demo already created
            // fetech stories from Parse server
            Log.d("populateList", "getStoryList");
            TimelineClient.getInstance().getStoryList(
                    getActivity(),
                    currentUser,
                    // set up callback
                    new TimelineClient.TimelineClientGetStoryListener() {
                        @Override
                        public void onGetStoryList(List<Story> itemList) {
                            addAll(itemList);
                        }
                    });
        }
        else {
            // TODO: production needs to remove demo things
            // demo not created yet
            // create fake mock stories
            Log.d("populateList", "getMockStoryList");
            addAll(TimelineClient.getInstance().getMockStoryList(getActivity()));
            currentUser.put("demoCreated", "true");
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("demoCreated", "done success");
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        Log.d("demoCreated", "done failed");
                        Log.d("demoCreated", e.toString());
                    }
                }
            });
        }
    }
}
