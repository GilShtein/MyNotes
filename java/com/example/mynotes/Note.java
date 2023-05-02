package com.example.mynotes;


import com.google.firebase.Timestamp;
import com.google.android.gms.maps.model.LatLng;

public class Note {
    String title;
    String content;
    Timestamp timestamp;
    private String location;
    private double latitude;
    private double longlitude;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLonglitude() {
        return longlitude;
    }

    public void setLonglitude(double longlitude) {
        this.longlitude = longlitude;
    }

    public Note() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
