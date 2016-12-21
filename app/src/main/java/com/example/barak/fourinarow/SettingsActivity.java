package com.example.barak.fourinarow;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {


    public final static String k_NumOfRows = "com.example.barak.fourinarow.rows";
    public final static String k_NumOfCols = "com.example.barak.fourinarow.cols";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void buttonCancelOnClicked(View sender) {
        this.finish();
    }

    public void buttonOKOnClick(View sender) {

        Spinner rowsSpinner = (Spinner) findViewById(R.id.spinnerRows2);
        int numOfRows = Integer.parseInt(rowsSpinner.getSelectedItem().toString());
        Spinner colsSpinner = (Spinner) findViewById(R.id.spinnerCols);
        int numOfCols = Integer.parseInt(colsSpinner.getSelectedItem().toString());

        Intent intent = getIntent();
        intent.putExtra(k_NumOfRows, numOfRows);
        intent.putExtra(k_NumOfCols, numOfCols);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }


}
