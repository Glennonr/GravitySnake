package edu.moravian.csci299.gravitysnake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
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


    private Spinner difficultySpinner;
    int highScore;

    private MediaPlayer mediaPlayer;

    private SwitchCompat soundSwitch;

    private TextView highScoreText;

    private SharedPreferences preferences;

    private int difficulty;
    private String highScoreKey;
    private final String difficultyKey = "difficulty_preference";
    private final String musicKey = "music_preference";
    private final String highScoreBeginnerKey = "high_beginner_preference";
    private final String highScoreEasyKey = "high_easy_preference";
    private final String highScoreMediumKey = "high_medium_preference";
    private final String highScoreHardKey = "high_hard_preference";
    private final String highScoreInsaneKey = "high_insane_preference";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //set up preference and media player for audio.
        this.preferences = this.getSharedPreferences("edu.moravian.csci299.gravitysnake", Context.MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.booamf);
        mediaPlayer.setLooping(true);
        highScore = 0;

        // Set up the difficulty spinner
        difficultySpinner = findViewById(R.id.difficultySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);

        difficultySpinner.setOnItemSelectedListener(this);
        this.difficulty = preferences.getInt(difficultyKey, 0);
        difficultySpinner.setSelection(difficulty);

        // Set up the "Play" button
        findViewById(R.id.playButton).setOnClickListener(this);



        // Set up the sound switch
        soundSwitch = findViewById(R.id.soundSwitch);

        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                preferences.edit().putBoolean(musicKey, true).apply();
            } else {
                mediaPlayer.pause();
                preferences.edit().putBoolean(musicKey, false).apply();
            }
        });
        // use preferences to see if user last wanted music on or off
        if (preferences.getBoolean(musicKey, true)){
            soundSwitch.setChecked(true);
            mediaPlayer.start();
        }
        else{
            soundSwitch.setChecked(false);
            mediaPlayer.pause();
        }

        // set high score textview to display appropriate number based on which difficulty is currently selected

        TextView highScoreLabel = findViewById(R.id.highScore);
        highScoreLabel.setText(R.string.highScore);
        highScoreText = findViewById(R.id.highScoreValue);
        highScoreText.setText(R.string.highScore);

        setHighScoreText();
    }

    /**
     * When the "Play!" button is pressed we proceed to the GameActivity
     *
     * @param v the view that was clicked (i.e. the "Play" button)
     */
    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.playButton)) {
            Intent intent = new Intent(this, GameActivity.class);
            String difficulty = (difficultySpinner.getSelectedItem()).toString();


            intent.putExtra("SoundOnOrOffSelected", soundSwitch.isChecked());
            intent.putExtra("spinnerDifficultySelected", difficulty);
            intent.putExtra("difficultyPreferenceKey", highScoreKey);
            startActivity(intent);
        }
    }

    /**
     * when activity is started must check to see if mediaPlayer was released and if it was then set it up again
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.booamf);
            mediaPlayer.setLooping(true);
        }
    }

    /**
     * when activity is resumed we must check to see if the sound switch is on and if it is we must play music audio.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(soundSwitch.isChecked())
            mediaPlayer.start();
    }
    /**
     * when activity is paused we must pause music audio if it is currently playing.
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
        mediaPlayer = null;
    }

    /**
     * onItemSelected method called by spinner listener every time spinner value is changed. Every time
     * this method is called we update the TextView to display high score for selected difficulty and we set difficultyKey
     * in preferences to the position that was selected to that preferences remember our last selected difficulty.
     *
     * @param parent
     * @param view- view object specifically for us will always be difficultySpinner
     * @param position- index of the selected item in the spinner array
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.difficulty = position;
        setHighScoreText();
        preferences.edit().putInt(difficultyKey, position).apply();
    }

    /**
     * does nothing, needed for onSelecterListerner interface.
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * method called to set the high score textview to display the high score of difficulty currently selected.
     * Also sets highScoreKey to appropriate difficulty key so preferences can later save a new high score to
     * that specific key.
     */
    private void setHighScoreText(){
        if (difficulty == 0){
            highScore = preferences.getInt(highScoreBeginnerKey, 0);
            highScoreKey = highScoreBeginnerKey;
        }
        else if (difficulty == 1){
            highScore = preferences.getInt(highScoreEasyKey, 0);
            highScoreKey = highScoreEasyKey;
        }
        else if (difficulty == 2){
            highScore = preferences.getInt(highScoreMediumKey, 0);
            highScoreKey = highScoreMediumKey;
        }
        else if (difficulty == 3){
            highScore = preferences.getInt(highScoreHardKey, 0);
            highScoreKey = highScoreHardKey;
        }
        else{
            highScore = preferences.getInt(highScoreInsaneKey, 0);
            highScoreKey = highScoreInsaneKey;
        }

        highScoreText.setText(Integer.toString(highScore));
    }

}