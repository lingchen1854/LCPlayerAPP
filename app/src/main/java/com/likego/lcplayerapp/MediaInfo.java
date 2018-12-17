package com.likego.lcplayerapp;

import android.graphics.Bitmap;
import android.util.Log;

public class MediaInfo {

    private String mMediaName;

    private Bitmap mMediaBitmap;

    public MediaInfo(String name, Bitmap bitmap) {
        this.mMediaName = name;
        this.mMediaBitmap = bitmap;
    }

    public String getMediaName() {
        return mMediaName;
    }

    public Bitmap getMediaImage() {
        return mMediaBitmap;
    }

}
