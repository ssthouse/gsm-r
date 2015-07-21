package com.xunce.gsmr.util;

import android.content.Context;
import android.os.Environment;

import com.xunce.gsmr.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;

import java.io.File;
import java.util.List;

/**
 * app的文件管理类
 * Created by ssthouse on 2015/7/16.
 */
public class FileHelper {

    /**
     * 删除一个PrjItem的数据
     * TODO
     *
     * @param prjItem
     */
    public static void deletePrjItem(PrjItem prjItem) {
        if (prjItem == null) {
            return;
        }
        //删除照片文件
        String path = Constant.PICTURE_PATH + prjItem.getPrjName();
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        //删除数据库文件
        List<MarkerItem> markerItemList = prjItem.getMarkerItemList();
        if (markerItemList != null) {
            for (MarkerItem item : markerItemList) {
                item.delete();
            }
        }
        prjItem.delete();
    }

    public static void changePrjItemName(PrjItem prjItem, String newName) {
        if (prjItem == null || newName == null) {
            return;
        }
        //删除照片文件
        String path = Constant.PICTURE_PATH + prjItem.getPrjName();
        File file = new File(path);
        if (file.exists()) {
            file.renameTo(new File(Constant.PICTURE_PATH + newName));
        }
        //修改数据库文件
        List<MarkerItem> markerItemList = prjItem.getMarkerItemList();
        if (markerItemList != null) {
            for (MarkerItem item : markerItemList) {
                item.setPrjName(newName);
                item.save();
            }
        }
        prjItem.setPrjName(newName);
        prjItem.save();
    }

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
        } else {
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
        } else {
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

    public static void creatPrjPath(String prjName) {

    }
}
