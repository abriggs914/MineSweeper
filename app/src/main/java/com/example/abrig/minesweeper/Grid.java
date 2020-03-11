package com.example.abrig.minesweeper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to store information about each square in a MineSweeper grid.
 * Maintains status of whether the square has been revealed, and the
 * current value associated with that grid either a hint or possible mine.
 */
public class Grid {

    private int n_cols;
    private int n_rows;
    private HashMap<String, HashMap<String, String>> grid;

    /**
     * Takes in a 2-dimensional string grid of only the mines. Initializes the hashmap grid
     * and sets the number of rows and columns.
     * @param gridIn 2D String grid, must contain only the mines and unmarked spaces.
     */
    Grid(String[][] gridIn) {
        this.n_rows = gridIn.length;
        this.n_cols = gridIn[0].length;
        this.grid = initializeGrid(gridIn);
    }

    int getNRows() {
        return n_rows;
    }

    int getNCols() {
        return n_cols;
    }

    /**
     * Creates a HashMap for each square in the grid with key value (r, c)
     * and sets a value of a new HashMap containing the current_value and
     * the checked_status for reference by the MineSweeper object.
     * @param gridIn passed from the Grid constructor.
     * @return the set HashMap.
     */
    private HashMap<String, HashMap<String, String>> initializeGrid(String[][] gridIn) {
        HashMap<String, HashMap<String, String>> res = new HashMap<>();
        for (int r = 0; r < n_rows; r++) {
            for (int c = 0; c < n_cols; c++) {
                String key = Utilities.keyify(r, c);
                String val = String.valueOf(gridIn[r][c]);
                HashMap<String, String> attrs = new HashMap<>();
                boolean checked = val.equals(Utilities.CHECKED); // could be a clue, so not a safe check
                boolean unChecked = val.equals(Utilities.UNCHECKED) || val.equals(Utilities.MINE);
                // bad naming, but checked WITH "ed" marks whether the square is marked with a mine
                // WITHOUT "ed" marks the in game status of whether the square has been revealed
                attrs.put("checked_status", Boolean.toString(!unChecked));
                attrs.put("current_value", String.valueOf(val));

                res.put(key, attrs);
            }
        }
        return res;
    }

    /**
     * Ensures that the given string grid is a complete square.
     * @param gridIn 2D string grid.
     * @return true if compelete square false otherwise.
     */
    private static boolean validateRectangle(String[][] gridIn){
        int r = gridIn.length;
        int total = 0;
        for (String[] row : gridIn) {
            total += row.length;
        }
        int x = total / r;
        return (x * r) == total;
    }

    /**
     * Return an ArrayList of keys of surrounding squares of the central
     * point given by r, c.
     * @param row row number
     * @param col col number
     * @return ArrayList of keys for the Grid HashMap.
     */
    private ArrayList<String> getAvailableSquaresKeys(int row, int col) {
        ArrayList<String> res = new ArrayList<>();
        if (row < 0 || row  >= n_rows) {
            return res;
        }
        if (col < 0 || col >= n_cols) {
            return res;
        }
        if (row > 0) {
            if (col > 0) {
                if (!getCheckStatusAt(row - 1 , col - 1)) {
                    res.add(Utilities.keyify(row - 1, col - 1));
                }
            }
            if (!getCheckStatusAt(row - 1, col)) {
                res.add(Utilities.keyify(row - 1, col));
            }
            if (col < (n_cols - 1)) {
                if (!getCheckStatusAt(row - 1, col + 1)) {
                    res.add(Utilities.keyify(row - 1, col + 1));
                }
            }
        }
        if (row < (n_rows - 1)) {
            if (col > 0) {
                if (!getCheckStatusAt(row + 1, col - 1)) {
                    res.add(Utilities.keyify(row + 1, col - 1));
                }
            }
            if (!getCheckStatusAt(row + 1, col)) {
                res.add(Utilities.keyify(row + 1, col));
            }
            if (col < (n_cols - 1)) {
                if (!getCheckStatusAt(row + 1, col + 1)) {
                    res.add(Utilities.keyify(row + 1, col + 1));
                }
            }
        }
        if (col > 0) {
            if (!getCheckStatusAt(row, col - 1)) {
                res.add(Utilities.keyify(row, col - 1));
            }
        }
        if (col < (n_cols - 1)) {
            if (!getCheckStatusAt(row, col + 1)) {
                res.add(Utilities.keyify(row, col + 1));
            }
        }
        return res;
    }

