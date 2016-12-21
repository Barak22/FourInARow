package com.example.barak.fourinarow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Barak on 24/07/2016.
 */
public class AI {

    private class Node {
        private final GameBoard r_GameBoard;
        private final ArrayList<Node> r_Paths;

        public Node(GameBoard i_GameBoard) {
            r_GameBoard = i_GameBoard;
            r_Paths = new ArrayList<Node>();
        }


        public GameBoard GetBoard() {
            return r_GameBoard;
        }

        public ArrayList GetPaths() {
            return r_Paths;
        }

    }

    private final short k_Depth = 2;
    private final short r_RandomSelection;
    private final char r_FirstPlayerSign;
    private final char r_SecondPlayerSign;

    public AI(short i_RandomNumber, char i_FirstPlayerSign, char i_SecondPlayerSign) {
        r_RandomSelection = i_RandomNumber;
        r_FirstPlayerSign = i_FirstPlayerSign;
        r_SecondPlayerSign = i_SecondPlayerSign;

    }

    public short GetMove(GameBoard i_GameBoard) {
        GameBoard newBoard;
        Node path;
        Node node;
        ArrayList<Short> listOfPossibleMoves;
        ArrayList<Short> listOfGoodMoves = new ArrayList<Short>();
        Random randomNum = new Random();
        double maximunScore = -Double.MAX_VALUE;
        double[] bucketOfScores;
        char currentPlayerSign = r_SecondPlayerSign;

        node = new Node(i_GameBoard);
        listOfPossibleMoves = getListOfPossibleMoves(node);
        bucketOfScores = new double[listOfPossibleMoves.size()];
        for (int i = 0; i < listOfPossibleMoves.size(); i++) {
            newBoard = new GameBoard(i_GameBoard);
            newBoard.InsertCoinToCell(r_SecondPlayerSign, listOfPossibleMoves.get(i));
            path = new Node(newBoard);
            createTree(r_SecondPlayerSign, path, 0);
            bucketOfScores[i] = calculateNodeScore(path, r_SecondPlayerSign, 0);
        }

        for (int i = 0; i < bucketOfScores.length; i++) {
            if (bucketOfScores[i] > maximunScore) {
                listOfGoodMoves.clear();
                listOfGoodMoves.add((short) i);
                maximunScore = bucketOfScores[i];
            } else if (bucketOfScores[i] == maximunScore) {
                listOfGoodMoves.add((short) i);
            }
        }
        return listOfPossibleMoves.get(listOfGoodMoves.get(randomNum.nextInt(listOfGoodMoves.size())));
    }

    private ArrayList<Short> getListOfPossibleMoves(Node i_Node) {
        ArrayList<Short> listOfMoves = new ArrayList<Short>();

        for (short i = 0; i < i_Node.GetBoard().getBoardWidth(); i++) {
            if (i_Node.GetBoard().CheckVacantColumn(i)) {
                listOfMoves.add(i);
            }
        }

        return listOfMoves;
    }

    private void createTree(char i_PlayerSign, Node i_Root, int i_Depth) {
        if (i_Depth < k_Depth) {
            List<Short> listOfPossibleMoves = getListOfPossibleMoves(i_Root);

            for (int i = 0; i < listOfPossibleMoves.size(); i++) {
                GameBoard newBoard = new GameBoard(i_Root.GetBoard());
                newBoard.InsertCoinToCell(i_PlayerSign, listOfPossibleMoves.get(i));
                Node path = new Node(newBoard);
                createTree(getRivalSign(i_PlayerSign), path, i_Depth + 1);
                i_Root.GetPaths().add(path);
            }
        }
    }

    private double calculateNodeScore(Node i_Node, char i_CurrentPlayerSign, int i_Depth) {
        double score = 0;

        for (int i = 0; i < i_Node.GetBoard().getBoardWidth(); i++) {
            for (int j = 0; j < i_Node.GetBoard().getBoardHeight(); j++) {
                if (i_Node.GetBoard().HasWinner(i, j, i_CurrentPlayerSign, 4)) {
                    if (i_Depth == 0) {
                        score = Double.MAX_VALUE;
                    } else {
                        score += (k_Depth - i_Depth + 1) * 10;
                    }
                } else if (i_Node.GetBoard().HasWinner(i, j, getRivalSign(i_CurrentPlayerSign), 4)) {
                    score -= (k_Depth - i_Depth + 1) * 1000;
                } else {
                    ArrayList<Node> currentPath = i_Node.GetPaths();
                    for (Node path : currentPath) {
                        score += calculateNodeScore(path, i_CurrentPlayerSign, i_Depth + 1);
                    }
                }
            }
        }

        return score;
    }

    public char getRivalSign(char i_CurrentPlayerSign) {
        if (i_CurrentPlayerSign == r_FirstPlayerSign) {
            return r_SecondPlayerSign;
        } else {
            return r_FirstPlayerSign;
        }
    }
}
