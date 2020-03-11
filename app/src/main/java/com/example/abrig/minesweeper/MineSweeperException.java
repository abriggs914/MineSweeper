package com.example.abrig.minesweeper;

import android.content.Context;
import android.widget.Toast;

public class MineSweeperException extends Throwable {

    private Context context;
    private MineSweeper mineSweeper;

    public MineSweeperException(Context context, MineSweeper mineSweeper, int code, String message) {
        this.context = context;
        this.mineSweeper = mineSweeper;
        switch (code) {
            case 0 :
                gameOverException(message);
                break;
            case 1 :
                invalidSubGridIndicies(message);
                break;
            case 2 :
                failedToClearSurrounding(message);
                break;
            case 3 :
                timesUpException(message);
                break;
            case 4 :
                youWinException(message);
                break;
            case 5:
                nullGridParseException(message);
                break;
            default :
                gameOverException(message);
        }
    }

    private void nullGridParseException(String message) {
        String s = ((message.length() == 0)? "" : "\n" + message);
        gameOverException("You Win!" + s);
    }

    private void youWinException(String message) {
        String s = ((message.length() == 0)? "" : "\n" + message);
        gameOverException("You Win!" + s);
    }

    private void invalidSubGridIndicies(String message) {
        String s = ((message.length() == 0)? "" : "\n" + message);
        gameOverException("Invalid grid indices" + s);
    }

    private void failedToClearSurrounding(String message) {
        String m = "\nFailed to clear surrounding";
        String s = ((message.length() == 0)? "" : "\n" + message);
        gameOverException(m + s);
    }

    private void timesUpException(String message) {
        String m = "Time\'s up!";
        String s = ((message.length() == 0)? "" : "\n" + message);
        gameOverException(m + s);
    }

    public void gameOverException(String message) {
//        this.printStackTrace();
        String m = "GAME OVER";
        String s = ((message.length() == 0)? "" : "\n" + message);
        Toast.makeText(context, (m + s), Toast.LENGTH_LONG).show();
        mineSweeper.setGameOver(true);
    }
}
