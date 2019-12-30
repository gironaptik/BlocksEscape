package com.afeka.blocksEscape;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.drawable.ColorDrawable;


public class GameOverActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USERNAME = "username";
    private final String Scores = "scores";
    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamover);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Intent intent = getIntent();
        String score = intent.getStringExtra(Scores);
        TextView scoreView = findViewById(R.id.score);
        scoreView.setText(score);
        findViewById(R.id.HomePage).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityChangeIntent = new Intent(GameOverActivity.this, WelcomeActivity.class);
                GameOverActivity.this.startActivity(activityChangeIntent);
            }
        });

        findViewById(R.id.resetGame).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activityChangeIntent = new Intent(GameOverActivity.this, MainActivity.class);
                GameOverActivity.this.startActivity(activityChangeIntent);
            }
        });
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        playerName = sharedPreferences.getString(USERNAME, "");
    }
}

