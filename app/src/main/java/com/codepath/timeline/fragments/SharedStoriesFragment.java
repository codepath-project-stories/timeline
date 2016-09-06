package com.codepath.timeline.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.codepath.timeline.R;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.models.UserClient;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;

public class SharedStoriesFragment extends BaseStoryModelFragment {

    private static final String TAG = SharedStoriesFragment.class.getSimpleName();

    @BindView(R.id.addBtn)
    com.github.clans.fab.FloatingActionButton add;

    // newInstance constructor for creating fragment with arguments
    public static SharedStoriesFragment newInstance(Context context, int page) {
        SharedStoriesFragment frag = new SharedStoriesFragment();
        frag.context = context;
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

        // start custom progress bar
//        startAnim();

        // remove floating button from this view, only implemented for adding a moment to friend's story
        add.setVisibility(View.GONE);

        final ParseUser currentUser = UserClient.getCurrentUser();
        TimelineClient.getInstance().getSharedStoryList(
                currentUser,
                // set up callback
                new TimelineClient.TimelineClientGetStoryListener() {
                    @Override
                    public void onGetStoryListSuccess(List<Story> itemList) {
                        if (itemList != null) {
                            Log.d(TAG, "populateList");
                            addAll(itemList);
                        }

                        // stop custom progress bar
                        stopAnim();
                    }

                    @Override
                    public void onGetStoryListFailed(String message) {
//                        stopAnim();
                    }
                }
        );
    }
}
