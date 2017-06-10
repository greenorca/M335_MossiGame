package com.example.sven.m335_mossigame_block05;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import javax.xml.transform.OutputKeys;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnSave;
    LinearLayout layoutNameDialog;
    private TextView highScoreText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button) findViewById(R.id.button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        highScoreText = (TextView) findViewById(R.id.textViewHighScore);
        
        btnSave =(Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(this);

        layoutNameDialog = (LinearLayout) findViewById(R.id.nameInputLayout);
        layoutNameDialog.setVisibility(View.GONE);
    }

    private void startGame(){
        Intent i = new Intent(this, GameActivity.class);
        startActivityForResult(i,1);
    }

    @Override
    protected void onResume(){
        super.onResume();
        int x = readHighScore();
        if (x > 0)
            highScoreText.setText(String.valueOf(x)+" von "+readHighScoreName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode==1){
            if (resultCode > readHighScore()){
                writeHighScore(resultCode);
                layoutNameDialog.setVisibility(View.VISIBLE);
            }
        }
    }

    private void writeHighScore(int resultCode) {
        SharedPreferences prefs = getSharedPreferences("Game", 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("HIGHSCORE", resultCode);
        edit.commit();
    }

    private int readHighScore() {
        SharedPreferences prefs = getSharedPreferences("Game", 0);
        return prefs.getInt("HIGHSCORE",0);
    }

    private void writeHighScoreName(String name) {
        SharedPreferences prefs = getSharedPreferences("Game", 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("HIGHSCORE_NAME", name);
        edit.commit();
    }
    private String readHighScoreName() {
        SharedPreferences prefs = getSharedPreferences("Game", 0);
        return prefs.getString("HIGHSCORE_NAME","");
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        EditText nameField = (EditText) findViewById(R.id.editTextHighscorename);
        String enteredName = nameField.getText().toString();
        if (enteredName.length()>0){
            writeHighScoreName(enteredName);
            layoutNameDialog.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
        }
    }
}
