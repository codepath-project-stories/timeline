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

    @Override
    protected void populateList() {
        // TODO: Change the storyId when making network request
        List<Story> stories = TimelineClient.getInstance().getStoryList(getActivity(), -1);
        addAll(stories);
    }
}
