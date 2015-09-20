package com.xunce.gsmr.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.app.Constant;

import java.io.Serializable;

/**
 * 单个的Marker对象---一个
 * Created by ssthouse on 2015/7/17.
 */
@Table(name = Constant.TABLE_MARKER_ITEM)
public class MarkerItem extends Model implements Serializable {
    private static final String TAG = "MarkerItem";

    @Column(name = "prjName")
    private String prjName;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "photoPathName")
    private String photoPathName;

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
        //根据当前时间创建路径名称
        this.photoPathName = System.currentTimeMillis() + "";
    }

    /**
     * 百度LatLng的构造方法
     *
     * @param latLng
     */
    public MarkerItem(String prjName, LatLng latLng) {
        super();
        this.prjName = prjName;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        //根据当前时间创建路径名称
        this.photoPathName = System.currentTimeMillis() + "";
    }

    /**
     * 高德地图LatLng的构造方法
     *
     * @param latLng
     */
    public MarkerItem(String prjName, com.amap.api.maps.model.LatLng latLng) {
        super();
        this.prjName = prjName;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        //根据当前时间创建路径名称
        this.photoPathName = System.currentTimeMillis() + "";
    }

    public MarkerItem() {
        super();
    }

    /**
     * 获取百度LatLng
     *
     * @return
     */
    public LatLng getBaiduLatLng() {
        return new LatLng(latitude, longitude);
    }

    /**
     * 获取高德LatLng
     *
     * @return
     */
    public com.amap.api.maps.model.LatLng getGaodeLatLng() {
        return new com.amap.api.maps.model.LatLng(latitude, longitude);
    }

    /**
     * 获取改点的照片路径
     *
     * @return
     */
    public String getFilePath() {
        return Constant.PICTURE_PATH + prjName + "/" + photoPathName + "/";
    }

    /**
     * 改变MarkerItem的经纬度
     *
     * @param latLng
     */
    public void changeName(com.amap.api.maps.model.LatLng latLng) {
        //先改变文件路径
//        File file = new File(Constant.PICTURE_PATH + this.getPrjName() + "/" +
//                +this.getLatitude() + "_" + this.getLongitude());
//        file.renameTo(new File(Constant.PICTURE_PATH + this.getPrjName() + "/" +
//                +latLng.latitude + "_" + latLng.longitude));
        //修改数据
        this.setLatitude(latLng.latitude);
        this.setLongitude(latLng.longitude);
        //保存数据
        this.save();
    }

    /**
     * 改变MarkerItem的经纬度
     *
     * @param latLng
     */
    public void changeName(LatLng latLng) {
        //先改变文件路径
//        File file = new File(Constant.PICTURE_PATH + this.getPrjName() + "/" +
//                +this.getLatitude() + "_" + this.getLongitude());
//        file.renameTo(new File(Constant.PICTURE_PATH + this.getPrjName() + "/" +
//                +latLng.latitude + "_" + latLng.longitude));
        //修改数据
        this.setLatitude(latLng.latitude);
        this.setLongitude(latLng.longitude);
        //保存数据
        this.save();
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

    public String getPhotoPathName() {
        return photoPathName;
    }

    public void setPhotoPathName(String photoPathName) {
        this.photoPathName = photoPathName;
    }
}
