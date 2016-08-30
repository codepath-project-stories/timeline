package com.codepath.timeline.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.AppConstants;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/*
    Displays moment details when the item is clicked while scrolling automatically
 */
public class AutoPlayInfoActivity extends AppCompatActivity {
  @BindView(R.id.ivProfilePhoto)
  ImageView ivProfilePhoto;
  @BindView(R.id.ivMedia)
  ImageView ivMedia;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auto_play_info);
    ButterKnife.bind(this);

    initDetailinfo();
  }

  public static Intent newInstance(Activity activity, Moment moment) {
    Intent intent = new Intent(activity, AutoPlayInfoActivity.class);
    intent.putExtra(AppConstants.MOMENT_EXTRA, Parcels.wrap(moment));
    return intent;
  }


  private void initDetailinfo() {
    Moment moment = Parcels.unwrap(getIntent().getParcelableExtra(AppConstants.MOMENT_EXTRA));
    if (moment != null) {
      if (moment.getAuthor() != null) {
        Glide.with(this).load(UserClient.getProfileImageUrl(moment.getAuthor()))
            .fitCenter()
            .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
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
