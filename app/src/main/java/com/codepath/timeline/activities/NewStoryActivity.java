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
import com.codepath.timeline.models.Story;
import com.codepath.timeline.util.NewItemClass;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewStoryActivity extends NewItemClass {

    @BindView(R.id.ivBackground) ImageView ivBackground;
    @BindView(R.id.ivCameraIcon) ImageView ivCameraIcon;
    @BindView(R.id.etAddTitle) EditText etStoryTitle;
    @BindView(R.id.tvAddPeople) TextView tvAddPeople;
    @BindView(R.id.flStoryPhoto) FrameLayout flStoryPhoto;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newstory);
        ButterKnife.bind(this);

        toolbar.setTitle("New story");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Todo: add collaborators to the story
    // on click attached to text view id="@+id/tvAddPeople"
    public void addPeople(View view) {
        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();
    }

    // on click attached to text view id="@+id/tvPublish"
    public void publish(View view) {
        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (etStoryTitle.getText().length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Fill out required fields", Snackbar.LENGTH_SHORT).show();
            etStoryTitle.startAnimation(shake);
        }
        else if (ivBackground.getDrawable() == null) {
            Snackbar.make(findViewById(android.R.id.content), "Fill out required fields", Snackbar.LENGTH_SHORT).show();
            ivBackground.startAnimation(shake);
        } else {
            // create a story
            Story story = new Story();
            story.setBackgroundImageUrl(takenPhotoUri.getPath());
            story.setTitle(etStoryTitle.getText().toString());
            Log.d("DEBUG", story.toString());

            // send result back
            Intent data = new Intent();
            data.putExtra("story", Parcels.wrap(story));
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
