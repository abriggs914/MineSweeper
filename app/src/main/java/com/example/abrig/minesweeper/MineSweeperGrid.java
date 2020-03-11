package com.example.abrig.minesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class MineSweeperGrid extends View implements QuitGamePopUp.ExampleDialogListener {

    private Paint squarePaint;
    private Paint textPaint;
    private MineSweeper mineSweeper;
    private int n_rows, n_cols;
    private int secondsPast;
    private ArrayList<String> animatedKeys;
    private GestureDetector myGestureDetector;
    private AndroidGestureDetector androidGestureDetector;
    private FragmentManager supportFragmentManager;
    private boolean quitGameResponse, shuffleGridResponse, resetGame, gameOverHandled;
    private CountDownTimer countDownTimer;

    public MineSweeperGrid(
            Context context, FragmentManager supportFragmentManager,
            Grid grid, int layoutX, int layoutY) throws MineSweeperException {
        super(context);
        this.supportFragmentManager = supportFragmentManager;
        this.squarePaint = new Paint();
        this.textPaint = new Paint();
        this.mineSweeper = new MineSweeper(grid, context);
        this.n_rows = grid.getNRows();
        this.n_cols = grid.getNCols();
        this.animatedKeys = new ArrayList<>();
        this.androidGestureDetector = new AndroidGestureDetector();
        this.myGestureDetector = new GestureDetector(getContext(), androidGestureDetector);
        this.setLongClickable(true);
        this.quitGameResponse = false;
        this.shuffleGridResponse = false;
        this.resetGame = false;
        this.gameOverHandled = false;

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return myGestureDetector.onTouchEvent(event);
            }
        });

        setTimer();

    }

    public MineSweeper getMineSweeper() {
        return mineSweeper;
    }

    public double getGridWidthSpace() {
        return (getWidth() * 0.2) / 2;
    }

    public double getGridHeightSpace() {
        return (getHeight() * 0.3) / 2;
    }

    public double getGridColWidth() { return (getWidth() / n_cols) * 0.8; }

    public double getGridRowHeight() { return (getHeight() / n_rows) * 0.8;}

    public int getSecondsPast() { return secondsPast; }

    public int getScore() {
        Grid grid = mineSweeper.getGameGrid();
        double searched = grid.countCheckedSquares();
        double numSquares = grid.getNumSquares();
        double progress = searched / numSquares;
        return (int) (progress * 100);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Grid grid = mineSweeper.getGameGrid();
        double widthSpace = getGridWidthSpace();
        double heightSpace = getGridHeightSpace();
        double spacePerCol = getGridColWidth();
        double spacePerRow = getGridRowHeight();

        // --------------------- background ---------------------
        squarePaint.setStyle(Paint.Style.FILL);
        squarePaint.setColor(Color.rgb(100, 100, 100));
        canvas.drawPaint(squarePaint);

        // --------------------- toolbar ---------------------
        textPaint.setTextSize(75);
        squarePaint.setColor(Color.GRAY);
        canvas.drawRect(0,0, getRight(), (float)getGridHeightSpace() - 50, squarePaint);

        // --------------------- main button ---------------------
        textPaint.setColor(Color.BLACK);
        squarePaint.setColor(Color.rgb(220, 220, 220));
        canvas.drawRect(30, 80, 230, 205, squarePaint);
        canvas.drawText("Main", 45, 180, textPaint);

        // --------------------- mines counter ---------------------
        squarePaint.setColor(Color.BLACK);
        canvas.drawRect(255, 80, 410, 205, squarePaint);
        textPaint.setColor(Color.rgb(255, 3, 3));
        String numMinesMarked = String.format("%03d", (mineSweeper.countSoln(Utilities.MINE) - mineSweeper.count(Utilities.POSSIBLE)));
        boolean solvedGrid = false;
        if (numMinesMarked.equals("000")) {
            solvedGrid = mineSweeper.checkSolution();
        }
        canvas.drawText(numMinesMarked, 270, 180, textPaint);

        // --------------------- game status ---------------------
        squarePaint.setColor(Color.rgb(250,232, 3));
        canvas.drawCircle(530, 142, 75, squarePaint);

        if (solvedGrid) {
            // mouth
            squarePaint.setColor(Color.rgb(92, 4, 4));
            canvas.drawArc(485, 150, 575, 210, 0f, 180f, true, squarePaint);
            // tongue
            squarePaint.setColor(Color.rgb(242, 92, 92));
            canvas.drawArc(510, 195, 550, 205, 0f, 180f, true, squarePaint);
            squarePaint.setColor(Color.BLACK);
            squarePaint.setStrokeWidth(10);
            // sunglasses
            canvas.drawCircle(500, 130, 25, squarePaint);
            canvas.drawCircle(560, 130, 25, squarePaint);
            canvas.drawLine(510, 125, 550, 125, squarePaint);
            canvas.drawLine(490, 125, 450, 118, squarePaint);
            canvas.drawLine(570, 125, 610, 118, squarePaint);

            try {
                countDownTimer.cancel();
                throw new MineSweeperException(getContext(), mineSweeper, 4, "");
            } catch (MineSweeperException ignored) { }

//            handleGameOver();
        }
        else {
            if (!mineSweeper.isGameOver()) {
                // game on
                squarePaint.setColor(Color.rgb(92, 4, 4));
                // mouth
                canvas.drawArc(485, 150, 575, 210, 0f, 180f, true, squarePaint);
                squarePaint.setColor(Color.rgb(242, 92, 92));
                // tongue
                canvas.drawArc(510, 195, 550, 205, 0f, 180f, true, squarePaint);
                squarePaint.setColor(Color.BLACK);
                // left & right eyes
                canvas.drawCircle(500, 130, 14, squarePaint);
                canvas.drawCircle(560, 130, 14, squarePaint);
            } else {
                countDownTimer.cancel();
                // game over
                squarePaint.setColor(Color.rgb(92, 4, 4));
                // mouth
                canvas.drawArc(485, 150, 575, 210, 180f, 180f, false, squarePaint);

                squarePaint.setColor(Color.BLACK);
                squarePaint.setStrokeWidth(5);
                // left eye
                canvas.drawLine(490, 120, 510, 142, squarePaint);
                canvas.drawLine(490, 142, 510, 120, squarePaint);
                // right eye
                canvas.drawLine(550, 142, 570, 120, squarePaint);
                canvas.drawLine(550, 120, 570, 142, squarePaint);

//                handleGameOver();
            }
        }

        // --------------------- mine progress bar ---------------------
        Rect mineProgressBounds = new Rect(650, 80, 805, 205);
        canvas.drawRect(mineProgressBounds, squarePaint);
        double distance = Math.max(0, (mineProgressBounds.right - mineProgressBounds.left) - 20);
        double searched = grid.countCheckedSquares();
        double numSquares = grid.getNumSquares();
        double progress = searched / numSquares;
        distance *= progress;
        int redVal = 255;
        int greenVal = 3;
        int blueVal = 3;
        for (double x = progress; x > 0.1; x -= 0.1) {
            if ((greenVal + 50) < 255) {
                greenVal += 50;
            }
            else {
                redVal -= 50;
            }
        }
        squarePaint.setColor(Color.rgb(redVal, greenVal, blueVal));
        canvas.drawRect(
                mineProgressBounds.left + 10,
                mineProgressBounds.top + 10,
                (float) (mineProgressBounds.left + distance) + 10,
                mineProgressBounds.bottom - 10,
                squarePaint);

        // --------------------- timer ---------------------
        Rect timerBounds = new Rect(835, 80, 1035, 205);
        squarePaint.setColor(Color.BLACK);
        canvas.drawRect(timerBounds, squarePaint);
        textPaint.setColor(Color.rgb(255, 3, 3));
        String timeString = Utilities.parseTime(secondsPast);
        if (timeString.equals("TIME\'S UP")) {
            try {
                throw new MineSweeperException(getContext(), mineSweeper, 3, "");
            } catch (MineSweeperException ignored) { }
        }
        canvas.drawText(timeString, timerBounds.left + 10, timerBounds.bottom - 10, textPaint);

        // --------------------- grid ---------------------
        for (int r = 0; r < n_rows; r++) {
            for (int c = 0; c < n_cols; c++) {
                boolean status = grid.getCheckStatusAt(r, c);
                boolean isAnimated = animatedKeys.contains(Utilities.keyify(r, c));
                int gridValue = grid.getValueAt(r, c);
                String squareText = ""; //(a + b) + "";
                float p = (float) ((r * spacePerRow) + heightSpace);
                float q = (float) ((c * spacePerCol) + widthSpace);
                squarePaint.setColor(Color.LTGRAY);
                textPaint.setColor(Color.BLACK);
                if (status || mineSweeper.isGameOver()) {
                    squarePaint.setColor(Color.GRAY);
                    boolean isMine = grid.isMine(r, c);
                    if (isMine) {
                        squareText = Utilities.MINE;
                        squarePaint.setColor(Color.RED);
                    }
                    else if (gridValue > 0){
                        // adjust hint squares
                        switch (gridValue) {
                            case 1 :
                                textPaint.setColor(Color.rgb(0, 255, 0)); // green
                                squareText = "1";
                                break;
                            case 2 :
                                textPaint.setColor(Color.rgb(240, 252, 3)); // yellow
                                squareText = "2";
                                break;
                            case 3 :
                                textPaint.setColor(Color.rgb(252, 140, 3)); // orange
                                squareText = "3";
                                break;
                            case 4 :
                                textPaint.setColor(Color.rgb(252, 3, 3)); // red
                                squareText = "4";
                                break;
                            case 5 :
                                textPaint.setColor(Color.rgb(161, 3, 252)); // purple
                                squareText = "5";
                                break;
                            case 6 :
                                textPaint.setColor(Color.rgb(3, 15, 252)); // blue
                                squareText = "6";
                                break;
                            case 7 :
                                textPaint.setColor(Color.rgb(252, 3, 98)); // pink
                                squareText = "7";
                                break;
                            case 8:
                                textPaint.setColor(Color.rgb(3, 252, 232)); // diamond blue
                                squareText = "8";
                                break;
                            default:
                                if (gridValue == Utilities.POSSIBLE.charAt(0)) {
                                    squarePaint.setColor(Color.rgb(50, 50, 50));
                                    textPaint.setColor(Color.WHITE);
                                    squareText = Utilities.MINE;
                                }
                                break;
                        }
                    }
                }
                double widthHeight = Math.min(getGridColWidth(), getGridRowHeight()) / 1.75;
                textPaint.setTextSize((float) widthHeight);
                Rect squareBounds = new Rect(
                        (int) q + 5,
                        (int) p + 5,
                        (int) ((q + spacePerCol) - 5),
                        (int) ((p + spacePerRow) - 5));
                if (isAnimated) {
                    // draw border
                    Rect borderBounds = new Rect(
                            (int) q + 5,
                            (int) p + 5,
                            (int) ((q + spacePerCol) - 5),
                            (int) ((p + spacePerRow) - 5));
                    squarePaint.setColor(Color.BLACK);
                    canvas.drawRect(borderBounds, squarePaint);
                    squarePaint.setColor(Color.LTGRAY);

                    squareBounds = new Rect(
                            squareBounds.left + 5,
                            squareBounds.top + 5,
                            squareBounds.right - 5,
                            squareBounds.bottom - 5);
                }
                canvas.drawRect(squareBounds,
                        squarePaint);
                canvas.drawText(squareText,
                        (float)(q + (spacePerCol * 0.3)),
                        (float)(p + (spacePerRow * 0.6)),
                        textPaint);
            }
        }
        if (animatedKeys.size() > 0) {
            animatedKeys.clear();
        }
    }

    public boolean isGameOverHandled() { return gameOverHandled; }

    public void handleGameOver() {
        if (!gameOverHandled) {
            SharedPreferencesHandler.increment("games_num");
            stopTimer();
            this.gameOverHandled = true;
            Grid grid = mineSweeper.getGameGrid();
            double searched = grid.countCheckedSquares();
            double numSquares = grid.getNumSquares();

            boolean winner = mineSweeper.checkSolution();
            int time = secondsPast;
            int score = getScore();
            if (winner) {
                score = 100;
            }

            int difficulty = (int) ((mineSweeper.getNumMines() / numSquares) * 100);

            SharedPreferencesHandler.add("score_total", score);
            SharedPreferencesHandler.add("time_total", time);
            SharedPreferencesHandler.add("difficulty_total", difficulty);
            if (winner) {
                SharedPreferencesHandler.increment("games_won");
            } else {
                SharedPreferencesHandler.increment("games_lost");
            }

            // evaluate after updating
            int totalScore = SharedPreferencesHandler.getTotalScore();
            int totalTime = SharedPreferencesHandler.getTotalTime();
            int totalDifficulty = SharedPreferencesHandler.getTotalDifficulty();
            int numGames = SharedPreferencesHandler.getNumGames();
            int bestScore = SharedPreferencesHandler.getBestScore();
            int worstScore = SharedPreferencesHandler.getWorstScore();
            int bestTime = SharedPreferencesHandler.getBestTime();
            int worstTime = SharedPreferencesHandler.getWorstTime();

            int avgScore = Math.round(totalScore / numGames);
            int avgTime = Math.round(totalTime / numGames);
            int avgDifficulty = Math.round(totalDifficulty / numGames);

            SharedPreferencesHandler.write("score_average", avgScore);
            SharedPreferencesHandler.write("time_average", avgTime);
            SharedPreferencesHandler.write("difficulty_average", avgDifficulty);

            if (score > bestScore) {
                SharedPreferencesHandler.write("score_best", score);
            }
            if (score < worstScore) {
                SharedPreferencesHandler.write("score_worst", score);
            }

            if (time < bestTime) {
                SharedPreferencesHandler.write("time_best", time);
            }
            if (time > worstTime) {
                SharedPreferencesHandler.write("time_worst", time);
            }

            // game_num, win/loss(1/0), time, score, searched
            int winLoss = ((winner) ? 1 : 0);
            String gameString = GameHistoryParser.genHistoryString(mineSweeper, numGames, winLoss, time, score, (int) searched);
            SharedPreferencesHandler.writeGame(numGames, gameString);
        }
    }

    public void shuffleGrid() throws MineSweeperException {
        this.mineSweeper = mineSweeper.shuffleGrid();
    }

    // MAX 1 hour
    public void setTimer(long... millis) {
        final long time;
        final long offset;
        if (millis.length > 0) {
            time = 3600000 - (1000 * millis[0]);
            offset = 1000 * millis[0];
        }
        else {
            time = 3600000;
            offset = 0;
        }
        this.countDownTimer = new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = time - millisUntilFinished;
                secondsPast = (int) ((seconds + offset) / 1000);
                invalidate();
            }

            public void onFinish() {
                try {
                    throw new MineSweeperException(getContext(), mineSweeper, 3,"");
                } catch (MineSweeperException e) {
                    e.printStackTrace();
                }
                invalidate();
            }

        }.start();
    }

    public void stopTimer() {
        countDownTimer.cancel();
    }

    @Override
    public void applyTexts(boolean keepPlaying, boolean shuffleGrid, boolean resetGame) { }

    class AndroidGestureDetector implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Grid grid = mineSweeper.getGameGrid();
            int xpos = (int) e.getX();
            int ypos = (int) e.getY();
            if (!mineSweeper.isGameOver()) {
                if (Utilities.inRange(getGridWidthSpace(), xpos, getWidth())) {
                    if (Utilities.inRange(getGridHeightSpace(), ypos, getHeight())) {
                        int[] rowCol = getRowColFromTap(xpos, ypos);
                        int row = rowCol[0];
                        int col = rowCol[1];
                        int val = grid.getValueAt(row, col);
                        boolean status = grid.getCheckStatusAt(row, col);
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (status) {
                                    if (val == Utilities.POSSIBLE.charAt(0)) {
                                        try {
                                            mineSweeper.resetPossibleMine(row, col);
                                        } catch (MineSweeperException e1) {
                                        }
                                    }
                                }
                                else {
                                    mineSweeper.setPossibleMine(row, col);
                                }
                                break;
                        }
                        invalidate();
                    }
                }
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Grid grid = mineSweeper.getGameGrid();
            int xpos = (int) e.getX();
            int ypos = (int) e.getY();
            boolean xRange = Utilities.inRange(getGridWidthSpace(), xpos, getWidth());
            boolean yRange = Utilities.inRange(getGridHeightSpace(), ypos, getHeight());
            if (!mineSweeper.isGameOver()) {
                if (xRange) {
                    if (yRange) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                int[] rowCol = getRowColFromTap(xpos, ypos);
                                int row = rowCol[0];
                                int col = rowCol[1];
                                int val = grid.getValueAt(row, col);
                                boolean status = grid.getCheckStatusAt(row, col);
                                try {
                                    if (status) {
                                        Grid subGrid = mineSweeper.subGrid(grid, row - 1, row + 1, col - 1, col + 1);
                                        Grid subGridSoln = mineSweeper.subGrid(mineSweeper.getSolnGrid(), row - 1, row + 1, col - 1, col + 1);
                                        int subGridMines = subGrid.count(Utilities.MINE) + subGrid.count(Utilities.POSSIBLE);
                                        int subGridSolnMines = subGridSoln.count(Utilities.MINE);
                                        if (subGridMines == subGridSolnMines && val < 9) {
                                            // all mines in the 3x3 grid are marked
                                            mineSweeper.setSurroundingChecked(row, col);
                                        } else {
                                            animatedKeys = new ArrayList<>(grid.getUncheckedSurrounding(row, col));
                                        }
                                    } else {
                                        boolean success = mineSweeper.selectSquare(row, col, true);
                                    }
                                } catch (MineSweeperException ignored) { }
                                invalidate();
                                break;
                        }
                    }
                }
            }
            if (Utilities.inRange(0, ypos, getGridHeightSpace())) {
                // check main button press
                if (Utilities.inRange(80, ypos, 205)) {
                    if (Utilities.inRange(50, xpos, 250)) {
                        startQuitGame();
                    }
                }

                // check reset smiley
                if (Utilities.inRange(55, ypos, 205)) {
                    if (Utilities.inRange(475, xpos, 625)) {
                        startQuitGame();
                    }
                }
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }

    public void startQuitGame() {
        QuitGamePopUp popUp = new QuitGamePopUp();
        popUp.show(supportFragmentManager, "Quit?");

        applyTexts(quitGameResponse, shuffleGridResponse, resetGame);
        invalidate();

    }

    public int[] getRowColFromTap(int x, int y) {
        int[] rowCol = new int[2];
        double widthPerSquare = getGridColWidth();
        double heightPerSquare = getGridRowHeight();
        double r, c;
        for (int i = 0; i < n_rows; i++) {
            for (int j = 0; j < n_cols; j++) {
                r = (i * heightPerSquare) + getGridHeightSpace();
                c = (j * widthPerSquare) + getGridWidthSpace();
                if (Utilities.inRange(r, y, (r + heightPerSquare))) {
                    if (Utilities.inRange(c, x, (c + widthPerSquare))) {
                        rowCol[0] = i;
                        rowCol[1] = j;
                        break;
                    }
                }
            }
        }
        return rowCol;
    }

}
