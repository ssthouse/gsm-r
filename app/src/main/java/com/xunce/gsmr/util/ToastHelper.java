package com.xunce.gsmr.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.xunce.gsmr.R;

/**
 * Toast工具类
 * Created by ssthouse on 2015/7/15.
 */
public class ToastHelper {

    public static void showSnack(Context context, View view, String toastStr) {
        Snackbar snackbar = Snackbar.make(view, toastStr, Snackbar.LENGTH_SHORT);
        snackbar.getView().getLayoutParams().height = (int) context.getResources()
                .getDimension(R.dimen.snack_bar_height);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.color_primary));
        TextView tv = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        tv.setTextColor(context.getResources().getColor(R.color.white));
        tv.setTextSize(16);
        snackbar.show();
    }


    public static void show(Context context, BDLocation bdLocation) {
        Toast.makeText(context, bdLocation.getLongitude() + " : " + bdLocation.getLatitude(),
                Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
