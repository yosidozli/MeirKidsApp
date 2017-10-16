package com.yosidozli.meirkidsapp;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by yosid on 08/06/2017.
 */

public class LessonSet implements Serializable{
    private String ID;
    private String title;
    private String bigImagePath;
    private String smallImagePath;
    private Bitmap image;

    public LessonSet(String ID, String title, String bigImagePath, String smallImagePath) {
        this.ID = ID;
        this.title = title;
        this.bigImagePath = bigImagePath;
        this.smallImagePath = smallImagePath;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBigImagePath() {
        return bigImagePath;
    }

    public void setBigImagePath(String bigImagePath) {
        this.bigImagePath = bigImagePath;
    }

    public String getSmallImagePath() {
        return smallImagePath;
    }

    public void setSmallImagePath(String smallImagePath) {
        this.smallImagePath = smallImagePath;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
