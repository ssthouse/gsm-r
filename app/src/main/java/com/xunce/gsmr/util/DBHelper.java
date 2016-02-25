package com.xunce.gsmr.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.orhanobut.logger.Logger;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.lib.kmlParser.GpsPoint;
import com.xunce.gsmr.lib.kmlParser.KmlData;
import com.xunce.gsmr.model.BitmapItem;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库处理的工具类(处理app存储的本地数据库) Created by ssthouse on 2015/7/17.
 */
public class DBHelper {
    private static String SELECTION_PRJNAME = DBConstant.photo_column_prjname + " = ?";

    /************************************************判断部分*****************************/
    /**
     * 判断prjItem是不是空的
     *
     * @param prjItem
     * @return 数据库中PrjItem是不是空的
     */
//    public static boolean isPrjEmpty(PrjItem prjItem) {
//        List<MarkerItem> markerList = new Select()
//                .from(MarkerItem.class)
//                .where("prjName = " + "'" + prjItem.getId() + "'")
//                .execute();
//        return markerList == null || markerList.size() == 0;
//    }
    public static boolean isPrjEmpty(PrjItem prjItem) {
        SQLiteDatabase db = openDatabase(prjItem.getDbLocation());
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_MARKER_ITEM, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return false;
        } else {
            return true;
        }
    }
//    /**
//     * 根据MarkerItem生成的时间获取数据库中的MarkerItem 从数据库中获取MarkerItem
//     *
//     * @param markerItem 序列化的MarkerItem
//     * @return 在数据库中保存的MarkerItem
//     */
//    public static MarkerItem getMarkerItemInDB(MarkerItem markerItem) {
//        String prjName = markerItem.getId();
//        String photoPathName = markerItem.getMarkerId();
//        MarkerItem markerItemInDB = new Select()
//                .from(MarkerItem.class)
//                .where("prjName =" + " '" + prjName
//                        + "' and "
//                        + MarkerItem.MarkerItemCons.column_photo_path_name
//                        + " =" + " '" + photoPathName + "'")
//                .executeSingle();
////        Timber.e("我从数据库中找到的东西是:   ");
////        Timber.e("原来的:   " + markerItem.getId() + markerItem.getMarkerId());
////        Timber.e("我找到的是" + (markerItemInDB == null));
//        return markerItemInDB;
//    }

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

    /********************************************获取部分************************************/
    /**
     * 根据MarkerItem生成的时间获取数据库中的MarkerItem 从数据库中获取MarkerItem
     *
     * @param path     数据库路径
     * @param MarkerId 生成时间
     * @return
     */
    public static MarkerItem getMarkerItemInDB(String path, String MarkerId) {
        SQLiteDatabase db = openDatabase(path);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_MARKER_ITEM +
                " WHERE " + DBConstant.photo_column_maker_id + " = ? ", new String[]{MarkerId});
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        MarkerItem item = getMarkerItem(cursor);
        db.close();
        return item;
    }

    /**
     * 获取KMLPoly表格里的内容
     *
     * @param path
     * @return 所有点的List
     */
    public static List<KmlData> getKMLPolyInDB(String path) {
        SQLiteDatabase db = openDatabase(path);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_KML_POLY + " order by "
                + DBConstant.id + " , " + DBConstant.orderId, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        List<KmlData> kmlDataList = new ArrayList<>();
        KmlData data = null;
        do {
            if (cursor.getInt(cursor.getColumnIndex(DBConstant.orderId)) == 0) {
                if (data != null && data.getPointList().size() != 0) {
                    kmlDataList.add(data);
                }
                data = new KmlData();
                String content = cursor.getString(cursor.getColumnIndex(DBConstant.content));
                double latitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.latitude));
                double longitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.longitude));
                data.setName(content);
                data.setStyleUrl(KmlData.POLY_STYLE);
                data.setLatitude(Double.toString(latitude));
                data.setLongitude(Double.toString(longitude));
            } else {
                double latitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.latitude));
                double longitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.longitude));
                GpsPoint gpsPoint = new GpsPoint(longitude, latitude);
                data.getPointList().add(gpsPoint);
            }
        } while (cursor.moveToNext());
        db.close();
        return kmlDataList;
    }

    /**
     * 获取KMLText表格里的内容
     *
     * @param dbPath
     * @return
     */
    public static List<KmlData> getKMLTextInDB(String dbPath) {
        SQLiteDatabase db = openDatabase(dbPath);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_KML_TEXT, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }
        List<KmlData> kmlDataList = new ArrayList<>();
        KmlData data = null;
        do {
            data = new KmlData();
            String content = cursor.getString(cursor.getColumnIndex(DBConstant.content));
            double latitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.latitude));
            double longitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.longitude));
            data.setName(content);
            data.setStyleUrl(KmlData.TEXT_STYLE);
            data.setLatitude(Double.toString(latitude));
            data.setLongitude(Double.toString(longitude));
            GpsPoint gpsPoint = new GpsPoint(longitude, latitude);
            data.getPointList().add(gpsPoint);
            kmlDataList.add(data);
        } while (cursor.moveToNext());
        db.close();
        return kmlDataList;
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
     * 根据项目名查询PrjItem 返回单例
     *
     * @param prjName
     * @return
     */
    public static PrjItem getPrjItemByName(String prjName) {
        return new Select()
                .from(PrjItem.class)
                .where("prjName = " + "'" + prjName + "'")
                .executeSingle();
    }
