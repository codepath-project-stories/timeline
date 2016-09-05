package com.codepath.timeline.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.timeline.models.Comment;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.models.UserClient;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.DateUtil;
import com.codepath.timeline.view.DepthPageTransformer;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DetailDialogFragment extends DialogFragment {
    // DetailDialogFragment
    // R.layout.fragment_detail_dialog
    // ScreenSlidePagerAdapter
    // MomentDetailFragment
    // R.layout.fragment_moment_detail
    // CommentsAdapter
    // R.layout.item_comment_media
    // R.layout.item_comment
    // R.layout.item_comment
    // R.layout.item_comment

    private static final String TAG = DetailDialogFragment.class.getSimpleName();

    @BindView(R.id.vpMoment)
    ViewPager vpMoment;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.btPost)
    Button btPost;
    @BindView(R.id.etComment)
    EditText etComment;
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvDate)
    TextView tvDate;

    private ScreenSlidePagerAdapter mPagerAdapter;
    private List<Moment> mMomentList;
    private Moment mMoment;
    private int index;
    private String mStoryObjectId;
    private Context context;
    private boolean isChat;

    public static DetailDialogFragment newInstance(Context context,
                                                   String storyObjectId,
                                                   int index, boolean isChat) {
        DetailDialogFragment frag = new DetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(AppConstants.OBJECT_ID, storyObjectId);
        args.putInt(AppConstants.INDEX, index);
        frag.setArguments(args);
        frag.context = context;
        frag.isChat = isChat;

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_dialog, container);
        ButterKnife.bind(this, view);
        // http://stackoverflow.com/questions/20308359/android-dialogfragment-layout-odd-extra-whitespace
        // http://stackoverflow.com/questions/18465002/layout-margin-padding-at-the-top-of-dialog-fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        index = getArguments().getInt(AppConstants.INDEX);
        mStoryObjectId = getArguments().getString(AppConstants.OBJECT_ID, null);
        if (mStoryObjectId == null) {
            Log.e(TAG, "Story OBJECT_ID is NULL");
            return;
        }

        // TODO: no need to query
        // TODO: let caller pass data into DetailDialogFragment
        TimelineClient.getInstance().getMomentList(mStoryObjectId,
                new TimelineClient.TimelineClientGetMomentListListener() {
                    @Override
                    public void onGetMomentList(List<Moment> itemList) {
                        if (!isChat) {
                            if (itemList != null) {
                                mMomentList = new ArrayList<Moment>();
                                mMomentList.addAll(itemList);
                                initDialog();
                            }
                        }
                    }
                    @Override
                    public void onGetMomentChatList(List<Moment> itemList) {
                        if (isChat) {
                            if (itemList != null) {
                                mMomentList = new ArrayList<Moment>();
                                mMomentList.addAll(itemList);;
                                initDialog();
                            }
                        }
                    }
        });
  }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            // DialogFragment is not taking up the whole screen
            // http://stackoverflow.com/a/26163346
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    void updateMomentHeader(Moment moment) {
        ParseUser user = moment.getAuthor();
        if (user != null) {
            tvName.setText(UserClient.getName(user));
            Glide.with(context)
                    .load(UserClient.getProfileImageUrl(user))
                    .fitCenter()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(ivProfilePhoto);
        }

        String location = moment.getLocation();
        if (location != null) {
            tvLocation.setText(location);
        }

        Date createdAtReal = moment.getCreatedAtReal();
        if (createdAtReal != null) {
            String formattedDate = DateUtil.getFormattedTimelineDate(context, createdAtReal);
            tvDate.setText(formattedDate);
        } else {
            tvDate.setText("");
        }
    }

    private void initDialog() {
        mMoment = mMomentList.get(index);

        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        vpMoment.setAdapter(mPagerAdapter);
        // TODO: different animation instead of DepthPageTransformer? maybe cube transition?
        vpMoment.setPageTransformer(true, new DepthPageTransformer());
        vpMoment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // TODO: need animation here
            }

            @Override
            public void onPageSelected(int position) {
                // Log.d("addOnPageChangeListener", "onPageSelected");
                // Log.d("addOnPageChangeListener", "position = " + position);

                Moment moment = mMomentList.get(position);
                // Log.d("addOnPageChangeListener", moment.toString());

                updateMomentHeader(mMomentList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // TODO: need animation here
                /*
                0: SCROLL_STATE_IDLE
                1: SCROLL_STATE_DRAGGING
                2: SCROLL_STATE_SETTLING
                */
            }
        });
        if (index == 0) {
            updateMomentHeader(mMoment);
        }
        else {
            vpMoment.setCurrentItem(index);
        }

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    btPost.setEnabled(true);
                } else {
                    btPost.setEnabled(false);
                }
            }
        });
    }

    @OnClick(R.id.ivClose)
    public void onCloseButtonClicked() {
        Log.d(TAG, "---------- close ");
        dismiss();
    }

    @OnClick(R.id.btPost)
    public void onPostbuttonClicked() {
        Log.d(TAG, "---------- post");

        MomentDetailFragment fragment = (MomentDetailFragment) mPagerAdapter.getRegisteredFragment(vpMoment.getCurrentItem());
        if (fragment != null) {
            Comment comment = new Comment();
            comment.setCreatedAtReal(DateUtil.getCurrentDate());
            comment.setBody(etComment.getText().toString());
            comment.setAuthor(UserClient.getCurrentUser());

            fragment.addComment(comment);

            // If successful, clear the text and disable the button
            etComment.setText("");
            btPost.setEnabled(false);
        }
    }

    private class ScreenSlidePagerAdapter extends SmartFragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mMoment = mMomentList.get(position);
            return MomentDetailFragment.newInstance(mMoment.getObjectId());
        }

        @Override
        public int getCount() {
            return mMomentList.size();
        }
    }
}
