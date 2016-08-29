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
import com.codepath.timeline.adapters.CommentItemAnimator;
import com.codepath.timeline.adapters.CommentsAdapter;
import com.codepath.timeline.models.Comment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.DateUtil;
import com.parse.ParseUser;

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
  private String mObjectId;

  public MomentDetailFragment() {
    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
  }

  public static MomentDetailFragment newInstance(String momentObjectId) {
    MomentDetailFragment frag = new MomentDetailFragment();
    Bundle args = new Bundle();
    args.putString(AppConstants.OBJECT_ID, momentObjectId);
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

    mObjectId = getArguments().getString(AppConstants.OBJECT_ID, null);
    if (mObjectId == null) {
      Log.e(TAG, "Moment OBJECT_ID is NULL");
      return;
    }

    TimelineClient.getInstance().getMoment(mObjectId, new TimelineClient.TimelineClientGetMomentListener() {
      @Override
      public void onGetMomentListener(Moment moment) {
        Log.d(TAG, "Moment detail: " + moment);
        mMoment = moment;
        updateMoment();
      }
    });

  }

  private void initList() {
    mCommentList = new ArrayList<>();
    mAdapter = new CommentsAdapter(getActivity(), mCommentList);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    rvComments.setLayoutManager(linearLayoutManager);
    rvComments.setAdapter(mAdapter);
    rvComments.setItemAnimator(new CommentItemAnimator());
  }

  private void updateMoment() {
    Log.d(TAG, "updating moment");

    // Add the moment photo as the first comment
    Comment momentDetail = new Comment();
    // TODO: make sure it is not null here
    if (mMoment.getAuthor() != null) {
      momentDetail.setUser(mMoment.getAuthor());
    }
    else {
      momentDetail.setUser(ParseUser.getCurrentUser());
    }
    momentDetail.setLocation(mMoment.getLocation());
    // TODO: make sure it is not null here
    /*
    TODO: Update date
    if (mMoment.getCreatedAtReal() != null) {
      String formattedDate = DateUtil.getFormattedTimelineDate(getActivity(), mMoment.getCreatedAtReal().toString());
      momentDetail.setCreatedAtReal(formattedDate);
    }
    else {
      momentDetail.setCreatedAtReal("2016-09-31T19:22:54.695Z");
    }
    */
    momentDetail.setBody(mMoment.getDescription());
    momentDetail.setMediaUrl(mMoment.getMediaUrl());
    mCommentList.add(momentDetail);
    mAdapter.notifyItemInserted(0);

    List<Comment> commentList = mMoment.getCommentList();
    if (commentList != null && commentList.size() > 0) {
      mCommentList.addAll(commentList);
      mAdapter.notifyItemRangeInserted(0, mCommentList.size());
    }

    Log.d(TAG, "FINISHED updating moment");
  }

  public void addComment(Comment comment) {
    mCommentList.add(comment);
    mAdapter.notifyItemRangeInserted(mCommentList.size()-1, 1);

    // Scroll to the bottom so that the newly added comment is displayed
    rvComments.smoothScrollToPosition(mCommentList.size());
  }
}
