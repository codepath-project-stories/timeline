package com.codepath.timeline.activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.VideoView;

import com.codepath.timeline.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.input_email)
    EditText input_email;
    @BindView(R.id.login_video)
    VideoView login_video;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_login);
        ButterKnife.bind(this);

        input_email.requestFocus();
        login_video.setVideoURI(
                Uri.parse(
                        "android.resource://"
                                + getPackageName() + "/"
                                + R.raw.login_video_1
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

    // http://guides.codepath.com/android/Activity-Lifecycle
    @Override
    protected void onResume() {
        super.onResume();
        login_video.start();
    }
}
