package com.codepath.timeline.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.timeline.R;
import com.codepath.timeline.fragments.SearchFriendsDialogFragment;
import com.codepath.timeline.network.TimelineClient;
import com.codepath.timeline.network.UserClient;
import com.codepath.timeline.util.AppConstants;
import com.codepath.timeline.util.NewItemClass;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewStoryActivity extends NewItemClass
        implements SearchFriendsDialogFragment.SearchDialogListener {
    private static final String TAG = NewStoryActivity.class.getSimpleName();

    @BindView(R.id.ivBackground)
    ImageView ivBackground;
    @BindView(R.id.ivCameraIcon)
    ImageView ivCameraIcon;
    @BindView(R.id.etAddTitle)
    EditText etStoryTitle;
    @BindView(R.id.tvCount)
    TextView tvCount;
    @BindView(R.id.tvAddPeople)
    TextView tvAddPeople;
    @BindView(R.id.flStoryPhoto)
    FrameLayout flStoryPhoto;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnPublish)
    Button btnPublish;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;

    @BindView(R.id.ivSearch)
    ImageView ivSearch;
    @BindView(R.id.ivCollaborator1)
    ImageView ivCollaborator1;
    @BindView(R.id.ivCollaborator2)
    ImageView ivCollaborator2;
    @BindView(R.id.ivCollaborator3)
    ImageView ivCollaborator3;
    @BindView(R.id.ivCollaborator4)
    ImageView ivCollaborator4;
    @BindView(R.id.tvCollaborator1)
    TextView tvCollaborator1;
    @BindView(R.id.tvCollaborator2)
    TextView tvCollaborator2;
    @BindView(R.id.tvCollaborator3)
    TextView tvCollaborator3;
    @BindView(R.id.tvCollaborator4)
    TextView tvCollaborator4;
    @BindView(R.id.ivSelected1)
    ImageView ivSelected1;
    @BindView(R.id.ivSelected2)
    ImageView ivSelected2;
    @BindView(R.id.ivSelected3)
    ImageView ivSelected3;
    @BindView(R.id.ivSelected4)
    ImageView ivSelected4;

    private List<ParseUser> mFriendsList;
    private Context context;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newstory);
        ButterKnife.bind(this);

        toolbar.setTitle("New story");
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        getFriendsList();
        setupViews();
    }

    public void setupViews() {

        // TODO: maybe use a small button instead of the following?
        // TODO: most apps in the market do not have this kind of things.
        // R.drawable.camera_background_1
        // R.drawable.camera_background_2
        // R.drawable.camera_background_3
        Glide.with(this).load(R.drawable.camera_background_1).centerCrop().into(ivBackground);

        etStoryTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // this will show characters remaining
                int num = 35 - s.toString().length();
                tvCount.setText(num + "");
                if (num <= 10) {
                    tvCount.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    tvCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                // Todo: add better visual for disabled button
                btnPublish.setEnabled(etStoryTitle.getText().length() <= 35);
            }
        });
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: fix the search according to the API
                FragmentManager fragmentManager = getSupportFragmentManager();
                SearchFriendsDialogFragment composeDialogFragment =
                        SearchFriendsDialogFragment.newInstance(context, "Search friends");
                composeDialogFragment.show(fragmentManager, "fragment_compose");
            }
        });
    }

    private void getFriendsList() {
        ParseUser user = UserClient.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error from getting friends list");
        }

        TimelineClient.getInstance().getFriendsList(user, new TimelineClient.TimelineClientGetFriendListListener() {
            @Override
            public void onGetFriendList(List<ParseUser> friendsList) {
                mFriendsList = friendsList;
                initFriendsList();
            }
        });
    }

    private void initFriendsList() {
        CropCircleTransformation profilePhotoTransformation = new CropCircleTransformation(context);
        // Todo: HACKY -- need to convert to horizontal recyclerview and set the adapter once instead of setting each view property individually
        if(mFriendsList != null) {
            if (mFriendsList.size() > 0 && mFriendsList.get(0) != null) {
                ParseUser user1 = mFriendsList.get(0);
                ivCollaborator1.setVisibility(View.VISIBLE);
                Glide.with(context).load(UserClient.getProfileImageUrl(user1))
                    .fitCenter()
                    .bitmapTransform(profilePhotoTransformation)
                    .into(ivCollaborator1);

                tvCollaborator1.setVisibility(View.VISIBLE);
                tvCollaborator1.setText(UserClient.getName(user1));
            }

            if (mFriendsList.size() > 1 && mFriendsList.get(1) != null) {
                ParseUser user2 = mFriendsList.get(1);
                ivCollaborator2.setVisibility(View.VISIBLE);
                Glide.with(context).load(UserClient.getProfileImageUrl(user2))
                    .fitCenter()
                    .bitmapTransform(profilePhotoTransformation)
                    .into(ivCollaborator2);

                tvCollaborator2.setVisibility(View.VISIBLE);
                tvCollaborator2.setText(UserClient.getName(user2));
            }

            if (mFriendsList.size() > 2 && mFriendsList.get(2) != null) {
                ParseUser user3 = mFriendsList.get(2);
                ivCollaborator3.setVisibility(View.VISIBLE);
                Glide.with(context).load(UserClient.getProfileImageUrl(user3))
                    .fitCenter()
                    .bitmapTransform(profilePhotoTransformation)
                    .into(ivCollaborator3);

                tvCollaborator3.setVisibility(View.VISIBLE);
                tvCollaborator3.setText(UserClient.getName(user3));
            }

            if (mFriendsList.size() > 3 && mFriendsList.get(3) != null) {
                ParseUser user4 = mFriendsList.get(3);
                ivCollaborator4.setVisibility(View.VISIBLE);
                Glide.with(context).load(UserClient.getProfileImageUrl(user4))
                    .fitCenter()
                    .bitmapTransform(profilePhotoTransformation)
                    .into(ivCollaborator4);

                tvCollaborator4.setVisibility(View.VISIBLE);
                tvCollaborator4.setText(UserClient.getName(user4));
            }
        }
    }

    // http://stackoverflow.com/questions/29390695/onclick-array-with-optional-ids-butterknife
    // @Nullable
    // @Optional
    @OnClick({R.id.ivSelected1, R.id.ivSelected2, R.id.ivSelected3, R.id.ivSelected4})
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
    @OnClick(R.id.tvAddPeople)
    public void addPeople(View view) {
//        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();

    }

    // on click attached to text view id="@+id/tvPublish"
    @OnClick(R.id.btnPublish)
    public void publish(View view) {
//        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        if (etStoryTitle.getText().length() == 0) {
            Snackbar.make(findViewById(android.R.id.content), "Fill out required fields", Snackbar.LENGTH_SHORT).show();
            llTitle.startAnimation(shake);
        } else if (ivBackground.getDrawable() == null) {
            Snackbar.make(findViewById(android.R.id.content), "Fill out required fields", Snackbar.LENGTH_SHORT).show();
            ivBackground.startAnimation(shake);
        } else {
            ArrayList<String> collabUserIdList = new ArrayList<>();
            if (isViewSelected(ivSelected1)) {
                collabUserIdList.add(mFriendsList.get(0).getObjectId());
            }
            if (isViewSelected(ivSelected2)) {
                collabUserIdList.add(mFriendsList.get(1).getObjectId());
            }
            if (isViewSelected(ivSelected3)) {
                collabUserIdList.add(mFriendsList.get(2).getObjectId());
            }
            if (isViewSelected(ivSelected4)) {
                collabUserIdList.add(mFriendsList.get(3).getObjectId());
            }

            // send result back
            Intent data = new Intent();
            data.putExtra(AppConstants.STORY_TITLE, etStoryTitle.getText().toString());
            data.putExtra(AppConstants.STORY_BACKGROUND_IMAGE_URL, takenPhotoUri.getPath());
            data.putExtra(AppConstants.STORY_COLLABORATOR_LIST, collabUserIdList);
            setResult(1, data);

            // closes the activity, pass data to parent, which is UserStoriesFragment onActivityResult()
            finish();
        }
    }

    public boolean isViewSelected(View view) {
        return (Integer.parseInt(view.getTag().toString()) == 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                takenPhotoUri = getPhotoFileUri(); // extends NewItemClass
                Log.d("onActivityResult", takenPhotoUri.toString());
                // by this point we have the camera photo on disk

                // works for sdk 23 only
                // Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // ivBackground.setImageBitmap(takenImage);

                // backward compatible
                Glide.with(this).load(takenPhotoUri).centerCrop().into(ivBackground);
                ivCameraIcon.setVisibility(View.INVISIBLE);
            } else { // Result was a failure
                Snackbar.make(findViewById(android.R.id.content), "Picture wasn't taken!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFinishSearchDialog(List<ParseUser> collabs) {
        // TODO: change the corresponding items in R.layout.activity_newstory
        String output = "from MultiAutoCompleteTextView\n";
        for (ParseUser user : collabs) {
            output = output + UserClient.getName(user) + "\n";
        }
        Toast.makeText(NewStoryActivity.this, output, Toast.LENGTH_LONG).show();
    }

    @Override
    @OnClick(R.id.flStoryPhoto)
    public void onLaunchCamera(View view) {
        super.onLaunchCamera(view);
    }
}
