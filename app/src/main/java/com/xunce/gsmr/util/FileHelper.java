package com.xunce.gsmr.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.ipaulpro.afilechooser.FileChooserActivity;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.BitmapItem;
import com.xunce.gsmr.model.event.CompressFileEvent;
import com.xunce.gsmr.util.view.ToastHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * app的文件管理类
 * Created by ssthouse on 2015/7/16.
 */
public class FileHelper {
    /**
     * 将app的数据打包发出去
     */
    public static void sendZipFile(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                EventBus.getDefault().post(new CompressFileEvent(CompressFileEvent.Event.BEGIN));
                try {
                    //首先压缩文件
                    String srcPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GSM/Picture";
                    String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GSM_输出.zip";
                    //首先将数据库文件复制到当前路径下
                    FileHelper.copyFile(new FileInputStream(context.getDatabasePath(DBHelper.DB_NAME)),
                            srcPath + "/" + DBHelper.DB_NAME);
                    ZipUtil.zipFolder(srcPath, outputPath);
                    //然后发送压缩文件
                    Intent sendFileIntent = new Intent(Intent.ACTION_SEND);
                    sendFileIntent.setType("*/*");
                    sendFileIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(outputPath)));
                    context.startActivity(sendFileIntent);
                    ToastHelper.show(context, "文件发售给你成功\n 路径为：" + outputPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    Timber.e("something is wrong");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                EventBus.getDefault().post(new CompressFileEvent(CompressFileEvent.Event.END));
            }
        }.execute();
    }

    /**
     * 获取SD卡路径
     *
     * @return  获取SD卡的根路径
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
        Intent getContentIntent = new Intent(context, FileChooserActivity.class);
        // Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        context.startActivityForResult(getContentIntent, requestCode);
    }

    /**
     * 发送图片文件
     *
     * @param context   上下文
     * @param bitmapItemList    bitmapItem的list
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
     * 将本地的数据库文件发送出去
     *
     * @param context
     */
    public static void sendDbFile(Activity context) {
        File tempDbFile = new File(Constant.TEMP_FILE_PATH, DBHelper.DB_NAME);
        try {
            tempDbFile.createNewFile();
        } catch (IOException e) {
            Timber.e("create new file is wrong");
            e.printStackTrace();
        }
        try {
            copyFile(new FileInputStream(new File(context.getDatabasePath(DBHelper.DB_NAME)
                    .getAbsolutePath()))
                    , tempDbFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Timber.e("copy file wrong");
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempDbFile));
        intent.setType("*/*");
        context.startActivity(intent);
    }

    /**
     * 复制单个文件
     * @param fis   输入文件流
     * @param newPath   输出路径
     */
    public static void copyFile(FileInputStream fis, String newPath) {
        try {
            //确保输出文件
            File outputFile = new File(newPath);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            //复制文件
            int byteRead;
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[1444];
            while ((byteRead = fis.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            fis.close();
        } catch (Exception e) {
            Timber.e("复制单个文件出错");
            e.printStackTrace();
        }
    }
}
