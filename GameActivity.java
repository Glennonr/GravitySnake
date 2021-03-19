package edu.moravian.csci299.gravitysnake;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * Activity that runs the actual game. Besides making sure the app is displayed
 * full-screen, this Activity sets the difficulty for the game and gets the
 * sensor for the game, adding the game view as the listener for the sensor.
 *
 * NOTE: the layout for this Activity is done for you, the Activity is forced
 * to be in portrait mode so you don't have to worry about the rotation problem,
 * and the fullscreen handling is done as well. You only need to deal with
 * setting the difficulty and the sensors.
 */
public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private SharedPreferences preferences;
    private SensorManager sensorManager; // the system manager for sensors
    private Sensor gravitySensor; // the gravity sensor

    private SnakeGameView snakeGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        hideSystemUI(); // forces it to be fullscreen
        preferences = this.getSharedPreferences("edu.moravian.csci299.gravitysnake", Context.MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        snakeGameView = findViewById(R.id.snakeGameView);

        Intent intent = getIntent();
        int difficulty = intent.getIntExtra("spinnerDifficultySelected", 0);
        String highScoreKey = intent.getStringExtra("spinnerDifficultySelected");
        snakeGameView.setDifficulty(difficulty);
        snakeGameView.setHighScoreKey(highScoreKey);
        snakeGameView.setPreferences(this.preferences);

    }

    ///// Don't worry about the rest of this code - it deals with making a fullscreen app /////

    /** Timeout handler to re-hide the system UI after a delay */
    private final Handler timeoutHandler = new Handler();
    /** The Runnable version of the hideSystemUI() function */
    private final Runnable hideUIRunnable = this::hideSystemUI;

    /** Hides the system UI elements for the app, making the app full-screen. */
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        // Keep the screen on as well
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /** When the focus of the app changes, possibly hide the system UI elements */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) { hideSystemUI(); }
    }

    /**
     * When the user interacts, the timer is reset for re-hiding the system UI.
     */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        timeoutHandler.removeCallbacks(hideUIRunnable);
        timeoutHandler.postDelayed(hideUIRunnable, 2000);
    }

    /**
     * Called every time listener detects change in sensor values when checked at the sampling interval
     * if any changes occurs listner calls method passing in SensorEvent containing information we need to
     * pass to snakeGameView's onSensorChanged method to update view using new values.
     * @param event- SensorEvent passed from listener, specifically in our case from gravity sensor
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        snakeGameView.onSensorChanged(event);
    }

    /**
     * necessary for implementing onSensorListener but does nothing
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//      Do Nothing
    }

    /**
     * Register listeners when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    /**
     * Unregister listeners when the activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
