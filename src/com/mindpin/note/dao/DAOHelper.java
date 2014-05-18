package com.mindpin.note.dao;

import static android.provider.BaseColumns._ID;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class DAOHelper extends SQLiteOpenHelper implements DatabaseConstants {

    protected static final String NOTES_TABLE_NAME = "notes";
    protected static final String NOTES_TITLE = "notes_tilte";
    protected static final String NOTES_CONTENT_PATH = "notes_path";
    protected static final String NOTES_PICTURE = "notes_picture_path";
    protected static final String NOTES_RECORD = "notes_record_path";
    protected static final String NOTES_CREATE_TIME = "notes_time";

    protected static final String[] NOTES_ALL_COLUMS = { _ID, NOTES_TITLE, NOTES_CONTENT_PATH,
        NOTES_PICTURE, NOTES_RECORD,NOTES_CREATE_TIME};

    public DAOHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
       
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("anyshare", "sqllite data base is created.....");
        db.execSQL("CREATE TABLE " + NOTES_TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOTES_TITLE + " TEXT NOT NULL, " +
                NOTES_CONTENT_PATH + " TEXT NOT NULL, " +
                NOTES_PICTURE + " TEXT NOT NULL, " +
                NOTES_RECORD + " TEXT NOT NULL, " +
                NOTES_CREATE_TIME + " DATE NOT NULL "+ 
       
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        onCreate(db);
    }

    protected void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
