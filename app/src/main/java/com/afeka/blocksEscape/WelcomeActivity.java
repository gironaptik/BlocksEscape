package com.afeka.blocksEscape;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String Columns = "columns";
    private static final String Player = "player";
    private static final String On = "on";
    private static final String Off = "off";
    private EditText nameOfPlayer;
    private String playerName;
    private MediaPlayer loginSong;
    private String columnName = "3";
    private ImageView soundController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        soundController = findViewById(R.id.soundIcon);
        loginSong = MediaPlayer.create(getApplicationContext(), R.raw.soundtrack);
        loginSong.setLooping(true);
        loginSong.start();
        nameOfPlayer = findViewById(R.id.usernameEdit);
        Spinner spinner = findViewById(R.id.columnSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.columns_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        nameOfPlayer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        nameOfPlayer.addTextChangedListener(watcher);
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            playerName = nameOfPlayer.getText().toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        columnName = parent.getItemAtPosition(position).toString();

        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void clickToPlay(View view) {
        Intent activityChangeIntent = new Intent(WelcomeActivity.this, MainActivity.class);
        activityChangeIntent.putExtra(Columns, columnName);
        activityChangeIntent.putExtra(Player, playerName);
        loginSong.pause();
        WelcomeActivity.this.startActivity(activityChangeIntent);
    }

    public void soundControl(View view){
        String name = soundController.getTag().toString();
        if(soundController.getTag().toString().equals(On)){
            soundController.setImageResource(R.drawable.unmute);
            soundController.setTag(Off);
            loginSong.pause();
        }
        else{
            soundController.setImageResource(R.drawable.mute);
            soundController.setTag(On);
            loginSong.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        loginSong.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginSong.start();
    }
}
