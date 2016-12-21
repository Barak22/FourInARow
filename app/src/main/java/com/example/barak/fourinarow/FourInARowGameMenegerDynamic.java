package com.example.barak.fourinarow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FourInARowGameMenegerDynamic extends AppCompatActivity implements CellHasChangedHandleInterface, HasStatusChangedHandler, ClearWinnerListInterface, WinCellHasCheckedInterface, ButtonHandleUserChoiceInterface {

    /* -------------- Data members -------------- */

    private final int k_PictureSize = 67;

    private final int k_Draw = 1;

    private final int k_HasWinner = 2;

    private long mLastClickTime = 0;

    private TextView labelFirstPlayer;
    private TextView labelSecondPlayer;
    private TextView labelPlayer1Score;
    private TextView labelPlayer2Score;

    private FourInLineGame r_TheGame;

    private ArrayList<MyButton> r_BoardButtonList;

    private ArrayList<MyButton> r_WinnerList = new ArrayList<MyButton>();

    private Button pictureBoxFalling;

    private boolean m_HasNewGameStartNow = false;

    private boolean m_IsUserChangeGameProperties = false;


    private boolean m_GamePropertiesChangeRequest = false;
    private short m_CurrentUserChoice;
    private short[] m_NumOfVacantRowForEachCol;
    private Intent m_Intent;
    private RelativeLayout m_Layout;
    private int m_LayoutWidth;
    private int m_LayoutHeight;
    private GridLayout m_BoardGridLayout;
    private TextView m_TextViewTitle;
    private Button m_LastButtonHasChange = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_in_arow_game_meneger_dynamic);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        int result = am.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                                              @Override
                                              public void onAudioFocusChange(int focusChange) {

                                              }
                                          },
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Start playback.
        }

        m_Layout = (RelativeLayout) findViewById(R.id.DymanicRelativeLayout);
        ViewTreeObserver vto = m_Layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_Layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                m_LayoutWidth = m_Layout.getMeasuredWidth();
                m_LayoutHeight = m_Layout.getMeasuredHeight();
                init();

            }
        });
    }


    private void init() {
        m_Intent = getIntent();
        r_TheGame = new FourInLineGame();
        r_TheGame.AddCellHasChangedHandleListener(this);
        r_TheGame.AddHasStatusChangedListener(this);
        r_TheGame.getBoard().AddWinCellHasChangedListener(this);
        r_TheGame.getBoard().AddClearWinnerListListener(this);

        initiateGame();
        r_BoardButtonList = new ArrayList<MyButton>(r_TheGame.getBoard().getBoardWidth() * r_TheGame.getBoard().getBoardHeight());
        myInitializeComponent();
    }

    private void initiateGame() {
        String firstPlayerName = m_Intent.getStringExtra(MainMenu.k_Player1Name);
        String secondPlayerName = m_Intent.getStringExtra(MainMenu.k_Player2Name);
        int boardRows = m_Intent.getIntExtra(SettingsActivity.k_NumOfRows, 0);
        int boardCols = m_Intent.getIntExtra(SettingsActivity.k_NumOfCols, 0);
        boolean isPlayer2IsAComputer = m_Intent.getBooleanExtra(MainMenu.k_IsComputer, true);
        r_TheGame.IntiateGame(firstPlayerName, secondPlayerName, !isPlayer2IsAComputer, boardRows, boardCols);
    }


    private void myInitializeComponent() {
        m_TextViewTitle = new TextView(this);
        m_TextViewTitle.setText("Four In A Row!");
        m_TextViewTitle.setWidth(m_LayoutWidth);
        m_TextViewTitle.setTextSize(30);
        m_TextViewTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        m_Layout.addView(m_TextViewTitle);

        m_BoardGridLayout = new GridLayout(this);
        m_BoardGridLayout.setColumnCount(r_TheGame.getBoard().getBoardWidth());
        m_BoardGridLayout.setRowCount(r_TheGame.getBoard().getBoardHeight());
        m_BoardGridLayout.setLayoutParams(new RelativeLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        m_Layout.addView(m_BoardGridLayout, m_LayoutWidth, m_LayoutHeight);

//        initializeInsertionButtons();
        m_TextViewTitle.measure(0, 0);
        m_BoardGridLayout.setY(((int) (m_TextViewTitle.getMeasuredHeight() * 1.2)));
        initializeBoardButtons();


        // First Player //
        TextView player1Label = new TextView(this);
        player1Label.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        player1Label.setText("Player 1:");
        player1Label.setX(m_LayoutWidth / 10);
        player1Label.setY(((float) (m_LayoutHeight / 10 * 9.2)));
        m_Layout.addView(player1Label);

        Button firstPlayerColor = new Button(this);
        firstPlayerColor.setX(player1Label.getX() - 50);
        firstPlayerColor.setY(player1Label.getY() - 7);
        firstPlayerColor.getBackground().setColorFilter(getResources().getColor(R.color.colorFirstPlayer), PorterDuff.Mode.DARKEN);
        firstPlayerColor.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        m_Layout.addView(firstPlayerColor);

        TextView score1Label = new TextView(this);
        score1Label.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        score1Label.setText("Score:");
        score1Label.setX(m_LayoutWidth / 10 * 5);
        score1Label.setY((float) (m_LayoutHeight / 10 * 9.2));
        m_Layout.addView(score1Label);

        labelFirstPlayer = new TextView(this);
        labelPlayer1Score = new TextView(this);
        initializePlayer(player1Label, labelFirstPlayer, score1Label, labelPlayer1Score, r_TheGame.getFirstPlayerName(), r_TheGame.getFirstPlayerScore());

// Second Player //
        TextView player2Label = new TextView(this);
        player2Label.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        player2Label.setText("Player 2:");
        player1Label.measure(0, 0);
        player2Label.setX(player1Label.getX());
        player2Label.setY(player1Label.getY() + player1Label.getMeasuredHeight());
        m_Layout.addView(player2Label);

        Button secondPlayerColor = new Button(this);
        secondPlayerColor.setX(player2Label.getX() - 50);
        secondPlayerColor.setY(player2Label.getY() - 7);
        secondPlayerColor.getBackground().setColorFilter(getResources().getColor(R.color.colorSecondPlayer), PorterDuff.Mode.DARKEN);
        secondPlayerColor.setLayoutParams(new RelativeLayout.LayoutParams(50, 50));
        m_Layout.addView(secondPlayerColor);

        TextView score2Label = new TextView(this);
        score2Label.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        score2Label.setText("Score:");
        score1Label.measure(0, 0);
        score2Label.setX(score1Label.getX());
        score2Label.setY(score1Label.getY() + score1Label.getMeasuredHeight());
        m_Layout.addView(score2Label);

        labelSecondPlayer = new TextView(this);
        labelPlayer2Score = new TextView(this);
        initializePlayer(player2Label, labelSecondPlayer, score2Label, labelPlayer2Score, r_TheGame.getSecondPlayerName(), r_TheGame.getSecondPlayerScore());

        Button buttonNewGame = new Button(this);
        buttonNewGame.setText("New Game");
        score1Label.measure(0, 0);
        buttonNewGame.setWidth(m_LayoutWidth / 6);
        buttonNewGame.setX(score1Label.getX() + score1Label.getMeasuredWidth() + 100);
        buttonNewGame.setY(score1Label.getY() - 20);
        score2Label.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r_TheGame.ResetGame(); // Resets the game for next round.
                clearBoardButtons(); // Clears the board buttons.
                enableAllDelegateForBoardButtons();
                m_LastButtonHasChange = null;
            }
        });
        m_Layout.addView(buttonNewGame);


    }


    private void initializePlayer(TextView i_Player1Label, TextView i_PlayerNameLabel, TextView i_ScoreLabel, TextView i_PlayerScoreLabel, String i_PlayerName, int i_PlayerScore) {

        i_PlayerNameLabel.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        i_PlayerNameLabel.setText(i_PlayerName);
        i_Player1Label.measure(0, 0);
        i_PlayerNameLabel.setX(i_Player1Label.getX() + i_Player1Label.getMeasuredWidth() + 5);
        i_PlayerNameLabel.setY(i_Player1Label.getY());
        m_Layout.addView(i_PlayerNameLabel);


        i_PlayerScoreLabel.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        i_PlayerScoreLabel.setText(String.valueOf(i_PlayerScore));
        i_ScoreLabel.measure(0, 0);
        i_PlayerScoreLabel.setX(i_ScoreLabel.getX() + i_ScoreLabel.getMeasuredWidth() + 5);
        i_PlayerScoreLabel.setY(i_ScoreLabel.getY());
        m_Layout.addView(i_PlayerScoreLabel);

    }

    private void initializeBoardButtons() {
        for (int i = 1; i <= r_TheGame.getBoard().getBoardHeight(); i++) {
            for (int j = 0; j < r_TheGame.getBoard().getBoardWidth(); j++) {
                MyButton newButton = new MyButton(this);
                newButton.SetColIndex(j);
                newButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View sender) {
                        buttonHandleUserChoice_Click(sender);
                    }
                });
                m_BoardGridLayout.addView(newButton, m_LayoutWidth / r_TheGame.getBoard().getBoardWidth(), m_LayoutHeight / 10);
                r_BoardButtonList.add(newButton);
            }
        }
    }


    private void clearBoardButtons() {
        for (MyButton currentBoardButton : r_BoardButtonList) {
            currentBoardButton.getBackground().clearColorFilter();

        }
    }

    private void updateScores() {
        labelPlayer1Score.setText(String.valueOf(r_TheGame.getFirstPlayerScore()));
        labelPlayer2Score.setText(String.valueOf(r_TheGame.getSecondPlayerScore()));
    }


    @Override
    public void buttonHandleUserChoice_Click(View sender) {
        boolean validUserChoice;
        short userChoice;
        MyButton buttonSender = (MyButton) sender;
        userChoice = Short.parseShort(String.valueOf(buttonSender.GetColIndex()));
        validUserChoice = r_TheGame.CheckIsValidColumn(userChoice);

        if (validUserChoice) {
            r_TheGame.MakeTurn(userChoice + 1);
        }

    }

    @Override
    public void ClearWinnerListDelegate() {
        r_WinnerList.clear();
    }

    @Override
    public void HasStatusChangedDelegate(int i_Status) {
        updateScores(); // Uptades the scores for both players.

        // Checks which status the game has changed to.
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        disableAllDelegateForBoardButtons();
        m_LastButtonHasChange = null;
        switch (i_Status) {
            case k_HasWinner:
                builder1.setMessage("It's a win! Do you play another round?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                r_TheGame.ResetGame(); // Resets the game for next round.
                                clearBoardButtons(); // Clears the board buttons.
                                enableAllDelegateForBoardButtons();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                handleCloseMessage();
                            }
                        });

                break;

            case k_Draw:
                builder1.setMessage("It's a draw. Do you play another round?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                r_TheGame.ResetGame(); // Resets the game for next round.
                                clearBoardButtons(); // Clears the board buttons.
                                enableAllDelegateForBoardButtons();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                handleCloseMessage();
                            }
                        });
                break;

        }
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    @Override
    public void WinCellHasCheckedDelegate(int i_Row, int i_Col) {
        r_WinnerList.add(r_BoardButtonList.get((i_Row * r_TheGame.getBoard().getBoardWidth()) + i_Col));
    }

    @Override
    public void CellHasChangedDelegate(char i_Sign, int i_Row, int i_Col) {
        MyButton button = (MyButton) r_BoardButtonList.get((i_Row * r_TheGame.getBoard().getBoardWidth()) + i_Col);


        if (i_Sign == 'O') {
            if (m_LastButtonHasChange != null) {
                m_LastButtonHasChange.getBackground().setColorFilter(getResources().getColor(R.color.colorFirstPlayer), PorterDuff.Mode.DARKEN);
            }
            m_LastButtonHasChange = button;
            button.getBackground().setColorFilter(getResources().getColor(R.color.lastButtonColor), PorterDuff.Mode.DARKEN);
        } else {
            if (m_LastButtonHasChange != null) {

                m_LastButtonHasChange.getBackground().setColorFilter(getResources().getColor(R.color.colorSecondPlayer), PorterDuff.Mode.DARKEN);
            }
            m_LastButtonHasChange = button;
            button.getBackground().setColorFilter(getResources().getColor(R.color.colorFirstPlayer), PorterDuff.Mode.DARKEN);
        }
    }


    private void handleCloseMessage() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("OK GoodBye!");
        builder1.setCancelable(true);

        setResult(RESULT_CANCELED, m_Intent);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        exitFromTheApp();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void exitFromTheApp() {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to quit?" +
                "It will reset the game. ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void disableAllDelegateForBoardButtons() {
        for (Button button : r_BoardButtonList) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


    private void enableAllDelegateForBoardButtons() {
        for (Button button : r_BoardButtonList) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonHandleUserChoice_Click(v);
                }
            });
        }
    }

}
