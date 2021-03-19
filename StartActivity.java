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



public class StartActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    //  The spinner object
    private Spinner spinner;
    int highScore = 0;


    private MediaPlayer mediaPlayer;

    private SwitchCompat soundSwitch;

    private SharedPreferences preferences;
    private TextView highScoreText;

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

        this.preferences = this.getSharedPreferences("edu.moravian.csci299.gravitysnake", Context.MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.booamf);
        mediaPlayer.setLooping(true);
        highScore = 0;

        // Set up the difficulty spinner
        spinner = findViewById(R.id.difficultySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        this.difficulty = preferences.getInt(difficultyKey, 0);
        spinner.setSelection(difficulty);

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

        if (preferences.getBoolean(musicKey, true)){
            soundSwitch.setChecked(true);
            mediaPlayer.start();
        }
        else{
            soundSwitch.setChecked(false);
            mediaPlayer.pause();
        }

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
            String difficulty = (spinner.getSelectedItem()).toString();


            intent.putExtra("SoundOnOrOffSelected", soundSwitch.isChecked());
            intent.putExtra("spinnerDifficultySelected", difficulty);
            intent.putExtra("difficultyPreferenceKey", highScoreKey);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.difficulty = position;
        setHighScoreText();
        preferences.edit().putInt(difficultyKey, position).apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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