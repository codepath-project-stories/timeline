package com.codepath.timeline.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.timeline.R;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.NewItemClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewMomentActivity extends NewItemClass {

    @BindView(R.id.ivBackground)
    ImageView ivBackground;
    @BindView(R.id.etAddTitle)
    EditText etMomentTitle;
    //    @BindView(R.id.tvAddLocation) TextView tvAddLocation;
    @BindView(R.id.flStoryPhoto)
    FrameLayout flStoryPhoto;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnPublish)
    Button btnPublish;
    @BindView(R.id.tvCount)
    TextView tvCount;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmoment);
        ButterKnife.bind(this);

        toolbar.setTitle("New moment");
        setSupportActionBar(toolbar);
        etMomentTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // this will show characters remaining
                // Todo: add more characters for the moment
                int num = 35 - s.toString().length();
                tvCount.setText(num + "");
                if (num <= 10) {
                    tvCount.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    tvCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                // Todo: add better visual for disabled button
                btnPublish.setEnabled(etMomentTitle.getText().length() <= 35);
            }
        });
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
            llTitle.startAnimation(shake);
        } else if (ivBackground.getDrawable() == null) {
            Snackbar.make(findViewById(android.R.id.content), "Fill out required fields", Snackbar.LENGTH_SHORT).show();
            ivBackground.startAnimation(shake);
        } else {
            // send result back
            Intent data = new Intent();
            data.putExtra(AppConstants.MOMENT_DESCRIPTION, etMomentTitle.getText().toString());
            // TODO: v2 change to current location
            data.putExtra(AppConstants.MOMENT_LOCATION, "San Francisco, CA");
            data.putExtra(AppConstants.PHOTO_URI, takenPhotoUri.getPath());
            setResult(1, data);

            // closes the activity, pass data to parent
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // Load the taken image into a preview
                ivBackground.setImageBitmap(takenImage);
            } else { // Result was a failure
                Snackbar.make(findViewById(android.R.id.content), "Picture wasn't taken!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
