package com.xunce.gsmr.util;

import com.activeandroid.query.Select;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;

import java.util.List;

/**
 * 数据库处理的工具类(处理app存储的本地数据库)
 * Created by ssthouse on 2015/7/17.
 */
public class DBHelper {
    public static final String DB_NAME = "Location.db";

    /**
     * 判断prjItem是不是空的
     *
     * @param prjItem
     * @return  数据库中PrjItem是不是空的
     */
    public static boolean isPrjEmpty(PrjItem prjItem) {
        List<MarkerItem> markerList = new Select()
                .from(MarkerItem.class)
                .where("prjName = " + "'" + prjItem.getPrjName() + "'")
                .execute();
        return markerList == null || markerList.size() == 0;
    }

    /**
     * 根据MarkerItem生成的时间获取数据库中的MarkerItem
     * 从数据库中获取MarkerItem
     *
     * @param markerItem    序列化的MarkerItem
     * @return  在数据库中保存的MarkerItem
     */
    public static MarkerItem getMarkerItemInDB(MarkerItem markerItem) {
        String prjName = markerItem.getPrjName();
        String photoPathName = markerItem.getPhotoPathName();
        MarkerItem markerItemInDB = new Select()
                .from(MarkerItem.class)
                .where("prjName ="
                        + " '" + prjName + "' and "
                        + "photoPathName ="
                        + " '" + photoPathName + "'")
                .executeSingle();
//        Timber.e("我从数据库中找到的东西是:   ");
//        Timber.e("原来的:   " + markerItem.getPrjName() + markerItem.getPhotoPathName());
//        Timber.e("我找到的是" + (markerItemInDB == null));
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
        return !(prjImteList == null || prjImteList.size() == 0);
    }

    /**
     * 按照创建的Id的顺序获取PrjItm的列表
     */
    public static List<PrjItem> getPrjItemList() {
        return new Select()
                .from(PrjItem.class)
                .orderBy("Id ASC")
                .execute();
    }

    /**
     * 获取一个PrjItem所有的Marker点
     *
     * @param prjItem
     * @return
     */
    public static List<MarkerItem> getMarkerList(PrjItem prjItem) {
        return new Select().from(MarkerItem.class)
                .where("prjName = " + "'" + prjItem.getPrjName() + "'")
                .execute();
    }
}
