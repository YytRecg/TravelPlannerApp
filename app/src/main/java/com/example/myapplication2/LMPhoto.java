package com.example.myapplication2;

public class LMPhoto {
    String id;
    String title;
    String dateTaken;
    String photoURL;
//    String geoInfo;


    public LMPhoto(String id, String title, String dateTaken, String photoURL) {
        this.id = id;
        this.title = title;
        this.dateTaken = dateTaken;
        this.photoURL = photoURL;
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
