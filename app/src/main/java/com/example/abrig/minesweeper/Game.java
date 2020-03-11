package com.example.abrig.minesweeper;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class to represent a game history object.
 */
public class Game {

    private int gameNum, time, score;
    private boolean win;
    private Grid grid;
    private int difficulty;

    Game(int gameNum, boolean win, int time, int score, MineSweeper mineSweeper) {
        this.gameNum = gameNum;
        this.win = win;
        this.time = time;
        this.score = score;
        this.grid = mineSweeper.getGameGrid();
        this.difficulty = mineSweeper.getDifficulty();
    }

    int getDifficulty() {
        return difficulty;
    }

    int getSecondsPast() {
        return time;
    }

    /**
     * Create a new grid without clues.
     * @return the copied grid.
     */
    Grid getNoCluesGrid() {
        int r = grid.getNRows();
        int c = grid.getNCols();
        String[][] stringGrid = new String[r][c];
        ArrayList<ArrayList<String>> stringVals = grid.getStringValues();
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                int val = grid.getValueAt(i, j);
                if (val < 9) {
                    stringGrid[i][j] = Utilities.UNCHECKED;
                }
                else {
                    stringGrid[i][j] = stringVals.get(i).get(j);
                }
            }
        }
        return new Grid(stringGrid);
    }

    @Override
    public String toString() {
        return "Game #" + String.format("%05d", gameNum)
                + ((win)? " win" : " loss") + ", time: "
                + Utilities.parseTime(time)
                + ", score: " + score
                + ", difficulty: " + difficulty;
    }

    /**
     * Compare by game number in ascending order.
     */
    static class GameNumberComparator implements Comparator<Game> {
        public int compare(Game game1, Game game2) {
            Integer g1 = game1.gameNum;
            Integer g2 = game2.gameNum;
            return g1.compareTo(g2);
        }
    }

    /**
     * Compare by game time in ascending order.
     */
    static class GameTimeComparator implements Comparator<Game> {
        public int compare(Game game1, Game game2) {
            Integer g1 = game1.time;
            Integer g2 = game2.time;
            return g1.compareTo(g2);
        }
    }

    /**
     * Compare by game score in ascending order.
     */
    static class GameScoreComparator implements Comparator<Game> {
        public int compare(Game game1, Game game2) {
            Integer g1 = game1.score;
            Integer g2 = game2.score;
            return g1.compareTo(g2);
        }
    }
}
