package com.xunce.gsmr.model;

/**
 * Created by ssthouse on 2015/7/16.
 */
public class WordItem {

    private String item1;

    private String item2;

    public WordItem(String item1, String item2) {
        this.item1 = item1;
        this.item2 = item2;
    }


    //getter-----and-----setter------------------------------------

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }
}
