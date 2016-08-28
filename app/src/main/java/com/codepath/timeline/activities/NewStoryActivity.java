package com.codepath.timeline.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.fragments.SearchFriendsDialogFragment;
import com.codepath.timeline.models.Story;
import com.codepath.timeline.models.User_Temp;
import com.codepath.timeline.util.NewItemClass;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class NewStoryActivity extends NewItemClass implements SearchFriendsDialogFragment.SearchDialogListener {

    @BindView(R.id.ivBackground) ImageView ivBackground;
    @BindView(R.id.ivCameraIcon) ImageView ivCameraIcon;
    @BindView(R.id.etAddTitle) EditText etStoryTitle;
    @BindView(R.id.tvAddPeople) TextView tvAddPeople;
    @BindView(R.id.flStoryPhoto) FrameLayout flStoryPhoto;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.ivSearch) ImageView ivSearch;
    @BindView(R.id.ivCollaborator1) ImageView ivCollaborator1;
    @BindView(R.id.ivCollaborator2) ImageView ivCollaborator2;
    @BindView(R.id.ivCollaborator3) ImageView ivCollaborator3;
    @BindView(R.id.ivCollaborator4) ImageView ivCollaborator4;
    @BindView(R.id.tvCollaborator1) TextView tvCollaborator1;
    @BindView(R.id.tvCollaborator2) TextView tvCollaborator2;
    @BindView(R.id.tvCollaborator3) TextView tvCollaborator3;
    @BindView(R.id.tvCollaborator4) TextView tvCollaborator4;
    @BindView(R.id.ivSelected1) ImageView ivSelected1;
    @BindView(R.id.ivSelected2) ImageView ivSelected2;
    @BindView(R.id.ivSelected3) ImageView ivSelected3;
    @BindView(R.id.ivSelected4) ImageView ivSelected4;


    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newstory);
        ButterKnife.bind(this);

        toolbar.setTitle("New story");
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        setupViews();
    }

    public void setupViews() {

        // TODO: read from the List<User> friends, add names
        Glide.with(context).load("https://pbs.twimg.com/profile_images/1752229650/icontwit.png")
                .fitCenter()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(ivCollaborator1);

        tvCollaborator1.setText("Amanda Brown");

        Glide.with(context).load("https://pbs.twimg.com/profile_images/740895191003975681/kTD5CP9x.jpg")
                .fitCenter()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(ivCollaborator2);

        tvCollaborator2.setText("Clair White");

        Glide.with(context).load("https://pbs.twimg.com/profile_images/1752229650/icontwit.png")
                .fitCenter()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(ivCollaborator3);

        tvCollaborator3.setText("Megan Cox");

        Glide.with(context).load("https://pbs.twimg.com/profile_images/740895191003975681/kTD5CP9x.jpg")
                .fitCenter()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(ivCollaborator4);

        tvCollaborator4.setText("Julie Korosteleva Brown");

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                SearchFriendsDialogFragment composeDialog = SearchFriendsDialogFragment.newInstance("Choose friends:");
                Bundle bundle = new Bundle();
                // Todo: pass meaningful data (probably user's id)
                composeDialog.setArguments(bundle);
                composeDialog.show(fm, "fragment_compose_dialog");
            }
        });
    }

    public void highlightSelected(View view) {
        if (Integer.parseInt(view.getTag().toString()) == 1) {
            view.setBackgroundResource(R.drawable.circle_accent);
            view.setTag(2);
        } else {
            view.setBackgroundResource(R.drawable.circle_collaborators);
            view.setTag(1);
        }
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
                if (ivBackground.getDrawable() == null && etStoryTitle.getText().length() == 0 && tvAddPeople.getText().length() == 0) {
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
            // Todo: check tags on the collaborators images, if 2 then it is selected
            if (ivSelected1.isSelected()) {

            }
            if (ivSelected2.isSelected()) {

            }
            if (ivSelected3.isSelected()) {

            }
            if (ivSelected4.isSelected()) {

            }

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

    public boolean isSelected(View view) {
        return (Integer.parseInt(view.getTag().toString()) == 2);
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

    @Override
    public void onFinishSearchDialog(List<User_Temp> collabs) {

    }
}
