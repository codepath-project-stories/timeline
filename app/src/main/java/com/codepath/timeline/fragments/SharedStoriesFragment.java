package com.codepath.timeline.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;

public class SharedStoriesFragment extends BaseStoryModelFragment {

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
    protected void populateList() {}
}
