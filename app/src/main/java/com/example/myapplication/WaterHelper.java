package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class WaterHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "water";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_WATER = "water";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_MESSAGE = "message";

    public WaterHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_WATER + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_MESSAGE + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 업그레이드 시 필요한 로직을 구현합니다.
    }

    public void insertWater(String title, String date, String message) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_MESSAGE, message);
        db.insert(TABLE_WATER, null, values);
        db.close();
    }

    public List<WaterMemo> getAllWaterMemos() {
        List<WaterMemo> waterMemos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WATER, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String message = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE));

                WaterMemo waterMemo = new WaterMemo(id, title, date, message);
                waterMemos.add(waterMemo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return waterMemos;
    }
}
