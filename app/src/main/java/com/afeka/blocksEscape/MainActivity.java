package com.afeka.blocksEscape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Rect;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.animation.Animation;
import android.content.Intent;

import com.facebook.stetho.Stetho;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_1 = 1;
    private static final String Columns = "columns";
    private static final String Player = "player";
    private static final String Scores = "scores";
    private static final String BonusTag = "Bonus";
    private static final String BrickTag = "Brick";
    private final int BonusIndex = 6;
    private static int lastDelay = 0;
    private final int[] bricksList = {R.drawable.brick1, R.drawable.brick2, R.drawable.brick3, R.drawable.brick4, R.drawable.brick5, R.drawable.brick6, R.drawable.brick7};
    private ImageView builderPlayer;
    private int NUM_OF_COL;
    private String finalScore;
    private String playername;
    private FusedLocationProviderClient client;
    private double lat;
    private double lng;
    ValueAnimator animation;
    FrameLayout frame;
    LinearLayout parentLinearLayout;
    ImageView[] bricks;
    Button settings;
    ValueAnimator[] animations;
    Intent intent;
    DatabaseHelper playersDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        playersDb = new DatabaseHelper(this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonShowPopupWindowClick(view);
            }
        });

        requestPermission();
        getLocation();

        Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());

        intent = getIntent();
        int columns =  Integer.parseInt(intent.getStringExtra(Columns));        /// CHECK IF NULL
        playername = intent.getStringExtra(Player);
        NUM_OF_COL = columns;
        bricks = new ImageView[columns];
        builderPlayer =  findViewById(R.id.player_center);
        if (NUM_OF_COL % 2 == 0) {
            builderPlayer.setX((getResources().getDisplayMetrics().widthPixels / (NUM_OF_COL*2)));
        }
        createColumns(columns);
        animations = new ValueAnimator[bricks.length];
        dropping(builderPlayer);
        //movePressed();
    }
    public void createColumns(int columns){
        parentLinearLayout = findViewById(R.id.dropsLayout);
        for (int i=0; i<columns; i++){
            LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.field, parentLinearLayout,  false);
            ImageView brick = rowView.findViewById(R.id.brick);
            int randomIndex = new Random().nextInt(bricksList.length);
            if(randomIndex == BonusIndex){
                brick.setTag(BonusTag);
            }
            else{
                brick.setTag(BrickTag);
            }
            brick.setImageResource(bricksList[randomIndex]);
            parentLinearLayout.addView(brick, parentLinearLayout.getChildCount() - 1);
            bricks[i] = brick;
        }
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
                        gameOver(i);
                    }
                });
            }
        }).start();
    }

     public void gameOver(int i){
         if(i == 0){
             TextView currentScore = findViewById(R.id.textResults);
//             Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
//             intent.putExtra(Scores, currentScore.getText());
             finalScore = currentScore.getText().toString();
             Intent activityChangeIntent = new Intent(MainActivity.this, GameOverActivity.class);
             activityChangeIntent.putExtra(Scores, finalScore);
             activityChangeIntent.putExtra(Player, playername);
             MainActivity.this.startActivity(activityChangeIntent);
//             startActivityForResult(intent, REQUEST_CODE_1);
//             LayoutInflater inflater = getLayoutInflater();
//             View myView = inflater.inflate(R.layout.activity_gamover, null);
//             final Button button = myView.findViewById(R.id.HomePage);
//             button.setOnClickListener(new View.OnClickListener() {
//                 public void onClick(View v) {
//                     // Perform action on click
//                     Intent activityChangeIntent = new Intent(MainActivity.this, WelcomeActivity.class);    ////FIX!!!
//                     MainActivity.this.startActivity(activityChangeIntent);
//                 }
//             });

             AddData();
//             TextView score = myView.findViewById(R.id.score);
//             score.setText(currentScore.getText().toString());
             Animation animShake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
             stopAnimations();
             frame.startAnimation(animShake);
             AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
             alertDialogBuilder.show();
             finish();
         }
     }

    //Create each brick with its own thread
    private void dropping(final ImageView playerLocation){
        for(int i=0; i<bricks.length; i++){
            if(i%2 == 0)
                blocksDropping(bricks[i], i, 0);
            else
                blocksDropping(bricks[i], i, 1);
        }
    }

    //Brick Drop animation and hit calculation
    private void blocksDropping(final ImageView view, final int i, final int delayIndex) {

                        frame = findViewById(R.id.frameLayout);
                        final TextView score = findViewById(R.id.textResults);
                        animation = ValueAnimator.ofInt(0,getResources().getDisplayMetrics().heightPixels);
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
                                    if(view.getTag().toString().equals(BonusTag)){
                                        score.setText(String.valueOf(Integer.parseInt(score.getText().toString()) + 100));
                                        view.setImageResource(bricksList[new Random().nextInt(bricksList.length)]);
                                        view.setY(0);
                                        view.setVisibility(View.INVISIBLE);
                                        valueAnimator.start();
                                    }
                                    else {
                                        checkLife(frame);
                                        view.setImageResource(bricksList[new Random().nextInt(bricksList.length)]);
                                        view.setY(0);
                                        view.setVisibility(View.INVISIBLE);
                                        valueAnimator.start();
                                    }
                                }
                                //Updating Score
                                if(view.getY() > frame.getHeight()){
                                    score.setText(String.valueOf(Integer.parseInt(score.getText().toString()) + 1));
                                    int randomIndex = new Random().nextInt(bricksList.length);
                                    if(randomIndex == BonusIndex){
                                        view.setTag(BonusTag);
                                    }
                                    else{
                                        view.setTag(BrickTag);
                                    }
                                    view.setImageResource(bricksList[randomIndex]);
                                    view.setVisibility(View.INVISIBLE);
                                    valueAnimator.start();
                                }
                            }
                        });

                    animation.setRepeatCount(ValueAnimator.INFINITE);
                    animation.start();
                    animations[i] = animation;

    }

    public void clickToMoveRight(View view) {
        if (builderPlayer.getX() + getResources().getDisplayMetrics().widthPixels / NUM_OF_COL < (getResources().getDisplayMetrics().widthPixels ))
            builderPlayer.setX(builderPlayer.getX() + getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
    }

    public void clickToMoveLeft(View view) {
        if (builderPlayer.getX() >= (getResources().getDisplayMetrics().widthPixels * 1 / NUM_OF_COL))
            builderPlayer.setX(builderPlayer.getX() - getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
    }

    public void stopAnimations(){
        for (int i = 0; i < animations.length; i++){
            animations[i].pause();
        }
    }

    public void resumeAnimations(){
        for (int i = 0; i < animations.length; i++){
            animations[i].resume();
        }
    }
    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.settings, null);

        // create the popup window
        int width = (int)(getResources().getDisplayMetrics().widthPixels/1.5);
        int height = (int)(getResources().getDisplayMetrics().heightPixels/3);
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        stopAnimations();
        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //popupWindow.dismiss();
                //resumeAnimations();
                return true;
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                resumeAnimations();
            }
        });
    }


    public  void AddData() {
        playersDb.insertData(playername,
                finalScore,
                String.valueOf(lat), String.valueOf(lng));
    }

    public void getLocation(){
        client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
            }
        });
    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[] {ACCESS_FINE_LOCATION}, 1);
    }
}