package com.xunce.gsmr.lib.cadparser;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 保存一个xml文件中所有的公里标
 * Created by ssthouse on 2015/11/27.
 */
public class KilometerMarkHolder {

    /**
     * 所有的公里标
     */
    private List<KilometerMark> kilometerMarkList = new ArrayList<>();

    /**
     * Excel中的字符串常量
     */
    interface ExcelCons{
        String sideDirectionLeft = "下行左侧";
        String sideDirectionRight = "下行右侧";
    }

    /**
     * 判断给定的数据是否可以算出经纬度
     * @param text
     * @param sideDirection
     * @param distanceToRail
     * @return 数据是否正确
     */
    public boolean isDataValid(String text, String sideDirection, String distanceToRail){
        //判断是否有一样的text
        boolean hasSameText = false;
        for(KilometerMark kilometerMark : kilometerMarkList){
            if(kilometerMark.getText().equals(text));
            hasSameText = true;
        }
        if(!hasSameText){
            return false;
        }
        //判断sideDirection是否正确
        if(!sideDirection.equals(ExcelCons.sideDirectionLeft) &&
                !sideDirection.equals(ExcelCons.sideDirectionRight)){
            return false;
        }
        //判断distanceToRail是否正确
        double distance;
        try {
            distance = Double.parseDouble(distanceToRail);
        }catch (Exception e){
            return false;
        }
        if(distance <= 0.0){
            return false;
        }
        return true;
    }

    /**
     * TODO
     * 获取给定数据点的经纬度
     * 根据:
     * 公里标(str)
     * 方向(str)
     * 距离(str)
     * 首先需要判断:
     * 数据是否正确
     *
     * @return
     */
    public double[] getPosition(String text, String sideDirection, String distanceToRail) {
        //判断给的数据是不是对的
        double[] result = new double[2];
        //TODO--计算经纬度的核心算法
        //首先找到对应的公里标(因为前面有判断----所以肯定是有数据的)
        KilometerMark currentMark;
        for(KilometerMark kilometerMark : kilometerMarkList){
            if(kilometerMark.getText().equals(text)){
                currentMark = kilometerMark;
            }
        }
        //

        return result;
    }

    /**
     * 添加KilometerMark
     */
    public void addKilometerMark(KilometerMark kilometerMark) {
        if (kilometerMark != null) {
            kilometerMarkList.add(kilometerMark);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (KilometerMark kilometer : kilometerMarkList) {
            sb.append(kilometer.toString());
            sb.append("\n");
            Timber.e("我添加了一个公里标str");
        }
        return sb.toString();
    }
}
