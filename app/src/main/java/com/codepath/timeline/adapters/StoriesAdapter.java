package com.codepath.timeline.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.models.Story;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class StoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Story> mStories;
    Context context;
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

    private void configureViewHolderSimpleStory(StoriesAdapter.ViewHolderSimpleStory holder, int position) {
        Story story = mStories.get(position);
        Log.d("DEBUG", story.toString());
        if (story != null) {
            holder.getTvStoryTitle().setText(story.getStoryTitle());
            Glide.with(context).load(story.getBackgroundImageUrl())
                    .fitCenter()
                    .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                    .into(holder.getIvBackgroundImage());
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
        // later here will be different types of tweets (with image/video or simple text)
//        Story inspect = mStories.get(position);
        return SIMPLE;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] parts = relativeDate.split(" ");
        if (parts.length <= 1) {
            return relativeDate;
        }
        else {
            String dateToShow = parts[0] + parts[1].charAt(0);
            return dateToShow;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ViewHolderSimpleStory extends ViewHolder {

        @BindView(R.id.ivBackgroundImage)
        ImageView ivBackgroundImage;

        public ImageView getIvBackgroundImage() {
            return ivBackgroundImage;
        }

        public TextView getTvStoryTitle() {
            return tvStoryTitle;
        }

        public TextView getTvMomentsCount() {
            return tvMomentsCount;
        }

        @BindView(R.id.tvStoryTitle)
        TextView tvStoryTitle;
        @BindView(R.id.tvMomentsCount)
        TextView tvMomentsCount;

        public ViewHolderSimpleStory(View itemView) {
            super(itemView);
        }

    }
}
