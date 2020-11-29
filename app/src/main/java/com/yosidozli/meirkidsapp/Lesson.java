package com.yosidozli.meirkidsapp;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by yosid on 18/05/2017.
 */

public class Lesson implements Serializable {
    private String id;
    private String title;
    private String imageUrl;
    private String setName;
    private String postUrl;
    private String cropUrl;
    private String lessonSetID;
    private String vimeoId;
    private final String mp4Url;
    private boolean forUsersOnly;

    transient private Bitmap image;





    public Lesson(String id, String title, String imageUrl, String setName, String postUrl, String cropUrl, String lessonSetID, boolean forUsersOnly,String vimeoId , String mp4Url) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.setName = setName;
        this.postUrl = postUrl;
        this.cropUrl = cropUrl;
        this.lessonSetID = lessonSetID;
        this.forUsersOnly = forUsersOnly;
        this.vimeoId = vimeoId;
        this.mp4Url = mp4Url;
       // setImage(imageUrl);
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public Bitmap getImage() {
        return image;
    }


    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isForUsersOnly() {
        return forUsersOnly;
    }

    public void setForUsersOnly(boolean forUsersOnly) {
        this.forUsersOnly = forUsersOnly;
    }

    public String getCropUrl() {
        return cropUrl;
    }

    public void setCropUrl(String cropUrl) {
        this.cropUrl = cropUrl;
    }

    public String getLessonSetID() {
        return lessonSetID;
    }

    public void setLessonSetID(String lessonSetID) {
        this.lessonSetID = lessonSetID;
    }

    public String getVimeoId() {
        return vimeoId;
    }

    public void setVimeoId(String vimeoId) {
        this.vimeoId = vimeoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMp4Url() {
        return mp4Url;
    }
}
