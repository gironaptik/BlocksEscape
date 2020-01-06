package com.afeka.blocksEscape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.graphics.Rect;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Intent;
import android.widget.Toast;
import com.facebook.stetho.Stetho;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final String Columns = "columns";
    private static final String Player = "player";
    private static final String Scores = "scores";
    private static final String BonusTag = "Bonus";
    private static final String BrickTag = "Brick";
    private static final String Lat = "lat";
    private static final String Lng = "lng";
    private static final int BonusIndex = 6;
    private int animationSpeed = 2000;
    private int locationRequestCode = 1000;
    private static int lastDelay = 0;
    private final int[] bricksList = {R.drawable.brick1, R.drawable.brick2, R.drawable.brick3, R.drawable.brick4, R.drawable.brick5, R.drawable.brick6, R.drawable.brick7};
    private ImageView builderPlayer;
    private int NUM_OF_COL;
    private String finalScore;
    private String playername;
    private FusedLocationProviderClient client;
    private double lat;
    private double lng;
    private Switch sensorSwitch;
    private Switch soundSwitch;
    private boolean sensorValue = true;
    private boolean soundValue = true;
    private boolean popUpStatus = false;
    private View rowView;
    private Gyroscope gyroscope;
    private ValueAnimator animation;
    private FrameLayout frame;
    private LinearLayout parentLinearLayout;
    private ImageView[] bricks;
    private Button settings;
    private ValueAnimator[] animations;
    private Intent intent;
    private DatabaseHelper playersDb;
    private MediaPlayer loginSong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestPermissionn();
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = LocationServices.getFusedLocationProviderClient(this);
        playersDb = new DatabaseHelper(this);
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonShowPopupWindowClick(view);
            }
        });
        gyroscope = new Gyroscope(this);
        loginSong = MediaPlayer.create(getApplicationContext(), R.raw.soundtrack);
        loginSong.setLooping(true);
        loginSong.start();
        motionListener();
        getLocation();
        //Debugging DB
        Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());
        intent = getIntent();
        int columns = Integer.parseInt(intent.getStringExtra(Columns));        /// CHECK IF NULL
        playername = intent.getStringExtra(Player);
        NUM_OF_COL = columns;
        bricks = new ImageView[columns];
        builderPlayer = findViewById(R.id.player_center);
        if (NUM_OF_COL % 2 == 0) {
            builderPlayer.setX((getResources().getDisplayMetrics().widthPixels / (NUM_OF_COL * 2)));
        }
        createColumns(columns);
        animations = new ValueAnimator[bricks.length];
        dropping(builderPlayer);
    }


    private void motionListener() {
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {      // Rotate left
                if (rz > 2.0f && (rx < 2.0f || rx < -2.0f)) {
                    clickToMoveLeft(builderPlayer);

                } else if (rz < -2.0f && (rx < 2.0f || rx < -2.0f)) {        //Rotate right
                    clickToMoveRight(builderPlayer);
                }
            }
        });
    }

    public void createColumns(int columns) {
        parentLinearLayout = findViewById(R.id.dropsLayout);
        for (int i = 0; i < columns; i++) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.field, parentLinearLayout, false);
            ImageView brick = rowView.findViewById(R.id.brick);
            tagBrick(brick);
            parentLinearLayout.addView(brick, parentLinearLayout.getChildCount() - 1);
            bricks[i] = brick;
        }
    }

    public void tagBrick(ImageView brick) {
        int randomIndex = new Random().nextInt(bricksList.length);
        if (randomIndex == BonusIndex) {
            brick.setTag(BonusTag);
        } else {
            brick.setTag(BrickTag);
        }
        brick.setImageResource(bricksList[randomIndex]);
    }

    private boolean hit(View e, View p) {
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
        final ImageView[] lives = {findViewById(R.id.live1), findViewById(R.id.live2), findViewById(R.id.live3)};

        int i;
        for (i = lives.length - 1; i >= 0; i--) {
            if ((lives[i].getVisibility() == View.VISIBLE)) {
                lives[i].setVisibility(View.INVISIBLE);
                break;
            }
        }
        //GameOver
        gameOver(i);

    }

    public void gameOver(int i) {
        if (i == 0) {
            TextView currentScore = findViewById(R.id.textResults);
            finalScore = currentScore.getText().toString();
            Intent activityChangeIntent = new Intent(this, GameOverActivity.class);
            activityChangeIntent.putExtra(Scores, finalScore);
            activityChangeIntent.putExtra(Player, playername);
            activityChangeIntent.putExtra(Lat, String.valueOf(lat));
            activityChangeIntent.putExtra(Lng, String.valueOf(lng));
            startActivity(activityChangeIntent);
            AddData();
            finish();
            return;
        }
    }

    //Create each brick with its own thread
    private void dropping(final ImageView playerLocation) {
        for (int i = 0; i < bricks.length; i++) {
            if (i % 2 == 0) {
                blocksDropping(bricks[i], i, 0);
            } else {
                blocksDropping(bricks[i], i, 1);
            }
        }
    }

    //Brick Drop animation and hit calculation
    private void blocksDropping(final ImageView view, final int i, final int delayIndex) {

        frame = findViewById(R.id.frameLayout);
        final TextView score = findViewById(R.id.textResults);
        animation = ValueAnimator.ofInt(0, getResources().getDisplayMetrics().heightPixels);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(animationSpeed);
        if (delayIndex == 0) {
            int delay = 1000 * ((new Random().nextInt(6)) + 1);
            lastDelay = delay;
            animation.setStartDelay(delay);
        } else {
            int delay = 1000 * (new Random().nextInt((15 - 7) + 1) + 7);
            lastDelay = delay;
            animation.setStartDelay(delay);
        }
        animation.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setVisibility(View.VISIBLE);
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                view.setTranslationY(animatedValue);
                if (hit(view, builderPlayer)) {
                    if (view.getTag().toString().equals(BonusTag)) {
                        score.setText(String.valueOf(Integer.parseInt(score.getText().toString()) + 100));
                        tagBrick(view);
                        view.setY(0);
                        view.setVisibility(View.INVISIBLE);
                        valueAnimator.start();
                    } else {
                        checkLife(frame);
                        tagBrick(view);
                        view.setY(0);
                        view.setVisibility(View.INVISIBLE);
                        valueAnimator.start();
                    }
                }
                //Updating Score
                if (view.getY() > frame.getHeight()) {
                    score.setText(String.valueOf(Integer.parseInt(score.getText().toString()) + 1));
                    tagBrick(view);
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
        if (builderPlayer.getX() + getResources().getDisplayMetrics().widthPixels / NUM_OF_COL < (getResources().getDisplayMetrics().widthPixels))
            builderPlayer.setX(builderPlayer.getX() + getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
    }

    public void clickToMoveLeft(View view) {
        if (builderPlayer.getX() >= (getResources().getDisplayMetrics().widthPixels * 1 / NUM_OF_COL))
            builderPlayer.setX(builderPlayer.getX() - getResources().getDisplayMetrics().widthPixels / NUM_OF_COL);
    }

    public void stopAnimations() {
        for (int i = 0; i < animations.length; i++) {
            animations[i].pause();
        }
    }

    public void resumeAnimations() {
        for (int i = 0; i < animations.length; i++) {
            animations[i].resume();
        }
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.settings, parentLinearLayout, false);
        int width = (int) (getResources().getDisplayMetrics().widthPixels / 1.5);
        int height = (int) (getResources().getDisplayMetrics().heightPixels / 3);
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        stopAnimations();
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popUpStatus = true;
                return true;
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popUpStatus = false;
                resumeAnimations();
            }
        });
        sensorSwitch = popupView.findViewById(R.id.sensorSwitch);
        soundSwitch = popupView.findViewById(R.id.soundSwitch);
        sensorSwitchChanged();
        soundSwitchChanged();

    }

    public void AddData() {
        playersDb.insertData(playername,
                finalScore,
                String.valueOf(lat), String.valueOf(lng));
    }

    public void getLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                }
            });
        }
    }

    public void requestPermissionn() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
        }
    }


    @Override
    public void onBackPressed() {
        stopAnimations();
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
        overridePendingTransition(0, 0);
        return;
    }


    public void sensorSwitchChanged() {
        sensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    sensorValue = false;
                    gyroscope.unregister();
                    buttonView.setChecked(false);
                } else {
                    sensorValue = true;
                    gyroscope.register();
                    buttonView.setChecked(true);
                }
            }

        });
        sensorSwitch.setChecked(sensorValue);
    }

    public void soundSwitchChanged() {
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    soundValue = false;
                    loginSong.pause();
                    buttonView.setChecked(false);
                } else {
                    soundValue = true;
                    loginSong.start();
                    buttonView.setChecked(true);
                }
            }

        });
        soundSwitch.setChecked(soundValue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroscope.register();
        if (!soundValue) {
            loginSong.pause();
        }
        else{
            loginSong.start();
        }
        if(popUpStatus) {
            resumeAnimations();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        gyroscope.unregister();
        loginSong.pause();
        stopAnimations();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                lat = location.getLatitude();
                                lng = location.getLongitude();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
