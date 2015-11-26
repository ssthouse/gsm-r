package com.xunce.gsmr.model.gaodemap.graph;

import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.xunce.gsmr.util.gps.PositionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 矢量图形
 * Created by ssthouse on 2015/10/16.
 */
public class Vector extends BaseGraph {
    private static final String TAG = "Vector";

    private static final int POLYLINE_WIDTH = 6;

    /**
     * 当前矢量的名称
     */
    private String name;
    /**
     * 矢量在地图中的类型
     */
    private String typeInMap;

    /**
     * 一个矢量的所有点
     */
    private List<Point> pointList = new ArrayList<>();

    /**
     * 画在地图上的数据
     */
    private PolylineOptions polylineOptions;
    private Polyline polyline;

    /**
     * 传入name的构造方法
     *
     * @param name
     */
    public Vector(String name) {
        this.name = name;
    }

    /**
     * 传入一个name的构造方法
     *
     * @param name
     */
    public Vector(String name, String typeInMap) {
        this.name = name;
        this.typeInMap = typeInMap;
    }

    /**
     * 初始化需要画在地图上的数据
     */
    public void initPolylineOptions() {
        polylineOptions = new PolylineOptions();
        polylineOptions.width(POLYLINE_WIDTH).color(Color.BLUE);
        //添加点
        for (int i = 0; i < pointList.size(); i++) {
            Point point = pointList.get(i);
            polylineOptions.add(PositionUtil.gps84_To_Gcj02(point.getLatitude(), point.getLongitude()));
        }
        //判断需不需要改变颜色
        //L.log(TAG, "name:\t" + name);
        if (name != null && name.contains("Railway")) {
            //L.log(TAG, "我改变了颜色");
            polylineOptions.color(Color.RED);
            polylineOptions.width(POLYLINE_WIDTH * 2);
        }
    }

    @Override
    public void draw(AMap aMap) {
        if (polylineOptions == null) {
            initPolylineOptions();
            polyline = aMap.addPolyline(polylineOptions);
            return;
        }
        if (polyline == null) {
            polyline = aMap.addPolyline(polylineOptions);
        } else {
            polyline.setVisible(true);
        }
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (polyline != null) {
            polyline.setVisible(false);
        }
    }

    /**
     * 销毁
     */
    public void destory() {
        if (polyline != null) {
            polyline.remove();
        }
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
