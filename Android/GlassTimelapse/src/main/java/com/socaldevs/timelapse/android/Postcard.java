package com.socaldevs.timelapse.android;

import android.graphics.Bitmap;

/**
 * Created by vincente on 9/7/13.
 */
public class Postcard {

    private String user, location, videoUrl, previewUrl;
    private Bitmap previewBitMap;

    public Postcard(String user, String location, String videoUrl, String preview){
        this.previewUrl = preview;
        this.user       = user;
        this.location   = location;
        this.videoUrl   = videoUrl;
        this.previewBitMap = null;
    }

    public String getUser() {
        return user;
    }

    public String getLocation() {
        return location;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public Bitmap getPreviewBitMap() {
        return previewBitMap;
    }

    public void setPreviewBitMap(Bitmap previewBitMap) {
        this.previewBitMap = previewBitMap;
    }
}
