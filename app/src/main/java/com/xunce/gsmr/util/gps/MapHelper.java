package com.xunce.gsmr.util.gps;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;

import java.util.List;

/**
 * 用于对Map进行操作的工具类
 * Created by ssthouse on 2015/7/19.
 */
public class MapHelper {

    //标记点相关的
    private static BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_blue);

    public static void animateToPoint(BaiduMap baiduMap, LatLng latLng) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(u);
    }

    /**
     * TODO--在各种地图上加载铁路地图
     * 加载铁路地图
     *
     * @return
     */
    public static boolean loadMap(BaiduMap baiduMap, PrjItem prjItem) {

        return true;
    }

    /**
     * 加载Marker
     * @param baiduMap
     * @param prjItem
     * @return
     */
    public static boolean loadMarker(BaiduMap baiduMap, PrjItem prjItem) {
        if (prjItem == null || baiduMap == null) {
            return false;
        }
        if (DBHelper.isPrjEmpty(prjItem)) {
            return false;
        }
        baiduMap.clear();
        List<MarkerItem> markerList = DBHelper.getMarkerList(prjItem);
        //加载marker
        for (int i = 0; i < markerList.size(); i++) {
            OverlayOptions redOverlay = new MarkerOptions()
                    .position(new LatLng(markerList.get(i).getLatitude(),
                            markerList.get(i).getLongitude()))
                    .icon(descriptorBlue)
                    .zIndex(9).draggable(true);
            baiduMap.addOverlay(redOverlay);
        }
        animateToPoint(baiduMap,
                new LatLng(markerList.get(0).getLatitude(),
                        markerList.get(0).getLongitude()));
        return true;
    }
}
