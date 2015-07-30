package com.xunce.gsmr.util.gps;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于对Map进行操作的工具类
 * Created by ssthouse on 2015/7/19.
 */
public class MapHelper {

    //直线的参数
    private static int lineColor =  0xAAFF0000;
    private static int lineWidth = 10;
    //圆圈的参数
    private static Stroke circleStroke = new Stroke(5, 0xAA000000);
    //文字的参数
    private static int textColor = 0xFFFF00FF;
    private static int textBgColor = 0x00FF00FF;
    private static int textSize = 24;

    /**
     * 动画放大
     * @param baiduMap
     * @param zoomLevel
     */
    public static void animateZoom(BaiduMap baiduMap, int zoomLevel){
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
        baiduMap.animateMapStatus(u);
    }

    /**
     * 在地图中画出直线----point的数据是经纬度!!!
     * @param baiduMap
     * @param latlngBegin
     * @param latlngEnd
     */
    public static void drawLine(BaiduMap baiduMap, LatLng latlngBegin, LatLng latlngEnd){
        // 添加折线
        List<LatLng> points = new ArrayList<LatLng>();
        points.add(latlngBegin);
        points.add(latlngEnd);
        OverlayOptions ooPolyline = new PolylineOptions()
                .width(lineWidth)
                .color(lineColor)
                .points(points);
        baiduMap.addOverlay(ooPolyline);
    }

    /**
     * 画出圆
     * @param baiduMap
     * @param latlngCenter
     * @param radius
     */
    public static void drawCircle(BaiduMap baiduMap, LatLng latlngCenter, int radius){
        // 添加圆
        OverlayOptions ooCircle = new CircleOptions()
                .fillColor(0x000000FF)
                .center(latlngCenter)
                .stroke(circleStroke)
                .radius(radius);
        baiduMap.addOverlay(ooCircle);
    }

    /**
     * 画出文字
     * @param baiduMap
     * @param text
     * @param latLng
     * @param rotate
     */
    public static void drawText(BaiduMap baiduMap, String text, LatLng latLng, int rotate){
        // 添加文字
        OverlayOptions ooText = new TextOptions()
                .bgColor(textBgColor)
                .fontSize(textSize)
                .fontColor(textColor)
                .text(text)
                .rotate(-rotate)
                .position(latLng);
        baiduMap.addOverlay(ooText);
    }

    /**
     * TODO---在各种地图上加载铁路地图
     * 加载铁路地图
     *
     * @return
     */
    public static boolean loadMap(Context context, BaiduMap baiduMap, PrjItem prjItem) {
        //TODO---这里直接将制定路径的文件读取出来就好
        DBHelper.openExternalDbFile(context);
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

    //标记点相关的
    private static BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_blue);

    /**
     * 动画聚焦到一个点
     * @param baiduMap
     * @param latLng
     */
    public static void animateToPoint(BaiduMap baiduMap, LatLng latLng) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(u);
    }
}
