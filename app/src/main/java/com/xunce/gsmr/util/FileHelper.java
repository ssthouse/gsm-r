package com.xunce.gsmr.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.DhcpInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.ipaulpro.afilechooser.FileChooserActivity;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.BitmapItem;
import com.xunce.gsmr.model.PrjItem;
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
        EventBus.getDefault().post(new CompressFileEvent(CompressFileEvent.Event.BEGIN));
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //将sd卡中不属于当前项目的文件删除
                    deleteNoneUseFile();
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

                } catch (Exception e) {
                    e.printStackTrace();
                    Timber.e(e.getMessage());
                    Timber.e("something is wrong");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GSM_输出.zip";
                ToastHelper.show(context, "文件发售给你成功\n 路径为：" + outputPath);
                EventBus.getDefault().post(new CompressFileEvent(CompressFileEvent.Event.END));
            }
        }.execute();
    }

    /**
     * 删除不必要的文件
     */
    private static void deleteNoneUseFile() {
        List<PrjItem> prjItemList = DBHelper.getPrjItemList();
        List<String> prjNameList = new ArrayList<>();
        for (PrjItem prjItem : prjItemList) {
            prjNameList.add(prjItem.getPrjName());
            Timber.e("找到一个工程名:\t" + prjItem.getPrjName());
        }
        //遍历文件夹
        String srcPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GSM/Picture";
        String files[] = new File(srcPath).list();
        for (String filePath : files) {
            File file = new File(srcPath+"/"+filePath);
            if (!prjNameList.contains(file.getName()) && !file.getName().equals(DBHelper.DB_NAME)) {
                deleteFile(file);
            }
        }
    }

    //递归删除文件夹
    private static void deleteFile(File file) {
        if (file.exists()) {//判断文件是否存在
            if (file.isFile()) {//判断是否是文件
                file.delete();//删除文件
            } else if (file.isDirectory()) {//否则如果它是一个目录
                File[] files = file.listFiles();//声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) {//遍历目录下所有的文件
                    deleteFile(files[i]);//把每个文件用这个方法进行迭代
                }
                file.delete();//删除文件夹
            }
        } else {
            System.out.println("所删除的文件不存在");
        }
    }

    /**
     * 获取SD卡路径
     *
     * @return 获取SD卡的根路径
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
     * @param context        上下文
     * @param bitmapItemList bitmapItem的list
     */
    public static void sendPicture(Context context, SQLiteDatabase db, List<BitmapItem>
            bitmapItemList) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> uriList = new ArrayList<>();
        //获取对应TourItem的文件的URL
        for (BitmapItem item : bitmapItemList) {
            //将原图片从数据库中取出存放至临时文件夹并发送
            uriList.add(DBHelper.selectPicture(db,PictureHelper.getNameFromPath(item.getPath())));
//            File file = new File(item.getPath());
//            uriList.add(Uri.fromFile(file));
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
     *
     * @param fis     输入文件流
     * @param newPath 输出路径
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
