package com.example.podroznik_s14983;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

class MyLocation implements Serializable {
    private int id;
    private String name;
    private double lat;
    private double lon;
    private int circle;
    private String note;
    private String encodedPhoto;

    public MyLocation() {
    }

    public MyLocation(int id, String name, double lat, double lon, int circle, String note) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.circle = circle;
        this.note = note;
    }

    public MyLocation(int id, String name, double lat, double lon, int circle, String note, String encodedPhoto) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.circle = circle;
        this.note = note;
        this.encodedPhoto = encodedPhoto;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getCircle() {
        return circle;
    }

    public String getNote() {
        return note;
    }

    public void setEncodedPhoto(String encodedPhoto) {
        this.encodedPhoto = encodedPhoto;
    }

    public String getEncodedPhoto() {
        return encodedPhoto;
    }

    // kodowanie bitmapy do stringa (potrzebne do zapisu zdjęcia w bazie)
    public static String encodeBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return  encodedImage;
    }

    // dekodowanie stringa do bitmapy (potrzebne przy odczycie zdjęcia z bazy)
    public static Bitmap decodeBitmapFromString(String image) {
        if (image != null) {
            byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        }
        else return  null;
    }
}
