package com.codepath.timeline.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.util.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutoPlayTopFragment extends Fragment {
    private static final String TAG = AutoPlayTopFragment.class.getSimpleName();

    @BindView(R.id.ivMedia)
    ImageView ivMedia;

    private String mMomentObjectId;

    public AutoPlayTopFragment() {
    }

    public static AutoPlayTopFragment newInstance(String momentObjectId) {
        AutoPlayTopFragment frag = new AutoPlayTopFragment();
        Bundle args = new Bundle();
        args.putString(AppConstants.OBJECT_ID, momentObjectId);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_play_top, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMomentObjectId = getArguments().getString(AppConstants.OBJECT_ID, null);
        if (mMomentObjectId == null) {
            Log.e(TAG, "Moment OBJECT_ID is NULL");
            return;
        }

        TimelineClient.getInstance().getMoment(mMomentObjectId, new TimelineClient.TimelineClientGetMomentListener() {
            @Override
            public void onGetMomentListener(Moment moment) {
                updateMoment(moment);
            }
        });
    }

    private void updateMoment(Moment moment) {
        try {
            if (moment.getMediaUrl() != null) {
                Glide.with(this).load(moment.getMediaUrl())
                        .centerCrop()
                        .into(ivMedia);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}