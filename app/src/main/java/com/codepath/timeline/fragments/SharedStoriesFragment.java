package com.codepath.timeline.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import com.codepath.timeline.R;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.ParseApplication;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.BindView;

public class SharedStoriesFragment extends BaseStoryModelFragment {

    @BindView(R.id.addBtn)
    FloatingActionButton add;

    // newInstance constructor for creating fragment with arguments
    public static SharedStoriesFragment newInstance(int page) {
        SharedStoriesFragment frag = new SharedStoriesFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void populateList() {

        // remove floating button from this view, only implemented for adding a moment to friend's story
        add.setVisibility(View.GONE);

        // Todo: change functionality to quering the list of friends' stories and displaying
        if (!ParseApplication.TURN_ON_PARSE) {
            addAll(TimelineClient.getInstance().getMockStoryList(getContext()));
            return;
        }

        final ParseUser currentUser = UserClient.getCurrentUser();
        String demoCreatedString = (String) currentUser.get("demoCreated");
        boolean demoCreated = demoCreatedString != null && demoCreatedString.equals("true");
        if (!ParseApplication.DEMO_MODE || (ParseApplication.DEMO_MODE && demoCreated)) {
            // demo already created
            // fetch stories from Parse server
            Log.d("populateList", "getStoryList");
            TimelineClient.getInstance().getStoryList2(
                    currentUser,
                    // set up callback
                    new TimelineClient.TimelineClientGetStoryListener() {
                        @Override
                        public void onGetStoryList(List<Story> itemList) {
                            if (itemList != null) {
                                addAll(itemList);
                            }
                        }
                    }
            );
        } else if (ParseApplication.DEMO_MODE && !demoCreated) {
            // demo not created yet
            // create fake mock stories
            Log.d("populateList", "getMockStoryList");
            List<Story> storyList = TimelineClient.getInstance().getMockStoryList(getActivity());
            addAll(storyList);
            TimelineClient.getInstance().addStoryList(storyList,
                    new TimelineClient.TimelineClientAddStoryListener() {
                        @Override
                        public void onAddStoryList() {
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
                    });
        }
    }
}
