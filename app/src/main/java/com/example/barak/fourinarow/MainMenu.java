package com.example.barak.fourinarow;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;


public class MainMenu extends AppCompatActivity {

    public final static String k_Player1Name = "com.example.barak.fourinarow.player1name";
    public final static String k_Player2Name = "com.example.barak.fourinarow.player2name";
    public final static String k_IsComputer = "com.example.barak.fourinarow.iscomputer";
    private static final int START_GAME = 671;
    private static final int START_SETTINGS = 398;

    private Intent m_SettingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        m_SettingInfo = new Intent("SettingsInfo");
        m_SettingInfo.putExtra(k_Player1Name, "User");
        m_SettingInfo.putExtra(SettingsActivity.k_NumOfRows, 4);
        m_SettingInfo.putExtra(SettingsActivity.k_NumOfCols, 4);
    }


    public void ExitGame_OnClick(View sender) {
        AlertDialog.Builder exitAlert = new AlertDialog.Builder(this);
        exitAlert.setMessage("Are your sure you want to quit?");
        exitAlert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleExitGame();
            }
        });
        exitAlert.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        exitAlert.show();
    }

    private void handleExitGame() {
        this.finish();
    }

    public void Settings_OnClick(View sender) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, START_SETTINGS);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == START_SETTINGS) {
            if (resultCode == RESULT_OK) {
                m_SettingInfo = data;
            }
        } else if (requestCode == START_GAME) {
            if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    public void StartGame_OnClick(View sender) {
        EditText player1 = (EditText) findViewById(R.id.editTextPlayer1Name);
        String player1Name = player1.getText().toString();
        EditText player2 = (EditText) findViewById(R.id.editTextPlayer2Name);
        String player2Name = player2.getText().toString();
        CheckBox checkBoxPlayer2 = (CheckBox) findViewById(R.id.checkBoxPlayer2);
        boolean isComputer = checkBoxPlayer2.isChecked();

        m_SettingInfo.setClass(this, FourInARowGameMenegerDynamic.class);
        if (player1Name != "") {
            m_SettingInfo.putExtra(k_Player1Name, player1Name);
        }
        m_SettingInfo.putExtra(k_Player2Name, player2Name);
        m_SettingInfo.putExtra(k_IsComputer, isComputer);

        startActivityForResult(m_SettingInfo, START_GAME);
    }


    public void About_OnClick(View sender) {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }

    public void checkBoxHasChanged(View sender) {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxPlayer2);
        EditText editText = (EditText) findViewById(R.id.editTextPlayer2Name);
        if (checkBox.isChecked()) {
            editText.setEnabled(true);
            editText.setText("");
        } else {
            editText.setEnabled(false);
            editText.setText("[Computer]");
        }
    }
}
