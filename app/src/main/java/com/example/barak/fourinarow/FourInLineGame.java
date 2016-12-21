package com.example.barak.fourinarow;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Barak on 7/6/2016.
 */

public class FourInLineGame // Class for four in line game.
{
        /* -------------- Data members -------------- */


    private final short k_CurrentPlayersToPlay = 2; // Number of players.

    private final short k_MaxSize = 8; // Maximum size for board width/height.

    private final short k_MinSize = 4; // Minimum size for board width/height.

    private final short k_QuitValue = (short) 'Q'; // Input char for if player quit.

    private final char k_FirstPlayerSign = 'X'; // First player board sign.

    private final char k_SecondPlayerSign = 'O'; // Second player board sign.

    public enum eGameStatus // Enum for current game status.
    {
        Not_Finish(0),
        Draw(1),
        Win(2),
        Quit(3);

        int value;

        eGameStatus(int i_Value) {
            value = i_Value;
        }

        int getValue() {
            return value;
        }
    }

    private Player[] m_PlayersArray = new Player[k_CurrentPlayersToPlay]; // Players array.

    private GameBoard m_Board = new GameBoard(); // Game board.

    public GameBoard getBoard() {
        return m_Board;
    }// Get board.

    private Player m_CurrentPlayer; // Current player in game.

    private FourInLineGame.eGameStatus m_CurrentGameStatus; // Current status in game.

    //public event HasStatusChangedDelegate StatusHasChanged; // Delegate for game status change.

    public char getCurrentPlayerSign() {
        return m_CurrentPlayer.getSign();
    }
    // Get player sign.

    public String getCurrentPlayerName() {
        return m_CurrentPlayer.getName();
    } // Get current player name.

    public int getCurrentPlayerScore() {
        return m_CurrentPlayer.getScore();
    }// Get current player score.

    public String getFirstPlayerName() {
        return m_PlayersArray[0].getName();
    } // Get first player name.

    public int getFirstPlayerScore() {
        return m_PlayersArray[0].getScore();
    } // Get first player score.

    public String getSecondPlayerName() {
        return m_PlayersArray[1].getName();
    } // Get second player name.

    public int getSecondPlayerScore() {
        return m_PlayersArray[1].getScore();
    } // Get second player score.

    public boolean getIsCurrentPlayerIsComputer() {
        return m_CurrentPlayer.isComputer();
    } // Get true/false if the current player is computer.

    private ArrayList<CellHasChangedHandleInterface> m_ListCellHasChangedListener = new ArrayList<CellHasChangedHandleInterface>();
    private ArrayList<HasStatusChangedHandler> m_ListStatusGameHasChangedListener = new ArrayList<HasStatusChangedHandler>();
    private int m_HowManyAI = 3;

        /* -------------- Function members -------------- */

    public void ResetGame() // Ckear board and set "Not finish" status for game.
    {
        m_Board.ClearBoard();
        m_CurrentPlayer = m_PlayersArray[0];
        m_CurrentGameStatus = eGameStatus.Not_Finish;
    }

    private void nextTurn() {
        if (m_CurrentPlayer == m_PlayersArray[0]) {
            m_CurrentPlayer = m_PlayersArray[1];
        } else {
            m_CurrentPlayer = m_PlayersArray[0];
        }
    } // Move turn to the second player.

    public void GetPlayers(GetTwoPlayersClass io_getTwoPlayersClass) {
        io_getTwoPlayersClass.SetCurrentPlayer(m_CurrentPlayer);

        if (io_getTwoPlayersClass.GetCurrentPlayer() == m_PlayersArray[0]) {
            io_getTwoPlayersClass.SetSecondPlayer(m_PlayersArray[1]);
        } else {
            io_getTwoPlayersClass.SetSecondPlayer(m_PlayersArray[0]);
        }
    } // Get players current and second in turn.

