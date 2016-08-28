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

import com.codepath.timeline.R;
import com.codepath.timeline.adapters.CommentsAdapter;
import com.codepath.timeline.adapters.CommentItemAnimator;
import com.codepath.timeline.models.Comment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.AppConstants;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    // TODO: make sure it is not null here
    if (mMoment.getUser() != null) {
      momentDetail.setUser(mMoment.getUser());
    }
    else {
      momentDetail.setUser(ParseUser.getCurrentUser());
    }
    momentDetail.setLocation(mMoment.getLocation());
    // TODO: make sure it is not null here
    if (mMoment.getCreatedAtReal() != null) {
      momentDetail.setCreatedAtReal(mMoment.getCreatedAtReal());
    }
    else {
      momentDetail.setCreatedAtReal("2016-09-31T19:22:54.695Z");
    }
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
    rvComments.setItemAnimator(new CommentItemAnimator());
  }

  public void addComment(Comment comment) {
    mCommentList.add(comment);
    mAdapter.notifyItemRangeInserted(mCommentList.size()-1, 1);

    // Scroll to the bottom so that the newly added comment is displayed
    rvComments.smoothScrollToPosition(mCommentList.size());
  }
}
