package com.xunce.gsmr.util.gps;

import android.widget.EditText;

import com.xunce.gsmr.util.LogHelper;

/**
 * 地图标注---还有选址Util
 * Created by ssthouse on 2015/7/16.
 */
public class MarkerHelper {
    private static final String TAG = "MarkerHelper";

    /**
     * 判断两个输入框中的数据是否有效
     *
     * @param etLatitude
     * @param etLongitude
     * @return
     */
    public static boolean isDataValid(EditText etLatitude, EditText etLongitude) {
        try {
            String latitudeStr = etLatitude.getText().toString();
            String longitudeStr = etLongitude.getText().toString();
            if (latitudeStr == null
                    || longitudeStr == null
                    || latitudeStr.length() == 0
                    || longitudeStr.length() == 0) {
                return false;
            }
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);
            if (longitude >= 360
                    || longitude <= 0
                    || latitude <= -90
                    || latitude >= 90) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LogHelper.log(TAG, e.toString());
            return true;
        }
    }

    public static double getLatitude(EditText etLatitude) {
        return Double.parseDouble(etLatitude.getText().toString());
    }

    public static double getLongitude(EditText etLongitude) {
        return Double.parseDouble(etLongitude.getText().toString());
    }
}