    public void MakeTurn(int i_ColumnIndexByUser) {
        int rowIndexAfterInsert;
        boolean isPlayerWin = false;

        if (i_ColumnIndexByUser == k_QuitValue) {
            m_CurrentGameStatus = FourInLineGame.eGameStatus.Quit; // Quit case.
        } else {
            rowIndexAfterInsert = m_Board.InsertCoinToCell(m_CurrentPlayer.getSign(), i_ColumnIndexByUser - 1); // Insert coin to column.
            if (rowIndexAfterInsert != -1000) {
                OnCellHasChanged(m_CurrentPlayer.getSign(), rowIndexAfterInsert, i_ColumnIndexByUser - 1);
                isPlayerWin = m_Board.HasWinner((short) (i_ColumnIndexByUser - 1), rowIndexAfterInsert, m_CurrentPlayer.getSign(), 4); // Check winner.
            }
        }

        if (isPlayerWin == true) {
            m_CurrentGameStatus = FourInLineGame.eGameStatus.Win; // Winner case.
            int currentScore = m_CurrentPlayer.getScore();
            currentScore++;
            m_CurrentPlayer.SetScore(currentScore); // Update score.
        } else if (checkFullBoard()) {
            m_CurrentGameStatus = FourInLineGame.eGameStatus.Draw; // Draw case.
        }

        if (m_CurrentGameStatus == FourInLineGame.eGameStatus.Not_Finish) {
            nextTurn(); // If game isn't finish switch turn.
        }

        if (m_CurrentGameStatus != FourInLineGame.eGameStatus.Not_Finish) {
            OnStatusHasChanged(m_CurrentGameStatus.getValue());
        }

        if (m_CurrentPlayer.isComputer() && m_CurrentGameStatus == FourInLineGame.eGameStatus.Not_Finish) {
            int computerChoice;
            int[] columnInputByUser = new int[1];
            AI artifitialIntelegance = new AI((short) RandVacantColumn(), k_FirstPlayerSign, k_SecondPlayerSign);

            MakeTurn(artifitialIntelegance.GetMove(m_Board) + 1);
        }
    } // Play turn of current player.

    protected void OnStatusHasChanged(int i_CurrentGameStatus) {
        if (!m_ListStatusGameHasChangedListener.isEmpty()) {
            for (HasStatusChangedHandler currentListener : m_ListStatusGameHasChangedListener) {
                currentListener.HasStatusChangedDelegate(i_CurrentGameStatus);
            }
        }
    }

    protected void OnCellHasChanged(char i_Sign, int i_RowIndexAfterInsert, int i_ColumnIndexByUser) {
        if (!m_ListCellHasChangedListener.isEmpty()) {
            for (CellHasChangedHandleInterface currentListener : m_ListCellHasChangedListener) {
                currentListener.CellHasChangedDelegate(i_Sign, i_RowIndexAfterInsert, i_ColumnIndexByUser);
            }
        }
    }

//    public int ArtificialIntelligence(int[] o_ColumnInputByUser, int i_HowManyTimes, char i_Sign) // Get artificial step for computerized player.
//    {
//        int[] tempColumnInputByUser = new int[1];
//        int rowForInsert;
//        int tmpMax = 0;
//        int max = 0;
//        int tmpMaxOnce = 0;
//        boolean checked = false;
//        if (i_HowManyTimes == 1) {
//            max = 0;
//        } else {
//
//            for (int currentColumnToCheck = 0; currentColumnToCheck < m_Board.getBoardWidth(); currentColumnToCheck++) {
//                if (m_Board.CheckVacantColumn(currentColumnToCheck)) {
//                    rowForInsert = m_Board.InsertCoinToCell(i_Sign, currentColumnToCheck); // Insert coin to column to check sequence.
//
//                    if (i_Sign == k_SecondPlayerSign) {
//                        max *= -1;
//                        max += ArtificialIntelligence(o_ColumnInputByUser, i_HowManyTimes - 1, k_FirstPlayerSign);
//                        max *= -1;
//                        max/=2;
//                    } else {
//                        max += ArtificialIntelligence(o_ColumnInputByUser, i_HowManyTimes - 1, k_SecondPlayerSign);
//                    }
//                    if (!checked) {
//                        tmpMaxOnce = max;
//                        checked = true;
//                    }
//                    tmpMax = tmpMaxOnce;
//
//                    if (checkPlayerSequence(i_Sign, 4, tempColumnInputByUser, currentColumnToCheck, rowForInsert)) {
//                        tmpMax += 1000 * (m_HowManyAI+i_HowManyTimes);
//                        if (tmpMax > max) {
//                            o_ColumnInputByUser[0] = tempColumnInputByUser[0];
//                        }
////                        tmpMax = tmpMaxOnce;
//                    } else if (checkPlayerSequence(i_Sign, 3, tempColumnInputByUser, currentColumnToCheck, rowForInsert)) {
//                        tmpMax += 100 * (m_HowManyAI+i_HowManyTimes);
//                        if (tmpMax > max) {
//                            o_ColumnInputByUser[0] = tempColumnInputByUser[0];
//                        }
////                        tmpMax = tmpMaxOnce;
//                    } else if (checkPlayerSequence(i_Sign, 2, tempColumnInputByUser, currentColumnToCheck, rowForInsert)) {
//                        tmpMax += 10 * (m_HowManyAI+i_HowManyTimes);
//                        if (tmpMax > max) {
//                            o_ColumnInputByUser[0] = tempColumnInputByUser[0];
//                        }
////                        tmpMax = tmpMaxOnce;
//                    }
//                    if (tmpMax > max) {
//                        max = tmpMax;
//                    }
//                    m_Board.ClearUpperCell(currentColumnToCheck, rowForInsert);
//                }
//            }
//        }
//        if (max == 0 && i_HowManyTimes == m_HowManyAI) {
//            o_ColumnInputByUser[0] = randVacantColumn();
//        }
//        return max;
//    }

