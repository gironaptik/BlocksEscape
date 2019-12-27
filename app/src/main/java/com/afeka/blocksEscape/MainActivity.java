package com.afeka.blocksEscape;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.graphics.Rect;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.animation.Animation;
import android.content.Intent;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_1 = 1;
    LinearLayout parentLinearLayout;
    ImageView[] bricks;
    private final String Columns = "columns";
    private final String Scores = "scores";
    private static int lastDelay = 0;
    private final int[] bricksList = {R.drawable.brick1, R.drawable.brick2, R.drawable.brick3, R.drawable.brick4, R.drawable.brick5, R.drawable.brick6};
    private ImageView builderPlayer;
    private int NUM_OF_COL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int columns =  Integer.parseInt(intent.getStringExtra(Columns));
        NUM_OF_COL = columns;
        bricks = new ImageView[columns];
        setContentView(R.layout.activity_main);
        builderPlayer =  findViewById(R.id.player_center);
        if (NUM_OF_COL % 2 == 0) {
            builderPlayer.setX((getResources().getDisplayMetrics().widthPixels / (NUM_OF_COL*2)));
        }
        createColumns(columns);
        dropping(builderPlayer);
        //movePressed();
    }

    public void createColumns(int columns){
        parentLinearLayout = findViewById(R.id.dropsLayout);
        for (int i=0; i<columns; i++){
            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.field, parentLinearLayout,  false);
            ImageView brick = rowView.findViewById(R.id.brick);
            brick.setImageResource(bricksList[new Random().nextInt(bricksList.length)]);
            parentLinearLayout.addView(brick, parentLinearLayout.getChildCount() - 1);
            bricks[i] = brick;
        }
    }

    //Control player location
    private void controlPlayer(final int direction, final ImageView[] playerLocation) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0; i<playerLocation.length; i++){
                            if( (i+direction >= 0  && i+direction <= 2) && playerLocation[i].getVisibility() == View.VISIBLE){
                                playerLocation[i].setVisibility(View.INVISIBLE);
                                playerLocation[i+direction].setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private boolean hit(View e,View p) {
        int[] enemy_locate = new int[2];
        int[] player_locate = new int[2];
        // Computes the coordinates of this view on the screen
        e.getLocationOnScreen(enemy_locate);
        p.getLocationOnScreen(player_locate);
        Rect rect1 = new Rect(enemy_locate[0], enemy_locate[1], (int) (enemy_locate[0] + e.getWidth()), (int) (enemy_locate[1] + e.getHeight()));
        Rect rect2 = new Rect(player_locate[0], player_locate[1], (int) (player_locate[0] + p.getWidth()), (int) (player_locate[1] + p.getHeight()));
        return Rect.intersects(rect1, rect2);
    }

    //Check life and game over
    private void checkLife(final FrameLayout frame) {
        // This 'handler' is created in the Main Thread, therefore it has a connection to the Main Thread.
        final ImageView[] lives = {findViewById(R.id.live1), findViewById(R.id.live2), findViewById(R.id.live3)};
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int i;
                        for(i=lives.length-1; i>=0; i--){
                            if( (lives[i].getVisibility() == View.VISIBLE)){
                                lives[i].setVisibility(View.INVISIBLE);
                                break;
                            }
                        }
                        //GameOver
                        if(i == 0){
                            TextView currentScore = findViewById(R.id.textResults);
                            Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                            intent.putExtra(Scores, currentScore.getText());
                            startActivityForResult(intent, REQUEST_CODE_1);

                            LayoutInflater inflater = getLayoutInflater();
                            View myView = inflater.inflate(R.layout.activity_gamover, null);
                            final Button button = myView.findViewById(R.id.HomePage);
                            button.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    // Perform action on click
                                    Intent activityChangeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                                    MainActivity.this.startActivity(activityChangeIntent);
                                }
                            });

                            TextView score = myView.findViewById(R.id.score);
                            score.setText(currentScore.getText().toString());
                            Animation animShake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
                            frame.startAnimation(animShake);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                            alertDialogBuilder.show();
                        }
                    }
                });
            }
        }).start();
    }

    //Create each brick with its own thread
    private void dropping(final ImageView playerLocation){
        //final ImageView[] bricks = {findViewById(R.id.v_left), findViewById(R.id.v_center), findViewById(R.id.v_right)};
        //If we want only 3 items
        //final ImageView[] bricks = {findViewById(R.id.v_left), findViewById(R.id.v_center), findViewById(R.id.v_right)};
        for(int i=0; i<bricks.length; i++){
            if(i%2 == 0)
                blocksDropping(bricks[i], playerLocation, 0);
            else
                blocksDropping(bricks[i], playerLocation, 1);
        }
    }

    //Brick Drop animation and hit calculation
    private void blocksDropping(final ImageView view, final ImageView playerLocation, final int delayIndex) {

                        final FrameLayout frame = findViewById(R.id.frameLayout);
                        final TextView score = findViewById(R.id.textResults);
                        final ValueAnimator animation = ValueAnimator.ofInt(0,getResources().getDisplayMetrics().heightPixels);
                        animation.setInterpolator(new LinearInterpolator());
                        animation.setDuration(2000);
                        if (delayIndex == 0) {
                            int delay = 1000 * ((new Random().nextInt(6)) + 1);
                            lastDelay = delay;
                            animation.setStartDelay(delay);
                        }
                        else {
                            int delay = 1000 * (new Random().nextInt((14 - 5) + 1) + 5);
                            lastDelay = delay;
                            animation.setStartDelay(delay);
                        }
                        animation.addUpdateListener(new AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                view.setVisibility(View.VISIBLE);
                                int animatedValue = (int)valueAnimator.getAnimatedValue();
                                view.setTranslationY(animatedValue);
                                if(hit(view, builderPlayer)){
                                    checkLife(frame);
                                    view.setImageResource(bricksList[new Random().nextInt(bricksList.length)]);
                                    view.setY(0);
                                    view.setVisibility(View.INVISIBLE);
                                    valueAnimator.start();
                                }
                                //Updating Score
                                if(view.getY() > frame.getHeight()){
                                    score.setText(String.valueOf(Integer.parseInt(score.getText().toString()) + 1));
                                    view.setImageResource(bricksList[new Random().nextInt(bricksList.length)]);
                                    view.setVisibility(View.INVISIBLE);
                                    valueAnimator.start();
                                }
                            }
                        });

