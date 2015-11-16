package com.xunce.gsmr.model.gaodemap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.model.gaodemap.graph.Circle;
import com.xunce.gsmr.model.gaodemap.graph.Line;
import com.xunce.gsmr.model.gaodemap.graph.Text;
import com.xunce.gsmr.util.L;

import java.util.ArrayList;
import java.util.List;

/**
 * 高德地图地图铁路绘图管理器
 * Created by ssthouse on 2015/9/15.
 */
public class GaodeRailWayHolder {
    private static final String TAG = "GaodeRailWayHolder";

    /**
     * 所有绘图数据
     */
    private List<Circle> circles;
    private List<Line> lines;
    private List<Text> texts;

    /**
     * 构造方法
     * @param context
     * @param prjItem
     */
    public GaodeRailWayHolder(Context context, PrjItem prjItem){
        circles = new ArrayList<>();
        lines = new ArrayList<>();
        texts = new ArrayList<>();

        //TODO---先手动添加一些数据
        circles.add(new Circle(new LatLng(30.51667, 114.31667), 20));
        circles.add(new Circle(new LatLng(30.51667, 114.31667), 40));
        circles.add(new Circle(new LatLng(30.51667, 114.31667), 60));
        lines.add(new Line(new LatLng(30.51667, 114.31667), new LatLng(30.52667, 114.33667)));

        texts.add(new Text(new LatLng(30.51667, 114.31667), 20, "哈哈哈哈哈哈哈"));
    }

    /**
     * 画出自己
     */
    public void draw(AMap aMap){
        for(Circle circle : circles){
            circle.draw(aMap);
        }
        for(Line line :lines){
            line.draw(aMap);
        }
        for(Text text : texts){
            text.draw(aMap);
        }
    }

    /**
     * 获取LineList
     * @param database
     * @return
     */
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
            L.log(TAG, cursor.getFloat(1) + ":" + cursor.getFloat(2) + ":" +
                    cursor.getFloat(1) + ":" + cursor.getFloat(2));
            cursor.moveToNext();
        }
        return lineList;
    }

    /**
     * 获取Circle列表
     * @param database
     * @return
     */
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
            L.log(TAG, cursor.getFloat(1) + ":" + cursor.getFloat(2) + ":" +
                    cursor.getFloat(3));
            cursor.moveToNext();
        }
        return circleList;
    }

    /**
     * 获取文字List
     * @param database
     * @return
     */
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
            L.log(TAG, cursor.getString(1) + ":" + cursor.getFloat(2) + ":" +
                    cursor.getFloat(3));
            cursor.moveToNext();
        }
        return circleList;
    }
}

