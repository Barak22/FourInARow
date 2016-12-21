package com.example.barak.fourinarow;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void Back_OnClick(View sender) {
        finish();
    }
}
