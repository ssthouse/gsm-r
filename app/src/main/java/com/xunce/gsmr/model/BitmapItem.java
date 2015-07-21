package com.xunce.gsmr.model;

import android.graphics.Bitmap;

/**
 * Created by ssthouse on 2015/7/20.
 */
public class BitmapItem {

    private Bitmap bitmap;

    private String path;

    public BitmapItem(Bitmap bitmap, String path) {
        this.bitmap = bitmap;
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
