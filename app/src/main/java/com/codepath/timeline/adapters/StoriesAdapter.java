package com.codepath.timeline.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.activities.TimelineActivity;
import com.codepath.timeline.models.Story;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Story> mStories;
    private Context context;
    private final int SIMPLE = 0;

    public StoriesAdapter(List<Story> stories) {
        this.mStories = stories;
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
        switch(viewHolder.getItemViewType()) {
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
        Log.d("DEBUG", story.toString());
        holder.tvStoryTitle.setText("Fun times");
        Glide.with(context)
                .load(R.drawable.image_test2)
                .into(holder.ivBackgroundImage);

        Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                // access palette colors here
                Palette.Swatch swatch = palette.getVibrantSwatch();
                // Gets an appropriate title text color
                if (swatch != null) {
                    // If we have a vibrant color
                    // update the title TextView
                    holder.tvStoryTitle.setBackgroundColor(
                            swatch.getBodyTextColor());
                }
            }
        };

        final Bitmap myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_test2);
        if (myBitmap != null && !myBitmap.isRecycled()) {
            Palette.from(myBitmap).generate(paletteListener);
        }

        holder.ivBackgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TimelineActivity.class);
                Story story = mStories.get(position);
                i.putExtra("story", Parcels.wrap(story));
                i.putExtra("imageUrl", story.getBackgroundImageUrl());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, v, "background");
                context.startActivity(i, options.toBundle());
            }
        });
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

        @BindView(R.id.ivBackgroundImage) ImageView ivBackgroundImage;
        @BindView(R.id.tvStoryTitle) TextView tvStoryTitle;

        public ViewHolderSimpleStory(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
