package com.xunce.gsmr.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;

/**
 * Bitmap加Path
 * Created by ssthouse on 2015/7/20.
 */
public class BitmapItem {

    private Bitmap bitmap;

    private String path;

    public BitmapItem(Bitmap bitmap, String path) {
        this.bitmap = bitmap;
        this.path = path;
    }

    /**
     * 当前Bitmap开启相册查看
     * @param context
     */
    public void showInAlbum(Context context){
        //使用Intent
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //Uri mUri = Uri.parse("file://" + picFile.getPath());
        intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
        context.startActivity(intent);
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
