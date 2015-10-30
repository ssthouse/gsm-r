package com.xunce.gsmr.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.BitmapItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * app的文件管理类
 * Created by ssthouse on 2015/7/16.
 */
public class FileHelper {
    private static final String TAG = "FileHelper";

    /**
     * 获取指定数据库的绝对路径
     *
     * @param context
     * @param dataBaseName
     * @return
     */
    public static String getDataBasePath(Context context, String dataBaseName) {
        return context.getDatabasePath(dataBaseName).getAbsolutePath();
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDPath() {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        } else {
            return null;
        }
    }

    /**
     * 开启文件选择程序
     */
    public static void showFileChooser(Activity context, int requestCode) {
        // Create the ACTION_GET_CONTENT Intent
        Intent getContentIntent = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 发送图片文件
     *
     * @param context
     * @param bitmapItemList
     */
    public static void sendPicture(Context context, List<BitmapItem> bitmapItemList) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> uriList = new ArrayList<>();
        //获取对应TourItem的文件的URL
        for (BitmapItem item : bitmapItemList) {
            File file = new File(item.getPath());
            uriList.add(Uri.fromFile(file));
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        intent.setType("image/jpg");
        //调用系统的----发送
        context.startActivity(Intent.createChooser(intent, "Share　Image"));
    }

    /**
     * 本地生成的数据库的文件名
     */
    private static final String DB_FILE_NAME = "Location.db";

    /**
     * 将本地的数据库文件发送出去
     *
     * @param context
     */
    public static void sendDbFile(Activity context) {
        File tempDbFile = new File(Constant.TEMP_FILE_PATH, DB_FILE_NAME);
        try {
            tempDbFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "create new file is wrong");
            e.printStackTrace();
        }
        try {
            copyFile(new FileInputStream(new File(context.getDatabasePath(DB_FILE_NAME).getAbsolutePath())),
                    tempDbFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "copy file wrong");
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempDbFile));
        intent.setType("*/*");
        context.startActivity(intent);
    }

    /**
     * 复制单个文件
     *
     * @return boolean
     */
    public static void copyFile(FileInputStream fis, String newPath) {
        try {
            int byteRead;
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[1444];
            while ((byteRead = fis.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            fis.close();
        } catch (Exception e) {
            LogHelper.log(TAG, "复制单个文件出错");
            e.printStackTrace();
        }
    }
}
