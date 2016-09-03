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
import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.DateUtil;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class MomentsAdapter extends RecyclerView.Adapter<MomentsAdapter.ViewHolder> {
    private static final String TAG = "TimelineLog:" + MomentsAdapter.class.getSimpleName();

    protected List<Moment> mMomentList;
    protected Context mContext;

    public MomentsAdapter(Context context, List<Moment> mMomentList) {
        this.mContext = context;
        this.mMomentList = mMomentList;
    }

    @Override
    public MomentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moment, parent, false);
        mContext = view.getContext();

        ViewHolder viewHolder = new MomentsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MomentsAdapter.ViewHolder holder, int position) {
        Moment moment = mMomentList.get(position);
        Log.d(TAG, "Binding moment: " + moment);

        MomentsViewHolder viewHolder = (MomentsViewHolder) holder;
        ParseUser author = moment.getAuthor();
        if (author != null) {
            Log.d(TAG, "URL: " + UserClient.getProfileImageUrl(author));

            viewHolder.tvName.setText(UserClient.getName(author));
            Glide.with(mContext).load(UserClient.getProfileImageUrl(author))
                    .fitCenter()
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .into(viewHolder.ivProfilePhoto);
        }

        if (moment.getMediaUrl() != null) {
            Log.d(TAG, "Displaying mediaUrl");

            // Demo data
            Glide.with(mContext).load(moment.getMediaUrl())
                    .centerCrop()
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
            String formattedDate = DateUtil.getFormattedTimelineDate(mContext, moment.getCreatedAtReal());
            Log.d(TAG, "formattedDate: " + formattedDate);
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
