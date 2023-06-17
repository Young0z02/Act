package com.example.myapplication;

public class WaterMemo {
    private String title;
    private String date;
    private String message;

    public WaterMemo(int id, String title, String date, String message) {
        this.title = title;
        this.date = date;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}
