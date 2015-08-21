package com.xunce.gsmr.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * 震动工具类
 * Created by ssthouse on 2015/8/21.
 */
public class VibrateHelper {


    public static void vibrate(Context context, long time){
        if(time > 1000){
            return;
        }
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    public static void shortVibrate(Context context){
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }
}