    private boolean checkPlayerSequence(char i_Sign, int i_Length, int[] o_ColumToInsert, int i_ColumnToCheck, int i_RowToCheck) {
        boolean isFoundSequence = false;
        int rowForInsert;

        // Check "better" step

        isFoundSequence = m_Board.HasWinner(i_ColumnToCheck, i_RowToCheck, i_Sign, i_Length); // Check sequence.

        if (isFoundSequence) {
//                isFoundSequence = true; // Set found sequence.
            o_ColumToInsert[0] = i_ColumnToCheck + 1; // Set better choice for computer.
        }

//            m_Board.ClearUpperCell(i_ColumnToCheck, rowForInsert); // Clear from coin from column.

        return isFoundSequence;
    } // Check sequence in board.

//public int ArtificialIntelligence(int[] i_ColumnInputByUser) // Get artificial step for computerized player.
//    {
//        boolean isFoundSequance = false;
//        int max=0;
//        int[] columnInputByUser = new int[1];
//        columnInputByUser[0] = randVacantColumn();
//
//        // First case - possible to win in a sequence of 4 coins.
//        if(checkPlayerSequence(m_PlayersArray[1].getSign(), 4, columnInputByUser))
//        {
//            max+=20;
//        }
//
//        // Second case - possible to block the second player to win in a sequence of 4 coins.
//            if(checkPlayerSequence(m_PlayersArray[0].getSign(), 4, columnInputByUser))
//            {
//                max+=18;
//            }
//
//        // Third case - possible to create a sequence of 3 signs.
//            if(checkPlayerSequence(m_PlayersArray[1].getSign(), 3, columnInputByUser))
//            {
//                max+=16;
//            }
//
//        // Fourth case - possible to block the second player to create sequence of 3 signs.
//            if(checkPlayerSequence(m_PlayersArray[0].getSign(), 3, columnInputByUser))
//            {
//                max+=14;
//            }
//
//        // Fivth case - possible to create a sequence of 2 signs.
//            if(checkPlayerSequence(m_PlayersArray[1].getSign(), 2, columnInputByUser))
//            {
//                max+=12;
//            }
//
//        return columnInputByUser[0];
//    }


