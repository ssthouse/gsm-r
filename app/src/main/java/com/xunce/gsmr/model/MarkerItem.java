package com.xunce.gsmr.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.util.gps.PositionUtil;

import java.io.Serializable;

/**
 * 单个的Marker对象---一个
 * 坐标系是:    GCJ
 * Created by ssthouse on 2015/7/17.
 */
@Table(name = Constant.TABLE_MARKER_ITEM)
public class MarkerItem extends Model implements Serializable {
    private static final String TAG = "MarkerItem";

    /**
     * 工程名
     */
    @Column(name = "prjName")
    private String prjName;
    /**
     * 纬度
     */
    @Column(name = "latitude")
    private double latitude;
    /**
     * 经度
     */
    @Column(name = "longitude")
    private double longitude;
    /**
     * 照片路径___文件名
     */
    @Column(name = "photoPathName")
    private String photoPathName;
    /**
     * 设备类型
     */
    @Column(name = "device_type")
    private String deviceType;
    /**
     * 公里标
     */
    @Column(name = "kilometer_mark")
    private String kilometerMark;
    /**
     * 侧向
     */
    @Column(name = "side_direction")
    private String sideDirection;

    /**
     * 传入经纬度的构造方法
     *
     * @param latitude  wgs纬度
     * @param longitude wgs经度
     */
    public MarkerItem(String prjName, double latitude, double longitude) {
        super();
        this.prjName = prjName;
        this.latitude = latitude;
        this.longitude = longitude;
        //根据当前时间创建路径名称
        this.photoPathName = System.currentTimeMillis() + "";
    }

    /**
     * 使用prjItem的构造方法
     *
     * @param prjItem 根据PrjItem创建的MarkerItem
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
     * @param latLng 百度地图的LatLng
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
     * @param latLng 高德地图LatLng
     */
    public MarkerItem(String prjName, com.amap.api.maps.model.LatLng latLng) {
        super();
        this.prjName = prjName;
        //转换坐标
        double wgsLatlng[] = PositionUtil.gcj_To_Gps84(latLng.latitude, latLng.longitude);
        this.latitude = wgsLatlng[0];
        this.longitude = wgsLatlng[1];
        //根据当前时间创建路径名称
        this.photoPathName = System.currentTimeMillis() + "";
    }

    /**
     * 无参构造方法
     */
    public MarkerItem() {
        super();
        this.latitude = 0;
        this.longitude = 0;
        //根据当前时间创建路径名称
        this.photoPathName = System.currentTimeMillis() + "";
    }

    /**
     * 获取百度LatLng
     * 将国测局坐标转换为百度坐标
     *
     * @return 百度地图LatLng
     */
    public LatLng getBaiduLatLng() {
        // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标
        converter.coord(new LatLng(latitude, longitude));
        return converter.convert();
    }

    /**
     * 获取高德LatLng
     *
     * @return 高德地图LatLng
     */
    public com.amap.api.maps.model.LatLng getGaodeLatLng() {
        //将数据库中WGS的数据转换为---gcj的数据
        return PositionUtil.gps84_To_Gcj02(latitude, longitude);
    }

    /**
     * 获取改点的照片路径
     *
     * @return 当前Marker的照片存放路径
     */
    public String getFilePath() {
        return Constant.PICTURE_PATH + prjName + "/" + photoPathName + "/";
    }

    /**
     * 改变MarkerItem的经纬度
     *
     * @param latLng 传入高德地图的数据
     */
    public void changeData(com.amap.api.maps.model.LatLng latLng) {
        //改变坐标
        double wgsLatlng[] = PositionUtil.gcj_To_Gps84(latLng.latitude, latLng.longitude);
        this.latitude = wgsLatlng[0];
        this.longitude = wgsLatlng[1];
        //保存数据
        this.save();
    }

    /**
     * 传入wgs的数据并保存
     *
     * @param wgsLatlng 第一个数据是latitude---第二个数据是longitude
     */
    public void changeData(double[] wgsLatlng) {
        this.latitude = wgsLatlng[0];
        this.longitude = wgsLatlng[1];
        this.save();
    }

    //getter-----and------setter------------------------------------------
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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getKilometerMark() {
        return kilometerMark;
    }

    public void setKilometerMark(String kilometerMark) {
        this.kilometerMark = kilometerMark;
    }

    public String getSideDirection() {
        return sideDirection;
    }

    public void setSideDirection(String sideDirection) {
        this.sideDirection = sideDirection;
    }
}
