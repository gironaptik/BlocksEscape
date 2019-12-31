package com.afeka.blocksEscape;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import java.util.Collections;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;


public class ScoresFragment extends Fragment {

    private final static int top = 5;
    private final String Scores = "scores";
    private static final String Player = "player";
    private String finalScore;
    private String playerName;
    private ArrayList<Player> playerList;
    TextView currentPlayerScoreValue;
    TextView currentPlayerPlayed;
    Intent intent;
    TableLayout scoreTable;
    DatabaseHelper playersDb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        intent = getActivity().getIntent();
        finalScore = intent.getStringExtra(Scores);
        playerName = intent.getStringExtra(Player);
        playersDb = new DatabaseHelper(getActivity());
        //viewAll();
        View view = inflater.inflate(R.layout.fragment_scores, container, false);
        scoreTable = view.findViewById(R.id.scoresTable);
        currentPlayerScoreValue = view.findViewById(R.id.currentPlayerScoreValue);
        currentPlayerPlayed = view.findViewById(R.id.currentPlayerName);
        currentPlayerScoreValue.setText(finalScore);
        currentPlayerPlayed.setText(playerName);
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


        public void createTable(){
            final TableRow firsTableRow = new TableRow(getActivity());
            firsTableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            final TextView nameTitle = new TextView(getActivity());
            nameTitle.setText("Name");
            nameTitle.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            final TextView scoreTitle = new TextView(getActivity());
            scoreTitle.setText("Score");
            scoreTitle.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            final TextView latTitle = new TextView(getActivity());
            latTitle.setText("Lat");
            latTitle.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            final TextView lngTitle = new TextView(getActivity());
            lngTitle.setText("Lng");
            lngTitle.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            firsTableRow.addView(nameTitle);
            firsTableRow.addView(scoreTitle);
            firsTableRow.addView(latTitle);
            firsTableRow.addView(lngTitle);
            scoreTable.addView(firsTableRow);

            int tableSize = top > playerList.size() ? playerList.size() : top;
            for(int i=0; i<tableSize; i++){
                final TableRow tableRow = new TableRow(getActivity());
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                final TextView name = new TextView(getActivity());
                name.setText(playerList.get(i).getName());
                name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                final TextView score = new TextView(getActivity());
                score.setText(String.valueOf(playerList.get(i).getScore()));
                score.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                final TextView lat = new TextView(getActivity());
                lat.setText(String.valueOf(playerList.get(i).getLat()));
                lat.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                final TextView lng = new TextView(getActivity());
                lng.setText(String.valueOf(playerList.get(i).getLng()));
                lng.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                tableRow.addView(name);
                tableRow.addView(score);
                tableRow.addView(lat);
                tableRow.addView(lng);

                scoreTable.addView(tableRow);

            }
        }

}
