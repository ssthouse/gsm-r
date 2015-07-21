package com.xunce.gsmr.util;

import android.content.Context;
import android.widget.Toast;

import com.baidu.location.BDLocation;

/**
 * Created by ssthouse on 2015/7/15.
 */
public class ToastHelper {

    public static void show(Context context, String toastStr){
        Toast.makeText(context, toastStr, Toast.LENGTH_SHORT).show();
    }



    public static void show(Context context, BDLocation bdLocation){
        Toast.makeText(context, bdLocation.getLongitude()+" : "+bdLocation.getLatitude(),
                Toast.LENGTH_SHORT).show();
    }
}