    /**
     * Iterate the keysList and add clues to each square if a mine is adjacent to it.
     */
    void addClues() {
        for (int r = 0; r < n_rows; r++) {
            for (int c = 0; c < n_cols; c++) {
                ArrayList<String> availableSquares = getAvailableSquaresKeys(r, c);
                if (getValueAt(r, c) == Utilities.MINE.charAt(0)) {
                    for (String key : availableSquares) {
                        if (getValueAt(key) != Utilities.MINE.charAt(0)) {
                            putValueAt(key, getValueAt(key) + 1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the value at the given row and column.
     * @param r row number.
     * @param c column number.
     * @return the integer value at the square.
     */
    int getValueAt(int r, int c) {
        String curr = String.valueOf(this.grid.get(Utilities.keyify(r, c)).get("current_value"));
        int res;
        try {
            res = Integer.parseInt(curr);
        }
        catch (Exception e) {
            if (curr.equals(Utilities.CHECKED)) {
                res = 0;
            }
            else {
                res = (int) curr.charAt(0);
            }
        }
        return res;
    }

    /**
     * Get the value at the given row and column.
     * @param key The grid key.
     * @return the integer value at the square.
     */
    int getValueAt(String key) {
        String curr = String.valueOf(this.grid.get(key).get("current_value"));
        int res;
        try {
            res = Integer.parseInt(curr);
        }
        catch (Exception e) {
            if (curr.equals(Utilities.CHECKED)) {
                res = 0;
            }
            else {
                res = (int) curr.charAt(0);
            }
        }
        return res;
    }

    /**
     * Check the revealed status at the given square.
     * @param r row number.
     * @param c column number.
     * @return the checked status.
     */
    boolean getCheckStatusAt(int r, int c) {
        return getCheckStatusAt(Utilities.keyify(r, c));
    }

    /**
     * Check the revealed status at the given square.
     * @param key the grid key.
     * @return the checked status.
     */
    boolean getCheckStatusAt(String key) {
        return Boolean.parseBoolean(this.grid.get(key).get("check_status"));
    }

    /**
     * Is the given square a mine?
     * @param r row number.
     * @param c column number.
     * @return yer or no.
     */
    boolean isMine(int r, int c) {
        return getValueAt(r, c) == Utilities.MINE.charAt(0);
    }

    /**
     * Set the reveal status at the key.
     * @param key grid key.
     * @param checked checked status.
     */
    void setCheckStatusAt(String key, boolean checked) {
        this.grid.get(key).put("check_status", Boolean.toString(checked));
    }

    /**
     * Set the reveal status at the key.
     * @param r row number.
     * @param c column number.
     * @param checked checked status.
     */
    void setCheckStatusAt(int r, int c, boolean checked) {
        setCheckStatusAt(Utilities.keyify(r, c), checked);
    }

    /**
     * Put a string value at the given square.
     * @param r row number.
     * @param c column number.
     * @param val value to be placed.
     */
    void putValueAt(int r, int c, String val) {
        this.grid.get(Utilities.keyify(r, c)).put("current_value", val);
    }

    /**
     * Put an integer value at the given square.
     * @param r row number.
     * @param c column number.
     * @param val value to be placed.
     */
    void putValueAt(int r, int c, int val) {
        putValueAt(r, c, Integer.toString(val));
    }

    /**
     * Put a value at the given square.
     * @param key the grid key.
     * @param val value to be placed.
     */
    private void putValueAt(String key, int val) {
        this.grid.get(key).put("current_value", Integer.toString(val));
    }

    /**
     * Get a copy of the HashMap.
     * @return the HashMap copy.
     */
    HashMap<String, HashMap<String, String>> getMap() {
        return new HashMap<>(grid);
    }

    /**
     * Get the number of squares in the grid.
     * @return number of squares.
     */
    int getNumSquares() {
        return n_rows * n_cols;
    }

    /**
     * Count the number of checked squares.
     * @return number of checked squares.
     */
    int countCheckedSquares() {
        int res = 0;
        ArrayList<String> keys = getAllKeys();
        for (String key : keys) {
            if (getCheckStatusAt(key)) {
                res++;
            }
        }
        return res;
    }

    /**
     * Try and select a given square.
     * @param r row number.
     * @param c column number.
     * @param doCascade whether to cascade the selection to it's surrounding or not.
     * @param soln the grid solution for verification.
     * @return a boolean representing the success of selecting that square.
     */
    boolean selectSquare(int r, int c, boolean doCascade, Grid soln) {
        boolean stop = false;
        if (r < 0 || r  >= n_rows) {
            stop = true;
        }
        if (c < 0 || c >= n_cols) {
            stop = true;
        }
        if (stop) {
            return false;
        }
        int val = getValueAt(r, c);
        if (val == Utilities.MINE.charAt(0)) {
            setCheckStatusAt(r, c, true);
            return false;
        }
        else {
            if (doCascade) {
                cascadeSelection(r, c, soln, true);
            }
            setCheckStatusAt(r, c, true);
            return true;
        }
    }

    /**
     * Recursively check the surrounding of each square as long as the square is either a
     * hint or an empty space.
     * @param r row number.
     * @param c columnn number.
     * @param soln the grid solution for verification.
     * @param lastPlacedSpace whether the last square placed was a space or a hint.
     */
    private void cascadeSelection(int r, int c, Grid soln, boolean lastPlacedSpace) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int x = r + i;
                int y = c + j;
                if (0 <= x && x < n_rows) {
                    if (0 <= y && y < n_cols) {
                        int solVal = soln.getValueAt(x, y);
                        boolean status = getCheckStatusAt(x, y);
                        if (!status && solVal == 0) {
                            this.putValueAt(x, y, solVal);
                            this.setCheckStatusAt(x, y, true);
                            cascadeSelection(x, y, soln, true);
                        }
                        else if (!status && lastPlacedSpace && (solVal < 9)) {
                            this.putValueAt(x, y, solVal);
                            this.setCheckStatusAt(x, y, true);
                            cascadeSelection(x, y, soln, false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the string grid representation of the grid.
     * @return the string grid, with no hints.
     */
    String[][] getStringGrid() {
        String[][] res = new String[n_rows][n_cols];
        for (int r = 0; r < n_rows; r++) {
            for(int c = 0; c < n_cols; c++) {
                int val = getValueAt(r, c);
                String stringVal;
                if (val < 9) {
                    stringVal = Utilities.UNCHECKED;
                }
                else {
                    stringVal = Character.toString((char) val);
                }
                res[r][c] = stringVal;
            }
        }
        return res;
    }

    /**
     * Get the string values in a 2-dimensional ArrayList.
     * @return The list of string values.
     */
    ArrayList<ArrayList<String>> getStringValues() {
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        for(int i = 0; i < n_rows; i++) {
            ArrayList<String> row = new ArrayList<>();
            for (int j = 0; j < n_cols; j++) {
                String val = Character.toString((char) getValueAt(i, j));
                row.add(val);
            }
            res.add(row);
        }
        return res;
    }

    /**
     * Get the surrounding square's keys
     * @param r row number.
     * @param c column number
     * @return a list of surrounding keys.
     */
    ArrayList<String> getSurroundingSquaresKeys(int r, int c) {
        ArrayList<String> res = new ArrayList<>();
        if (Utilities.inRange(0, r, n_rows - 1) && Utilities.inRange(0, c, n_cols - 1)) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    int x = r + i;
                    int y = c + j;
                    if (Utilities.inRange(0, x, n_rows - 1)) {
                        if (Utilities.inRange(0, y, n_cols - 1)) {
                            if (x != r || y != c) {
                                res.add(Utilities.keyify(x, y));
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    /**
     * Get the keys of the given square's surrounding unchecked squares.
     * @param r row number.
     * @param c column number.
     * @return the list of keys.
     */
    ArrayList<String> getUncheckedSurrounding(int r, int c) {
        ArrayList<String> strings = getSurroundingSquaresKeys(r, c);
        ArrayList<String> res = new ArrayList<>();
        for (String s : strings) {
            boolean status = getCheckStatusAt(s);
            if (!status){
                res.add(s);
            }
        }
        return res;
    }

    /**
     * Get all keys.
     * @return a list of all square's keys.
     */
    private ArrayList<String> getAllKeys() {
        ArrayList<String> res = new ArrayList<>();
        for (int r = 0; r < n_rows; r++) {
            for (int c = 0; c < n_cols; c++) {
                res.add(Utilities.keyify(r, c));
            }
        }
        return res;
    }

    /**
     * Get all keys that have a square with a matching value.
     * @param val The value for searching.
     * @return a list of keys.
     */
    ArrayList<String> getAllKeys(String val) {
        int intVal = val.charAt(0);
        ArrayList<String> res = new ArrayList<>();
        for (int r = 0; r < n_rows; r++) {
            for (int c = 0; c < n_cols; c++) {
                int gridVal = getValueAt(r, c);
                if (gridVal == intVal) {
                    res.add(Utilities.keyify(r, c));
                }
            }
        }
        return res;
    }

    /**
     * Count the grid for the given value.
     * @param s the grid value to count.
     * @return the count.
     */
    int count(String s) {
        int res = 0;
        int checkVal = s.charAt(0);
        for (int r = 0; r < n_rows; r++) {
            for (int c = 0; c < n_cols; c++) {
                int val = getValueAt(r, c);
                if (val == checkVal) {
                    res++;
                }
            }
        }
        return res;
    }

    /**
     * Set all squares in the grid to be checked / revealed.
     * Called on the solution grid for verification.
     */
    void setCheckedAll() {
        for (int r = 0; r < n_rows; r++) {
            for (int c = 0; c < n_cols; c++) {
                setCheckStatusAt(r, c, true);
            }
        }
    }

    /**
     * Get the string grid representation of the grid in the current state.
     * @return a 2D string grid.
     */
    String[][] getGameGrid() {
        String[][] res = new String[n_rows][n_cols];
        for (int r = 0; r < n_rows; r++) {
            String[] row = new String[n_cols];
            for (int c = 0; c < n_cols; c++) {
                int val = getValueAt(r, c);
                boolean status = getCheckStatusAt(r, c);
                if (status) {
                    row[c] = val + "";
                }
                else {
                    row[c] = Utilities.UNCHECKED;
                }
            }
            res[r] = row;
        }
        return res;
    }

    /**
     * Creates a Horizontal string of border characters.
     * @return the border string.
     */
    private String genHorizontalBorder() {
        StringBuilder res = new StringBuilder();
        String border = Utilities.BORDER;
        for (int c = 0; c < n_cols + 2; c++) {
            res.append(border);
        }
        return res.toString();
    }

    /**
     * Return a game style representation of the grid including a border.
     * @return the grid string.
     */
    public String toString() {
        StringBuilder res = new StringBuilder("\n");
        String borderString = genHorizontalBorder();
        res.append(borderString + "\n");
        for (int r = 0; r < n_rows; r++) {
            res.append(Utilities.BORDER);
            for (int c = 0; c < n_cols; c++) {
                int val = getValueAt(r, c);
                if (val < 10) {
                    if (val == 0) {
                        res.append(Utilities.CHECKED);
                    }
                    else {
                        res.append(val);
                    }
                }
                else {
                    res.append(Utilities.MINE);
                }
            }
            res.append(Utilities.BORDER + "\n");
        }
        res.append(borderString).append("\n");
        return res.toString();
    }

    /**
     * Create a game view of the grid.
     * @return the game string.
     */
    String toGameView() {
        StringBuilder res = new StringBuilder("\n");
        String borderString = genHorizontalBorder();
        res.append(borderString + "\n");
        for (int r = 0; r < n_rows; r++) {
            res.append(Utilities.BORDER);
            for (int c = 0; c < n_cols; c++) {
                int val = getValueAt(r, c);
                boolean status = getCheckStatusAt(r, c);
                if (status) {
                    if (val == 0) {
                        res.append(" ");
                    }
                    else if (val == Utilities.MINE.charAt(0)) {
                        res.append(Utilities.MINE);
                    }
                    else {
                        res.append(val);
                    }
                }
                else {
                    res.append(Utilities.UNCHECKED);
                }
            }
            res.append(Utilities.BORDER + "\n");
        }
        res.append(borderString).append("\n");
        return res.toString();
    }

    /**
     * Parse a given 2D grid string or return null if invalid grid.
     * @param gridIn 2D string grid.
     * @return a new grid object or null.
     */
    static Grid parseGrid(String[][] gridIn) {
        int n_rows = gridIn.length;
        if (n_rows == 0 || !validateRectangle(gridIn)) {
            return null;
        }
        int n_cols = gridIn[0].length;
        ArrayList<String> acceptedInput = Utilities.getAcceptedInput();
        for (int r = 0; r < n_rows; r++) {
            for (int c = 0; c < n_cols; c++) {
                String val = String.valueOf(gridIn[r][c]);
                if (!acceptedInput.contains(val)) {
                    return null;
                }
            }
        }
        return new Grid(gridIn);
    }
}
