package com.afeka.blocksEscape;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.animation.ObjectAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import java.lang.Math;
import android.view.animation.Animation;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import java.util.Random;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView[] playerLocation = {findViewById(R.id.player_left), findViewById(R.id.player_center), findViewById(R.id.player_right)};
        dropping(playerLocation);
//        ViewGroup player = null;

        findViewById(R.id.leftArrow).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlPlayer(-1, playerLocation);
            }
        });

        findViewById(R.id.rightArrow).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlPlayer(1, playerLocation);
            }
        });
    }

    private void controlPlayer(final int direction, final ImageView[] playerLocation) {
        // This 'handler' is created in the Main Thread, therefore it has a connection to the Main Thread.
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
                            Animation animShake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
                            frame.startAnimation(animShake);
                            TextView score = findViewById(R.id.textResults);
                            AlertDialog window = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Game Over")
                                    .setMessage("Your Score is: " + score.getText())
                                    .setView(R.layout.gameover)
                                    .setCancelable(false)
                                    .setPositiveButton("Let's Try again", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);
                                        }
                                    }).show();
                            window.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        }
                    }
                });
            }
        }).start();
    }

    private void dropping(final ImageView[] playerLocation){
        final ImageView[] bricks = {findViewById(R.id.v_left), findViewById(R.id.v_left2), findViewById(R.id.v_center), findViewById(R.id.v_center2), findViewById(R.id.v_right), findViewById(R.id.v_right2)};
        //final ImageView[] bricks = {findViewById(R.id.v_left), findViewById(R.id.v_center), findViewById(R.id.v_right)};
        for(int i=0; i<bricks.length; i++){

            blocksDropping(bricks[i], playerLocation);
        }

    }

    private void blocksDropping(final ImageView view, final ImageView[] playerLocation) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final FrameLayout frame = findViewById(R.id.frameLayout);
                        //final ImageView[] playerLocation = {findViewById(R.id.player_left), findViewById(R.id.player_center), findViewById(R.id.player_right)};
                        final RelativeLayout playerL = findViewById(R.id.playerLayout);
                        final TextView score = findViewById(R.id.textResults);
                        final ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", frame.getHeight());
                        animation.setInterpolator(new LinearInterpolator());
                        animation.setDuration(6000);
                        int delay = 1000*(int)((new Random().nextInt(15))+1);
                        animation.setStartDelay(delay);
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
//                                Random r = new Random();
//                                double delay = 1000 * (0.1 + (2 - 0.1) * r.nextDouble());
//                                int delay = 1000*(int)((new Random().nextInt(6))+1);
//                                animation.setStartDelay(delay);
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
