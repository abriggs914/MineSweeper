package com.example.abrig.minesweeper;

import android.content.Context;

import java.util.Arrays;

/**
 * Class to act as an intermediate between game strings parsed from
 * SharedPreferencesHandler and Game objects.
 */
public class GameHistoryParser {

    public GameHistoryParser() { }

    /**
     * Creates a Game object from a history string.
     * @param context Application context from calling view.
     * @param gameString The formatted game history string.
     * @return The new Game object parsed from the gameString.
     * @throws MineSweeperException From creation of the Minesweeper object.
     */
    static Game getGameFromString(Context context, String gameString) throws MineSweeperException {
        String[] gridSplit = gameString.split("\\[\\[");
        String[] statsSplit = gridSplit[0].split(",");
        int gameNum = Integer.parseInt(statsSplit[0].trim());
        boolean win = Integer.parseInt(statsSplit[1].trim()) != 0;
        int time = Integer.parseInt(statsSplit[2].trim());
        int score = Integer.parseInt(statsSplit[3].trim());
        int searched = Integer.parseInt(statsSplit[4].trim());
        String[][] arr = Utilities.arrify("[[" + gridSplit[2].trim());
        for (int i = 0; i < arr.length; i++) {
            String[] row = arr[i];
            for (int j = 0; j < row.length; j++) {
                String str = row[j];
                if (!Utilities.getAcceptedInput().contains(str)) {
                    arr[i][j] = Utilities.UNCHECKED;
                }
                else if (Integer.parseInt(str) < 9) {
                    arr[i][j] = Utilities.UNCHECKED;
                }
            }
        }
        MineSweeper mineSweeper = new MineSweeper(arr, context);
        return new Game(gameNum, win, time, score, mineSweeper);
    }

    /**
     * Create a history string from a MineSweeper object and the game state's variables.
     * values format: [game_num, win/loss(1/0), time, score, searched].
     * @param mineSweeper Contains all grid state information.
     * @param values MineSweeperGrid object variables in the above order.
     * @return A concatenated string [values, gridString, gridSolnString]
     */
    static String genHistoryString(MineSweeper mineSweeper, int... values) {
        StringBuilder label = new StringBuilder();
        Grid grid = mineSweeper.getGameGrid();
        Grid gridSoln = mineSweeper.getSolnGrid();
        String[][] gridString = grid.getGameGrid();
        String[][] gridSolnString = gridSoln.getGameGrid();
        for (int i : values) {
            label.append(i).append(", ");
        }
        label.append("[");
        for(String[] arr : gridString) {
            label.append(Arrays.toString(arr));
        }
        label.append("]");

        label.append("[");
        for(String[] arr : gridSolnString) {
            label.append(Arrays.toString(arr));
        }
        label.append("]");
        return label.toString();
    }
}
