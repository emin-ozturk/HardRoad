package com.emin.hardroad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "app";
    static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME = "data";
    static final String ID = "data";
    static final String SCORE = "score";
    static final String SOUND = "sound"; //0: sessiz, 1: sesli
    static final String VIBRATION = "vibration"; //0: titreşimli, 1: titreşimsiz
    static final String LANGUAGE = "language";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SCORE + " TEXT NOT NULL, "
                + SOUND + " TEXT NOT NULL, "
                + VIBRATION + " TEXT NOT NULL, "
                + LANGUAGE + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SCORE, 0);
        cv.put(SOUND, 1);
        cv.put(VIBRATION, 1);
        cv.put(LANGUAGE, "en");
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public List<String> list() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> data = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                data.add(cursor.getString(0)
                        +
                        " _ "
                        + cursor.getInt(1)
                        +
                        " _ "
                        + cursor.getInt(2)
                        +
                        " _ "
                        + cursor.getInt(3)
                        +
                        " _ "
                        + cursor.getString(4));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getScore() {
        int score = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String[] row = {SCORE};
            Cursor cursor = db.query(TABLE_NAME, row, null, null, null, null, null);
            while (cursor.moveToNext()) {
                score = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }

    public int getSound() {
        int sound = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String[] row = {SOUND};
            Cursor cursor = db.query(TABLE_NAME, row, null, null, null, null, null);
            while (cursor.moveToNext()) {
                sound = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sound;
    }

    public int getVibration() {
        int vibration = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String[] row = {VIBRATION};
            Cursor cursor = db.query(TABLE_NAME, row, null, null, null, null, null);
            while (cursor.moveToNext()) {
                vibration = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vibration;
    }

    public String getLanguage() {
        String language = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String[] row = {LANGUAGE};
            Cursor cursor = db.query(TABLE_NAME, row, null, null, null, null, null);
            while (cursor.moveToNext()) {
                language = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return language;
    }

    public void updateScore(int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SCORE, score);
        db.update(TABLE_NAME, cv, null, null);
        db.close();
    }

    public void updateSound(int sound) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SOUND, sound);
        db.update(TABLE_NAME, cv, null, null);
        db.close();
    }

    public void updateVibration(int vibration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(VIBRATION, vibration);
        db.update(TABLE_NAME, cv, null, null);
        db.close();
    }

    public void updateLanguage(String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LANGUAGE, language);
        db.update(TABLE_NAME, cv, null, null);
        db.close();
    }

}
