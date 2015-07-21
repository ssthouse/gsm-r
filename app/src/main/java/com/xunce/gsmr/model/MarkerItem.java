package com.xunce.gsmr.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.Constant;

import java.io.Serializable;
import java.util.List;

/**
 * 单个的Marker对象---一个
 * Created by ssthouse on 2015/7/17.
 */
@Table(name = Constant.TABLE_MARKER_ITEM)
public class MarkerItem extends Model implements Serializable{

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
     * @param prjItem
     */
    public MarkerItem(com.xunce.gsmr.model.PrjItem prjItem){
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

    public List<com.xunce.gsmr.model.PhotoItem> getPhotpItemList() {
        List<com.xunce.gsmr.model.PhotoItem> photoItemList = new Select().from(com.xunce.gsmr.model.PhotoItem.class)
                .where("latitude = " +
                        "'" + latitude + "'" +
                        " AND longitude = " +
                        "'" + longitude + "'"
                        + " AND prjName = " +
                        "'" + prjName + "'")
                .execute();
        return photoItemList;
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