//    /**
//     * 获取一个PrjItem所有的Marker点
//     *
//     * @param prjItem
//     * @return
//     */
//    public static List<MarkerItem> getMarkerList(PrjItem prjItem) {
//        return new Select().from(MarkerItem.class)
//                .where("prjName = " + "'" + prjItem.getId() + "'")
//                .execute();
//    }

    /**
     * 获取db中所有的Marker点
     *
     * @param db
     * @return
     */
    public static List<MarkerItem> getMarkerList(SQLiteDatabase db) {
        ArrayList<MarkerItem> markerItemList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_MARKER_ITEM, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return markerItemList;
        }
        do {
            markerItemList.add(getMarkerItem(cursor));
        } while (cursor.moveToNext());
        return markerItemList;
    }

    /**
     * 获取Picture的缩略图用于展示,如果没有缩略图就从数据库读（数据库读的非常慢）
     *
     * @param dbPath
     * @param markerItem
     * @return
     */
    public static List<BitmapItem> getPictureItemList(String dbPath, MarkerItem markerItem) {
        //要返回的数据
        SQLiteDatabase db = openDatabase(dbPath);
        List<BitmapItem> bitmapList = new ArrayList<>();
//        db.execSQL(DBSqliteHelper.sql_create_table);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_PICTURE_ITEM + " WHERE " +
                DBConstant.photo_column_maker_id + " = ? ",
                new String[]{ markerItem.getMarkerId()});
        if (cursor == null || !cursor.moveToFirst()) {
            db.close();
            return bitmapList;
        }
        do {
            String picName = cursor.getString(cursor.getColumnIndex(DBConstant.photo_column_picName));
//            如果有缓存的图片就不用读取数据库了，直接将缓存文件读出来
            if (PictureHelper.hasTempPicture(picName)) {
                Bitmap bitmap = BitmapFactory.decodeFile(Constant.TEMP_FILE_PATH + picName);
                bitmapList.add(new BitmapItem(bitmap, Constant.TEMP_FILE_PATH + picName));
            } else {
                //将Blob数据转化为字节数组
                byte[] imagequery = cursor.getBlob(cursor.getColumnIndex(DBConstant.photo_column_blob));
                Bitmap imgbitmap = BitmapFactory.decodeByteArray(imagequery, 0, imagequery.length);
                PictureHelper.saveImage(imgbitmap, markerItem.getFilePath() + picName);
                Bitmap bitmap = PictureHelper.getSmallBitmap(markerItem.getFilePath() + picName, 120, 120);
                bitmapList.add(new BitmapItem(bitmap, Constant.TEMP_FILE_PATH + picName));
                //将其缓存
                PictureHelper.saveImage(bitmap, Constant.TEMP_FILE_PATH + picName);
            }
        } while (cursor.moveToNext());
        db.close();
        return bitmapList;
    }

    /**
     * 根据照片的名称查找照片
     *
     * @param dbpath        数据库
     * @param PhotoName Photo的名称
     * @return
     */
    public static Uri selectPicture(String dbpath, String PhotoName) {
        Uri uri = null;
        SQLiteDatabase db = openDatabase(dbpath);
        if (PictureHelper.hasSharePicture(PhotoName)) {
            File file = new File(Constant.TEMP_SHARE_PATH + PhotoName);
            uri = Uri.fromFile(file);
            db.close();
            return uri;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constant.TABLE_PICTURE_ITEM + " WHERE " +
                        DBConstant.photo_column_picName + " = ? ",
                new String[]{PhotoName});
        if (cursor == null || !cursor.moveToFirst()) {
            db.close();
            return uri;
        } else {
            byte[] imagequery = cursor.getBlob(cursor.getColumnIndex(DBConstant.photo_column_blob));
            Bitmap imagebitmap = BitmapFactory.decodeByteArray(imagequery, 0, imagequery.length);
            PictureHelper.saveImage(imagebitmap, Constant.TEMP_SHARE_PATH + PhotoName);
            File file = new File(Constant.TEMP_SHARE_PATH + PhotoName);
            uri = Uri.fromFile(file);
        }
        db.close();
        return uri;
    }

    /**
     * 光标生成MarkerItem
     *
     * @param cursor
     * @return
     */
    private static MarkerItem getMarkerItem(Cursor cursor) {
        String MarkerId = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_MarkerId));
        double distorail = cursor.getDouble(cursor.getColumnIndex(DBConstant
                .basestation_column_distance_to_rail));
        String antenna1 = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_antenna_direction_1));
        String antenna2 = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_antenna_direction_2));
        String antenna3 = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_antenna_direction_3));
        String antenna4 = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_antenna_direction_4));
        String towerheight = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_tower_height));
        String sidedir = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_side_direction));
        double latitude = cursor.getDouble(cursor.getColumnIndex(DBConstant
                .basestation_column_latitude));
        double longitude = cursor.getDouble(cursor.getColumnIndex(DBConstant
                .basestation_column_longitude));
        String devicetype = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_device_type));
        String towertype = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_tower_type));
        String kilmark = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_kilometer_mark));
        String Id = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_Id));
        String comment = cursor.getString(cursor.getColumnIndex(DBConstant
                .basestation_column_comment));
        return new MarkerItem(latitude, longitude, MarkerId, devicetype, kilmark,
                sidedir, distorail, comment, towertype, towerheight, antenna1, antenna2, antenna3,
                antenna4);
    }
    /******************************************************打开数据库*************************/
    /**
     * 打开数据库
     *
     * @param Path 数据库的路径
     * @return
     */
    public static SQLiteDatabase openDatabase(String Path) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(Path, null,
                SQLiteDatabase.OPEN_READWRITE);
        return db;
    }

    /*******************************************************插入部分***************************/
    /**
     * 将照片数据存入数据库中
     *
     * @param makerId   Maker的Id
     * @param photodata 照片数据
     */
    public static void insertPicture(String dbPath, String makerId, String
            PhotoName, byte[] photodata) {
        SQLiteDatabase db = openDatabase(dbPath);
        Object[] args = new Object[]{makerId, PhotoName, photodata};
        try {
//            SQLiteDatabase db = SQLiteDatabase.openDatabase(Constant.DbPath, null,
//                    SQLiteDatabase.CREATE_IF_NECESSARY);
//            db.execSQL(DBSqliteHelper.sql_create_table);
            db.execSQL(DBConstant.Photo_sql_insert, args);
            db.close();
            Logger.d("图片插入成功hah");
        } catch (SQLException ex) {
            Logger.w("图片插入失败 %s", ex.toString());
        }
    }

    /**
     * 将MarkerItem保存到数据库中
     *
     * @param db
     */
    public static void insertMarkerItem(SQLiteDatabase db, MarkerItem markerItem) {
        Object[] args = new Object[]{markerItem.getMarkerId(), markerItem.getId(), markerItem
                .getDeviceType(), markerItem.getKilometerMark(), markerItem.getSideDirection(),
                markerItem.getDistanceToRail(), markerItem.getLongitude(), markerItem
                .getLatitude(), markerItem.getComment()};
        try {
            db.execSQL(DBConstant.Basestation_xml_sql_insert, args);
            Logger.d("MarkerItem插入成功 markerId: %s", markerItem.getMarkerId());
        } catch (SQLException ex) {
            Logger.w("MarkerItem插入失败 %s", ex.toString());
        }
    }

    public static void insertLine(SQLiteDatabase db, double longitude_start, double latitude_start,
                                  double longitude_end, double latitude_end) {
        Object[] args = new Object[]{longitude_start, latitude_start, longitude_end, latitude_end};
        try {
            db.execSQL(DBConstant.Line_sql_insert, args);
        } catch (SQLException ex) {
            Logger.w("Line插入失败 %s", ex.toString());
        }
    }

    public static void insertText(SQLiteDatabase db, double longitude, double latitude, String
            content) {
        Object[] args = new Object[]{longitude, latitude, content};
        try {
            db.execSQL(DBConstant.Text_sql_insert, args);
        } catch (SQLException ex) {
            Logger.w("Text插入失败 %s", ex.toString());
        }
    }

    public static void insertPoly(SQLiteDatabase db, int id, int orderId, double longitude, double
            latitude) {
        Object[] args = new Object[]{id, orderId, longitude, latitude};
        try {
            db.execSQL(DBConstant.Poly_sql_insert, args);
        } catch (SQLException ex) {
            Logger.w("Poly插入失败 %s", ex.toString());
        }
    }

    public static void insertP2DPoly(SQLiteDatabase db, int id, int orderId, double longitude, double
            latitude) {
        Object[] args = new Object[]{id, orderId, longitude, latitude};
        try {
            db.execSQL(DBConstant.P2DPoly_sql_insert, args);
        } catch (SQLException ex) {
            Logger.w("P2DPoly插入失败 %s", ex.toString());
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
//                        "" + DBSqliteHelper.photo_column_prjname + "= ? ",
//                new String[]{markerItem.getId()});
//        Logger.d("保存的路径是:%s", markerItem.getFilePath());
//        byte[] imagequery = null;
//        if (cursor == null || !cursor.moveToFirst()) {
//            return;
//        }
//        do {
//            //将Blob数据转化为字节数组
//            String picName = cursor.getString(cursor.getColumnIndex(DBSqliteHelper.photo_column_picName));
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
//            imagequery = cursor.getBlob(cursor.getColumnIndex(DBSqliteHelper.photo_column_blob));
//            Bitmap imagebitmap = BitmapFactory.decodeByteArray(imagequery, 0, imagequery.length);
//            PictureHelper.saveImage(imagebitmap, markerItem.getFilePath() + picName);
//        } while (cursor.moveToNext());
//    }


    /*********************************************删除部分*******************************************/
    /**
     * 删除指定名称的照片
     *
     * @param dbPath        数据库
     * @param PhotoName 照片名称
     */
    public static void deletePicture(String dbPath, String PhotoName) {
        SQLiteDatabase db = openDatabase(dbPath);
        db.execSQL("DELETE FROM " + Constant.TABLE_PICTURE_ITEM +
                " WHERE " + DBConstant.photo_column_picName + " = " +
                "'" + PhotoName + "'");
        db.close();
        Logger.w("删除了照片:%s", PhotoName);
    }

    /**
     * 删除指定Id的Marker数据
     *
     * @param dbLocation
     * @param markerId
     */
    public static void deleteMarkerItem(String dbLocation, String markerId) {
        SQLiteDatabase db = openDatabase(dbLocation);
        db.execSQL("DELETE FROM " + Constant.TABLE_MARKER_ITEM +
                " WHERE " + DBConstant.photo_column_maker_id + " = " +
                "'" + markerId + "'");
        Logger.w("删除了照片:%s", markerId);
    }

    /**
     * 删除PrjItem
     * @param PrjName
     */
    public static void deletePrjItem(String PrjName){
        new Delete()
                .from(PrjItem.class)
                .where("prjName = " + "'" + PrjName + "'")
                .execute();
    }
    /***************************************************
     * 更新部分
     **************************************/

    /**
     * 更新MarkerItem的经纬度
     *
     * @param dbLocation
     * @param markerId
     * @param longitude
     * @param latitude
     */
    public static void updateMarkerItemLatlng(String dbLocation, String markerId, double longitude,
                                              double latitude) {
        SQLiteDatabase db = openDatabase(dbLocation);
        ContentValues values = new ContentValues();
        values.put(DBConstant.longitude, longitude);
        values.put(DBConstant.latitude, latitude);
        db.update(Constant.TABLE_MARKER_ITEM, values, "markerId = " + "'" + markerId + "'", null);
        Logger.d("更新了markerId:%s  的坐标 经度：%f 纬度：%f", markerId, longitude, latitude);
    }

    /**
     * 更新MarkerItem所有信息
     * @param dbLocation
     * @param markerItem
     */
    public static void updateMarkerItemAll(String dbLocation, MarkerItem markerItem) {
        SQLiteDatabase db = openDatabase(dbLocation);
        ContentValues values = new ContentValues();
        values.put(DBConstant.longitude,markerItem.getLongitude());
        values.put(DBConstant.latitude,markerItem.getLatitude());
        values.put(DBConstant.basestation_column_antenna_direction_1,markerItem.getAntennaDirection1());
        values.put(DBConstant.basestation_column_antenna_direction_2,markerItem.getAntennaDirection2());
        values.put(DBConstant.basestation_column_antenna_direction_3,markerItem.getAntennaDirection3());
        values.put(DBConstant.basestation_column_antenna_direction_4,markerItem.getAntennaDirection4());
        values.put(DBConstant.basestation_column_comment,markerItem.getComment());
        values.put(DBConstant.basestation_column_device_type,markerItem.getDeviceType());
        values.put(DBConstant.basestation_column_distance_to_rail,markerItem.getDistanceToRail());
        values.put(DBConstant.basestation_column_Id,markerItem.getId());
        values.put(DBConstant.basestation_column_kilometer_mark,markerItem.getKilometerMark());
        values.put(DBConstant.basestation_column_side_direction,markerItem.getSideDirection());
        values.put(DBConstant.basestation_column_tower_height,markerItem.getTowerHeight());
        values.put(DBConstant.basestation_column_tower_type,markerItem.getTowerType());
        db.update(Constant.TABLE_MARKER_ITEM,values, "markerId = " + "'" + markerItem.getMarkerId() + "'",
                null);
    }
}
