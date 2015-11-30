package com.xunce.gsmr.lib.cadparser;

import timber.log.Timber;

/**
 * 数学上的一条直线
 * Created by ssthouse on 2015/11/30.
 */
public class MathLine {

    /**
     * 斜率
     */
    private double k;
    /**
     * 偏移量
     */
    private double b;

    /**
     * 根据给定的两个点的坐标___获取一条直线
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static MathLine getMathLine(double x1, double y1, double x2, double y2) {
        //算出k
        double k = (y2 - y1) / (x2 - x1);
        //算出b
        double b = y2 - k * x2;
        //返回一条直线
        return new MathLine(k, b);
    }

    /**
     * 根据给定的基准点___获取垂线段
     *
     * @return
     */
    public MathLine getVerticalLine(double x, double y) {
        double kVertical = -(1.0 / k);
        double bVertical = y - x * kVertical;
        return new MathLine(kVertical, bVertical);
    }

    /**
     * 根据给定的原始点___距离___方向
     *
     * @param baseX
     * @param baseY
     * @param length
     * @param isRight
     * @return
     */
    public double[] getPosition(double baseX, double baseY, double length, boolean isRight) {
        double deltaX;
        if (isRight) {
            deltaX = length / Math.sqrt(1 + Math.pow(k, 2));
        } else {
            deltaX = (-1) * length / Math.sqrt(1 + Math.pow(k, 2));
        }
        double deltaY = deltaX * k;
        double result[] = new double[]{baseX + deltaX, baseY + deltaY};
        Timber.e("横向偏移距离为:\t" + deltaX);
        Timber.e("纵向偏移距离为:\t" + deltaY);
        Timber.e("原始大地坐标为\t" + baseX + "\t" + baseY);
        Timber.e("新的大地坐标为\t" + result[0] + "\t" + result[1]);
        return result;
    }

    /**
     * 构造方法
     *
     * @param k
     * @param b
     */
    public MathLine(double k, double b) {
        this.k = k;
        this.b = b;
    }
}
