package com.xunce.gsmr.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.xunce.gsmr.Constant;

import java.io.Serializable;
import java.util.List;

/**
 * 单个工程Item
 * Created by ssthouse on 2015/7/18.
 */
@Table(name = Constant.TABLE_PRJ_ITEM)
public class PrjItem extends Model implements Serializable{

    @Column(name = "prjName")
    private String prjName;

    public PrjItem(String prjName) {
        super();
        this.prjName = prjName;
    }

    public PrjItem() {
        super();
        prjName = "";
    }

    public List<MarkerItem> getMarkerItemList(){
        return new Select().from(MarkerItem.class)
                .where("prjName = "+ "'"+prjName+"'")
                .execute();
    }

    //getter-----------and---------------setter---------
    public String getPrjName() {
        return prjName;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }
}
