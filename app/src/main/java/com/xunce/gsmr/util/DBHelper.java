package com.xunce.gsmr.util;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.activeandroid.query.Select;
import com.orhanobut.logger.Logger;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.BitmapItem;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库处理的工具类(处理app存储的本地数据库) Created by ssthouse on 2015/7/17.
 */
public class DBHelper {
    private static String SELECTION_PRJNAME = DBSqliteHelper.column_prjname + " = ?";
    public static final String DB_NAME = "Location.db";

    /**
     * 判断prjItem是不是空的
     *
     * @param prjItem
     * @return 数据库中PrjItem是不是空的
     */
    public static boolean isPrjEmpty(PrjItem prjItem) {
        List<MarkerItem> markerList = new Select()
                .from(MarkerItem.class)
                .where("prjName = " + "'" + prjItem.getPrjName() + "'")
                .execute();
        return markerList == null || markerList.size() == 0;
    }

    /**
     * 根据MarkerItem生成的时间获取数据库中的MarkerItem 从数据库中获取MarkerItem
     *
     * @param markerItem 序列化的MarkerItem
     * @return 在数据库中保存的MarkerItem
     */
    public static MarkerItem getMarkerItemInDB(MarkerItem markerItem) {
        String prjName = markerItem.getPrjName();
        String photoPathName = markerItem.getPhotoPathName();
        MarkerItem markerItemInDB = new Select()
                .from(MarkerItem.class)
                .where("prjName =" + " '" + prjName
                        + "' and "
                        + MarkerItem.MarkerItemCons.column_photo_path_name
                        + " =" + " '" + photoPathName + "'")
                .executeSingle();
//        Timber.e("我从数据库中找到的东西是:   ");
//        Timber.e("原来的:   " + markerItem.getPrjName() + markerItem.getPhotoPathName());
//        Timber.e("我找到的是" + (markerItemInDB == null));
        return markerItemInDB;
    }

    /**
     * 判断工程是否在---数据库---中已存在
     *
     * @param prjName
     * @return
     */
    public static boolean isPrjExist(String prjName) {
        List<PrjItem> prjItems = new Select()
                .from(PrjItem.class)
                .where("prjName = " + "'" + prjName + "'")
                .execute();
        return !(prjItems == null || prjItems.size() == 0);
    }

    /**
     * 按照创建的Id的顺序获取PrjItm的列表
     */
    public static List<PrjItem> getPrjItemList() {
        return new Select()
                .from(PrjItem.class)
                .orderBy("Id ASC")
                .execute();
    }

    /**
     * 获取一个PrjItem所有的Marker点
     *
     * @param prjItem
     * @return
     */
    public static List<MarkerItem> getMarkerList(PrjItem prjItem) {
        return new Select().from(MarkerItem.class)
                .where("prjName = " + "'" + prjItem.getPrjName() + "'")
                .execute();
    }

    /**
     * 将照片数据存入数据库中
     *
     * @param prjName   工程名
     * @param makerId   Maker的Id
     * @param photodata 照片数据
     */
    public static void insertPicture(SQLiteDatabase db, String prjName, String makerId, String
            picName, byte[] photodata) {
        Object[] args = new Object[]{prjName, makerId, picName, photodata};
        try {
//            SQLiteDatabase db = SQLiteDatabase.openDatabase(Constant.DbPath, null,
//                    SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL(DBSqliteHelper.sql_create_table);
            db.execSQL(DBSqliteHelper.sql_insert, args);
//            db.close();
            Logger.d("图片插入成功hah");
        } catch (SQLException ex) {
            Logger.w("图片插入失败 %s", ex.toString());
        }
    }

