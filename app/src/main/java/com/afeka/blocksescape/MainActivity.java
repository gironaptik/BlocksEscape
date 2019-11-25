package com.afeka.blocksescape;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.animation.ObjectAnimator;
import android.widget.FrameLayout;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        findViewById(R.id.v_left).setOnClickListener(this);
        findViewById(R.id.v_middle).setOnClickListener(this);
        findViewById(R.id.v_right).setOnClickListener(this);

    }

    @Override
    public void onClick(final View view) {
        FrameLayout frame = findViewById(R.id.frameLayout);
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", frame.getHeight());
        animation.setDuration(4000);
        animation.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                    Random r = new Random();
                    double delay = 1000*(0.1 + (2 - 0.1) * r.nextDouble());
                    animation.setStartDelay((int)delay);
                    animation.start();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
        animation.start();

    }
}
