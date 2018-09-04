package com.techexchange.mobileapps.assignment1;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class DifficultyActivity extends AppCompatActivity {

    Intent data = new Intent();

    public static final String KEY_EASY = "Easy";
    public static final String KEY_MEDIUM = "Medium";
    public static final String KEY_HARD = "Hard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        Button easyButton = findViewById(R.id.easy_button);
        easyButton.setOnClickListener(v -> onEasyPressed());

        Button mediumButton = findViewById(R.id.medium_button);
        mediumButton.setOnClickListener(v -> onMediumPressed());

        Button hardButton = findViewById(R.id.hard_button);
        hardButton.setOnClickListener(v -> onHardPressed());
    }

    private void onHardPressed() {
        data.putExtra(KEY_HARD, true);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void onMediumPressed() {
        data.putExtra(KEY_MEDIUM, true);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void onEasyPressed() {
        data.putExtra(KEY_EASY, true);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
