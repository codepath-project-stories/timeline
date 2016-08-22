package com.codepath.timeline.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.TimelineClient;

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

        List<Story> stories = TimelineClient.getInstance().getMockStoryList(getContext());
        addAll(stories);

//        ParseUser currentUser = ParseUser.getCurrentUser();
//        String demoCreatedString = (String) currentUser.get("demoCreated");
//        boolean demoCreated = demoCreatedString != null && demoCreatedString.equals("true");
//        if (!ParseApplication.DEMO_MODE || (ParseApplication.DEMO_MODE && demoCreated)) {
//            // demo already created
//            // fetch stories from Parse server
//            Log.d("populateList", "getStoryList");
//            TimelineClient.getInstance().getStoryList(
//                    getActivity(),
//                    currentUser,
//                    // set up callback
//                    new TimelineClient.TimelineClientGetStoryListener() {
//                        @Override
//                        public void onGetStoryList(List<Story> itemList) {
//                            addAll(itemList);
//                        }
//                    });
//        }
//        else if (ParseApplication.DEMO_MODE && !demoCreated) {
//            // demo not created yet
//            // create fake mock stories
//            Log.d("populateList", "getMockStoryList");
//            addAll(TimelineClient.getInstance().getMockStoryList(getActivity()));
//            currentUser.put("demoCreated", "true");
//            currentUser.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        Log.d("demoCreated", "done success");
//                    } else {
//                        // Sign up didn't succeed. Look at the ParseException
//                        // to figure out what went wrong
//                        Log.d("demoCreated", "done failed");
//                        Log.d("demoCreated", e.toString());
//                    }
//                }
//            });
//        }


    }
}
