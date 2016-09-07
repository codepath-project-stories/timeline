package com.codepath.timeline.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.codepath.timeline.R;
import com.codepath.timeline.models.UserClient;
import com.codepath.timeline.network.ParseApplication;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {
    // LoginActivity calls LandingActivity

    private static final String TAG = LoginActivity.class.getSimpleName();

    static boolean play_video = true;

    VideoView login_video;

    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.input_fullname_layout)
    TextInputLayout input_fullname_layout;
    @BindView(R.id.input_fullname)
    EditText input_fullname;
    @BindView(R.id.input_email)
    EditText input_email;
    @BindView(R.id.input_password)
    EditText input_password;
    @BindView(R.id.button_start)
    Button button_start;
    @BindView(R.id.text_switch)
    TextView text_switch;

    SharedPreferences mSettings;
    boolean lock;
    boolean state;
    // false: login_here
    // true: create_account

    private Transition.TransitionListener mEnterTransitionListener;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

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
        if (UserClient.getCurrentUser() != null) {
            if (!ParseAnonymousUtils.isLinked(UserClient.getCurrentUser())) {
                onLoginSuccess();
            }
        }
        // If user is anonymous, ask the user to login or signup

        if (play_video) {
            setContentView(R.layout.activity_login);
            login_video = (VideoView) findViewById(R.id.login_video);
        }
        else {
            setContentView(R.layout.activity_login_no_video);
        }
        ButterKnife.bind(this);

        setupState();

        input_email.setText(mSettings.getString("input_email", ""));
        // input_email.requestFocus();

        if (play_video) {
            setupVideo();
        }

        setupAnimation();
    }

    // http://guides.codepath.com/android/Activity-Lifecycle
    @Override
    protected void onResume() {
        super.onResume();

        setupState();

        if (play_video) {
            if (login_video != null) {
                login_video.start();
            }
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

    void setupState() {
        state = false;
        input_fullname_layout.setVisibility(View.GONE);
        button_start.setText(getResources().getString(R.string.start));
        text_switch.setText(getResources().getString(R.string.create_account));
    }

    @OnClick(R.id.button_start)
    void start() {
        if (!state) {
            login();
        }
        else {
            create_account();
        }
    }

    // TODO: need animation here
    @OnClick(R.id.text_switch)
    void switch_state() {
        if (!state) {
            // switch from login_here to create_account
            state = true;
            input_fullname_layout.setVisibility(View.VISIBLE);
            text_switch.setText(getResources().getString(R.string.login_here));
        }
        else {
            // switch from create_account to login_here
            state = false;
            input_fullname_layout.setVisibility(View.GONE);
            text_switch.setText(getResources().getString(R.string.create_account));
        }
    }

    void login() {
        Log.d(TAG, "login()");
        if (lock) {
            return;
        }
        if (input_email.getText().length() == 0
                || input_password.getText().length() == 0) {
            showMaterialDialog(getResources().getString(R.string.missing));
            return;
        }
        Log.d(TAG, "login() starts lock");
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
                            ParseUser currentUser = UserClient.getCurrentUser();
                            if (currentUser != null) {
                                // do stuff with the user
                                Log.d("logInInBackground", currentUser.toString());
                                onLoginSuccess();
                            } else {
                                // something wrong
                                // show the signup or login screen
                                Log.d("logInInBackground", "getCurrentUser failed");
                                // create();
                                showMaterialDialog(convertParseExceptionToString(e));
                            }
                        } else {
                            // usually go here
                            // Signup failed. Look at the ParseException to see what happened.
                            Log.d("logInInBackground", "done failed");
                            Log.d("logInInBackground", e.toString());
                            // create();
                            showMaterialDialog(convertParseExceptionToString(e));
                        }
                    }
        });
    }
    void create_account() {
        // Create the ParseUser
        ParseUser newUser = new ParseUser();
        // Set core properties
        newUser.setUsername(input_email.getText().toString());
        newUser.setEmail(input_email.getText().toString());
        newUser.setPassword(input_password.getText().toString());
        // Set custom properties
        newUser.put("name", input_fullname.getText().toString());
        newUser.put("demoCreated", "false");
        // TODO: get name and profileImageUrl from EditText
        if (ParseApplication.DEMO_MODE) {
            newUser.put("profileImageUrl",
                    "https://pbs.twimg.com/profile_images/761636511238516736/k5XbteDD.jpg");
        }
        // Invoke signUpInBackground
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    ParseUser currentUser = UserClient.getCurrentUser();
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
                    showMaterialDialog(convertParseExceptionToString(e));
                }
            }
        });
    }

    String convertParseExceptionToString(ParseException e) {
        /*
        return e.toString()
                .replaceAll("^.*: ", "")
                .replaceAll("^Account already exists.*$", "Wrong password.");
                */
        return e.toString()
                .replaceAll("^.*: ", "");
    }

    void showMaterialDialog(String input) {
        lock = false;
        button_start.setText(getResources().getString(R.string.start));
        /*
        new MaterialDialog.Builder(LoginActivity.this)
                .content(input)
                .positiveText(android.R.string.ok)
                // .backgroundColorRes(R.color.colorPrimaryLoginDark)
                .backgroundColorRes(R.color.colorPrimaryDark)
                .show();
                */
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .show();
    }

    void onLoginSuccess() {
        lock = false;
        if (input_fullname != null) {
            input_fullname.setText("");
        }
        if (input_email != null) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("input_email", input_email.getText().toString());
            editor.apply();
        }
        if (input_password != null) {
            input_password.setText("");
        }
        Intent i;
        if (ParseApplication.TEST_PARSE) {
            i = new Intent(this, TestParseActivity.class);
        }
        else if (ParseApplication.TEST_SPOTIFY) {
            i = new Intent(this, SpotifyActivity.class);
        }
        else if (ParseApplication.TEST_TokenAutoComplete) {
            i = new Intent(this, TokenActivity.class);
        }
        else {
            i = new Intent(this, LandingActivity.class);
        }
        // int story = stories.get(position);
        // i.putExtra("story", Parcels.wrap(story));
        startActivity(i);
    }

    void enterReveal(final View myView) {
        // previously invisible view
        // final View myView = findViewById(R.id.my_view);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        myView.setVisibility(View.VISIBLE);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    void setupAnimation() {
        root.setVisibility(View.INVISIBLE);
        root.post(new Runnable() {
            @Override
            public void run() {
                //create your anim here
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 1 second
                        enterReveal(root);
                    }
                }, 1000);
            }
        });
        mEnterTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                Log.d("TransitionListener", "onTransitionStart");
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                Log.d("TransitionListener", "onTransitionEnd");
                enterReveal(root);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                Log.d("TransitionListener", "onTransitionCancel");
            }

            @Override
            public void onTransitionPause(Transition transition) {
                Log.d("TransitionListener", "onTransitionPause");
            }

            @Override
            public void onTransitionResume(Transition transition) {
                Log.d("TransitionListener", "onTransitionResume");
            }
        };
        getWindow().getEnterTransition().addListener(mEnterTransitionListener);
    }

}
