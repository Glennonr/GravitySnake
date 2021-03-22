package edu.moravian.csci299.gravitysnake;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * The custom View for the Snake Game. This handles the user interaction and
 * sensor information for the snake game but has none of the game logic. That
 * is all within SnakeGame and Snake.
 *
 * NOTE: This class is where most of the work is required. You must document
 * *all* methods besides the constructors (this includes methods already
 * declared that don't have documentation). You will also need to add at least
 * a few methods to this class.
 */
public class SnakeGameView extends View implements SensorEventListener {
    /** The paints and drawables used for the different parts of the game */
    // TODO: you will need to add many of these (this one is provided as an example for text paint)
    private final Paint scorePaint = new Paint();
    private final Paint foodPaint = new Paint();
    private final Paint snakePaint = new Paint();
    private final Paint headPaint = new Paint();
    private final Paint wallPaint = new Paint();



    /** The metrics about the display to convert from dp and sp to px */
    private final DisplayMetrics displayMetrics;

    /** The snake game for the logic behind this view */
    private final SnakeGame snakeGame;

    private String highScoreKey;
    private SharedPreferences preferences;
    private GameActivity gameActivity;



    // Required constructors for making your own view that can be placed in a layout
    public SnakeGameView(Context context) { this(context, null);  }
    public SnakeGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Get the metrics for the display so we can later convert between dp, sp, and px
        displayMetrics = context.getResources().getDisplayMetrics();

        // Make the game
        snakeGame = new SnakeGame();
        this.gameActivity = (GameActivity) context;

        // This color is automatically painted as the background
        // TODO: feel free to change this (and it can even be changed to any Drawable if you use setBackground() instead)
        setBackgroundColor(0xFF333333);

        // Setup all of the paints and drawables used for drawing later
        // TODO: this one paint is a demonstration for text, solid colors usually just require setting the color
        scorePaint.setColor(Color.WHITE);
        scorePaint.setAntiAlias(true);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setTextSize(spToPx(24)); // use sp for text
        scorePaint.setFakeBoldText(true);

        foodPaint.setColor(Color.RED);
        snakePaint.setColor(Color.parseColor("#21C14C"));
        headPaint.setColor(Color.GREEN);
        wallPaint.setColor(Color.YELLOW);
    }

    /**
     * @return the snake game for this view
     */
    public SnakeGame getSnakeGame() { return snakeGame; }

    /**
     * Utility function to convert dp units to px units. All Canvas and Paint
     * function use numbers in px units but dp units are better for
     * inter-device support.
     * @param dp the size in dp (device-independent-pixels)
     * @return the size in px (pixels)
     */
    public float dpToPx(float dp) { return dp * displayMetrics.density; }

    /**
     * Utility function to convert sp units to px units. All Canvas and Paint
     * function use numbers in px units but sp units are better for
     * inter-device support, especially for text.
     * @param sp the size in sp (scalable-pixels)
     * @return the size in px (pixels)
     */
    public float spToPx(float sp) { return sp * displayMetrics.scaledDensity; }

    /** Scales the initial speed, the probability of wall placements, and the length increase per food by calling approaptiate
     * setters in SnakeGame class.
     * @param difficulty the new difficulty for the game
     */
    public void setDifficulty(int difficulty) {
        // TODO: may need to set lots of things here to change the game's difficulty
        if(difficulty == 0) {
            snakeGame.setInitialSpeed(0.5);
            snakeGame.setWallPlacementProbability(0);
            snakeGame.setSpeedIncreasePerFood(0.01);
        }
        else if (difficulty == 1) {
            snakeGame.setInitialSpeed(0.75);
            snakeGame.setWallPlacementProbability(0.0025);
            snakeGame.setSpeedIncreasePerFood(0.02);
        }
        else if (difficulty == 2) {
            snakeGame.setInitialSpeed(1);
            snakeGame.setWallPlacementProbability(0.0025);
            snakeGame.setSpeedIncreasePerFood(0.04);
        }
        else if (difficulty == 3) {
            snakeGame.setInitialSpeed(1.75);
            snakeGame.setWallPlacementProbability(0.005);
            snakeGame.setSpeedIncreasePerFood(0.05);
        }
        else{
            snakeGame.setInitialSpeed(2);
            snakeGame.setWallPlacementProbability(0.0075);
            snakeGame.setSpeedIncreasePerFood(0.06);
        }
        snakeGame.setLengthIncreasePerFood(10);


        }

    /**
     * Once the view is laid out, we know the dimensions of it and can start
     * the game with the snake in the middle (if the game hasn't already
     * started). We also take this time to set the dp to px factor of the
     * snake.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // NOTE: this function is done for you
        super.onLayout(changed, left, top, right, bottom);
        if (snakeGame.hasNotStarted()) {
            snakeGame.startGame(right - left, bottom - top);
            snakeGame.setDpToPxFactor(displayMetrics.density);
        }
        invalidate();
    }

    /**
     *Continuously invalidated method for continuous play back. Draws canvas, goes through and draws food object as circle,
     * draws the score, draws the snakes body iterating through snakeLocation list, draws the walls by iterating through the wallLocations
     * list, calls update to update() in SnakeGame the snakeGame object and finally checks to see if a new high score was reached if
     * so then updates preferences appropriately.
     * @param canvas Canvas object containing what to draw
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        postInvalidateOnAnimation(); // automatically invalidate every frame so we get continuous playback

        // TODO: update the game and draw the view
        PointF foodLocation = snakeGame.getFoodLocation();

        canvas.drawCircle(foodLocation.x, foodLocation.y, dpToPx(SnakeGame.FOOD_SIZE_DP), foodPaint);
        canvas.drawText(String.valueOf(snakeGame.getScore()), getWidth() >> 1,100, scorePaint);
        drawSnake(canvas);
        drawWalls(canvas);

        snakeGame.update();

        if(snakeGame.getScore() > preferences.getInt(highScoreKey, 100))
            this.preferences.edit().putInt(highScoreKey, snakeGame.getScore()).apply();
    }


    private void drawWalls(Canvas canvas) {
        List<PointF> wallLocations = snakeGame.getWallLocations();
        for(PointF point: wallLocations)
            canvas.drawCircle(point.x, point.y, dpToPx(SnakeGame.WALL_SIZE_DP), wallPaint);
    }

    private void drawSnake(Canvas canvas) {
        List<PointF> snakeLocation = snakeGame.getSnakeBodyLocations();
        for(int i = snakeLocation.size() - 1; i >= 0; i--){
            PointF point = snakeLocation.get(i);
            if(i==0)
                canvas.drawCircle(point.x, point.y, dpToPx(Snake.BODY_PIECE_SIZE_DP), headPaint);
            else
                canvas.drawCircle(point.x, point.y, dpToPx(Snake.BODY_PIECE_SIZE_DP), snakePaint);
        }
    }


    /**
     * called from GameActivity class. Measures the angle between the gravity sensor's vector values of acceleration on x and y axeses
     * and uses Math.atan2() method to get the angle between the two vectors then passes to SnakeGame's setMovementDirection() method.
     * @param event the SensorEvent for the sensor that had a change
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO
        float x = event.values[0];
        float y = event.values[1];

        double angle = Math.atan2(y, -x);

        snakeGame.setMovementDirection(angle);
    }

    /** Does nothing but must be provided. */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    /**
     * Sets preferences class variable to the preferences needed to save information across lifecycle changes
     * @param preferences preference to set class variable preferences to
     */
    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    /**
     * sets class variable highScoreKey to be used to get high score to appropriate key based on difficulty
     * @param highScoreKey the difficulty that was selected so the appropriate high score is changed
     */
    public void setHighScoreKey(String highScoreKey) {
        this.highScoreKey = highScoreKey;
    }


    /**
     * Handles touch motionEvents. If the game is over then the touch event of pressing down brings us back to the start scree.
     * If the game is still in progress we pass the point containing the x and y values where MotionEvent occurred to touched()
     * method in our instance of snakeGame to see if touch affected game. We then invalidate to redraw view.
     * @param event MotionEvent that contains the PointF object containing info on x and y values touchEvent occurred at.
     * @return True after MotionEvent is handled
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF point = new PointF(event.getX(), event.getY());
        if (snakeGame.isGameOver()){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                this.gameActivity.finish();
            }
        }
        else{
            snakeGame.touched(point);
        }
        invalidate();
        return true;
    }

    /**
     * sets gameActivity class variable to the instance of GameActivity that called it so that we can call finish and end its lifecycle
     * after game is over
     * @param gameActivity the GameActivity that is using this view
     */
    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }



}
