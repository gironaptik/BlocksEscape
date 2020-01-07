package com.afeka.blocksEscape;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import java.util.Collections;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import java.util.ArrayList;
import java.util.Comparator;
import android.widget.Button;


public class ScoresFragment extends Fragment {

    private static final int top = 5;
    private static final String Scores = "scores";
    private static final String Player = "player";
    private static final String Lat = "lat";
    private static final String Lng = "lng";
    private String finalScore;
    private String playerName;
    private ArrayList<Player> playerList;
    private LocationFragment locationFragment;
    private Button newGame;
    private String lat;
    private String lng;
    private GoogleMap googleMap;
    private TextView currentPlayerScoreValue;
    private TextView currentPlayerPlayed;
    private Intent intent;
    private TableLayout scoreTable;
    private DatabaseHelper playersDb;
    Player currentPlayer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        checkPermission();
        intent = getActivity().getIntent();
        finalScore = intent.getStringExtra(Scores);
        playerName = intent.getStringExtra(Player);
        lat = intent.getStringExtra(Lat);
        lng = intent.getStringExtra(Lng);
        playersDb = new DatabaseHelper(getActivity());
        locationFragment = new LocationFragment();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.map, locationFragment).commit();
        View view = inflater.inflate(R.layout.fragment_scores, container, false);
        scoreTable = view.findViewById(R.id.scoresTable);
        currentPlayerPlayed(view);
        newGame = view.findViewById(R.id.resetGame);
        newGame.setOnClickListener(mButtonClickListener);
        viewAll();
        createTable();
        return view;
    }

    public void viewAll() {
        playerList = new ArrayList<>();
        Cursor res = playersDb.getAllData();
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            Player player = new Player(Integer.parseInt(res.getString(0)), res.getString(1), Integer.parseInt(res.getString(2)), Float.valueOf(res.getString(3)), Float.valueOf(res.getString(4)));
            playerList.add(player);
        }
        Collections.sort(playerList, new Comparator<Player>() {
            @Override
            public int compare(Player z1, Player z2) {
                if (z1.getScore() > z2.getScore())
                    return 1;
                if (z1.getScore() < z2.getScore())
                    return -1;
                return 0;
            }
        });
        Collections.reverse(playerList);
    }

    public void createTable() {
        TableRow.LayoutParams lp =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 20, 20, 20);
        final TableRow firsTableRow = new TableRow(getActivity());
        firsTableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        final TextView nameTitle = new TextView(getActivity());
        nameTitle.setTypeface(null, Typeface.BOLD);
        nameTitle.setText("Name");
        nameTitle.setLayoutParams(lp);
        final TextView scoreTitle = new TextView(getActivity());
        scoreTitle.setTypeface(null, Typeface.BOLD);
        scoreTitle.setText("Score");
        scoreTitle.setLayoutParams(lp);
        final TextView latTitle = new TextView(getActivity());
        latTitle.setTypeface(null, Typeface.BOLD);
        latTitle.setText("Lat");
        latTitle.setLayoutParams(lp);
        final TextView lngTitle = new TextView(getActivity());
        lngTitle.setTypeface(null, Typeface.BOLD);
        lngTitle.setText("Lng");
        lngTitle.setLayoutParams(lp);

        firsTableRow.addView(nameTitle);
        firsTableRow.addView(scoreTitle);
        firsTableRow.addView(latTitle);
        firsTableRow.addView(lngTitle);
        scoreTable.addView(firsTableRow);

        int tableSize = top > playerList.size() ? playerList.size() : top;
        for (int i = 0; i < tableSize; i++) {
            currentPlayer = playerList.get(i);
            final TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            final TextView name = new TextView(getActivity());
            name.setLayoutParams(lp);
            name.setTypeface(null, Typeface.BOLD);
            name.setText(playerList.get(i).getName());

            final TextView score = new TextView(getActivity());
            score.setLayoutParams(lp);
            score.setText(String.valueOf(playerList.get(i).getScore()));
            final TextView lat = new TextView(getActivity());
            lat.setLayoutParams(lp);
            lat.setText(String.valueOf(playerList.get(i).getLng()));
            final TextView lng = new TextView(getActivity());
            lng.setText(String.valueOf(playerList.get(i).getLat()));
            lng.setLayoutParams(lp);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    placeMarkerInMap(name.getText() + " Location", Float.parseFloat(lng.getText().toString()), Float.parseFloat(lat.getText().toString()));
                }
            });
            tableRow.addView(name);
            tableRow.addView(score);
            tableRow.addView(lat);
            tableRow.addView(lng);

            scoreTable.addView(tableRow);

        }

    }

    private void placeMarkerInMap(String title, float lat, float lng) {
        if (locationFragment != null) {
            locationFragment.placeMarker(title, lng, lat);
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }


    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), WelcomeActivity.class));
            getActivity().finish();
        }
    };

    public void currentPlayerPlayed(View view) {
        currentPlayerScoreValue = view.findViewById(R.id.currentPlayerScoreValue);
        currentPlayerPlayed = view.findViewById(R.id.currentPlayerName);
        currentPlayerPlayed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeMarkerInMap(playerName + " Location", Float.valueOf(lng), Float.valueOf(lat));
            }
        });
        currentPlayerScoreValue.setText(finalScore);
        currentPlayerPlayed.setText(playerName);
    }

}