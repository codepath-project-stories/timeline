package com.codepath.timeline.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.timeline.R;
import com.codepath.timeline.util.ParseApplication;
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
    @BindView(R.id.button_start)
    Button button_start;

    boolean lock;
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        lock = false;

        if (!ParseApplication.TURN_ON_PARSE) {
            onLoginSuccess();
            return;
        }

        // Determine whether the current user is an anonymous user
        if (ParseUser.getCurrentUser() != null) {
            if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                onLoginSuccess();
            }
        }
        // If user is anonymous, ask the user to login or signup

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        input_email.setText(mSettings.getString("input_email", ""));
        // input_email.requestFocus();

        setupVideo();
    }

    // http://guides.codepath.com/android/Activity-Lifecycle
    @Override
    protected void onResume() {
        super.onResume();
        if (login_video != null) {
            login_video.start();
        }
    }

    private void setupVideo() {
        // https://developer.android.com/guide/appendix/media-formats.html
        //
        // SD (High quality)
        // codec        H.264 Baseline Profile codec
        // resolution   480 x 360 px
        // frame rate   30 fps
        // bitrate      500 Kbps
        //
        // Currently we have
        // codec        h264 constrained baseline L3.1 with yuv420p
        // resolution   544x720 or 854x480
        // frame rate   30 fps
        // bitrate      500 Kbps
        //
        login_video.setVideoURI(
                Uri.parse(
                        "android.resource://"
                                + getPackageName() + "/"
                                + R.raw.login_video_1
                        // TODO: have a better video
                        // R.raw.login_video_2
                        // R.raw.login_video_3
                )
        );
        login_video.start();
        login_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    @OnClick(R.id.button_start)
    void login() {
        Log.d("LoginActivity", "login()");
        if (lock) {
            return;
        }
        if (input_email.getText().length() == 0
                || input_password.getText().length() == 0) {
            showMaterialDialog(getResources().getString(R.string.missing));
            return;
        }
        Log.d("LoginActivity", "login() starts lock");
        lock = true;
        button_start.setText(getResources().getString(R.string.loading));
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
        lock = false;
        button_start.setText(getResources().getString(R.string.start));
        new MaterialDialog.Builder(LoginActivity.this)
                .content(input)
                .positiveText(android.R.string.ok)
                .backgroundColorRes(R.color.colorPrimaryLoginDark)
                .show();
    }

    void onLoginSuccess() {
        lock = false;
        if (button_start != null) {
            button_start.setText(getResources().getString(R.string.start));
        }
        if (input_email != null) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("input_email", input_email.getText().toString());
            editor.apply();
        }
        if (input_password != null) {
            input_password.setText("");
        }
        Intent i = new Intent(this, LandingActivity.class);
        // int story = stories.get(position);
        // i.putExtra("story", Parcels.wrap(story));
        startActivity(i);
    }
}
