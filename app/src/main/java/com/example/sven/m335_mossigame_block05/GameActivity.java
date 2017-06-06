package com.example.sven.m335_mossigame_block05;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    int timeRemaining;
    int mosquitos;
    int mosquitosCatched;
    int level;
    int score;
    private static final int GAMETIME = 60;

    TextView txtLevel, txtScore, txtTimeRemaining,
            txtMossisCatched;

    Random random;
    FrameLayout barTime, barMosquitos;
    ViewGroup gameZone;
    Date tStart;

    private void startGame(){
        level = 0;
        score = 0;
        startLevel();
    }

    private void startLevel(){
        mosquitosCatched = 0;
        level = level+1;
        mosquitos = level * 10;
        timeRemaining = GAMETIME;
        tStart = new Date();
        refreshScreen();
        //showMosquito();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // init textView variables
        random = new Random();

        txtLevel = (TextView)findViewById(R.id.txtViewLevel);
        txtScore = (TextView) findViewById(R.id.txtViewScore);
        txtMossisCatched = (TextView) findViewById(R.id.textViewCatches);
        txtTimeRemaining = (TextView) findViewById(R.id.textViewTimeLeft);
        barTime = (FrameLayout)findViewById(R.id.bar_time);
        barMosquitos = (FrameLayout)findViewById(R.id.bar_catches);
        gameZone = (ViewGroup)findViewById(R.id.gameZone);

        Button showMossiBtn = (Button) findViewById(R.id.buttonShowMossi);
        //attach onclick listener to demonstrate mossi generation
        showMossiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMosquito();
            }
        });

        startGame();

    }

    /**
     * display current game information
     */
    private void refreshScreen(){

        int dTime = (int)(new Date().getTime() - tStart.getTime())/1000;
        timeRemaining = GAMETIME - dTime;
        txtLevel.setText(Integer.toString(level));
        txtScore.setText(Integer.toString(score));
        txtTimeRemaining.setText(Integer.toString(timeRemaining));
        txtMossisCatched.setText(Integer.toString(mosquitosCatched));

        // time bar handling
        ViewGroup.LayoutParams params = barTime.getLayoutParams();
        int fullWidth = gameZone.getMeasuredWidth();
        params.width = fullWidth / GAMETIME * (GAMETIME - dTime);
        // mosquito bar handling
        params = barMosquitos.getLayoutParams();
        params.width = fullWidth / mosquitos * mosquitosCatched;

    }

    private void showMosquito(){
        gameZone = (ViewGroup)findViewById(R.id.gameZone);
        int width = gameZone.getMeasuredWidth();
        int height = gameZone.getMeasuredHeight();
        Log.d("MOSSI_GAMEACTIVITY", "width:"+width+", height: "+height);
        int mossiSize = 150;

        int distTop = random.nextInt(height-mossiSize);
        int distLeft = random.nextInt(width-mossiSize);
        //neuen imageView für mossi anlegen
        ImageView newMossi = new ImageView(this);
        newMossi.setImageResource(R.drawable.mosquito);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(mossiSize, mossiSize);
        params.topMargin = distTop;
        params.leftMargin = distLeft;
        gameZone.addView(newMossi, params);

        newMossi.setOnClickListener(this);

    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        ImageView mossi = (ImageView)v;
        score += 100;
        mosquitosCatched++;
        gameZone.removeView(v);
        refreshScreen();
    }
}
