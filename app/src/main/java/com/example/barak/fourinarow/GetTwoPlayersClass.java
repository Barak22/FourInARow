package com.example.barak.fourinarow;

/**
 * Created by Barak on 7/6/2016.
 */
public class GetTwoPlayersClass {
    Player m_CurrentPlayer;
    Player m_SecondPlayer;

    public Player GetCurrentPlayer() {
        return m_CurrentPlayer;
    }

    public void SetCurrentPlayer(Player i_CurrentPlayer) {
        m_CurrentPlayer = i_CurrentPlayer;
    }


    public Player GetSecondPlayer() {
        return m_SecondPlayer;
    }

    public void SetSecondPlayer(Player i_SecondPlayer) {
        m_SecondPlayer = i_SecondPlayer;
    }
}

