package com.codepath.timeline.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class MomentsAdapter extends RecyclerView.Adapter<MomentsAdapter.ViewHolder> {
  private static final String TAG = MomentsAdapter.class.getSimpleName();

  public static final int TYPE_MOMENT_HEADER = 0;
  public static final int TYPE_MOMENT_LEFT = 1;
  public static final int TYPE_MOMENT_RIGHT = 2;

  private List<Moment> mMomentList;
  private Context mContext;

  public MomentsAdapter(Context context, List<Moment> mMomentList) {
    this.mContext = context;
    this.mMomentList = mMomentList;
  }

  @Override
  public int getItemViewType(int position) {
    Moment moment = mMomentList.get(position);
    if (moment.getHeader() != null) {
      return TYPE_MOMENT_HEADER;
    } else {
      if (position % 2 == 0) {
        return TYPE_MOMENT_LEFT;
      } else {
        return TYPE_MOMENT_RIGHT;
      }
    }
  }

  @Override
  public MomentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = null;
    ViewHolder viewHolder = null;
    switch (viewType) {
      case TYPE_MOMENT_HEADER:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moment_header, parent, false);
        viewHolder = new MomentsHeaderViewHolder(view);
        break;
      case TYPE_MOMENT_LEFT:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moment_left, parent, false);
        viewHolder = new MomentsViewHolder(view);
        break;
      case TYPE_MOMENT_RIGHT:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moment_right, parent, false);
        viewHolder = new MomentsViewHolder(view);
        break;
    }

    mContext = view.getContext();
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(MomentsAdapter.ViewHolder holder, int position) {
    Moment moment = mMomentList.get(position);
    if (holder instanceof MomentsViewHolder) {
      MomentsViewHolder viewHolder = (MomentsViewHolder) holder;
      if (moment.getUser() != null) {
        Log.d(TAG, "URL: " + moment.getUser().getProfileImageUrl());

        Glide.with(mContext).load(moment.getUser().getProfileImageUrl())
            .fitCenter()
            .bitmapTransform(new RoundedCornersTransformation(mContext, 25, 0))
            .into(viewHolder.ivProfilePhoto);
      }

      if (moment.getResourceUrl() != null) {
        Glide.with(mContext).load(moment.getResourceUrl())
            .centerCrop()
            .into(viewHolder.ivMedia);
      }
    }
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

    public MomentsViewHolder(View itemView) {
      super(itemView);
    }
  }

  public static class MomentsHeaderViewHolder extends ViewHolder {
    public MomentsHeaderViewHolder(View itemView) {
      super(itemView);
    }
  }
}
