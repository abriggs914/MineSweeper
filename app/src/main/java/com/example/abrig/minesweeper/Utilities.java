package com.example.abrig.minesweeper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Utilities {

    final static String BORDER = "#";
    final static String MINE = "M";
    final static String POSSIBLE = "P";
    final static String CHECKED = " ";
    final static String UNCHECKED = "0";

    // Inclusive
    public static boolean inRange(int start, int x, int end) {
        return start <= x && x <= end;
    }

    // Inclusive
    public static boolean inRange(double start, double x, double end) {
//        System.out.println(start + " <= " + x + " <= " + end + " -> " + (start <= x && x <= end));
        return start <= x && x <= end;
    }

    public static String keyify(int r, int c) {
        return "(" + r + ", " + c + ")";
    }

    public static String keyify(float r, float c) {
        return "(" + (int)r + ", " + (int)c + ")";
    }

    public static int[] unkeyify(String key) {
        String keys = key.replaceAll("\\(", "").replaceAll("\\)" , "").replaceAll(",", "");
        String[] splitString = keys.split(" ");
        return new int[] {Integer.parseInt(splitString[0]), Integer.parseInt(splitString[1])};
    }

    public static String twoDecimal(double d) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf.format(d);
    }

    public static int parseTime(String timeString) {
        String[] colonSplit = timeString.split(":");
        int mins = Integer.parseInt(colonSplit[0]);
        int seconds = Integer.parseInt(colonSplit[1]);
        return (mins * 60) + seconds;
    }

    public static String parseTime(int secondsPast) {
        String res = "";
        int mins = -1, seconds = 0;
        for (int x = secondsPast; x >= 0; x -= 60) {
            mins += 1;
            seconds = x;
            if (mins >= 60) {
                mins = 59;
                seconds = 59;
                break;
            }
        }
        if (mins < 0) {
            mins = 0;
        }
        res = mins + "";
        String secondsString = seconds + "";
        if (res.length() != 2) {
            res = "0" + res;
        }
        res += ":";
        if (secondsString.length() != 2) {
            secondsString = "0" + secondsString;
        }
        res += secondsString;
        return res;
    }

    public static String[][] arrify(String s) {
        String[] leftSplit = s.split("]\\[");
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (String possibleRow : leftSplit) {
            String[] commaSplit = possibleRow.split(",");
            ArrayList<String> rowList = new ArrayList<>();
            for (int i = 0; i < commaSplit.length; i++) {
                String entry = String.valueOf(commaSplit[i]);
                entry = entry.replaceAll("\\[", "");
                entry = entry.replaceAll("]", "");
                entry = entry.trim();
                rowList.add(entry);
            }
            list.add(rowList);
        }
        int x = 1, y = 1;
        if (list.size() > 0) {
            y = list.get(0).size();
            x = list.size();
        }
        String[][] res = new String[x][y];
        for (int r = 0; r < x; r++) {
            ArrayList<String> row = list.get(r);
            for (int c = 0; c < y; c++) {
                res[r][c] = row.get(c);
            }
        }
        return res;
    }

    public static HashMap<String,HashMap<String,String>> mapify(String stringVal) {
        HashMap<String,HashMap<String, String>> res = new HashMap<>();
        String[] split = stringVal.split("\\(");
        for (String s : split) {
            if (s.length() > 1) {
                s.replaceAll("\\{", "");
                s.replaceAll("\\}", "");
                s.trim();
                String[] spaceSplit = s.split(" ");
                String rowString = spaceSplit[0].replaceAll(",", "").trim();
                String colString = spaceSplit[1].trim();
                String cols = colString.split("\\)")[0].replaceAll(",", "").trim();
                int r = Integer.parseInt(rowString);
                int c = Integer.parseInt(cols);
                String key = keyify(r, c);

                String statusString = "check_status=";
                String valueString = "current_value=";
                int statusIdx = s.indexOf(statusString);
                int valueIdx = s.indexOf(valueString);
                String equalsSplit1 = s.substring(statusIdx + statusString.length());
                String equalsSplit2 = s.substring(valueIdx + valueString.length());
                // if no check_status, the square has not been revealed in the current game state
                if (statusIdx < 0) {
                    equalsSplit1 = "false";
                }

                equalsSplit1 = equalsSplit1.split(" ")[0]
                        .replaceAll(",", "")
                        .replaceAll("\\{", "")
                        .replaceAll("\\}", "")
                        .trim();
                equalsSplit2 = equalsSplit2.split(" ")[0]
                        .replaceAll(",", "")
                        .replaceAll("\\{", "")
                        .replaceAll("\\}", "")
                        .trim();

                HashMap<String, String> keyVal = new HashMap<>();
                keyVal.put("current_value", equalsSplit2);
                keyVal.put("check_status", equalsSplit1);
                res.put(key, keyVal);
            }
        }
        return res;
    }

    public static ArrayList<String> getAcceptedInput() {
        return new ArrayList<>(
                Arrays.asList(
                        BORDER,
                        MINE,
                        CHECKED,
                        UNCHECKED,
                        POSSIBLE,
                        Integer.toString(BORDER.charAt(0)),
                        Integer.toString(MINE.charAt(0)),
                        Integer.toString(CHECKED.charAt(0)),
                        Integer.toString(POSSIBLE.charAt(0)),
                        Integer.toString(UNCHECKED.charAt(0)),
                        "1", "2", "3", "4", "5", "6", "7", "8"));
    }
}
