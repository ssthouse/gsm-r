package com.xunce.gsmr.lib.cadparser;

import com.xunce.gsmr.util.gps.LonLatToUTMXY;
import com.xunce.gsmr.util.gps.UTMXY2BL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import timber.log.Timber;

/**
 * 保存一个xml文件中所有的公里标
 * Created by ssthouse on 2015/11/27.
 */
public class KilometerMarkHolder {

    /**
     * 所有的公里标
     */
    private ArrayList<KilometerMark> kilometerMarkList = new ArrayList<>();

    /**
     * Excel中的字符串常量
     */
    interface ExcelCons {
        String sideDirectionLeft = "下行左侧";
        String sideDirectionRight = "下行右侧";
    }

    /**
     * 判断给定的数据是否可以算出经纬度
     *
     * @param text
     * @param sideDirection
     * @param distanceToRail
     * @return 数据是否正确
     */
    public boolean isDataValid(String text, String sideDirection, double distanceToRail) {
        //判断是否有一样的text
        boolean hasSameText = false;
        for (KilometerMark kilometerMark : kilometerMarkList) {
            if (kilometerMark.getText().equals(text)) {
                hasSameText = true;
            }
        }
        if (!hasSameText) {
            return false;
        }
        //判断sideDirection是否正确
        if (!sideDirection.equals(ExcelCons.sideDirectionLeft) &&
                !sideDirection.equals(ExcelCons.sideDirectionRight)) {
            return false;
        }
        //判断distanceToRail是否正确
        if (distanceToRail <= 0.0) {
            return false;
        }
        return true;
    }

    /**
     * TODO
     * 获取给定数据点的经纬度
     * 调用该方法前___需要先检测数据是否正确
     * 根据:
     * 公里标(str)
     * 方向(str)
     * 距离(str)
     * 首先需要判断:
     * 数据是否正确
     *
     * @return
     */
    public double[] getPosition(String text, String sideDirection, double distanceToRail) {
        //获取应该偏移的方向
        boolean isRight = true;
        switch (sideDirection) {
            case ExcelCons.sideDirectionLeft:
                isRight = false;
                break;
            case ExcelCons.sideDirectionRight:
                isRight = true;
                break;
        }
        //TODO--计算经纬度的核心算法
        //首先找到对应的公里标(因为前面有判断----所以肯定是有数据的)
        KilometerMark currentMark = null;
        for (KilometerMark kilometerMark : kilometerMarkList) {
            if (kilometerMark.getText().equals(text)) {
                currentMark = kilometerMark;
            }
        }
        //TODO---根据找到的MarkerItem找前或后的MarkerItem
        //获取临近的一个公里标点
        KilometerMark nextMarker;
        int position = kilometerMarkList.indexOf(currentMark);
        if (position <= 0) {
            nextMarker = kilometerMarkList.get(position + 1);
        } else if (position >= kilometerMarkList.size() - 1) {
            nextMarker = kilometerMarkList.get(position - 1);
            //如果找到的点在自己前面---翻转方向
            isRight = !isRight;
        } else {
            nextMarker = kilometerMarkList.get(position + 1);
        }
        //计算当前点和前面点的横坐标差值___看是不是需要翻转方向
        if ((currentMark.getLongitude() - nextMarker.getLatitude()) < 0.0) {
            isRight = !isRight;
        }
        //算出直线
        Timber.e("当前点的经纬度是:\t" + currentMark.getLatitude() + "\t" + currentMark.getLongitude());
        double xy1[] = LonLatToUTMXY.LatLonToUTM(currentMark.getLatitude(), currentMark.getLongitude());
        double xy2[] = LonLatToUTMXY.LatLonToUTM(nextMarker.getLatitude(), nextMarker.getLongitude());

        Timber.e("当前点的大地坐标是:\t" + xy1[0] + "\t" + xy1[1]);
        MathLine markerLine = MathLine.getMathLine(xy1[0], xy1[1], xy2[0], xy2[1]);
        //获取垂直的线___根据找到的当前公里标
        MathLine verticalLine = markerLine.getVerticalLine(xy1[0], xy1[1]);
        //根据给定的距离当前点的距离___获取选定点的___大地坐标
        double xyPosition[] = verticalLine.getPosition(xy1[0], xy1[1],distanceToRail, isRight);
        double lonLat[] = UTMXY2BL.UTMWGSXYtoBL(xyPosition[1], xyPosition[0], xy1[2] * 6 - 180 - 3);
        //打印找到的点
        Timber.e("我找到的匹配的点是:\t" + currentMark.toString());
        Timber.e("我最终得到的大地坐标;\t" + xyPosition[0] + "\t" + xyPosition[1]);
        Timber.e("我最终得到的经纬度为;\t" + lonLat[0] + "\t" + lonLat[1]);
        return lonLat;
    }

    /**
     * 添加KilometerMark
     */
    public void addKilometerMark(KilometerMark kilometerMark) {
        if (kilometerMark != null) {
            Timber.e("我添加了一个点：\t" + kilometerMark.toString());
            kilometerMarkList.add(kilometerMark);
        }
    }

    /**
     * 将公里标List中的数据按照名称排序
     */
    public void sort() {
        Collections.sort(kilometerMarkList, new Comparator<KilometerMark>() {
            @Override
            public int compare(KilometerMark lhs, KilometerMark rhs) {
                //将文字进行比较
                return lhs.getText().compareTo(rhs.getText());
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (KilometerMark kilometer : kilometerMarkList) {
            sb.append(kilometer.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
