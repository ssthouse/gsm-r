package com.xunce.gsmr.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by ssthouse on 2015/7/16.
 */
public class FileHelper {

    /**
     * 获取File根目录的文件路径
     *
     * @return
     */
    public static String getFileParentPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static String getFilePath(Context context, String fileName) {
        return getFileParentPath(context) + fileName;
    }

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
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        }else{
            return null;
        }
    }

    public static String getSDPath(String fileName) {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString() + "/" + fileName;
        }else{
            return null;
        }
    }

    /**
     * 创建SD下的该app根目录
     */
    public static void creatAppPath() {
        File sd = Environment.getExternalStorageDirectory();
        if (!sd.exists())
            sd.mkdir();
    }

    public static void creatPrjPath(String prjName){

    }
}
