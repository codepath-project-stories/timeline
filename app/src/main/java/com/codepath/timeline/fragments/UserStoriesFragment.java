package com.codepath.timeline.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.codepath.timeline.models.Story;
import com.codepath.timeline.util.MockResponseGenerator;

import java.util.ArrayList;

public class UserStoriesFragment extends BaseStoryModelFragment {

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
        stories = (ArrayList<Story>) MockResponseGenerator.getInstance().getStoryList();
        addAll(stories);
    }
}
