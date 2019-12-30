package com.afeka.blocksEscape;

public class Player {

    private String name;
    private int score;
    private float lng;
    private float lat;

    public Player(String name,int score, float lng, float lat){
        this.name = name;
        this.score = score;
        this.lng = lng;
        this.lat = lat;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score){
        this.score = score;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public int getScore(){
        return score;
    }
    public float getLng() {
        return lng;
    }

    public float getLat() {
        return lat;
    }
}
