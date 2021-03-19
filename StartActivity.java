package edu.moravian.csci299.gravitysnake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.solver.widgets.Rectangle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;


public class StartActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private SharedPreferences preferences;
    private TextView highScoreText;
    private Spinner spinner;
    private MediaPlayer mediaPlayer;
    private SwitchCompat audioSwitch;
    private int difficulty;
    private String highScoreKey;
    private int highScore = 0;
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
        mediaPlayer = new MediaPlayer();

        this.audioSwitch = findViewById(R.id.audioSwitch);
        audioSwitch.setOnCheckedChangeListener(this);
        setAudio(R.raw.booamf);


        if (preferences.getBoolean(musicKey, true)){
            audioSwitch.setChecked(true);
            mediaPlayer.start();
        }
        else{
            audioSwitch.setChecked(false);
            mediaPlayer.pause();
        }

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

        highScoreText = findViewById(R.id.highScoreText);
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
            intent.putExtra("spinnerDifficultySelected", difficulty);
            intent.putExtra("difficultyPreferenceKey", highScoreKey);
            startActivity(intent);
        }
    }

    /**
     * Sets the audio being played from a resource ID. This re-uses the current
     * media player object.
     *
     * @param resourceid the resource audio for the audio, like R.raw.monkey.
     */
    private void setAudio(int resourceid) {
        AssetFileDescriptor afd = getResources().openRawResourceFd(resourceid);
        if (afd != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.prepare();
                afd.close();
            } catch (IOException ex) {
                Log.e("MainActivity", "set audio resource failed:", ex);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
            preferences.edit().putBoolean(musicKey, true).apply();
        } else {
            mediaPlayer.pause();
            preferences.edit().putBoolean(musicKey, false).apply();
        }
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
        highScoreText.setText(highScore);
    }


}