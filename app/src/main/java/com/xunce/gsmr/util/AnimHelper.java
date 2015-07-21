package com.xunce.gsmr.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.xunce.gsmr.R;

/**
 * 动画工具类
 * Created by ssthouse on 2015/7/19.
 */
public class AnimHelper {

    public static void rotateBigAnim(Context context, View view){
        Animation animation= AnimationUtils.loadAnimation(context, R.anim.rotate_big);
        view.startAnimation(animation);
    }

    public static void rotateSmallAnim(Context context, View view){
        Animation animation= AnimationUtils.loadAnimation(context, R.anim.rotate_small);
        view.startAnimation(animation);
    }
}