//                        animation.addListener(new Animator.AnimatorListener() {
//
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//                                view.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                view.setVisibility(View.INVISIBLE);
//                                if (delayIndex == 1) {
//                                    int delay = 1000 * (new Random().nextInt((15 - 1) + 1) + 1);
//                                    while(lastDelay == delay)
//                                        delay = 1000 * (new Random().nextInt((15 - 1) + 1) + 1);
//                                    animation.setStartDelay(1000+ delay);
//                                    lastDelay = delay;
//                                }
//                                else {
//                                    int delay = 1000 * (new Random().nextInt((15 - 1) + 1) + 1);
//                                    while(lastDelay == delay)
//                                        delay = 1000 * (new Random().nextInt((15 - 1) + 1) + 1);
//                                    animation.setStartDelay(1000 + delay);
//                                    lastDelay = delay;
//                                }
//                                animation.start();
//                                view.setVisibility(View.VISIBLE);
//
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//                                view.setVisibility(View.VISIBLE);
//                            }
//                        });
                    animation.setRepeatCount(ValueAnimator.INFINITE);
                    animation.start();

    }

    public void clickToMoveRight(View view) {
        if (builderPlayer.getX() + getResources().getDisplayMetrics().widthPixels / NUM_OF_COL < (getResources().getDisplayMetrics().widthPixels ))
            builderPlayer.setX(builderPlayer.getX() + getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
    }

    public void clickToMoveLeft(View view) {
        if (builderPlayer.getX() >= (getResources().getDisplayMetrics().widthPixels * 1 / NUM_OF_COL))
            builderPlayer.setX(builderPlayer.getX() - getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
    }

}