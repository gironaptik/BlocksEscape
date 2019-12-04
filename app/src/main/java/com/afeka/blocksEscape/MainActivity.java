package com.afeka.blocksEscape;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.animation.ObjectAnimator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import java.lang.Math;
import android.view.animation.Animation;
import android.content.Intent;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_1 = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static int lastDelay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView[] playerLocation = {findViewById(R.id.player_left), findViewById(R.id.player_center), findViewById(R.id.player_right)};
        dropping(playerLocation);
        movePressed(playerLocation);
    }

    //Move buttons (4 buttons)
    private void movePressed(final ImageView[] playerLocation){

        findViewById(R.id.buttonLeft).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlPlayer(-1, playerLocation);
            }
        });

        findViewById(R.id.leftArrow).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlPlayer(-1, playerLocation);
            }
        });

        findViewById(R.id.buttonRight).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlPlayer(1, playerLocation);
            }
        });

        findViewById(R.id.rightArrow).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlPlayer(1, playerLocation);
            }
        });


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
                        if(i == 0){

                            TextView currentScore = findViewById(R.id.textResults);
                            Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                            intent.putExtra("score", currentScore.getText());
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
    private void dropping(final ImageView[] playerLocation){
        final ImageView[] bricks = {findViewById(R.id.v_left), findViewById(R.id.v_center), findViewById(R.id.v_right), findViewById(R.id.v_left2), findViewById(R.id.v_center2), findViewById(R.id.v_right2)};
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
    private void blocksDropping(final ImageView view, final ImageView[] playerLocation, final int delayIndex) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final FrameLayout frame = findViewById(R.id.frameLayout);
                        final RelativeLayout playerL = findViewById(R.id.playerLayout);
                        final TextView score = findViewById(R.id.textResults);
                        final ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", frame.getHeight());
                        animation.setInterpolator(new LinearInterpolator());
                        animation.setDuration(7000);
                        if (delayIndex == 0) {
                            int delay = 1000 * ((new Random().nextInt(6)) + 1);
                            lastDelay = delay;
                            animation.setStartDelay(delay);
                        }
                        else {
                            int delay = 1000 * (new Random().nextInt((8 - 5) + 1) + 5);
                            lastDelay = delay;
                            animation.setStartDelay(delay);
                        }
                        animation.addUpdateListener(new AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                float imageYPosition = (Float)animation.getAnimatedValue();
                                if(Math.abs(imageYPosition - playerL.getTop()) < 3) {
                                    for (int i = 0; i < playerLocation.length; i++)
                                        if (playerLocation[i].getVisibility() == View.VISIBLE && Math.abs(view.getX() - playerLocation[i].getX()) < 20) {
                                            view.setVisibility(View.INVISIBLE);
                                            checkLife(frame);
                                        }
                                }
                                //}

                                //Updating Score
                                if(imageYPosition == frame.getHeight()){
                                    score.setText(String.valueOf(Integer.parseInt(score.getText().toString()) + 1));
                                }
                            }
                        });

                        animation.addListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {
                                view.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (delayIndex == 1) {
                                    int delay = 1000 * (int) (new Random().nextInt((15 - 1) + 1) + 1);
                                    while(lastDelay == delay)
                                        delay = 1000 * (int) (new Random().nextInt((15 - 1) + 1) + 1);
                                    animation.setStartDelay(1000+ delay);
                                    lastDelay = delay;
                                }
                                else {
                                    int delay = 1000 * (int) (new Random().nextInt((15 - 1) + 1) + 1);
                                    while(lastDelay == delay)
                                        delay = 1000 * (int) (new Random().nextInt((15 - 1) + 1) + 1);
                                    animation.setStartDelay(1000 + delay);
                                    lastDelay = delay;
                                }
                                animation.start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                        animation.start();
                    }
                }, 100);
            }
        }).start();
    }

}
