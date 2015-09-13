package com.xunce.gsmr.model;

import com.baidu.mapapi.model.LatLng;

/**
 * 定义高德地图和百度地图 共同行为 的接口
 * Created by ssthouse on 2015/9/13.
 */
public interface CustomMap {

    //动画定位到一个点
    void animateZoom(int level);

    //动画聚焦到一个点
    void animateToPoint(LatLng latLng);

    //InfoWindow
    void hideInfoWindow();
    void showInfoWindow(LatLng latLng);

    //获取当前选中点的位置
    LatLng getCurrentMarkerLatLng();

    //获取中心点
    LatLng getTarget();

    //加载铁路
    void loadRail();

    //加载Marker
    void loadMarker();

    //定位
    void locate();

    //生命周期
    void pause();
    void resume();
    void destory();
}
