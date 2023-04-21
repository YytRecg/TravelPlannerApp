package com.example.myapplication2;

public class LMPhoto {
    String id;
    String title;
    String dateTaken;
    String photoURL;

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    float lat;
    float lon;
//    String geoInfo;


    public LMPhoto(String id, String title, String dateTaken, String photoURL, float lat, float lon) {
        this.id = id;
        this.title = title;
        this.dateTaken = dateTaken;
        this.photoURL = photoURL;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "LMPhotos{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", dateTaken='" + dateTaken + '\'' +
                ", photoURL='" + photoURL + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public String getPhotoURL() {
        return photoURL;
    }
}
