package com.xunce.gsmr.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.Constant;

/**
 * 一个PhotoItem的数据
 * Created by ssthouse on 2015/7/17.
 */
@Table(name = Constant.TABLE_PHOTO_ITEM)
public class PhotoItem extends Model {

    @Column(name = "prjName")
    private String prjName;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "path")
    private String path;

    public PhotoItem(){
        super();
    }

    public PhotoItem(String prjName, double latitude, double longitude, String path) {
        super();
        this.prjName = prjName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.path = path;
    }

    public PhotoItem(String prjName, LatLng latLng, String path) {
        super();
        this.prjName = prjName;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        this.path = path;
    }

    //getter-------------and---------------setter-----------
    public String getPrjName() {
        return prjName;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
