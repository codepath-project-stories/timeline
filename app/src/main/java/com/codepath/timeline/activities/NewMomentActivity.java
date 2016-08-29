package com.codepath.timeline.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.timeline.R;
import com.codepath.timeline.models.Moment;
import com.codepath.timeline.util.NewItemClass;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewMomentActivity extends NewItemClass {

    @BindView(R.id.ivBackground) ImageView ivBackground;
    @BindView(R.id.etAddTitle) EditText etMomentTitle;
//    @BindView(R.id.tvAddLocation) TextView tvAddLocation;
    @BindView(R.id.flStoryPhoto) FrameLayout flStoryPhoto;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmoment);
        ButterKnife.bind(this);

        toolbar.setTitle("New moment");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_function, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miCancel:
                if (ivBackground.getDrawable() == null && etMomentTitle.getText().length() == 0) {
                    finish();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.all_changes_will_be_lost)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    // on click attached to text view id="@+id/tvPublish"
    public void publish(View view) {
//        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();

        // add animation to control the required input
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

        // set the date
        // Todo: adjust to db time
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
//        Snackbar.make(findViewById(android.R.id.content), dateFormat.format(cal.getTime()), Snackbar.LENGTH_SHORT).show();

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

            // TODO: v2 change to current location
            moment.setLocation("San Francisco, CA");

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
