package com.codepath.timeline.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.codepath.timeline.R;

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
        // Todo: add functionality for quering the list of friends' stories and displaying
    }
}
