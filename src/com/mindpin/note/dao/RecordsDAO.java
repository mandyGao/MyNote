package com.mindpin.note.dao;

import static android.provider.BaseColumns._ID;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mindpin.note.NoteInfo;

public class RecordsDAO extends DAOHelper {
    
    public RecordsDAO(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public List<NoteInfo> findAllRecordsByDate() { 
        List<NoteInfo> records = new ArrayList<NoteInfo>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            
            cursor = db.query(NOTES_TABLE_NAME, NOTES_ALL_COLUMS,
                    null,
                    null,
                    null, null,  NOTES_CREATE_TIME + " desc");
                while (cursor.moveToNext()) {
                 
                    records.add(createRecordItemFromCursorData(cursor));
                }
        } finally {
            closeCursor(cursor);
        }
        
        return records;
     }
    
     public void deleteRecordById (String id) {
         SQLiteDatabase db = getWritableDatabase();
         db.delete(NOTES_TABLE_NAME, _ID + " = ?", new String[]{id});
     }
     
     public void updateRecordById (NoteInfo noteInfo,String id) {
         SQLiteDatabase db = getWritableDatabase();
         ContentValues values = createContentValues(noteInfo);
         db.update(NOTES_TABLE_NAME, values,_ID + " = ?", new String[]{id});
     }
     
     public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(NOTES_TABLE_NAME, null, null);
        
    }
    public void insertRecord(NoteInfo noteInfo) {
        
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = createContentValues(noteInfo);
        try {
            db.insertOrThrow(NOTES_TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e("anyshare", "insertRecord... exception" + e.getMessage());
        }
      
    }
    
    private ContentValues createContentValues(NoteInfo noteInfo) {
        ContentValues values = new ContentValues();
        values.put(NOTES_TITLE, noteInfo.getNoteName());
        values.put(NOTES_CONTENT_PATH, noteInfo.getNotePath());
        values.put(NOTES_PICTURE, noteInfo.getPicturePath());
        values.put(NOTES_RECORD, noteInfo.getAudioPath());
      
        values.put(NOTES_CREATE_TIME, getDateTime(noteInfo.getCreateTime()));
   
        return values;
    }
    
    public static final String FORMAT_YMD = "yyyy-MM-dd";
    
    public static String getDateTime (long time) {
         SimpleDateFormat sdf= new SimpleDateFormat(FORMAT_YMD);
         Date dt = new Date(time); 
         String sDateTime = sdf.format(dt);
         return sDateTime;
      } 
    
    
    
    private NoteInfo createRecordItemFromCursorData(Cursor cursor) {
        
        long id = cursor.getLong(0);
        String noteTitle = cursor.getString(1);
        String notePath  = cursor.getString(2);
        String  picturePath = cursor.getString(3);
        String recordPath = cursor.getString(4);
       
        NoteInfo item = new NoteInfo();
        item.setId(String.valueOf(id));
        item.setNoteName(noteTitle);
        item.setNotePath(notePath);
        item.setPicturePath(picturePath);
        item.setAudioPath(recordPath);
        
        return item;
    }

}
