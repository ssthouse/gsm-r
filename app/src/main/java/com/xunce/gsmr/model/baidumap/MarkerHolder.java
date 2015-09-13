package com.xunce.gsmr.model.baidumap;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.gps.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 持有一个baiduMap的引用
 * 承载PrjEditActivity中的所有Marker
 * Created by ssthouse on 2015/8/21.
 */
public class MarkerHolder {
    private static final String TAG = "MarkerHolder";

    //标记点相关的
    public static BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_blue);
    public static BitmapDescriptor descriptorRed = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_red);

    private Marker currentMarker;
    private List<Marker> markerList;
    private BaiduMap baiduMap;
    private PrjItem prjItem;

    public MarkerHolder(PrjItem prjItem, BaiduMap baiduMap) {
        this.prjItem = prjItem;
        this.baiduMap = baiduMap;
        markerList = new ArrayList<>();

        initMarkerList();
    }

    /**
     * 初始化并且画出Marker
     */
    public void initMarkerList() {
        //清空markerList
        markerList.clear();
        //初始化markerList
        List<MarkerItem> markerItems = DBHelper.getMarkerList(prjItem);
        for (int i = 0; i < markerItems.size(); i++) {
            LatLng latLng = new LatLng(markerItems.get(i).getLatitude(),
                    markerItems.get(i).getLongitude());
            OverlayOptions redOverlay = new MarkerOptions()
                    .position(latLng)
                    .icon(descriptorBlue)
                    .zIndex(16)
                    .draggable(false);
            markerList.add((Marker) baiduMap.addOverlay(redOverlay));
        }
        //TODO---看要不要这个功能
//        if (markerItems.size() != 0) {
//            MapHelper.animateToPoint(baiduMap,
//                    new LatLng(markerList.get(markerList.size() - 1).getPosition().latitude,
//                            markerList.get(markerList.size() - 1).getPosition().latitude));
//            MapHelper.animateZoom(baiduMap, 15);
//        }
    }

    /**
     * 取消当前选中的Marker
     */
    public void clearSelection() {
        currentMarker.setIcon(descriptorBlue);
        //没有当前选中的Marker
        currentMarker = null;
    }

    public void setAll2Blue() {
        for (Marker marker : markerList) {
            marker.setIcon(descriptorBlue);
        }
    }

    public void setAll2Red() {
        for (Marker marker : markerList) {
            marker.setIcon(descriptorRed);
        }
    }

    //getter------------and------------setter-----------------
    public Marker getCurrentMarker() {
        return currentMarker;
    }

    public void setCurrentMarker(Marker currentMarker) {
        this.currentMarker = currentMarker;
    }

    public List<Marker> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<Marker> markerList) {
        this.markerList = markerList;
    }
}
