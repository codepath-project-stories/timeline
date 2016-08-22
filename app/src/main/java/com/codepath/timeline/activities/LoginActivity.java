package com.codepath.timeline.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.VideoView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.timeline.R;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    // LoginActivity calls LandingActivity

    @BindView(R.id.login_video)
    VideoView login_video;
    @BindView(R.id.input_email)
    EditText input_email;
    @BindView(R.id.input_password)
    EditText input_password;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Determine whether the current user is an anonymous user
        if (ParseUser.getCurrentUser() != null) {
            if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                onLoginSuccess();
            }
        }
        // If user is anonymous, ask the user to login or signup

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        input_email.requestFocus();

        // https://developer.android.com/guide/appendix/media-formats.html
//        login_video.setVideoURI(
//                Uri.parse(
//                        "android.resource://"
//                                + getPackageName() + "/"
//                                + R.raw.login_video_1
//                )
//        );
//        login_video.start();
//        login_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });
    }

    // http://guides.codepath.com/android/Activity-Lifecycle
    @Override
    protected void onResume() {
        super.onResume();
//        login_video.start();
    }

    @OnClick(R.id.btn_login)
    void login() {
        if (input_email.getText().length() == 0
                || input_password.getText().length() == 0) {
            showMaterialDialog(getResources().getString(R.string.missing));
            return;
        }
        ParseUser.logInInBackground(
                input_email.getText().toString(),
                input_password.getText().toString(),
                new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Hooray! The user is logged in.
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            if (currentUser != null) {
                                // do stuff with the user
                                Log.d("logInInBackground", currentUser.toString());
                                onLoginSuccess();
                            } else {
                                // something wrong
                                // show the signup or login screen
                                Log.d("logInInBackground", "getCurrentUser failed");
                                signup();
                            }
                        } else {
                            // usually go here
                            // Signup failed. Look at the ParseException to see what happened.
                            Log.d("logInInBackground", "done failed");
                            Log.d("logInInBackground", e.toString());
                            signup();
                        }
                    }
        });
    }

    void signup() {
        // Create the ParseUser
        ParseUser newUser = new ParseUser();
        // Set core properties
        newUser.setUsername(input_email.getText().toString());
        newUser.setPassword(input_password.getText().toString());
        newUser.setEmail(input_email.getText().toString());
        // Set custom properties
        newUser.put("demoCreated", "false");
        // Invoke signUpInBackground
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        // do stuff with the user
                        Log.d("signUpInBackground", currentUser.toString());
                        onLoginSuccess();
                    } else {
                        // something wrong
                        Log.d("signUpInBackground", "getCurrentUser failed");
                        showMaterialDialog(getResources().getString(R.string.something_wrong));
                    }
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.d("signUpInBackground", "done failed");
                    Log.d("signUpInBackground", e.toString());
                    showMaterialDialog(e.toString()
                            .replaceAll("^.*: ", "")
                            .replaceAll("^Account already exists.*$", "Wrong password."));
                }
            }
        });
    }

    void showMaterialDialog(String input) {
        new MaterialDialog.Builder(LoginActivity.this)
                .content(input)
                .positiveText(android.R.string.ok)
                .backgroundColorRes(R.color.colorPrimaryLoginDark)
                .show();
    }

    void onLoginSuccess() {
        Intent i = new Intent(this, LandingActivity.class);
        // int story = stories.get(position);
        // i.putExtra("story", Parcels.wrap(story));
        startActivity(i);
    }
}
