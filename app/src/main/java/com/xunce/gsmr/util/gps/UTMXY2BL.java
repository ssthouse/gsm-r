package com.xunce.gsmr.util.gps;

import timber.log.Timber;

/**
 * 大地坐标转换为经纬度
 * Created by ssthouse on 2015/11/30.
 */
public class UTMXY2BL {

    /// <summary>
    /// 将WGS的大地坐标转化为经纬度
    /// </summary>
    /// <param name="Xn"></param>
    /// <param name="Yn"></param>
    /// <returns></returns>
    public static double[] UTMWGSXYtoBL(double Xn, double Yn, double L0) {
        //最后输出的数据
        double XYtoBL[] = new double[2];
        Timber.e("我的中央子午线为:\t" + L0);
        //工程中的--------中央经线-----数据
        //TODO---这里是读取文本文件获取中央经线---有可能文本文件的数据格式有问题
        //double L0 = PrjConstant.getCentralLongitude();

        double Mf;
        double Nf;
        double Tf, Bf;
        double Cf;
        double Rf;
        double b1, b2, b3;
        double r1, r2;
        double K0 = 0.9996;
        double D, S;
        double FE = 500000; //东纬偏移
        double FN = 0;
        double a = 6378137;
        double b = 6356752.3142;
        double e1, e2, e3;
        double B;
        double L;

        L0 = L0 * Math.PI / 180; //弧度

        e1 = Math.sqrt(1 - Math.pow((b / a), 2.00));
        e2 = Math.sqrt(Math.pow((a / b), 2.00) - 1);
        e3 = (1 - b / a) / (1 + b / a);

        Mf = (Xn - FN) / K0;
        S = Mf / (a * (1 - Math.pow(e1, 2.00) / 4 - 3 * Math.pow(e1, 4.00) / 64 - 5 * Math.pow(e1, 6.00) / 256));

        b1 = (3 * e3 / 2.00 - 27 * Math.pow(e3, 3.00) / 32.00) * Math.sin(2.00 * S);
        b2 = (21 * Math.pow(e3, 2.00) / 16 - 55 * Math.pow(e3, 4.00) / 32) * Math.sin(4 * S);
        b3 = (151 * Math.pow(e3, 3.00) / 96) * Math.sin(6 * S);
        Bf = S + b1 + b2 + b3;

        Nf = (Math.pow(a, 2.00) / b) / Math.sqrt(1 + Math.pow(e2, 2.00) * Math.pow(Math.cos(Bf), 2.00));
        r1 = a * (1 - Math.pow(e1, 2.00));
        r2 = Math.pow((1 - Math.pow(e1, 2.00) * Math.pow(Math.sin(Bf), 2.00)), 3.0 / 2.0);
        Rf = r1 / r2;
        Tf = Math.pow(Math.tan(Bf), 2.00);
        Cf = Math.pow(e2, 2.00) * Math.pow(Math.cos(Bf), 2.00);
        D = (Yn - FE) / (K0 * Nf);

        b1 = Math.pow(D, 2.00) / 2.0;
        b2 = (5 + 3 * Tf + 10 * Cf - 4 * Math.pow(Cf, 2.0) - 9 * Math.pow(e2, 2.0)) * Math.pow(D, 4.00) / 24;
        b3 = (61 + 90 * Tf + 298 * Cf + 45 * Math.pow(Tf, 2.00) - 252 * Math.pow(e2, 2.0) - 3 * Math.pow(Cf, 2.0)) *
                Math.pow(D, 6.00) / 720;
        B = Bf - Nf * Math.tan(Bf) / Rf * (b1 - b2 + b3);
        B = B * 180 / Math.PI;
        L = (L0 + (1 / Math.cos(Bf)) * (D - (1 + 2 * Tf + Cf) * Math.pow(D, 3) / 6
                + (5 + 28 * Tf - 2 * Cf - 3 * Math.pow(Cf, 2.0) + 8 * Math.pow(e2, 2.0)
                + 24 * Math.pow(Tf, 2.0)) * Math.pow(D, 5.00) / 120)) * 180 / Math.PI;
        L0 = L0 * 180 / Math.PI; //转化为度

        //给结果赋值
        //经度
        XYtoBL[0] = B;
        //纬度
        XYtoBL[1] = L;
        return XYtoBL;
    }
}
