package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "memo.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "memo";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_DATE = "date";

    private Memo memo;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getContent());
        values.put(COLUMN_DATE, memo.getDate());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public void updateMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getContent());
        values.put(COLUMN_DATE, memo.getDate());

        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(memo.getId())});
        db.close();
    }

    public void deleteMemo(int memoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(memoId)});
        db.close();
    }

    public Memo getMemoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        Memo memo = null;

        if (cursor != null && cursor.moveToFirst()) {
            int memoId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
            String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
            memo = new Memo(memoId, title, content, date);
            cursor.close();
        }

        db.close();

        return memo;
    }

    public List<Memo> getAllMemos() {
        List<Memo> memoList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int memoId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                Memo memo = new Memo(memoId, title, content, date);
                memoList.add(memo);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return memoList;
    }
}

