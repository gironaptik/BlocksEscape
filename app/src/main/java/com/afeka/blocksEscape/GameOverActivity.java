package com.afeka.blocksEscape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.drawable.ColorDrawable;


public class GameOverActivity extends AppCompatActivity {

    private final String Scores = "scores";
    private static final String Lat = "lat";
    private static final String Lng = "lng";
    FragmentManager scoresFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamover);
        Intent intent = getIntent();
        String score = intent.getStringExtra(Scores);
        String lat = intent.getStringExtra(Lat);
        String lng = intent.getStringExtra(Lng);
        Intent scoreIntent = new Intent(GameOverActivity.this, ScoresFragment.class);
        scoreIntent.putExtra(Scores, score);
        scoreIntent.putExtra(Lat, lat);
        scoreIntent.putExtra(Lng, lng);

    }

}

