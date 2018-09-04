package com.techexchange.mobileapps.assignment1;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    public static final String KEY_RESTART_GAME = "RestartGame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Button againButton = findViewById(R.id.again_button);
        againButton.setOnClickListener(v -> onAgainButtonPressed());

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        TextView movesText = findViewById(R.id.moves_text);
        int moves = extras.getInt(MainActivity.KEY_MOVES,0);

        TextView timeText = findViewById(R.id.time_text);
        String time = extras.getString(MainActivity.KEY_TIME);

        timeText.setText(time);
        movesText.setText("Moves: " + moves);
    }

    private void onAgainButtonPressed() {
        Intent data = new Intent();
        data.putExtra(KEY_RESTART_GAME,true);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
