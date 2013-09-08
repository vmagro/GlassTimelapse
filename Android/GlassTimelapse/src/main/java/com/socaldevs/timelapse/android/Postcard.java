package com.socaldevs.timelapse.android;

import android.graphics.Bitmap;

/**
 * Created by vincente on 9/7/13.
 */
public class Postcard {

    private Bitmap preview;
    private String user, location, videoUrl;

    public Postcard(Bitmap preview, String user, String location, String videoUrl){
        this.preview    = preview;
        this.user       = user;
        this.location   = location;
        this.videoUrl   = videoUrl;
    }

    public String getUser() {
        return user;
    }

    public String getLocation() {
        return location;
    }

    public Bitmap getPreview() {
        return preview;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
