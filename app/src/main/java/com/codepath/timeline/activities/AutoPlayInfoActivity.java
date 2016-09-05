package com.codepath.timeline.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.models.UserClient;
import com.codepath.timeline.util.AppConstants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*
    Displays moment details when the item is clicked while scrolling automatically
 */
public class AutoPlayInfoActivity extends AppCompatActivity {
    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.ivMedia)
    ImageView ivMedia;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_play_info);
        ButterKnife.bind(this);

        initDetailinfo();
    }

    public static Intent newInstance(Activity activity, Moment moment) {
        String momentObjectId = moment.getObjectId();
        String momentTitle = moment.getDescription();
        String momentImageUrl = moment.getMediaUrl();
        Intent intent = new Intent(activity, AutoPlayInfoActivity.class);
        intent.putExtra("moment_id", momentObjectId);
        intent.putExtra("moment_title", momentTitle);
        intent.putExtra("moment_imageUrl", momentImageUrl);
        return intent;
    }


    private void initDetailinfo() {
        Moment moment = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.MOMENT_EXTRA));
        if (moment != null) {
            if (moment.getAuthor() != null) {
                Glide.with(this).load(UserClient.getProfileImageUrl(moment.getAuthor()))
                        .fitCenter()
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(ivProfilePhoto);
            }

            if (moment.getMediaUrl() != null) {
                Glide.with(this).load(moment.getMediaUrl())
                        .centerCrop()
                        .into(ivMedia);
            }

        }
    }
}
