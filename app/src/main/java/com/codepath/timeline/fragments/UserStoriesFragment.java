package com.codepath.timeline.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.codepath.timeline.R;
import com.codepath.timeline.activities.NewStoryActivity;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.models.UserClient;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.DateUtil;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class UserStoriesFragment extends BaseStoryModelFragment {
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment calls TimelineActivity
    private static final String TAG = "TimelineLog: " + UserStoriesFragment.class.getSimpleName();

    @BindView(R.id.addBtn)
    com.github.clans.fab.FloatingActionButton add;
    private int REQUEST_CODE = 5;


    // newInstance constructor for creating fragment with arguments
    public static UserStoriesFragment newInstance(Context context, int page) {
        UserStoriesFragment frag = new UserStoriesFragment();
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startAnim();
    }

    @Override
    protected void populateList() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewStoryActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        ParseUser currentUser = UserClient.getCurrentUser();
        if (currentUser != null) {
            TimelineClient.getInstance().getStoryList2(
                    currentUser,
                    // set up callback
                    new TimelineClient.TimelineClientGetStoryListener() {
                        @Override
                        public void onGetStoryListSuccess(List<Story> itemList) {
                            if (itemList != null) {
                                Log.d(TAG, "populateList from UserStoriesFragment");
                                // Sort by newly updated story on top of the list
                                Collections.sort(itemList);
                                addAll(itemList);
                            }

                            // stop custom progress bar
                            stopAnim();
                        }

                        @Override
                        public void onGetStoryListFailed(String message) {
                            Log.d(TAG, "Error fetching story list: " + message);
                            // TODO: Since the network request is set to CACHE_THEN_NETWORK, it fails the first time due
                            // to "results not cached", but should succeed the second time
                            if (!message.toLowerCase().contains("results not cached")) {
                                stopAnim();
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == REQUEST_CODE && resultCode == 1) {
            Story story = new Story();
            story.setCreatedAtReal(DateUtil.getCurrentDate());
            story.setTitle(data.getStringExtra(AppConstants.STORY_TITLE));
            story.setTempPhotoUri(data.getStringExtra(AppConstants.STORY_BACKGROUND_IMAGE_URL));
            story.setOwner(UserClient.getCurrentUser());

            ArrayList<String> collabObjectIdList = data.getStringArrayListExtra(AppConstants.STORY_COLLABORATOR_LIST);
            if (collabObjectIdList != null) {
                fetchCollabList(collabObjectIdList, story);
            }
        }
    }

    private void fetchCollabList(ArrayList<String> collabObjectIdList, final Story story) {
        TimelineClient.getInstance().getUserListByIds(collabObjectIdList, new TimelineClient.TimelineClientGetUserListListener() {
            @Override
            public void onGetUserList(List<ParseUser> userList) {
                story.setCollaboratorList(userList);
                addStory(story);
            }
        });
    }

    private void addStory(final Story story) {
        TimelineClient.getInstance().uploadFile("photo.jpg", story.getTempPhotoUri(), new TimelineClient.TimelineClientUploadFileListener() {
            @Override
            public void onUploadFileListener(ParseFile file) {
                story.setBackgroundImageMedia(file);
                story.setBackgroundImageUrl(file.getUrl());
                TimelineClient.getInstance().addStory(story);
            }
        });

        Log.d(TAG, "Adding new story to recyclerview");
        addNew(story);
    }
}
