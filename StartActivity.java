package edu.moravian.csci299.gravitysnake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    //  The spinner object
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Set up the difficulty spinner
        spinner = findViewById(R.id.difficultySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set up the "Play" button
        findViewById(R.id.playButton).setOnClickListener(this);
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
            intent.putExtra("spinnerDifficultySelected", difficulty);
            startActivity(intent);
        }
    }

}