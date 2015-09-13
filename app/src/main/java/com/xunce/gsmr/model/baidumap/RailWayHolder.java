package com.xunce.gsmr.model.baidumap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.model.baidumap.graph.Circle;
import com.xunce.gsmr.model.baidumap.graph.Line;
import com.xunce.gsmr.model.baidumap.graph.Text;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.gps.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 铁路的管理类
 * 1.一条铁路应该是对应的一个数据库中的数据
 * Created by ssthouse on 2015/8/21.
 */
public class RailWayHolder {
    private static final String TAG = "RailWayHolder";

    private List<Circle> circles;

    private List<Line> lines;

    private List<Text> texts;

    public RailWayHolder(Context context, PrjItem prjItem){
        circles = new ArrayList<>();
        lines = new ArrayList<>();
        texts = new ArrayList<>();

        //TODO---读取数据库中的数据(根据prjItem获取数据库路径)
        SQLiteDatabase db = context.openOrCreateDatabase(DBHelper.TEMP_DB_PATH,
                SQLiteDatabase.OPEN_READWRITE, null);

        //TODO---先手动添加一些数据
        circles.add(new Circle(new LatLng(30.51667, 114.31667), 20));
        circles.add(new Circle(new LatLng(30.51667, 114.31667), 40));
        circles.add(new Circle(new LatLng(30.51667, 114.31667), 60));
        lines.add(new Line(new LatLng(30.51667, 114.31667), new LatLng(30.52667, 114.33667)));

        texts.add(new Text(new LatLng(30.51667, 114.31667), "哈哈哈哈哈哈哈"));
    }

    /**
     * 画出自己
     */
    public void draw(BaiduMap baiduMap){
        for(Circle circle : circles){
            circle.draw(baiduMap);
        }
        for(Line line :lines){
            line.draw(baiduMap);
        }
        for(Text text : texts){
            text.draw(baiduMap);
        }
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
}
