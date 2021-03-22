package edu.moravian.csci299.gravitysnake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

/**
 * Activity that is first, its view acts as the "welcome" screen for the game. This class extends AppCompatActivity and
 * implements View.OnClickListener for the start button and AdapterView.OnItemSelectedListener for the difficulty spinner. Contains a spinner for switching
 * difficulties, spinner remembers the previously selected difficulties and populates that difficulty as selected item.
 * Displays high scores specific to that difficulty, also remembered after lifecycle is destroyed.
 * Contains the start button to initiate the start of the game, passing various information
 * through to GameActivity through an intent such as the difficulty to set appropriate parameters in SnakeGameView, as well as
 * whether or not to play music, and the highScoreKey String to be used too save with high score within preferences if player beats old
 * high score. Lifecycle methods are appropriately overridden to ensure that music audio starts when music is on and app is brought to foreground
 * but stops when app is paused, stopped, or destroyed.
 */
public class StartActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private Spinner spinner;
    private SwitchCompat soundSwitch;
    private MediaPlayer mediaPlayer;
    private TextView highScoreText;
    private SharedPreferences preferences;

    int highScore = 0;
    private int difficulty;
    private String highScoreKey;
    private final String difficultyKey = "difficulty_preference";
    private final String musicKey = "music_preference";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        this.preferences = this.getSharedPreferences("edu.moravian.csci299.gravitysnake", Context.MODE_PRIVATE);


        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.booamf);
        soundSwitch = findViewById(R.id.soundSwitch);
        highScoreText = findViewById(R.id.highScoreValue);
        TextView highScoreLabel = findViewById(R.id.highScore);
        findViewById(R.id.playButton).setOnClickListener(this);
        spinner = findViewById(R.id.difficultySpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        this.difficulty = preferences.getInt(difficultyKey, 0);
        spinner.setSelection(difficulty);



        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if(!mediaPlayer.isPlaying())
                    mediaPlayer.start();
                preferences.edit().putBoolean(musicKey, true).apply();
            }
            else {
                mediaPlayer.pause();
                preferences.edit().putBoolean(musicKey, false).apply();
            }
        });

        if (preferences.getBoolean(musicKey, true)){
            soundSwitch.setChecked(true);
            if(!mediaPlayer.isPlaying())
                mediaPlayer.start();
        }
        else{
            soundSwitch.setChecked(false);
            mediaPlayer.pause();
        }


        highScoreLabel.setText(R.string.highScore);
        setHighScoreText();
    }

    /**
     * When the "Play!" button is pressed we proceed to the GameActivity
     *
     * @param v the view that was clicked (i.e. the "Play" button)
     */
    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.playButton))
            createIntentStartGame();
    }

    /**
     * When the play button is clicked, this method is called.
     * Create an intent with the sound setting, difficulty selected, and the high score key
     * and pause the mediaPlayer
     */
    private void createIntentStartGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("SoundOnOrOffSelected", soundSwitch.isChecked());
        intent.putExtra("IndexOfDifficultySelected", difficulty);
        intent.putExtra("difficultyPreferenceKey", highScoreKey);
        mediaPlayer.pause();
        startActivity(intent);
    }

    /**
     * On activity start, create and configure the media player
     */
    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.booamf);
        mediaPlayer.setLooping(true);
    }

    /**
     * On activity pause, if the media player is playing, pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    /** When the activity stops, we release the media player. */
    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
//        mediaPlayer = null;
    }


    /**
     * On Activity Resume, if the media player is not playing and the sound switch is checked
     * Play the mediaPlayer
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(soundSwitch.isChecked() && !mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    /**
     * When an item is selected in the spinner, set the high score text to display the high score
     * for that difficulty
     * @param parent the parent AdapterView
     * @param view the view the item is selected in
     * @param position the position of the item selected
     * @param id the id of the item selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.difficulty = position;
        setHighScoreText();
        preferences.edit().putInt(difficultyKey, position).apply();
    }

    /**
     * but must be implemented but does nothing
     * @param parent the parent AdapterView
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
//        Do nothing
    }

    /**
     * method called to set the high score textView to display the high score of difficulty currently selected.
     * Also sets highScoreKey to appropriate difficulty key so preferences can later save a new high score to
     * that specific key.
     */
    private void setHighScoreText(){
        if (difficulty == 0){
            highScore = preferences.getInt("high_beginner_preference", 0);
            highScoreKey = "high_beginner_preference";
        }
        else if (difficulty == 1){
            highScore = preferences.getInt("high_easy_preference", 0);
            highScoreKey = "high_easy_preference";
        }
        else if (difficulty == 2){
            highScore = preferences.getInt("high_medium_preference", 0);
            highScoreKey = "high_medium_preference";
        }
        else if (difficulty == 3){
            highScore = preferences.getInt("high_hard_preference", 0);
            highScoreKey = "high_hard_preference";
        }
        else{
            highScore = preferences.getInt("high_insane_preference", 0);
            highScoreKey = "high_insane_preference";
        }
        highScoreText.setText(Integer.toString(highScore));
    }

}