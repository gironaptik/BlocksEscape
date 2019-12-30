package com.afeka.blocksEscape;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String Columns = "columns";
    private static final String Player = "player";
    private EditText nameOfPlayer;
    private String playername;
    String columnName = "3";
    //Player player;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        nameOfPlayer = findViewById(R.id.usernameField);
        Gson gson = new Gson();
//        player = new Player(" ",0,0, 0);
//        String json = gson.toJson(player);
//        Player exportPlayer = gson.fromJson(json, Player.class);

//        final Button button = findViewById(R.id.playButton);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent activityChangeIntent = new Intent(WelcomeActivity.this, MainActivity.class);
//                activityChangeIntent.putExtra(Columns, columnName);
//                activityChangeIntent.putExtra(Player, new Gson().toJson(player));
//                WelcomeActivity.this.startActivity(activityChangeIntent);
//            }
//        });

        //bundle = new Bundle();
        Spinner spinner = findViewById(R.id.columnSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.columns_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        nameOfPlayer.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                nameOfPlayer.setHint("");
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
            playername = nameOfPlayer.getText().toString().trim();
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

    public void clickToPlay(View view){
        Intent activityChangeIntent = new Intent(WelcomeActivity.this, MainActivity.class);
        activityChangeIntent.putExtra(Columns, columnName);
        activityChangeIntent.putExtra(Player, playername);
        WelcomeActivity.this.startActivity(activityChangeIntent);
    }
}
