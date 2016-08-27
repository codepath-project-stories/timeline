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
import com.codepath.timeline.models.Comment;
import com.codepath.timeline.models.User;
import com.codepath.timeline.util.DateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
  private static final String TAG = MomentsAdapter.class.getSimpleName();

  public static final int TYPE_COMMENT = 0;
  public static final int TYPE_COMMENT_MEDIA = 1;

  protected List<Comment> mCommentList;
  protected Context mContext;

  public CommentsAdapter(Context context, List<Comment> commentList) {
    this.mContext = context;
    this.mCommentList = commentList;
  }

  @Override
  public int getItemViewType(int position) {
    Comment comment = mCommentList.get(position);
    if (comment.getMediaUrl() == null) {
      return TYPE_COMMENT;
    } else {
      return TYPE_COMMENT_MEDIA;
    }
  }

  @Override
  public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    ViewHolder viewHolder;

    if (viewType == TYPE_COMMENT) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_comment, parent, false);
      viewHolder = new CommentsViewHolder(view);
    } else {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_comment_media, parent, false);
      viewHolder = new CommentsMediaViewHolder(view);
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(CommentsAdapter.ViewHolder holder, int position) {
    Comment comment = mCommentList.get(position);

    int type = getItemViewType(position);
    if (type == TYPE_COMMENT_MEDIA) {
      CommentsMediaViewHolder mediaViewHolder = (CommentsMediaViewHolder) holder;
      if (comment.getMediaUrl() != null) {
        Glide.with(mContext).load(comment.getMediaUrl())
            .centerCrop()
            .into(mediaViewHolder.ivMedia);
      }
    }

    User user = comment.getUser();
    if (user != null) {
      Log.d(TAG, "URL: " + comment.getUser().getProfileImageUrl());
      holder.tvName.setText(user.getName());
      Glide.with(mContext).load(user.getProfileImageUrl())
          .fitCenter()
          .bitmapTransform(new RoundedCornersTransformation(mContext, 25, 0))
          .into(holder.ivProfilePhoto);
    }

    if (comment.getCreatedAt() != null) {
      String formattedDate = DateUtil.getFormattedTimelineDate(mContext, comment.getCreatedAt());
      Log.d(TAG, "formattedDate: " + formattedDate);
      holder.tvDate.setText(formattedDate);
    } else {
      holder.tvDate.setText("");
    }

    holder.tvBody.setText(comment.getBody());
  }

  @Override
  public int getItemCount() {
    return mCommentList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvBody)
    TextView tvBody;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class CommentsViewHolder extends ViewHolder {
    public CommentsViewHolder(View itemView) {
      super(itemView);
    }
  }

  public static class CommentsMediaViewHolder extends ViewHolder {
    @BindView(R.id.ivMedia)
    ImageView ivMedia;

    public CommentsMediaViewHolder(View itemView) {
      super(itemView);
    }
  }
}