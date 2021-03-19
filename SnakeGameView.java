package edu.moravian.csci299.gravitysnake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;

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


    // Required constructors for making your own view that can be placed in a layout
    public SnakeGameView(Context context) { this(context, null);  }
    public SnakeGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Get the metrics for the display so we can later convert between dp, sp, and px
        displayMetrics = context.getResources().getDisplayMetrics();

        // Make the game
        snakeGame = new SnakeGame();

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
        snakePaint.setColor(Color.BLUE);
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

    /**
     * @param difficulty the new difficulty for the game
     */
    public void setDifficulty(int difficulty) {
        // TODO: may need to set lots of things here to change the game's difficulty
            snakeGame.setInitialSpeed(difficulty+0.5);
            snakeGame.setWallPlacementProbability(difficulty / 10 + .1);
            snakeGame.setLengthIncreasePerFood(difficulty+1);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        postInvalidateOnAnimation(); // automatically invalidate every frame so we get continuous playback

        // TODO: update the game and draw the view
        PointF foodLocation = snakeGame.getFoodLocation();
        List<PointF> wallLocations = snakeGame.getWallLocations();
        canvas.drawCircle(foodLocation.x, foodLocation.y, dpToPx(SnakeGame.FOOD_SIZE_DP), foodPaint);
        canvas.drawText(String.valueOf(snakeGame.getScore()), canvas.getWidth()/2,100, scorePaint);
        List<PointF> snakeLocation = snakeGame.getSnakeBodyLocations();
        for(int i = snakeLocation.size()-1; i > -1; i--){
            PointF point = snakeLocation.get(i);
            if(i==0)
                canvas.drawCircle(point.x, point.y, dpToPx(Snake.BODY_PIECE_SIZE_DP), headPaint);
            else
                canvas.drawCircle(point.x, point.y, dpToPx(Snake.BODY_PIECE_SIZE_DP), snakePaint);
        }
        for(int i = 0; i < wallLocations.size(); i++)
            canvas.drawCircle(wallLocations.get(i).x, wallLocations.get(i).y, dpToPx(SnakeGame.WALL_SIZE_DP), wallPaint);

        snakeGame.update();
        this.preferences.edit().putInt(highScoreKey, snakeGame.getScore()).apply();

    }





    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO
        float x = event.values[0];
        float y = event.values[1];
        double hypotenuse = Math.hypot(x, y);

//      Hypotenuse value must be from 0-1 so we need to normalize it
        double normalized = (hypotenuse) / (9.81);
        double angle = Math.atan2(y, -x);

        snakeGame.setMovementDirection(angle);
    }

    /** Does nothing but must be provided. */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }


    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void setHighScoreKey(String highScoreKey) {
        this.highScoreKey = highScoreKey;
    }

}
