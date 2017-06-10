package com.example.sven.m335_mossigame_block05;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    private static final String LOGTAG = "GameActivity";
    private static final int LEVEL_TIME = 60;
    public static final int MOSSI_LIFTIME = 2000;
    double timeRemaining;
    final int TIMESLOT = 100;
    int mosquitos, mosquitosCreated;
    int mosquitosCatched;
    int level;
    int score;

    TextView txtLevel, txtScore, txtTimeRemaining,
            txtMossisCatched;

    Random random;
    Handler handler;
    FrameLayout barTime, barMossis;
    ViewGroup gameZone;
    Date tStart;
    boolean isActive;


    private void startGame(){
        level = 0;
        score = 0;
        isActive = true;
        random = new Random();
        startLevel();
    }

    private void startLevel(){
        mosquitosCatched = 0;
        level = level+1;
        mosquitos = level * 10;
        timeRemaining = LEVEL_TIME;
        tStart = new Date();
        refreshScreen();
        mosquitosCreated = 0;
        handler.postDelayed(this, TIMESLOT);
    }

    private void decrementTime(){
        timeRemaining = LEVEL_TIME - (new Date().getTime() - tStart.getTime())/1000;
        //Log.d(LOGTAG, "timeRemaining: " + timeRemaining);

        if (!showGameFinished()) {
            if (!levelFinished()) {
                double pMossisToShow = 1.50 * mosquitos /(LEVEL_TIME * 1000/TIMESLOT);
                if (random.nextDouble() < pMossisToShow) {
                    showMosquito();
                    mosquitosCreated++;
                }

                hideMossis();
                refreshScreen();
                handler.postDelayed(this, TIMESLOT);

            }
            else {
                startLevel();
            }
        }
    }

    private boolean levelFinished(){
        return (mosquitosCatched>=mosquitos && timeRemaining>=0);
    }

    /**
     * displays a small GameOver screen
     * @return
     */
    private boolean showGameFinished(){
        if (timeRemaining==0){
            Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.game_over);
            dialog.show();
            Toast.makeText(this, String.valueOf(mosquitosCreated), Toast.LENGTH_LONG).show();
            isActive = false;
            //apply return value for the activity
            setResult(this.score);
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // init textView variables
        random = new Random();
        handler = new Handler();
        txtLevel = (TextView)findViewById(R.id.txtViewLevel);
        txtScore = (TextView) findViewById(R.id.txtViewScore);
        txtMossisCatched = (TextView) findViewById(R.id.textViewCatches);
        txtTimeRemaining = (TextView) findViewById(R.id.textViewTimeLeft);
        barTime = (FrameLayout)findViewById(R.id.bar_time);
        barMossis = (FrameLayout)findViewById(R.id.bar_catches);
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

        txtLevel.setText(Integer.toString(level));
        txtScore.setText(Integer.toString(score));
        txtTimeRemaining.setText(Integer.toString((int)timeRemaining));
        txtMossisCatched.setText(Integer.toString(mosquitosCatched));

        ViewGroup.LayoutParams params = barTime.getLayoutParams();
        int fullWidth = gameZone.getMeasuredWidth();
        int dTime = (int)(new Date().getTime() - tStart.getTime())/1000;
        params.width = fullWidth / LEVEL_TIME * (LEVEL_TIME - dTime);

        params = barMossis.getLayoutParams();
        params.width = fullWidth / mosquitos * (mosquitosCatched);


    }

    /**
     * create and show new mosquito
     */
    private void showMosquito(){
        gameZone = (ViewGroup)findViewById(R.id.gameZone);
        int width = gameZone.getMeasuredWidth();
        int height = gameZone.getMeasuredHeight();
        //Log.d("MOSSI_GAMEACTIVITY", "width:"+width+", height: "+height);
        int mossiSize = 150;

        int distTop = random.nextInt(height-mossiSize);
        int distLeft = random.nextInt(width-mossiSize);
        //neuen imageView für mossi anlegen
        ImageView newMossi = new ImageView(this);
        newMossi.setImageResource(R.drawable.mosquito);
        newMossi.setTag(R.id.bday,new Date());

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(mossiSize, mossiSize);
        params.topMargin = distTop;
        params.leftMargin = distLeft;
        gameZone.addView(newMossi, params);

        newMossi.setOnClickListener(this);

    }

    /**
     * hide mosquitos past their live time
     */
    private void hideMossis(){
        int currentIndex = 0;
        while (currentIndex < gameZone.getChildCount()){
            ImageView currentMossi = (ImageView)gameZone.getChildAt(currentIndex);
            Date bday = (Date)(currentMossi.getTag(R.id.bday));
            if (new Date().getTime()-bday.getTime() > MOSSI_LIFTIME+500) {
                gameZone.removeView(currentMossi);
            } else if (new Date().getTime()-bday.getTime() > MOSSI_LIFTIME &&
                    currentMossi.hasOnClickListeners()){
                gameZone.removeView(currentMossi);
            } else {
                currentIndex++;
            }
        }
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
        mossi.setImageResource(R.drawable.mosquito_hit);
        mossi.setOnClickListener(null);
        //refreshScreen();
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        decrementTime();
    }
}
