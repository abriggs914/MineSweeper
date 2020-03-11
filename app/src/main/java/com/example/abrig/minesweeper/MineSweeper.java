package com.example.abrig.minesweeper;

import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

public class MineSweeper {

    private Context context;
    private Grid originalGrid;
    private Grid grid;
    private Grid gridSoln;
    private boolean gameOver;

    public MineSweeper(Grid soln, Context context) throws MineSweeperException {
        this.context = context;
        this.gridSoln = soln;
        init();
    }

    public MineSweeper(String[][] arr, Context context) throws MineSweeperException {
        this.context = context;
        this.gridSoln = Grid.parseGrid(arr);
        if (gridSoln == null) {
            throw new MineSweeperException(context, this, 5, "");
        }
        init();
    }

    public void init() throws MineSweeperException {
        this.gameOver = false;
        this.gridSoln.addClues();
        this.gridSoln.setCheckedAll();
        this.grid = Grid.parseGrid(gridSoln.getGameGrid());
        this.originalGrid = Grid.parseGrid(gridSoln.getGameGrid());
        if (gridSoln == null || grid == null) {
            throw new MineSweeperException(context, this, 5, "");
        }
    }

    public Grid getOriginalGrid() {
        return originalGrid;
    }

    public Grid getGameGrid() {
        return grid;
    }

    public Grid getSolnGrid() {
        return gridSoln;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getNumSquares() { return this.grid.getNumSquares(); }

    public int getNumMines() {
        return this.gridSoln.count(Utilities.MINE);
    }

    public int getDifficulty() {
        return (int) (100 * (getNumMines() + 0.0) / (getNumSquares() + 0.0));
    }

    public boolean selectSquare(int r, int c, boolean doCascade) throws MineSweeperException {
        boolean success;
        if (!gameOver) {
            success = this.grid.selectSquare(r, c, doCascade, gridSoln);
            if (!success) {
                this.gameOver = true;
                throw new MineSweeperException(context, this, 0, "");
            }
        }
        else {
            throw new MineSweeperException(context, this, 0, "");
        }
        return true;
    }

    public void setPossibleMine(int r, int c) {
        this.grid.setCheckStatusAt(r, c, true);
        this.grid.putValueAt(r, c, Utilities.POSSIBLE);
    }

    public void resetPossibleMine(int r, int c) throws MineSweeperException{
        boolean status = grid.getCheckStatusAt(r, c);
        int val = grid.getValueAt(r, c);
        if (status) {
            if (val == Utilities.POSSIBLE.charAt(0)) {
                this.grid.putValueAt(r, c, this.gridSoln.getValueAt(r, c));
                this.grid.setCheckStatusAt(r, c, false);
                return;
            }
        }
        throw new MineSweeperException(context, this, 0, "");
    }

    public void setSurroundingChecked(int r, int c) throws MineSweeperException{
        ArrayList<String> surroundingKeys = grid.getSurroundingSquaresKeys(r, c);
        for (String key : surroundingKeys) {
            int currVal = grid.getValueAt(key);
            int val = gridSoln.getValueAt(key);
            grid.setCheckStatusAt(key, true);
            if (currVal == Utilities.MINE.charAt(0) && val == Utilities.MINE.charAt(0)) {
                throw new MineSweeperException(context, this, 2, "");
            }
        }
    }

    public int count(String s) {
        return this.grid.count(s);
    }

    public int countSoln(String s) {
        return this.gridSoln.count(s);
    }

    public boolean guessedAll() {
        return countSoln(Utilities.MINE) == count(Utilities.POSSIBLE);
    }

    public boolean checkSolution() {
        boolean valid = true;
        boolean guessAll = guessedAll();
        if (guessAll) {
            ArrayList<String> markedIndexes = grid.getAllKeys(Utilities.POSSIBLE);
            int mineVal = Utilities.MINE.charAt(0);
            for (String key : markedIndexes) {
                boolean status = grid.getCheckStatusAt(key);
                int val = gridSoln.getValueAt(key);
                if (!status || val != mineVal) {
                    valid = false;
                    break;
                }
            }
        }
        return guessAll && valid;
    }

    public String toString() {
        return grid.toGameView();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Grid subGrid(Grid gridIn, int startRow, int stopRow, int startCol, int stopCol) throws MineSweeperException{
        // logic
        int rows = gridIn.getNRows();
        int cols = gridIn.getNCols();
        boolean valid = false;
        startRow = Math.max(0, startRow);
        stopRow = Math.min(gridIn.getNRows() - 1, stopRow);
        startCol = Math.max(0, startCol);
        stopCol = Math.min(gridIn.getNCols() - 1, stopCol);
        if (Utilities.inRange(0, startRow, stopRow)) {
            if (Utilities.inRange(startRow, stopRow, rows)) {
                if (Utilities.inRange(0, startCol, stopCol)) {
                    if (Utilities.inRange(startCol, stopCol, cols)) {
                        valid = true;
                    }
                }
            }
        }
        if (!valid) {
            throw new MineSweeperException(context, this, 1, "");
        }

        int newRows = 1 + stopRow - startRow;
        int newCols = 1 + stopCol - startCol;
        int r = 0, c;
        String[][] res = new String[newRows][newCols];
        for (int i = startRow; i <= stopRow; i++) {
            c = 0;
            for (int j = startCol; j <= stopCol; j++) {
                int intVal = gridIn.getValueAt(i, j);
                boolean status = gridIn.getCheckStatusAt(i, j);
                String val = Integer.toString(intVal);
                if (status) {
                    res[r][c] = val;
                }
                else {
                    res[r][c] = Utilities.UNCHECKED;
                }
                c++;
            }
            r++;
        }
        Grid g = new Grid(res);
        return g;
    }

    public MineSweeper shuffleGrid() throws MineSweeperException {
        int rows = gridSoln.getNRows();
        int cols = gridSoln.getNCols();
        int mines = getNumMines();
        String[][] newGridString = generateStringGrid(rows, cols, mines);
        return new MineSweeper(newGridString, context);
    }

    private String[][] generateStringGrid(int numRows, int numCols, int numMines) {
        String[][] res = new String[numRows][numCols];
        double percentage = numMines / (numRows * numCols);
        int minesPlaced = 0;
        Random rand = new Random();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                double randomNum = rand.nextDouble();
                boolean isMine = randomNum <= percentage;
                String val = Utilities.CHECKED;
                if (isMine && minesPlaced < numMines) {
                    val = Utilities.MINE;
                    minesPlaced++;
                }
                res[r][c] = val;
            }
        }
        while (minesPlaced < numMines) {
            int r = rand.nextInt(numRows);
            int c = rand.nextInt(numCols);
            String val = res[r][c];
            if (!val.equals(Utilities.MINE)) {
                res[r][c] = Utilities.MINE;
                minesPlaced++;
            }
        }
        return res;
    }
}
