package com.xunce.gsmr.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;

import java.io.Serializable;

/**
 * 单个的Marker对象---一个
 * Created by ssthouse on 2015/7/17.
 */
@Table(name = Constant.TABLE_MARKER_ITEM)
public class MarkerItem extends Model implements Serializable {

    @Column(name = "prjName")
    private String prjName;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    /**
     * 传入经纬度的构造方法
     *
     * @param latitude
     * @param longitude
     */
    public MarkerItem(String prjName, double latitude, double longitude) {
        super();
        this.prjName = prjName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * 使用prjItem的构造方法
     *
     * @param prjItem
     */
    public MarkerItem(PrjItem prjItem) {
        super();
        this.prjName = prjItem.getPrjName();
        this.latitude = 0;
        this.longitude = 0;
    }

    /**
     * 传入一个LatLng的构造方法
     *
     * @param latLng
     */
    public MarkerItem(String prjName, LatLng latLng) {
        super();
        this.prjName = prjName;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public MarkerItem() {
        super();
    }


    public String getFilePath() {
        return Constant.PICTURE_PATH + prjName + "/" + latitude + "_" + longitude + "/";
    }

    public void draw(BaiduMap baiduMap) {
        if (latitude == 0 || longitude == 0) {
            return;
        }
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_launcher);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(bitmap)
                .title("hahaha")
                .zIndex(10)
                .draggable(true);

        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
    }

    //getter-----and------setter--------------------------
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPrjName() {
        return prjName;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }
}
