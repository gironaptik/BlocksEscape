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
    FragmentManager scoresFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamover);
//        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Intent intent = getIntent();
        String score = intent.getStringExtra(Scores);
        Intent scoreIntent = new Intent(GameOverActivity.this, ScoresFragment.class);
        scoreIntent.putExtra(Scores, score);
        //scoreIntent.putExtra(Player, playername);
//        TextView scoreView = findViewById(R.id.score);
//        scoreView.setText(score);
//        findViewById(R.id.HomePage).setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent parentActivityIntent = new Intent(GameOverActivity.this, WelcomeActivity.class);
//                parentActivityIntent.addFlags(
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                                Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(parentActivityIntent);
//                finish();
//                //Intent activityChangeIntent = new Intent(GameOverActivity.this, WelcomeActivity.class);
//                //GameOverActivity.this.startActivity(activityChangeIntent);
//            }
//        });
//
//        findViewById(R.id.resetGame).setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent activityChangeIntent = new Intent(GameOverActivity.this, MainActivity.class);
//                GameOverActivity.this.startActivity(activityChangeIntent);
//            }
//        });
    }

}

