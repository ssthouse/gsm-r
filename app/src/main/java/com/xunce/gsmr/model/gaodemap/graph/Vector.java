package com.xunce.gsmr.model.gaodemap.graph;

import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.xunce.gsmr.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 矢量图形
 * Created by ssthouse on 2015/10/16.
 */
public class Vector extends BaseGraph {
    private static final String TAG = "Vector";


    /**
     * 当前矢量的名称
     */
    private String name;

    /**
     * 一个矢量的所有点
     */
    private List<Point> pointList = new ArrayList<>();

    @Override
    public void draw(AMap aMap) {
        //先amap添加polylineOptions
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(10)
                .color(Color.RED);
        //添加点
        for(int i=0; i<pointList.size(); i++){
            Point point = pointList.get(i);
            polylineOptions.add(new LatLng(point.getLongitude(), point.getLatitude()));
            LogHelper.Log(TAG, "我在这条矢量中添加了一个点:  "+i);
        }

        aMap.addPolyline((new PolylineOptions())
                .add(new LatLng(43.828, 87.621), new LatLng(45.808, 126.55))
                .geodesic(true).color(Color.RED));

        //画出来
        aMap.addPolyline(polylineOptions);
    }

    /**
     * 传入一个name的构造方法
     * @param name
     */
    public Vector(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }
}
