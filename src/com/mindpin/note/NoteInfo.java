package com.mindpin.note;

import android.graphics.Bitmap;

public class NoteInfo {
    
    private String id;
    private String noteName;
    private String notePath;
    private String picturePath;
    private String audioPath;
    private long createTime;
    private Bitmap bitmap;
    
    
    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNoteName() {
        return noteName;
    }
    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }
    public String getNotePath() {
        return notePath;
    }
    public void setNotePath(String notePath) {
        this.notePath = notePath;
    }
    public String getPicturePath() {
        return picturePath;
    }
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
    public String getAudioPath() {
        return audioPath;
    }
    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
    public long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