    public int RandVacantColumn() {
        int vacantColumnsCounter = 0, columnsArrayIndex = 0, randIndex, randColumn;
        int[] validColumnsArray;
        Random rand = new Random();

        // Counting number of vacant columns.
        for (short columnForCheck = 0; columnForCheck < m_Board.getBoardWidth(); columnForCheck++) {
            if (m_Board.CheckVacantColumn(columnForCheck)) {
                vacantColumnsCounter++;
            }
        }

        validColumnsArray = new int[vacantColumnsCounter];

        // Build array of the vacant columns.
        for (short columnForCheck = 0; columnForCheck < m_Board.getBoardWidth(); columnForCheck++) {
            if (m_Board.CheckVacantColumn(columnForCheck)) {
                validColumnsArray[columnsArrayIndex] = (short) (columnForCheck + 1);
                columnsArrayIndex++;
            }
        }

        // Rand vacant column.
        randIndex = rand.nextInt(vacantColumnsCounter) + 1;
        randColumn = validColumnsArray[randIndex - 1];
        return randColumn;
    }  // Rands vacant column for computerized player.

    private boolean checkFullBoard() {
        boolean fullBoard = false;

        fullBoard = m_Board.CheckIsFullBoard();

        return fullBoard;
    } // Check if board is full.

    public boolean CheckIsValidColumn(int i_InputColumn) // Check range and vacancy f column.
    {
        boolean isValidColumn = false;

        if (i_InputColumn >= 0 && i_InputColumn <= m_Board.getBoardWidth() && m_Board.CheckVacantColumn(i_InputColumn) == true) {
            isValidColumn = true;
        }

        return isValidColumn;
    }

    public void ResetScores() {
        m_PlayersArray[0].SetScore(0);
        m_PlayersArray[1].SetScore(0);
    } // Reset players scores.

    public void IntiateGame(String i_FirstPlayerName, String i_SecondPlayerName, boolean i_IsComputer, int i_BoardHeight, int i_BoardWidth) {
        initiatePlayers(i_FirstPlayerName, i_SecondPlayerName, i_IsComputer);
        initiateBoard(i_BoardHeight, i_BoardWidth);
        m_CurrentGameStatus = FourInLineGame.eGameStatus.Not_Finish;
    } // Intiate game for the first turn.

    private void initiateBoard(int i_BoardHeight, int i_BoardWidth) {
        m_Board.InitiateBoard(i_BoardHeight, i_BoardWidth);
    } // Intiate board for the first turn.

    private void initiatePlayers(String i_FirstPlayerName, String i_SecondPlayerName, boolean i_IsComputer) {
        m_PlayersArray[0] = new Player(i_FirstPlayerName, k_FirstPlayerSign, 0, false);
        m_PlayersArray[1] = new Player(i_SecondPlayerName, k_SecondPlayerSign, 0, i_IsComputer);
        m_CurrentPlayer = m_PlayersArray[0];
    } // Intiate players for their first turn.

    public FourInLineGame.eGameStatus GetGameStatus() // Check end of game.
    {
        return m_CurrentGameStatus;
    }

    public char[][] GetBoard() {
        return m_Board.GetBoard();
    } // Get board.

    public boolean CheckBoardValidSize(short i_Height, short i_Width) // Check if board has valid size.
    {
        boolean isValidSize = false;

        if ((i_Height >= k_MinSize && i_Width >= k_MinSize) && (i_Height <= k_MaxSize && i_Width <= k_MaxSize)) {
            isValidSize = true;
        }

        return isValidSize;
    }

    public void AddCellHasChangedHandleListener(CellHasChangedHandleInterface i_Listener) {
        m_ListCellHasChangedListener.add(i_Listener);
    }

    public void RemoveCellHasChangedHandleListener(CellHasChangedHandleInterface i_Listener) {
        m_ListCellHasChangedListener.remove(i_Listener);
    }

    public void AddHasStatusChangedListener(HasStatusChangedHandler i_Listener) {
        m_ListStatusGameHasChangedListener.add(i_Listener);
    }

    public void RemoveHasStatusChangedListener(HasStatusChangedHandler i_Listener) {
        m_ListStatusGameHasChangedListener.remove(i_Listener);
    }
}
