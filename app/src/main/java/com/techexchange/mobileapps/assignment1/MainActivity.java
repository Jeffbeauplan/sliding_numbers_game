package com.techexchange.mobileapps.assignment1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import static com.techexchange.mobileapps.assignment1.DifficultyActivity.KEY_EASY;
import static com.techexchange.mobileapps.assignment1.DifficultyActivity.KEY_HARD;
import static com.techexchange.mobileapps.assignment1.DifficultyActivity.KEY_MEDIUM;
import static com.techexchange.mobileapps.assignment1.ScoreActivity.KEY_RESTART_GAME;

public class MainActivity extends AppCompatActivity {

    private static String[] tileGrid;
    private static GridView gridView;
    private static int currentIndexOfWhiteSpace;
    private int moves = 0;
    private int Columns = 3;
    private int Dimensions = Columns * Columns;
    private TextView movesText;
    private TextView timerText;
    private Button resetButton;
    private Button difficultyButton;
    private boolean easy = true;
    private boolean medium = false;
    private boolean hard = false;

    Stopwatch timer = new Stopwatch();
    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    final int MSG_PAUSE_TIMER = 3;
    final int MSG_RESUME_TIMER = 4;
    final int REFRESH_RATE = 100;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_TIMER:
                    timer.start(); //start timer
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;
                case MSG_UPDATE_TIMER:
                    timerText.setText(""+ timer.toString());
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER,REFRESH_RATE); //text view is updated every second,
                    break;
                case MSG_PAUSE_TIMER:
                    timer.pause();
                    mHandler.sendEmptyMessage(MSG_PAUSE_TIMER);
                    break;
                case MSG_RESUME_TIMER:
                    timer.resume();
                    mHandler.sendEmptyMessage(MSG_RESUME_TIMER);
                    break;
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER);
                    timer.stop();//stop timer
                    timerText.setText(""+ timer.toString());
                    break;

                default:
                    break;
            }
        }
    };


    private static GridviewAdapter adapter;
    static final String KEY_MOVES = "Moves";
    static final String KEY_TIME = "Time";
    Bundle state;
    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        gridView = findViewById(R.id.grid);
        movesText = findViewById(R.id.move);
        movesText.setText("Moves: " + moves);
        appContext = getApplicationContext();
        timerText = findViewById(R.id.timer_text);
        mHandler.sendEmptyMessage(MSG_START_TIMER);
        resetButton = findViewById(R.id.reset);
        resetButton.setOnClickListener(v -> onResetPressed());
        difficultyButton = findViewById(R.id.difficulty_button);
        difficultyButton.setOnClickListener(v -> onDifficultyPressed());

        if(savedInstanceState != null) {
            tileGrid = savedInstanceState.getStringArray("tileGrid");
            moves = savedInstanceState.getInt("move");
            state = savedInstanceState;
        }
        else {
            init();
            scramble();
        }

        display(getApplicationContext());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                swapTiles(getApplicationContext(), position);
            }
        });
    }

    private void onDifficultyPressed() {
        Intent difficulty = new Intent(this, DifficultyActivity.class);
        startActivityForResult(difficulty, 0);
    }

    private void onResetPressed() {
        scramble();
        moves = 0;
        movesText.setText("Moves: " + moves);
        mHandler.sendEmptyMessage(MSG_STOP_TIMER);
        display(appContext);
        mHandler.sendEmptyMessage(MSG_START_TIMER);
    }

    private void onPausePressed() {
        //Todo: Allow player to pause game

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("tileGrid", tileGrid);
        outState.putInt("moves", moves);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(state != null) {
            tileGrid = state.getStringArray("tileGrid");
            moves = state.getInt("moves");
        }
        else {
            init();
            scramble();
        }

        display(getApplicationContext());

    }

    public void swapTiles(Context context, int position) {
        int diff = Math.abs( currentIndexOfWhiteSpace - position );
        String newPosition = tileGrid[currentIndexOfWhiteSpace];
        if ( diff == Columns || diff == 1) {
            moves++;
            movesText.setText("Moves: " + moves);
            tileGrid[currentIndexOfWhiteSpace] = tileGrid[position];
            tileGrid[position] = newPosition;
            currentIndexOfWhiteSpace = position;

            display(context);

            if (isSolved()) {
                Intent intent = new Intent(this, ScoreActivity.class);
                Bundle extras = new Bundle();
                extras.putString(KEY_TIME, timerText.getText().toString());
                extras.putInt(KEY_MOVES, moves);
                intent.putExtras(extras);
                mHandler.sendEmptyMessage(MSG_STOP_TIMER);
                startActivityForResult(intent, 0);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || requestCode != 0 || data == null) {
            finish();
        }
        else if (data.getBooleanExtra(KEY_RESTART_GAME, false)) {
            moves = 0;
            movesText.setText("Moves: " + moves);
            scramble();
            display(appContext);
            mHandler.sendEmptyMessage(MSG_START_TIMER);
        }
        else if (data.getBooleanExtra(KEY_EASY, false)) {
                Log.d("Main", "Steped into Easy");
                Columns = 3;
                Dimensions = 9;
                gridView.setNumColumns(3);
                medium = false;
                easy = true;
                hard = false;
                init();
                scramble();
                display(appContext);
                mHandler.sendEmptyMessage(MSG_START_TIMER);
        }else if (data.getBooleanExtra(KEY_MEDIUM, false)) {
                Log.d("Main", "Steped into Medium");
                Columns = 4;
                Dimensions = 16;
                gridView.setNumColumns(4);
                medium = true;
                easy = false;
                hard = false;
                init();
                scramble();
                display(appContext);
                mHandler.sendEmptyMessage(MSG_START_TIMER);
        }else if (data.getBooleanExtra(KEY_HARD, false)) {
                Log.d("Main", "Steped into Hard");
                Columns = 5;
                Dimensions = 25;
                gridView.setNumColumns(5);
                medium = false;
                easy = false;
                hard = true;
                init();
                scramble();
                display(appContext);
                mHandler.sendEmptyMessage(MSG_START_TIMER);
        }
    }

    private void display(Context context) {
        ArrayList<ImageView> tiles = new ArrayList<>();
        ImageView tile;

        for(int i = 0; i < tileGrid.length; i++) {
            tile = new ImageView(context);
            if (tileGrid[i].equals("0"))
                tile.setBackgroundResource(R.drawable.tile000);
            else if (tileGrid[i].equals("1"))
                tile.setBackgroundResource(R.drawable.tile001);
            else if (tileGrid[i].equals("2"))
                tile.setBackgroundResource(R.drawable.tile002);
            else if (tileGrid[i].equals("3"))
                tile.setBackgroundResource(R.drawable.tile003);
            else if (tileGrid[i].equals("4"))
                tile.setBackgroundResource(R.drawable.tile004);
            else if (tileGrid[i].equals("5"))
                tile.setBackgroundResource(R.drawable.tile005);
            else if (tileGrid[i].equals("6"))
                tile.setBackgroundResource(R.drawable.tile006);
            else if (tileGrid[i].equals("7"))
                tile.setBackgroundResource(R.drawable.tile007);
            else if (tileGrid[i].equals("8")) {
                if(easy == true) {
                    tile.setBackgroundColor(0);
                    currentIndexOfWhiteSpace = i;
                }
                else
                    tile.setBackgroundResource(R.drawable.tile008);
            }
            else if (tileGrid[i].equals("9")) {
                Log.d("Main", "Made it past white space");
                tile.setBackgroundResource(R.drawable.tile009);
            }
            else if (tileGrid[i].equals("10"))
                tile.setBackgroundResource(R.drawable.tile011);
            else if (tileGrid[i].equals("11"))
                tile.setBackgroundResource(R.drawable.tile012);
            else if (tileGrid[i].equals("12"))
                tile.setBackgroundResource(R.drawable.tile013);
            else if (tileGrid[i].equals("13"))
                tile.setBackgroundResource(R.drawable.tile014);
            else if (tileGrid[i].equals("14"))
                tile.setBackgroundResource(R.drawable.tile015);
            else if (tileGrid[i].equals("15")) {
                if (medium) {
                    tile.setBackgroundColor(0);
                    currentIndexOfWhiteSpace = i;
                }
                else tile.setBackgroundResource(R.drawable.tile016);
            }
            else if (tileGrid[i].equals("16"))
                tile.setBackgroundResource(R.drawable.tile017);
            else if (tileGrid[i].equals("17"))
                tile.setBackgroundResource(R.drawable.tile018);
            else if (tileGrid[i].equals("18"))
                tile.setBackgroundResource(R.drawable.tile019);
            else if (tileGrid[i].equals("19"))
                tile.setBackgroundResource(R.drawable.tile020);
            else if (tileGrid[i].equals("20"))
                tile.setBackgroundResource(R.drawable.tile021);
            else if (tileGrid[i].equals("21"))
                tile.setBackgroundResource(R.drawable.tile022);
            else if (tileGrid[i].equals("22"))
                tile.setBackgroundResource(R.drawable.tile023);
            else if (tileGrid[i].equals("23"))
                tile.setBackgroundResource(R.drawable.tile024);
            else if (tileGrid[i].equals("24")) {
                currentIndexOfWhiteSpace = i;
                tile.setBackgroundColor(0);
            }
            tiles.add(tile);
        }

        adapter = new GridviewAdapter(context,tiles, 1050 / Columns,1050/ Columns);
        gridView.setAdapter(adapter);

    }

    private void scramble() {
        int index;
        String temp;
        Random rand = new Random();

        for (int i = tileGrid.length -1; i > 0; i--) {
            index =  rand.nextInt(i + 1);
            temp = tileGrid[index];
            tileGrid[index] = tileGrid[i];
            tileGrid[i] = temp;

        }
    }

    private void init() {
        tileGrid = new String[Dimensions];
        for(int i = 0; i < tileGrid.length; i++){
            tileGrid[i] = String.valueOf(i);
        }

    }

    private static boolean isSolved() {
        boolean solved = false;

        for (int i = 0; i < tileGrid.length; i++) {
            if (tileGrid[i].equals(String.valueOf(i))) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }


        return solved;
    }
}
