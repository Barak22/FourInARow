package com.example.barak.fourinarow;

import java.util.ArrayList;

/**
 * Created by Barak on 7/6/2016.
 */
public class GameBoard {

    /* -------------- Data members -------------- */

    private char[][] m_TheBoard; // Board array.

    private int m_BoardHeight; // Board height.

    private int m_BoardWidth; // Board width.

    public int getBoardHeight() {
        return m_BoardHeight;
    }// Get height of board.

    public int getBoardWidth() {
        return m_BoardWidth;
    } // Get width of board.

    public ArrayList<ClearWinnerListInterface> m_ClearWinnerListListeners = new ArrayList<ClearWinnerListInterface>();
    public ArrayList<WinCellHasCheckedInterface> m_WinCellHasChangedListListeners = new ArrayList<WinCellHasCheckedInterface>();

        /* -------------- Function members -------------- */

    public GameBoard(GameBoard i_GameBoard) {
        m_BoardHeight = i_GameBoard.getBoardHeight();
        m_BoardWidth = i_GameBoard.getBoardWidth();
        m_TheBoard = new char[i_GameBoard.getBoardHeight()][i_GameBoard.getBoardWidth()];

        for (int i = 0; i < m_BoardWidth; i++) {
            for (int j = 0; j < m_BoardHeight; j++) {
                m_TheBoard[j][i] = i_GameBoard.GetBoard()[j][i];
            }
        }
    }


    public GameBoard() {

    }

    public char[][] GetBoard() {
        return m_TheBoard;
    } // Get board.

    public void InitiateBoard(int i_BoardHeight, int i_BoardWidth) // Intiate board with the input height and width.
    {
        m_BoardHeight = i_BoardHeight; // Set height.

        m_BoardWidth = i_BoardWidth; // Set width.

        m_TheBoard = new char[m_BoardHeight][m_BoardWidth]; // Board allocation

        ClearBoard(); // Clear all cells in board.
    }

    public void ClearBoard() {
        for (int i = 0; i < m_BoardHeight; i++) {
            for (int j = 0; j < m_BoardWidth; j++) {
                m_TheBoard[i][j] = ' ';
            }
        }
    } // Intiate empty board.

    public boolean CheckVacantColumn(int i_Cell) {
        boolean isVacant = false;

        if (m_TheBoard[0][i_Cell] == ' ') {
            isVacant = true; // If top in column is avaialble - column is vacant.
        }

        return isVacant;
    } // Check if coulmn is vacant in board.

    public int InsertCoinToCell(char i_Sign, int i_columnToInsert) // Insert coin of player to board.
    {
        int rowToInsert = m_BoardHeight - 1;

        while (rowToInsert != -1 && m_TheBoard[rowToInsert][i_columnToInsert] != ' ') {
            rowToInsert--; // Intiate row for coin.
        }
        if (rowToInsert != -1) {
            m_TheBoard[rowToInsert][i_columnToInsert] = i_Sign; // Set coin in board.
            return rowToInsert;
        } else {
            return -1000;
        }
    }

    public boolean HasWinner(int i_InputColumn, int i_RowToInsert, char i_Sign, int i_LengthOfSequance) // Check if there is a winner in board.
    {
        boolean hasWinner = false;

        if (checkSequence(i_InputColumn, i_RowToInsert, i_Sign, 0, 1, i_LengthOfSequance) == true) {
            hasWinner = true; // Sequence of coins in row.
        } else if (checkSequence(i_InputColumn, i_RowToInsert, i_Sign, -1, 0, i_LengthOfSequance) == true) {
            hasWinner = true; // Sequence of coins in Column.
        } else if (checkSequence(i_InputColumn, i_RowToInsert, i_Sign, -1, 1, i_LengthOfSequance) == true) {
            hasWinner = true; // Sequence of coins in right diagonal.
        } else if (checkSequence(i_InputColumn, i_RowToInsert, i_Sign, -1, -1, i_LengthOfSequance) == true) {
            hasWinner = true; // Sequence of coins in left diagonal.
        }

        return hasWinner;
    }

    private boolean checkSequence(int i_ColumnIndex, int i_RowIndex, char i_Sign, int i_RowMove, int i_ColumnMove, int i_LengthOfSequance) // Check sequence of coins.
    {
        int counterOfSequence = 0;
        boolean hasSequence = false;

        if (!m_ClearWinnerListListeners.isEmpty()) {
            for (ClearWinnerListInterface currentListener : m_ClearWinnerListListeners) {
                currentListener.ClearWinnerListDelegate();
            }
        }

        // Moving to a starting point check in board of sequence check.
        while ((i_RowIndex + i_RowMove) < m_BoardHeight && (i_RowIndex + i_RowMove) >= 0 && (i_ColumnIndex + i_ColumnMove) < m_BoardWidth && (i_ColumnIndex + i_ColumnMove) >= 0 && m_TheBoard[i_RowIndex + i_RowMove][i_ColumnIndex + i_ColumnMove] == i_Sign) {
            i_RowIndex += i_RowMove;
            i_ColumnIndex += i_ColumnMove;
        }

        i_RowMove *= -1;
        i_ColumnMove *= -1;

        // Checking sequence.
        while (hasSequence == false && i_RowIndex < m_BoardHeight && i_RowIndex >= 0 && i_ColumnIndex < m_BoardWidth && i_ColumnIndex >= 0 && m_TheBoard[i_RowIndex][i_ColumnIndex] == i_Sign) {

            if (!m_WinCellHasChangedListListeners.isEmpty()) {
                for (WinCellHasCheckedInterface currentListener : m_WinCellHasChangedListListeners) {
                    currentListener.WinCellHasCheckedDelegate(i_RowIndex, i_ColumnIndex);
                }
            }

            counterOfSequence++;

            i_RowIndex += i_RowMove;

            i_ColumnIndex += i_ColumnMove;

            if (counterOfSequence == i_LengthOfSequance) {
                hasSequence = true; // Sequence is valid with at least 4 coins of the same type.
            }
        }

        return hasSequence;
    }

    public void ClearUpperCell(int i_BoardColumn, int i_BoardRow) {
        m_TheBoard[i_BoardRow][i_BoardColumn] = ' ';
    } // Clear cell from board.

    public boolean CheckIsFullBoard() {
        int currentColumn = 0;
        boolean isFull = true;

        while (currentColumn < m_BoardWidth && isFull == true) {
            if (m_TheBoard[0][currentColumn] == ' ') {
                isFull = false;
            }

            currentColumn++;
        }

        return isFull;
    } // Check if board is full.

    public void AddClearWinnerListListener(ClearWinnerListInterface i_Listener) {
        m_ClearWinnerListListeners.add(i_Listener);
    }

    public void RemoveClearWinnerListListener(ClearWinnerListInterface i_Listener) {
        m_ClearWinnerListListeners.remove(i_Listener);
    }

    public void AddWinCellHasChangedListener(WinCellHasCheckedInterface i_Listener) {
        m_WinCellHasChangedListListeners.add(i_Listener);
    }

    public void RemoveWinCellHasChangedListener(WinCellHasCheckedInterface i_Listener) {
        m_WinCellHasChangedListListeners.remove(i_Listener);
    }
}
