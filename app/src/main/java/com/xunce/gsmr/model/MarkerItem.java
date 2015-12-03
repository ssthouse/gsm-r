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
 * 单个的Marker对象---15个Field
 * 坐标系是:    GCJ
 * Created by ssthouse on 2015/7/17.
 */
@Table(name = Constant.TABLE_MARKER_ITEM)
public class MarkerItem extends Model implements Serializable {

    /**
     * 保存数据常量
     */
    public interface MarkerItemCons{
        //字段名(Column)
        String column_prjname = "prjName";                      //工程名
        String column_latitude = "latitude";                    //纬度
        String column_longitude = "longitude";                  //经度
        String column_photo_path_name = "photo_path_name";      //照片路径的文件名
        String column_device_type = "device_type";              //设备类型
        String column_kilometer_mark = "kilometer_mark";        //公里标
        String column_side_direction = "side_direction";        //侧向
        String column_distance_to_rail = "distance_to_rail";    //距线路中心距离
        String column_comment = "comment";                      //备注文本
        String column_tower_type = "tower_type";                //杆塔类型
        String column_tower_height = "tower_height";            //杆塔高度
        String column_antenna_direction_1 = "antenna_direction_1";//天线方向角1
        String column_antenna_direction_2 = "antenna_direction_2";//天线方向角2
        String column_antenna_direction_3 = "antenna_direction_3";//天线方向角3
        String column_antenna_direction_4 = "antenna_direction_4";//天线方向角4

        //下行方向的选项
        String sideDirectionUndefine = "未定义";
        String sideDirectionLeft = "下行左侧";
        String sideDirectionRight = "下行右侧";

        //杆塔类型的选项
        String towerTypeUndefine = "未定义";
        String towerTypePole = "杆";         //杆
        String towerTypeSingleTower = "独管塔";          //独管塔
        String towerTypeFourTower = "四管塔"; //四管塔
    }

    /**
     * 工程名
     */
    @Column(name = MarkerItemCons.column_prjname)
    private String prjName;
    /**
     * 纬度
     */
    @Column(name = MarkerItemCons.column_latitude)
    private double latitude;
    /**
     * 经度
     */
    @Column(name = MarkerItemCons.column_longitude)
    private double longitude;
    /**
     * 照片路径的文件名
     */
    @Column(name = MarkerItemCons.column_photo_path_name)
    private String photoPathName;
    /**
     * 设备类型
     */
    @Column(name = MarkerItemCons.column_device_type)
    private String deviceType;
    /**
     * 公里标
     */
    @Column(name = MarkerItemCons.column_kilometer_mark)
    private String kilometerMark;
    /**
     * 侧向
     */
    @Column(name = MarkerItemCons.column_side_direction)
    private String sideDirection;
    /**
     * 距线路中心距离
     */
    @Column(name = MarkerItemCons.column_distance_to_rail)
    private double distanceToRail;
    /**
     * 备注文本
     */
    @Column(name = MarkerItemCons.column_comment)
    private String comment;
    /**
     * 杆塔类型
     */
    @Column(name = MarkerItemCons.column_tower_type)
    private String towerType;
    /**
     * 杆塔高度
     */
    @Column(name = MarkerItemCons.column_tower_height)
    private String towerHeight;
    /**
     * 天线方向角(1, 2, 3, 4)
     */
    @Column(name = MarkerItemCons.column_antenna_direction_1)
    private String antennaDirection1;
    @Column(name = MarkerItemCons.column_antenna_direction_2)
    private String antennaDirection2;
    @Column(name = MarkerItemCons.column_antenna_direction_3)
    private String antennaDirection3;
    @Column(name = MarkerItemCons.column_antenna_direction_4)
    private String antennaDirection4;



    /**
     * 初始化除了---latitude---longitude---photoPathName外的数据
     */
    private void initFields(){
        deviceType = "";
        kilometerMark = "";
        sideDirection = MarkerItemCons.sideDirectionUndefine;
        distanceToRail = 0.0;
        comment = "";
        towerType = MarkerItemCons.towerTypeUndefine;
        towerHeight = "";
        antennaDirection1 = "";
        antennaDirection2 = "";
        antennaDirection3 = "";
        antennaDirection4 = "";
    }
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
        //初始化Fields
        initFields();
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
        //初始化Fields
        initFields();
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
        //初始化Fields
        initFields();
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

    public double getDistanceToRail() {
        return distanceToRail;
    }

    public void setDistanceToRail(double distanceToRail) {
        this.distanceToRail = distanceToRail;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTowerType() {
        return towerType;
    }

    public void setTowerType(String towerType) {
        this.towerType = towerType;
    }

    public String getTowerHeight() {
        return towerHeight;
    }

    public void setTowerHeight(String towerHeight) {
        this.towerHeight = towerHeight;
    }

    public String getAntennaDirection1() {
        return antennaDirection1;
    }

    public void setAntennaDirection1(String antennaDirection1) {
        this.antennaDirection1 = antennaDirection1;
    }

    public String getAntennaDirection2() {
        return antennaDirection2;
    }

    public void setAntennaDirection2(String antennaDirection2) {
        this.antennaDirection2 = antennaDirection2;
    }

    public String getAntennaDirection3() {
        return antennaDirection3;
    }

    public void setAntennaDirection3(String antennaDirection3) {
        this.antennaDirection3 = antennaDirection3;
    }

    public String getAntennaDirection4() {
        return antennaDirection4;
    }

    public void setAntennaDirection4(String antennaDirection4) {
        this.antennaDirection4 = antennaDirection4;
    }
}
