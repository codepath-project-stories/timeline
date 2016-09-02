package com.codepath.timeline.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.codepath.timeline.R;
import com.codepath.timeline.activities.NewStoryActivity;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.DateUtil;
import com.codepath.timeline.util.ParseApplication;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class UserStoriesFragment extends BaseStoryModelFragment {
    // UserStoriesFragment extends BaseStoryModelFragment
    // BaseStoryModelFragment calls TimelineActivity
    private static final String TAG = UserStoriesFragment.class.getSimpleName();

    @BindView(R.id.addBtn)
    com.github.clans.fab.FloatingActionButton add;
    private int REQUEST_CODE = 5;

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

        // start custom progress bar
        startAnim();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewStoryActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

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
                            if(itemList != null) {
                                // Sort by newly updated story on top of the list
                                Collections.sort(itemList);
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
                new TimelineClient.TimelineClientAddStoryListener(){
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

        // stop custom progress bar
        stopAnim();
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
            addStory(story);
        }
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
