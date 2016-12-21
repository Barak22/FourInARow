package com.example.barak.fourinarow;

/**
 * Created by Barak on 7/5/2016.
 */
public class Player {

    private String m_Name;
    private char m_Sign;
    private int m_Score;
    private boolean m_IsComputer;

        /* -------------- Function members -------------- */

    public Player(String i_FirstPlayerName, char i_Sign, int i_Score, boolean i_IsComputer) // Constructor.
    {
        m_Name = i_FirstPlayerName;
        m_Sign = i_Sign;
        m_Score = i_Score;
        m_IsComputer = i_IsComputer;
    }

    public String getName() {
        return m_Name;
    }

    public char getSign() {
        return m_Sign;
    }

    public int getScore() {
        return m_Score;
    }

    public void SetScore(int i_Score) {
        m_Score = i_Score;
    }

    public boolean isComputer() {
        return m_IsComputer;
    }
}
