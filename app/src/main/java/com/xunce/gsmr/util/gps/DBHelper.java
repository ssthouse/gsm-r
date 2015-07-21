package com.xunce.gsmr.util.gps;

import com.activeandroid.query.Select;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;

import java.util.List;

/**
 * 数据库处理的工具类
 * Created by ssthouse on 2015/7/17.
 */
public class DBHelper {

    /**
     * 判断prjItem是不是空的
     * @param prjItem
     * @return
     */
    public static boolean isPrjEmpty(PrjItem prjItem) {
        List<MarkerItem> markerList = new Select()
                .from(MarkerItem.class)
                .where("prjName = " + "'" + prjItem.getPrjName() + "'")
                .execute();
        if (markerList == null || markerList.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 从数据库中获取MarkerItem
     *
     * @param markerItem
     * @return
     */
    public static MarkerItem getMarkerItemInDB(MarkerItem markerItem) {
        String prjName = markerItem.getPrjName();
        double latitude = markerItem.getLatitude();
        double longitude = markerItem.getLongitude();
        MarkerItem markerItemInDB = new Select()
                .from(MarkerItem.class)
                .where("prjName ="
                        + " '" + prjName + "' and "
                        + "latitude ="
                        + " '" + latitude + "' and "
                        + "longitude ="
                        + " '" + longitude + "'").executeSingle();
        return markerItemInDB;
    }

    /**
     * 判断工程是否在---数据库---中已存在
     *
     * @param prjName
     * @return
     */
    public static boolean isPrjExist(String prjName) {
        List<PrjItem> prjImteList = new Select()
                .from(PrjItem.class)
                .where("prjName = " + "'" + prjName + "'")
                .execute();
        if (prjImteList == null || prjImteList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 按照创建的Id的顺序获取PrjItm的列表
     */
    public static List<PrjItem> getPrjItemList() {
        List<PrjItem> prjItemList = new Select()
                .from(PrjItem.class)
                .orderBy("Id ASC")
                .execute();
        return prjItemList;
    }

    /**
     * 获取一个PrjItem所有的Marker点
     * @param prjItem
     * @return
     */
    public static List<MarkerItem> getMarkerList(PrjItem prjItem){
        return new Select().from(MarkerItem.class)
                .where("prjName = "+"'"+prjItem.getPrjName()+"'")
                .execute();
    }
}
