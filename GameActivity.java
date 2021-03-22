package edu.moravian.csci299.gravitysnake;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that runs the actual game. Besides making sure the app is displayed
 * full-screen, this Activity sets the difficulty for the game and gets the
 * sensor for the game, adding the game view as the listener for the sensor.
 * <p>
 * NOTE: the layout for this Activity is done for you, the Activity is forced
 * to be in portrait mode so you don't have to worry about the rotation problem,
 * and the fullscreen handling is done as well. You only need to deal with
 * setting the difficulty and the sensors.
 */
public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager; // the system manager for sensors
    private Sensor gravitySensor; // the gravity sensor
    private SnakeGameView snakeGameView;
    private MediaPlayer mediaPlayer;
    private boolean soundOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        hideSystemUI(); // forces it to be fullscreen

        SharedPreferences preferences = this.getSharedPreferences("edu.moravian.csci299.gravitysnake", Context.MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        snakeGameView = findViewById(R.id.snakeGameView);


        Intent intent = getIntent();
        String highScoreKey = intent.getStringExtra("difficultyPreferenceKey");
        int difficultyInt = intent.getIntExtra("IndexOfDifficultySelected", 0);
        soundOn = intent.getBooleanExtra("SoundOnOrOffSelected", false);

        snakeGameView.setHighScoreKey(highScoreKey);
        snakeGameView.setPreferences(preferences);
        snakeGameView.setDifficulty(difficultyInt);

        if (soundOn) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.booamf);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    /**
     * called by listener for gravity sensor. Simply calls onSensorChanged() method in SnakeGameView class using its SensorEvent parameter
     * as an argument for the SnakeGameView onSensorChanged() method.
     * @param event- SensorEvent that contains information that we will pass along to the SnakeGameView onSensorChanged() method
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        snakeGameView.onSensorChanged(event);
    }

    /**
     * Does Nothing
     * @param sensor the sensor which changed accuracy
     * @param accuracy the accuracy value
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
     * when activity is stopped we must release mediaPlayer for efficiency sake.
     */
    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
    }

    /**
     * Unregister listener for gravity sensor when the activity is stopped
     */
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        sensorManager.unregisterListener(this);
    }

    /**
     * when activity is started we must release check to see if mediaPlayer has been released and if sound should be on
     * if the answer is yes to both we must set up our media player and start the game audio.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (soundOn && mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.booamf);
            mediaPlayer.start();
        }
    }

    ///// Don't worry about the rest of this code - it deals with making a fullscreen app /////

    /**
     * Timeout handler to re-hide the system UI after a delay
     */
    private final Handler timeoutHandler = new Handler();
    /**
     * The Runnable version of the hideSystemUI() function
     */
    private final Runnable hideUIRunnable = this::hideSystemUI;

    /**
     * Hides the system UI elements for the app, making the app full-screen.
     */
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        // Keep the screen on as well
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * When the focus of the app changes, possibly hide the system UI elements
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
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
}
