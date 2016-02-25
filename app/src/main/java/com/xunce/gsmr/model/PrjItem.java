package com.xunce.gsmr.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.util.DBHelper;
import com.xunce.gsmr.util.preference.PreferenceHelper;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * 单个工程Item
 * Created by ssthouse on 2015/7/18.
 */
@Table(name = Constant.TABLE_PRJ_ITEM)
public class PrjItem extends Model implements Serializable{
    private static final String TAG = "PrjItem";

    @Column(name = "prjName")
    private String prjName;

    @Column(name = "dbLocation")
    private String dbLocation;

    public PrjItem(String prjName) {
        super();
        this.prjName = prjName;
    }

    public PrjItem(String prjName, String dbLocation) {
        this.prjName = prjName;
        this.dbLocation = dbLocation;
    }

    public PrjItem() {
        super();
        prjName = "";
    }

//    public List<MarkerItem> getMarkerItemList(){
//        return new Select().from(MarkerItem.class)
//                .where("prjName = "+ "'"+prjName+"'")
//                .execute();
//    }


    /**
     * 删除一个PrjItem的所有数据
     */
    public void deletePrj(Context context){
        //首先要判断Preference中保存的是不是当前工程
        //如果是要删除Preference
        if(PreferenceHelper.getInstance(context).getLastEditPrjName(context).equals(getPrjName())){
            PreferenceHelper.getInstance(context).deleteLastEditPrjName(context);
        }
//        //删除照片文件
//        String path = Constant.PICTURE_PATH + this.getId();
//        File file = new File(path);
//        if (file.exists()) {
//            file.delete();
//        }
//        //删除数据库文件
//        List<MarkerItem> markerItemList = this.getMarkerItemList();
//        if (markerItemList != null) {
//            for (MarkerItem item : markerItemList) {
//                item.delete();
//            }
//        }
//        //删除外部的DB文件
//        File delefile = new File(dbLocation);
//        delefile.delete();

        this.delete();
    }

    /**
     * 改变工程名
     * @param context
     * @param newName
     */
    public void changeName(Context context, String newName){
        //首先要判断Preference中保存的是不是当前工程
        //如果是要修改Preference
        if(PreferenceHelper.getInstance(context).getLastEditPrjName(context).equals(getPrjName())){
            PreferenceHelper.getInstance(context).setLastEditPrjName(context, newName);
        }
//        //修改照片文件名称
//        String path = Constant.PICTURE_PATH + this.getId();
//        File file = new File(path);
//        if (file.exists()) {
//            file.renameTo(new File(Constant.PICTURE_PATH + newName));
//        }
//        //修改数据库文件
//        List<MarkerItem> markerItemList = this.getMarkerItemList();
//        if (markerItemList != null) {
//            for (MarkerItem item : markerItemList) {
//                item.setId(newName);
//                item.save();
//            }
//        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbLocation,null,SQLiteDatabase
                .OPEN_READWRITE);
        db.execSQL("update Projectinfo set prjName = "+newName+" WHERE prjName = " + prjName);
        this.setPrjName(newName);
        this.save();
    }

    //getter-----------and---------------setter---------
    public String getPrjName() {
        return prjName;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }

    public String getDbLocation() {
        return dbLocation;
    }

    public void setDbLocation(String dbLocation) {
        this.dbLocation = dbLocation;
    }
}
