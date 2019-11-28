package com.afeka.blocksescape;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.animation.ObjectAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blocksDropping((ImageView)findViewById(R.id.v_left));
        blocksDropping((ImageView)findViewById(R.id.v_middle));
        blocksDropping((ImageView)findViewById(R.id.v_right));
        findViewById(R.id.leftArrow).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlPlayer(-1);
            }
        });

        findViewById(R.id.rightArrow).setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlPlayer(1);
            }
        });
    }

    private void controlPlayer(final int direction) {
        // This 'handler' is created in the Main Thread, therefore it has a connection to the Main Thread.
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                    final ImageView[] playerLocation = {findViewById(R.id.v_playerLeft), findViewById(R.id.v_playerCenter), findViewById(R.id.v_playerRight)};
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0; i<=2; i++){
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

//    @Override
//    public void onClick(final View view) {
//        FrameLayout frame = findViewById(R.id.frameLayout);
//        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", frame.getHeight());
//        animation.setDuration(4000);
//        animation.addListener(new Animator.AnimatorListener() {
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                view.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                Random r = new Random();
//                double delay = 1000 * (0.1 + (2 - 0.1) * r.nextDouble());
//                animation.setStartDelay((int) delay);
//                animation.start();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//        animation.start();
//
//    }

    private void blocksDropping(final ImageView view) {
        // This 'handler' is created in the Main Thread, therefore it has a connection to the Main Thread.
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FrameLayout frame = findViewById(R.id.frameLayout);
                        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", frame.getHeight());
                        animation.setDuration(4000);
                        Random r = new Random();
                        double delay = 1000 * (0.1 + (3 - 0.5) * r.nextDouble());
                        animation.setStartDelay((int)delay);
                        animation.addListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {
                                view.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                Random r = new Random();
                                double delay = 1000 * (0.1 + (2 - 0.1) * r.nextDouble());
                                animation.setStartDelay((int) delay);
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