    /**
     * 从数据库获取MarkerItem对应的图片并保存至临时目录
     *
     * @param db         数据库
     * @param markerItem 对应的MarkerItem
     */
//    public static void getPictureItemList(SQLiteDatabase db, MarkerItem markerItem) {
//        File file = new File(markerItem.getFilePath());
//        if (file.exists()) {
//            PictureHelper.delete(markerItem.getFilePath());
//        }
//        file.getParentFile().mkdirs();
//        db.execSQL(DBSqliteHelper.sql_create_table);
//        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_PICTURE_ITEM + " WHERE " +
//                        "" + DBSqliteHelper.column_prjname + "= ? ",
//                new String[]{markerItem.getPrjName()});
//        Logger.d("保存的路径是:%s", markerItem.getFilePath());
//        byte[] imagequery = null;
//        if (cursor == null || !cursor.moveToFirst()) {
//            return;
//        }
//        do {
//            //将Blob数据转化为字节数组
//            String picName = cursor.getString(cursor.getColumnIndex(DBSqliteHelper.column_picName));
//            //如果有缓存的图片就不用读取数据库了，在临时文件夹建立一个虚假的文件，在读的时候会自动读取缓存文件
////            if (PictureHelper.hasTempPicture(picName)) {
////                File filetmp = new File(markerItem.getFilePath() + picName);
////                try {
////                    filetmp.createNewFile();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                continue;
////            }
//            imagequery = cursor.getBlob(cursor.getColumnIndex(DBSqliteHelper.column_blob));
//            Bitmap imagebitmap = BitmapFactory.decodeByteArray(imagequery, 0, imagequery.length);
//            PictureHelper.saveImage(imagebitmap, markerItem.getFilePath() + picName);
//        } while (cursor.moveToNext());
//    }

    /**
     * 获取Picture的缩略图用于展示,如果没有缩略图就从数据库读（数据库读的非常慢）
     *
     * @param db
     * @param markerItem
     * @return
     */
    public static List<BitmapItem> getPictureItemList(SQLiteDatabase db, MarkerItem markerItem) {
        //要返回的数据
        List<BitmapItem> bitmapList = new ArrayList<>();
        db.execSQL(DBSqliteHelper.sql_create_table);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_PICTURE_ITEM + " WHERE " +
                        DBSqliteHelper.column_prjname + " = ? " +
                        " and " + DBSqliteHelper.column_maker_id + " = ? ",
                new String[]{markerItem.getPrjName(), markerItem.getPhotoPathName()});
        if (cursor == null || !cursor.moveToFirst()) {
            return bitmapList;
        }
        do {
            String picName = cursor.getString(cursor.getColumnIndex(DBSqliteHelper.column_picName));
//            如果有缓存的图片就不用读取数据库了，直接将缓存文件读出来
            if (PictureHelper.hasTempPicture(picName)) {
                Bitmap bitmap = BitmapFactory.decodeFile(Constant.TEMP_FILE_PATH + picName);
                bitmapList.add(new BitmapItem(bitmap, Constant.TEMP_FILE_PATH + picName));
            } else {
                //将Blob数据转化为字节数组
                byte[] imagequery = cursor.getBlob(cursor.getColumnIndex(DBSqliteHelper.column_blob));
                Bitmap imgbitmap = BitmapFactory.decodeByteArray(imagequery, 0, imagequery.length);
                PictureHelper.saveImage(imgbitmap, markerItem.getFilePath() + picName);
                Bitmap bitmap = PictureHelper.getSmallBitmap(markerItem.getFilePath() + picName, 120, 120);
                bitmapList.add(new BitmapItem(bitmap, Constant.TEMP_FILE_PATH + picName));
                //将其缓存
                PictureHelper.saveImage(bitmap, Constant.TEMP_FILE_PATH + picName);
            }
        } while (cursor.moveToNext());
        return bitmapList;
    }

    /**
     * 删除指定名称的照片
     *
     * @param db      数据库
     * @param picName 照片名称
     */
    public static void deletePicture(SQLiteDatabase db, String picName) {
        db.execSQL("DELETE FROM " + Constant.TABLE_PICTURE_ITEM +
                " WHERE " + DBSqliteHelper.column_picName + " = " +
                "'" + picName + "'");
        Logger.w("删除了照片:%s", picName);
    }

    public static Uri selectPicture(SQLiteDatabase db, String picName) {
        Uri uri = null;
        if(PictureHelper.hasSharePicture(picName)){
            File file = new File(Constant.TEMP_SHARE_PATH + picName);
            uri = Uri.fromFile(file);
            return uri;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_PICTURE_ITEM + " WHERE " +
                        DBSqliteHelper.column_picName + " = ? ",
                new String[]{picName});
        if (cursor == null || !cursor.moveToFirst()) {
            return uri;
        } else {
            byte[] imagequery = cursor.getBlob(cursor.getColumnIndex(DBSqliteHelper.column_blob));
            Bitmap imagebitmap = BitmapFactory.decodeByteArray(imagequery, 0, imagequery.length);
            PictureHelper.saveImage(imagebitmap, Constant.TEMP_SHARE_PATH + picName);
            File file = new File(Constant.TEMP_SHARE_PATH + picName);
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
