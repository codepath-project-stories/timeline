package com.codepath.timeline.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.activities.TimelineActivity;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.AppConstants;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class StoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = StoriesAdapter.class.getSimpleName();

    private List<Story> mStories;
    private Context context;
    private final int SIMPLE = 0;
    String type;

    public StoriesAdapter(List<Story> stories, FragmentManager fragMan) {
        this.mStories = stories;
        type = fragMan.toString();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // later here will be different types of tweets (with image/video or simple text)
        switch (viewType) {
            case SIMPLE:
                View view1 = inflater.inflate(R.layout.viewholder_simple_story, viewGroup, false);
                viewHolder = new ViewHolderSimpleStory(view1);
                break;
            default:
                View view2 = inflater.inflate(R.layout.viewholder_simple_story, viewGroup, false);
                viewHolder = new ViewHolderSimpleStory(view2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case SIMPLE:
                ViewHolderSimpleStory vh1 = (ViewHolderSimpleStory) viewHolder;
                configureViewHolderSimpleStory(vh1, position);
                break;
            default:
                ViewHolderSimpleStory vh2 = (ViewHolderSimpleStory) viewHolder;
                configureViewHolderSimpleStory(vh2, position);
                break;
        }
    }

    private void configureViewHolderSimpleStory(final StoriesAdapter.ViewHolderSimpleStory holder, final int position) {
        final Story story = mStories.get(position);
        Log.d(TAG, story.toString());

        holder.rlMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TimelineActivity.class);
                Story story = mStories.get(position);
                i.putExtra(AppConstants.OBJECT_ID, story.getObjectId());
                i.putExtra("code", type);
                i.putExtra(AppConstants.STORY_TITLE, story.getTitle());
                i.putExtra(AppConstants.STORY_BACKGROUND_IMAGE_URL, story.getBackgroundImageUrl());
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (Activity) context,
                                holder.ivBackgroundImage,
                                "background"
                        );
                context.startActivity(i, options.toBundle());
            }
        });

        ParseUser owner = story.getOwner();
        if (owner != null) {
            updateOwnerDetails(holder, owner);
        } else {
            Log.d(TAG, "Story owner is NULL");
        }

        updateStoryDetails(holder, story);
        updateCollaboratorDetails(holder, story.getCollaboratorList());
    }

    private void updateOwnerDetails(final StoriesAdapter.ViewHolderSimpleStory holder, ParseUser owner) {
        String ownerImageUrl = UserClient.getProfileImageUrl(owner);
        // Update the profile_icon image only if they have one set
        if (ownerImageUrl != null && !ownerImageUrl.isEmpty()) {
            holder.ivAuthorProfilePhoto.setVisibility(View.VISIBLE);
            Glide.with(context).load(ownerImageUrl)
                    .fitCenter()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.ivAuthorProfilePhoto);
        } else {
            holder.ivAuthorProfilePhoto.setVisibility(View.GONE);
        }

        String name = owner.getString("name");
        if (name != null) {
            holder.tvStoryAuthor.setText("By " + name);
        }
    }

    private void updateStoryDetails(StoriesAdapter.ViewHolderSimpleStory holder, Story story) {
        holder.tvStoryTitle.setText(story.getTitle());
        Glide.with(context)
                .load(story.getBackgroundImageUrl())
                .centerCrop()
                .into(holder.ivBackgroundImage);

        // TODO: Update the number of moments
        holder.tvMomentCount.setText("30" + " Moments");
    }

    private void updateCollaboratorDetails(StoriesAdapter.ViewHolderSimpleStory holder, List<ParseUser> collaboratorList) {
        // Hide the collaborator list by default
        holder.ivCollaborator1.setVisibility(View.INVISIBLE);
        holder.ivCollaborator2.setVisibility(View.INVISIBLE);
        holder.tvUserCount.setVisibility(View.INVISIBLE);

        if (collaboratorList != null) {
            if (collaboratorList.size() >= 1) {
                holder.ivCollaborator2.setVisibility(View.VISIBLE);
                ParseUser user1 = collaboratorList.get(0);
                String user1ProfileImageUrl = UserClient.getProfileImageUrl(user1);
                if (user1ProfileImageUrl != null || !user1ProfileImageUrl.isEmpty()) {
                    Glide.with(context).load(user1ProfileImageUrl)
                            .fitCenter()
                            .bitmapTransform(new CropCircleTransformation(context))
                            .into(holder.ivCollaborator2);
                }
            }

            if (collaboratorList.size() >= 2) {
                holder.ivCollaborator1.setVisibility(View.VISIBLE);
                ParseUser user2 = collaboratorList.get(1);
                String user2ProfileImageUrl = UserClient.getProfileImageUrl(user2);
                if (user2ProfileImageUrl != null || !user2ProfileImageUrl.isEmpty()) {
                    Glide.with(context).load(user2ProfileImageUrl)
                            .fitCenter()
                            .bitmapTransform(new CropCircleTransformation(context))
                            .into(holder.ivCollaborator1);
                }
            }

            if (collaboratorList.size() >= 3) {
                holder.tvUserCount.setVisibility(View.VISIBLE);
                holder.tvUserCount.setText("+" + String.valueOf(collaboratorList.size() - 2));
            }

        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return this.mStories.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling
    @Override
    public int getItemViewType(int position) {
        return SIMPLE;
    }

    public static class ViewHolderSimpleStory extends RecyclerView.ViewHolder {

        @BindView(R.id.rlMainView)
        RelativeLayout rlMainView;
        @BindView(R.id.ivBackgroundImage)
        ImageView ivBackgroundImage;
        @BindView(R.id.ivAuthorProfilePhoto)
        ImageView ivAuthorProfilePhoto;
        @BindView(R.id.tvStoryTitle)
        TextView tvStoryTitle;
        @BindView(R.id.tvMomentCount)
        TextView tvMomentCount;
        @BindView(R.id.tvStoryAuthor)
        TextView tvStoryAuthor;
        @BindView(R.id.tvUserCount)
        TextView tvUserCount;
        @BindView(R.id.ivCollaborator1)
        ImageView ivCollaborator1;
        @BindView(R.id.ivCollaborator2)
        ImageView ivCollaborator2;

        public ViewHolderSimpleStory(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
