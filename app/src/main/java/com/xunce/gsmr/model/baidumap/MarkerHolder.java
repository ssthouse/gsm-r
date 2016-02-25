package com.xunce.gsmr.model.baidumap;

import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 持有一个baiduMap的引用
 * 承载PrjEditActivity中的所有Marker
 * Created by ssthouse on 2015/8/21.
 */
public class MarkerHolder {
    //标记点相关的
    public static BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marker_blue);
    public static BitmapDescriptor descriptorRed = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marker_red);

    /**
     * Marker相关
     */
    //当前选中的marker和markerItem
    private Marker currentMarker;
    private MarkerItem currentMarkerItem;
    //marker和markerItem的list
    private List<Marker> markerList;
    private List<MarkerItem> markerItemList;

    /**
     * 地图和数据源头
     */
    private BaiduMap baiduMap;
    private PrjItem prjItem;

    /**
     * 构造方法
     *
     * @param prjItem
     * @param baiduMap
     */
    public MarkerHolder(PrjItem prjItem, BaiduMap baiduMap) {
        this.prjItem = prjItem;
        this.baiduMap = baiduMap;
        markerList = new ArrayList<>();
        markerItemList = new ArrayList<>();

        initMarkerList();
    }

    /**
     * 初始化并且画出Marker
     */
    public void initMarkerList() {
        baiduMap.clear();
        //清空markerList
        markerList.clear();
        markerItemList.clear();
        //初始化markerList
        SQLiteDatabase db = SQLiteDatabase.openDatabase(prjItem.getDbLocation(),null,
                SQLiteDatabase.OPEN_READWRITE);
        markerItemList = DBHelper.getMarkerList(db);
        db.close();
        for (int i = 0; i < markerItemList.size(); i++) {
            LatLng latLng = markerItemList.get(i).getBaiduLatLng();
            OverlayOptions redOverlay = new MarkerOptions()
                    .position(latLng)
                    .icon(descriptorBlue)
                    .zIndex(16)
                    .draggable(false);
            markerList.add((Marker) baiduMap.addOverlay(redOverlay));
            Timber.e("我添加了一个点:    " + latLng.latitude + ":" + latLng.longitude);
        }
        if(markerItemList.size() >0) {
            //动画移动过去
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(
                    new LatLng(markerItemList.get(0).getLatitude(), markerItemList.get(0).getLongitude()));
            baiduMap.animateMapStatus(u);
        }
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
        this.currentMarkerItem = markerItemList.get(markerList.indexOf(currentMarker));
    }

    public MarkerItem getCurrentMarkerItem() {
        return currentMarkerItem;
    }

    public List<MarkerItem> getMarkerItemList() {
        return markerItemList;
    }

    public List<Marker> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<Marker> markerList) {
        this.markerList = markerList;
    }
}
