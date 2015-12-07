package com.xunce.gsmr.util.gps;

/**
 * 经纬度转大地坐标
 * Created by ssthouse on 2015/11/30.
 */
public class LonLatToUTMXY {

    static double pi = Math.PI;

    static double sm_a = 6378137.0;
    static double sm_b = 6356752.314;
    //static double sm_EccSquared = 6.69437999013e-03;

    static double UTMScaleFactor = 0.9996;

    //得到的结果是：x坐标，y坐标，区域编号
    public static double[] latLonToUTM(double lat, double lon) {
        double zone = Math.floor((lon + 180.0) / 6) + 1;

        double cm = UTMCentralMeridian(zone);

        double[] xy = MapLatLonToXY(lat / 180.0 * pi, lon / 180 * pi, cm);

        /* Adjust easting and northing for UTM system. */
        xy[0] = xy[0] * UTMScaleFactor + 500000.0;
        xy[1] = xy[1] * UTMScaleFactor;
        if (xy[1] < 0.0) {
            xy[1] = xy[1] + 10000000.0;
        }

        return new double[]{xy[0], xy[1], zone};
    }

    public static double UTMCentralMeridian(double zone) {
        double cmeridian;

        double deg = -183.0 + (zone * 6.0);

        cmeridian = deg / 180.0 * pi;

        return cmeridian;
    }

    static double[] MapLatLonToXY(double phi, double lambda, double lambda0) {
        double N, nu2, ep2, t, t2, l;
        double l3coef, l4coef, l5coef, l6coef, l7coef, l8coef;
        double tmp;

        /* Precalculate ep2 */
        ep2 = (Math.pow(sm_a, 2.0) - Math.pow(sm_b, 2.0)) / Math.pow(sm_b, 2.0);

        /* Precalculate nu2 */
        nu2 = ep2 * Math.pow(Math.cos(phi), 2.0);

        /* Precalculate N */
        N = Math.pow(sm_a, 2.0) / (sm_b * Math.sqrt(1 + nu2));

        /* Precalculate t */
        t = Math.tan(phi);
        t2 = t * t;
        tmp = (t2 * t2 * t2) - Math.pow(t, 6.0);

        /* Precalculate l */
        l = lambda - lambda0;

        /* Precalculate coefficients for l**n in the equations below
           so a normal human being can read the expressions for easting
           and northing
           -- l**1 and l**2 have coefficients of 1.0 */
        l3coef = 1.0 - t2 + nu2;

        l4coef = 5.0 - t2 + 9 * nu2 + 4.0 * (nu2 * nu2);

        l5coef = 5.0 - 18.0 * t2 + (t2 * t2) + 14.0 * nu2
                - 58.0 * t2 * nu2;

        l6coef = 61.0 - 58.0 * t2 + (t2 * t2) + 270.0 * nu2
                - 330.0 * t2 * nu2;

        l7coef = 61.0 - 479.0 * t2 + 179.0 * (t2 * t2) - (t2 * t2 * t2);

        l8coef = 1385.0 - 3111.0 * t2 + 543.0 * (t2 * t2) - (t2 * t2 * t2);

        /* Calculate easting (x) */

        double[] xy = new double[2];
        xy[0] = N * Math.cos(phi) * l
                + (N / 6.0 * Math.pow(Math.cos(phi), 3.0) * l3coef * Math.pow(l, 3.0))
                + (N / 120.0 * Math.pow(Math.cos(phi), 5.0) * l5coef * Math.pow(l, 5.0))
                + (N / 5040.0 * Math.pow(Math.cos(phi), 7.0) * l7coef * Math.pow(l, 7.0));

        /* Calculate northing (y) */
        xy[1] = ArcLengthOfMeridian(phi)
                + (t / 2.0 * N * Math.pow(Math.cos(phi), 2.0) * Math.pow(l, 2.0))
                + (t / 24.0 * N * Math.pow(Math.cos(phi), 4.0) * l4coef * Math.pow(l, 4.0))
                + (t / 720.0 * N * Math.pow(Math.cos(phi), 6.0) * l6coef * Math.pow(l, 6.0))
                + (t / 40320.0 * N * Math.pow(Math.cos(phi), 8.0) * l8coef * Math.pow(l, 8.0));

        return xy;
    }

    static double ArcLengthOfMeridian(double phi) {
        double alpha, beta, gamma, delta, epsilon, n;
        double result;

        /* Precalculate n */
        n = (sm_a - sm_b) / (sm_a + sm_b);

        /* Precalculate alpha */
        alpha = ((sm_a + sm_b) / 2.0)
                * (1.0 + (Math.pow(n, 2.0) / 4.0) + (Math.pow(n, 4.0) / 64.0));

        /* Precalculate beta */
        beta = (-3.0 * n / 2.0) + (9.0 * Math.pow(n, 3.0) / 16.0)
                + (-3.0 * Math.pow(n, 5.0) / 32.0);

        /* Precalculate gamma */
        gamma = (15.0 * Math.pow(n, 2.0) / 16.0)
                + (-15.0 * Math.pow(n, 4.0) / 32.0);

        /* Precalculate delta */
        delta = (-35.0 * Math.pow(n, 3.0) / 48.0)
                + (105.0 * Math.pow(n, 5.0) / 256.0);

        /* Precalculate epsilon */
        epsilon = (315.0 * Math.pow(n, 4.0) / 512.0);

        /* Now calculate the sum of the series and return */
        result = alpha
                * (phi + (beta * Math.sin(2.0 * phi))
                + (gamma * Math.sin(4.0 * phi))
                + (delta * Math.sin(6.0 * phi))
                + (epsilon * Math.sin(8.0 * phi)));

        return result;
    }
}
