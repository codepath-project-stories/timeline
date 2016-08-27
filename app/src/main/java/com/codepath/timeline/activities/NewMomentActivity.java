package com.codepath.timeline.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.NewItemClass;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewMomentActivity extends NewItemClass {

    @BindView(R.id.ivBackground) ImageView ivBackground;
    @BindView(R.id.etAddTitle) EditText etMomentTitle;
    @BindView(R.id.tvAddLocation) TextView tvAddLocation;
    @BindView(R.id.flStoryPhoto) FrameLayout flStoryPhoto;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmoment);
        ButterKnife.bind(this);

        toolbar.setTitle("New moment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Todo: add date
    // on click attached to text view id="@+id/tvAddDate"
    public void addDate(View view) {
        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();
    }

    // on click attached to text view id="@+id/tvPublish"
    public void publish(View view) {
        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (etMomentTitle.getText().length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Fill out required fields", Snackbar.LENGTH_SHORT).show();
            etMomentTitle.startAnimation(shake);
        }
        else if (ivBackground.getDrawable() == null) {
            Snackbar.make(findViewById(android.R.id.content), "Fill out required fields", Snackbar.LENGTH_SHORT).show();
            ivBackground.startAnimation(shake);
        } else {
            // create a moment
            Moment moment = new Moment();
            moment.setMediaUrl(takenPhotoUri.getPath());
            moment.setDescription(etMomentTitle.getText().toString());
            Log.d("DEBUG", moment.toString());

            // send result back
            Intent data = new Intent();
            data.putExtra("moment", Parcels.wrap(moment));
            setResult(1, data);

            // closes the activity, pass data to parent
            finish();
            // Todo: add collaborators, add location
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivBackground.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
