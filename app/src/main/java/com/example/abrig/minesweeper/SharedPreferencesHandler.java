package com.example.abrig.minesweeper;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesHandler {

    public SharedPreferencesHandler() { }

    public static int getIntVal(String label) {
        Map<String, ?> keyVals = MainActivity.preferences.getAll();
        Class clazz = keyVals.get(label).getClass();
        int val = 0;
        if (clazz == int.class) {
            val = (Integer) keyVals.get(label);
        }
        else if (clazz == String.class) {
            val = Integer.parseInt((String) keyVals.get(label));
        }
        return val;
    }

    public static String getStringVal(String label) {
        Map<String, ?> keyVals = MainActivity.preferences.getAll();
        Class clazz = keyVals.get(label).getClass();
        String val = "0";
        if (clazz == int.class) {
            val = Integer.toString((Integer) keyVals.get(label));
        }
        else if (clazz == String.class) {
            val = (String) keyVals.get(label);
        }
        return val;
    }

    public static void increment(String label) {
        int val = getIntVal(label);
        write(label, Integer.toString(val + 1));
    }

    public static void add(String label, int value) {
        int curr = getIntVal(label);
        write(label, Integer.toString(curr + value));
    }

    public static void write(String label, int value) {
        write(label, Integer.toString(value));
    }

    public static void writeGame(int gameNum, String value) {
        String label = "game #" + String.format("%05d", gameNum);
        write(label, value);
    }

    public static void write(String label, String value) {
        MainActivity.preferences.edit().putString(label, value).apply();
    }

    public static void write(String label,  boolean value) {
        MainActivity.preferences.edit().putBoolean(label, value).apply();
    }

    public static String getTopNGamesString(Context context, int n) throws MineSweeperException {
        n = Math.max(0, Math.min(n, getGamesList(context).size()));
        return "Top " + n + " game" + ((n == 0)? "" :"s") + ":";
    }

    public static String getLastNGamesString(Context context, int n) throws MineSweeperException {
        n = Math.max(0, Math.min(n, getGamesList(context).size()));
        return "Last " + n + " game" + ((n == 0)? "" :"s") + ":";
    }

    public static int getLostGames() {
        return getIntVal("games_lost");
    }

    public static int getWonGames() {
        return getIntVal("games_won");
    }

    public static int getNumGames() {
        return getIntVal("games_num");
    }

    public static int getBestScore() {
        return getIntVal("score_best");
    }

    public static int getBestTime() {
        return getIntVal("time_best");
    }

    public static int getWorstScore() {
        return getIntVal("score_worst");
    }

    public static int getWorstTime() {
        return getIntVal("time_worst");
    }

    public static int getAverageScore() {
        return getIntVal("score_average");
    }

    public static int getAverageTime() {
        return getIntVal("time_average");
    }

    public static int getAverageDifficulty() { return getIntVal("difficulty_average"); }

    public static int getTotalScore() { return getIntVal("score_total"); }

    public static int getTotalTime() { return getIntVal("time_total"); }

    public static int getTotalDifficulty() { return getIntVal("difficulty_total"); }

    public static ArrayList<String> getGamesStringList() {
        ArrayList<String> res = new ArrayList<>();
        Map<String, ?> keyVals = MainActivity.preferences.getAll();
        for (String key : keyVals.keySet()) {
            if (key.contains("game #")) {
                String val = (String) keyVals.get(key);
                res.add(val);
            }
        }
        return res;
    }

    public static ArrayList<Game> getGamesList(Context context) throws MineSweeperException {
        ArrayList<Game> res = new ArrayList<>();
        ArrayList<String> gameStrings = getGamesStringList();
        for(String gameString : gameStrings) {
            Game g = GameHistoryParser.getGameFromString(context, gameString);
            res.add(g);
        }
        return res;
    }

    public static void saveGame(MineSweeper mineSweeper, int... values) {
        String label = "savedGame";
        String value = GameHistoryParser.genHistoryString(mineSweeper, values);
        Grid gameGrid = mineSweeper.getGameGrid();
        HashMap<String, HashMap<String, String>> map = gameGrid.getMap();
        value += "{" + map + "}";

        write(label, value);
        write("gameInProgress", true);
    }

    public static MineSweeperGrid loadPreviousGame(Context context, FragmentManager fragmentManager) throws MineSweeperException {
        write("gameInProgress", false);
        String gameString = getStringVal("savedGame");
        String[] splitString = gameString.split("\\{\\{");
        Game game = GameHistoryParser.getGameFromString(context, splitString[0].trim());
        Grid gameGrid = game.getNoCluesGrid();
        String stringVal = "{" + splitString[1];
        HashMap<String, HashMap<String, String>> map = Utilities.mapify(stringVal);
        MineSweeperGrid res = new MineSweeperGrid(context, fragmentManager, gameGrid, 0, 0);
        res.stopTimer();
        for (int r = 0; r < res.getMineSweeper().getGameGrid().getNRows(); r++) {
            for (int c = 0; c < res.getMineSweeper().getGameGrid().getNCols(); c++) {
                String key = Utilities.keyify(r, c);
            int[] keys = Utilities.unkeyify(key);
            boolean checked = Boolean.parseBoolean(map.get(key).get("check_status"));
            int currVal = map.get(key).get("current_value").charAt(0);
           if (checked) {
                if (currVal == Utilities.POSSIBLE.charAt(0)) {
                    res.getMineSweeper().setPossibleMine(keys[0], keys[1]);
                }
                else {
                    res.getMineSweeper().selectSquare(keys[0], keys[1], false);
                }
            }
        }}
        res.setTimer((long) Utilities.parseTime(Utilities.parseTime(game.getSecondsPast())));
        return res;
    }
}
