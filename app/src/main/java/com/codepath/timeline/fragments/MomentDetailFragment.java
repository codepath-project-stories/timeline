package com.codepath.timeline.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.CommentsAdapter;
import com.codepath.timeline.models.Comment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.User;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.DateUtil;
import com.codepath.timeline.util.view.DividerItemDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MomentDetailFragment extends Fragment {
  private static final String TAG = MomentDetailFragment.class.getSimpleName();

  @BindView(R.id.rvComments)
  RecyclerView rvComments;

  private Moment mMoment;
  private List<Comment> mCommentList;
  private CommentsAdapter mAdapter;

  public MomentDetailFragment() {
    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
  }

  public static MomentDetailFragment newInstance(Moment moment) {
    MomentDetailFragment frag = new MomentDetailFragment();
    Bundle args = new Bundle();
    args.putParcelable(AppConstants.MOMENT_EXTRA, Parcels.wrap(moment));
    frag.setArguments(args);

    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_moment_detail, container, false);
    ButterKnife.bind(this, view);
    initList();
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mMoment = Parcels.unwrap(getArguments().getParcelable(AppConstants.MOMENT_EXTRA));
    if (mMoment == null) {
      Log.d(TAG, "Moment extra is NULL");
      return;
    }

    // Add the moment photo as the first comment
    Comment momentDetail = new Comment();
    momentDetail.setUser(mMoment.getUser());
    momentDetail.setLocation(mMoment.getLocation());
    momentDetail.setCreatedAt(mMoment.getCreatedAt());
    momentDetail.setBody(mMoment.getDescription());
    momentDetail.setMediaUrl(mMoment.getMediaUrl());
    mCommentList.add(momentDetail);

    List<Comment> commentList = mMoment.getCommentList();
    if (commentList != null && commentList.size() > 0) {
      mCommentList.addAll(commentList);
      mAdapter.notifyItemRangeInserted(0, mCommentList.size());
    }
  }

  private void initList() {
    mCommentList = new ArrayList<>();
    mAdapter = new CommentsAdapter(getActivity(), mCommentList);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    rvComments.setLayoutManager(linearLayoutManager);
    rvComments.setAdapter(mAdapter);
  }

  public void addComment(Comment comment) {
    mCommentList.add(comment);
    mAdapter.notifyItemRangeInserted(mCommentList.size()-1, 1);

    // Scroll to the bottom so that the newly added comment is displayed
    rvComments.smoothScrollToPosition(mCommentList.size());
  }
}
