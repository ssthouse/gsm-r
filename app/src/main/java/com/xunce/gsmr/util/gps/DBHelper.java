package com.xunce.gsmr.util.gps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.activeandroid.query.Select;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.model.map.Circle;
import com.xunce.gsmr.model.map.Line;
import com.xunce.gsmr.model.map.Text;
import com.xunce.gsmr.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库处理的工具类
 * Created by ssthouse on 2015/7/17.
 */
public class DBHelper {
    private static final String TAG = "DBHelper";


    //这些是---操作从外部接收的数据库文件的方法
    static final String EXTERNAL_DB_PATH = "/storage/sdcard0/GSM/DataBase/";

    static final String TEMP_DB_PATH = "/storage/sdcard0/tencent/QQfile_recv/test.db";

    /**
     * TODO---打开外部数据库文件
     *
     * @param context
     */
    public static void openExternalDbFile(Context context) {
        SQLiteDatabase database = context.openOrCreateDatabase(TEMP_DB_PATH,
                SQLiteDatabase.OPEN_READWRITE, null);
        //TODO---将数据读取出来
        LogHelper.Log(TAG, "成功打开文件");
       // getLineList(database);+//getTextList(database);
       // getCircleList(database);
    }

    private static List<Line> getLineList(SQLiteDatabase database) {
        List<Line> lineList = new ArrayList<>();
        Cursor cursor = database.query("Line", null, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            LatLng latLngBegin = new LatLng(cursor.getFloat(1), cursor.getFloat(2));
            LatLng latLngEnd = new LatLng(cursor.getFloat(3), cursor.getFloat(4));
            lineList.add(new Line(latLngBegin, latLngEnd));
            LogHelper.Log(TAG, cursor.getFloat(1) + ":" + cursor.getFloat(2) + ":" +
                    cursor.getFloat(1) + ":" + cursor.getFloat(2));
            cursor.moveToNext();
        }
        return lineList;
    }

    private static List<Circle> getCircleList(SQLiteDatabase database){
        List<Circle> circleList = new ArrayList<>();
        Cursor cursor = database.query("Circle", null, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            LatLng latLng = new LatLng(cursor.getFloat(1), cursor.getFloat(2));
            circleList.add(new Circle(latLng, (int) cursor.getFloat(3)));
            LogHelper.Log(TAG, cursor.getFloat(1) + ":" + cursor.getFloat(2) + ":" +
                    cursor.getFloat(3));
            cursor.moveToNext();
        }
        return circleList;
    }

    private static List<Text> getTextList(SQLiteDatabase database){
        List<Text> circleList = new ArrayList<>();
        Cursor cursor = database.query("Text", null, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            LatLng latLng = new LatLng(cursor.getFloat(2), cursor.getFloat(3));
            circleList.add(new Text(latLng, cursor.getString(1)));
            LogHelper.Log(TAG, cursor.getString(1) + ":" + cursor.getFloat(2) + ":" +
                    cursor.getFloat(3));
            cursor.moveToNext();
        }
        return circleList;
    }


    //下面是----处理----对象数据库----也就是自己创建的数据库的操作方法-------------------------

    /**
     * 判断prjItem是不是空的
     *
     * @param prjItem
     * @return
     */
    public static boolean isPrjEmpty(PrjItem prjItem) {
        List<MarkerItem> markerList = new Select()
                .from(MarkerItem.class)
                .where("prjName = " + "'" + prjItem.getPrjName() + "'")
                .execute();
        if (markerList == null || markerList.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 从数据库中获取MarkerItem
     *
     * @param markerItem
     * @return
     */
    public static MarkerItem getMarkerItemInDB(MarkerItem markerItem) {
        String prjName = markerItem.getPrjName();
        double latitude = markerItem.getLatitude();
        double longitude = markerItem.getLongitude();
        MarkerItem markerItemInDB = new Select()
                .from(MarkerItem.class)
                .where("prjName ="
                        + " '" + prjName + "' and "
                        + "latitude ="
                        + " '" + latitude + "' and "
                        + "longitude ="
                        + " '" + longitude + "'").executeSingle();
        return markerItemInDB;
    }

    /**
     * 判断工程是否在---数据库---中已存在
     *
     * @param prjName
     * @return
     */
    public static boolean isPrjExist(String prjName) {
        List<PrjItem> prjImteList = new Select()
                .from(PrjItem.class)
                .where("prjName = " + "'" + prjName + "'")
                .execute();
        if (prjImteList == null || prjImteList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 按照创建的Id的顺序获取PrjItm的列表
     */
    public static List<PrjItem> getPrjItemList() {
        List<PrjItem> prjItemList = new Select()
                .from(PrjItem.class)
                .orderBy("Id ASC")
                .execute();
        return prjItemList;
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
}
