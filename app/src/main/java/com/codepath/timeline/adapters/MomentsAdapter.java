package com.codepath.timeline.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.UserClient;
import com.codepath.timeline.util.DateUtil;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class MomentsAdapter extends RecyclerView.Adapter<MomentsAdapter.ViewHolder> {
    private static final String TAG = "TimelineLog:" + MomentsAdapter.class.getSimpleName();
    private static int ivProfilePhotoMode = 0;
    // 0: keep the author's profile photo visible at all times
    // 1: something like google/hangout

    protected List<Moment> mMomentList;
    protected Context mContext;
    protected int mode;

    public MomentsAdapter(Context context, List<Moment> mMomentList, int mode) {
        this.mContext = context;
        this.mMomentList = mMomentList;
        this.mode = mode;
    }

    @Override
    public int getItemViewType(int position) {
        if (mode == 1) {
            Moment moment = mMomentList.get(position);
            int result = 1;
            if (!moment.getAuthor().getEmail().equals(UserClient.getCurrentUser().getEmail())) {
                // from others
                result = result + 2;
            }
            if (moment.getMediaUrl() != null) {
                result = result + 4;
            } else if (moment.getTempPhotoUri() != null) {
                result = result + 4;
            } else if (moment.getMediaFile() != null) {
                result = result + 4;
            }
            return result;
        }
        return 0;
    }

    @Override
    public MomentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (mode == 1) {
            // TODO: need to implement a input text box for chat
            if ((viewType & 2) == 2) {
                // from others
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.list_item_message_left, parent, false);
            }
            else {
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.list_item_message_right, parent, false);
            }
            mContext = view.getContext();
        }
        else if (mode == 3) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_moment_two_columns, parent, false);
            mContext = view.getContext();
        }
        else {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_moment, parent, false);
            mContext = view.getContext();
        }
        ViewHolder viewHolder = new MomentsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MomentsAdapter.ViewHolder holder, int position) {
        Moment moment = mMomentList.get(position);
        int viewType = holder.getItemViewType();

        Log.d(TAG, "Binding moment: " + moment);
        Log.d(TAG, "getItemViewType(): " + viewType);

        MomentsViewHolder viewHolder = (MomentsViewHolder) holder;

        ParseUser author = moment.getAuthor();
        if (author != null) {
            Log.d(TAG, "URL: " + UserClient.getProfileImageUrl(author));

            viewHolder.tvName.setText(UserClient.getName(author));

            boolean show_ivProfilePhoto = true;

            if (ivProfilePhotoMode == 1) {
                if (mode == 1) {
                    if (position < getItemCount() - 1) {
                        if (author.getEmail().equals(mMomentList.get(position + 1).getAuthor().getEmail())) {
                            show_ivProfilePhoto = false;
                        }
                    }
                }
            }

            if (show_ivProfilePhoto) {
                viewHolder.ivProfilePhoto.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(UserClient.getProfileImageUrl(author))
                        .fitCenter()
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(viewHolder.ivProfilePhoto);
            }
            else {
                viewHolder.ivProfilePhoto.setVisibility(View.INVISIBLE);
            }
        }

        if (mode == 1) {
            if ((viewType & 4) != 4) {
                viewHolder.ivMedia.setVisibility(View.GONE);
            }
        }
        if (moment.getMediaUrl() != null) {
            Log.d(TAG, "Displaying mediaUrl");

            // speed up the performance
            // http://stackoverflow.com/questions/29776075/recyclerview-oncreateviewholder-called-excessively-when-scrolling-fast-with-dpad
            // recyclerView.getRecycledViewPool.setMaxRecycledViews(50);
            // .centerCrop()
            // .diskCacheStrategy(DiskCacheStrategy.ALL)
            // Demo data
            Glide.with(mContext).load(moment.getMediaUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.ivMedia);
        } else if (moment.getTempPhotoUri() != null) {
            Log.d(TAG, "Displaying tempPhotoUri");

            // Image that was just taken from the phone
            Glide.with(mContext).load(moment.getTempPhotoUri())
                    .centerCrop()
                    .into(viewHolder.ivMedia);
        } else if (moment.getMediaFile() != null) {
            Log.d(TAG, "Displaying mediaFile");

            // Uploaded image
            Glide.with(mContext).load(moment.getMediaFile().getUrl())
                    .centerCrop()
                    .into(viewHolder.ivMedia);
        }


        Date momentDate = moment.getCreatedAtReal();
        if (momentDate != null) {
            String formattedDate = DateUtil.getFormattedTimelineDate(mContext, momentDate);
            Log.d(TAG, "formattedDate: " + formattedDate);

            if (mode == 1) {
                // display the full date if it's chat view
                formattedDate = DateUtil.getFullDate(mContext, momentDate);
            }
            viewHolder.tvDate.setText(formattedDate);
        }
        viewHolder.tvLocation.setText(moment.getLocation());
        viewHolder.tvDescription.setText(moment.getDescription());

        Log.d(TAG, "Finished binding moment");
    }

    @Override
    public int getItemCount() {
        return mMomentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class MomentsViewHolder extends ViewHolder {
        @BindView(R.id.ivProfilePhoto)
        ImageView ivProfilePhoto;
        @BindView(R.id.ivMedia)
        ImageView ivMedia;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvLocation)
        TextView tvLocation;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.tvDescription)
        TextView tvDescription;

        public MomentsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
