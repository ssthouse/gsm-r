package com.xunce.gsmr.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.tts.BNTTSPlayer;
import com.baidu.navisdk.comapi.tts.BNavigatorTTSPlayer;
import com.baidu.navisdk.comapi.tts.IBNTTSPlayerListener;
import com.baidu.navisdk.model.datastruct.LocData;
import com.baidu.navisdk.model.datastruct.SensorData;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.routeguide.IBNavigatorListener;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;
import com.xunce.gsmr.util.FileHelper;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.ToastHelper;


/**
 * 百度自带----导航Activity
 * Created by ssthouse on 2015/7/17.
 */
public class NaviActivity extends Activity {
    private static final String TAG = "NaviActivity";

    public static void start(Context context){
        context.startActivity(new Intent(context, NaviActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initNavi(this);

        MapGLSurfaceView nMapView = BaiduNaviManager.getInstance().createNMapView(this);

        //创建导航视图
        View navigatorView = BNavigator.getInstance().init(NaviActivity.this,
                getIntent().getExtras(), nMapView);

        //填充视图---这个视图都是导航视图
        setContentView(navigatorView);

        //设置监听器
        BNavigator.getInstance().setListener(mBNavigatorListener);

        //开始导航
        BNavigator.getInstance().startNav();

        // 初始化TTS. 开发者也可以使用独立TTS模块，不用使用导航SDK提供的TTS
        BNTTSPlayer.initPlayer();

        //设置TTS播放回调
        BNavigatorTTSPlayer.setTTSPlayerListener(new IBNTTSPlayerListener() {

            @Override
            public int playTTSText(String arg0, int arg1) {
                //开发者可以使用其他TTS的API
                return BNTTSPlayer.playTTSText(arg0, arg1);
            }

            @Override
            public void phoneHangUp() {
                //手机挂断
            }

            @Override
            public void phoneCalling() {
                //通话中
            }

            @Override
            public int getTTSState() {
                //开发者可以使用其他TTS的API,
                return BNTTSPlayer.getTTSState();
            }
        });

        BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this, new RoutePlanObserver.IJumpToDownloadListener() {

            @Override
            public void onJumpToDownloadOfflineData() {
                // TODO Auto-generated method stub
            }
        }));
    }

    /**
     * 初始化导航功能
     */
    public static  void initNavi(final Activity context) {
        BNaviEngineManager.NaviEngineInitListener naviListener = new BNaviEngineManager.NaviEngineInitListener() {

            @Override
            public void engineInitStart() {
                LogHelper.Log(TAG, "初始化导航-----");
            }

            @Override
            public void engineInitSuccess() {
                LogHelper.Log(TAG, "初始化成功");

                BNMapController.getInstance().locateWithAnimation(
                        (int) (113.97348 * 1e5), (int) (22.53951 * 1e5));
            }

            @Override
            public void engineInitFail() {
                LogHelper.Log(TAG, "初始化失败");
            }
        };

        LBSAuthManagerListener authListener = new LBSAuthManagerListener() {

            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    ToastHelper.show(context, "key校验成功-----");
                } else {
                    ToastHelper.show(context, "key校验失败-----");
                }
            }
        };

        BaiduNaviManager.getInstance().initEngine(context, FileHelper.getSDPath(),
                naviListener, authListener);
    }

    private IBNavigatorListener mBNavigatorListener = new IBNavigatorListener() {

        @Override
        public void onYawingRequestSuccess() {
            // TODO 偏航请求成功
        }

        @Override
        public void onYawingRequestStart() {
            // TODO 开始偏航请求
        }

        @Override
        public void onPageJump(int jumpTiming, Object arg) {
            // TODO 页面跳转回调
            if(IBNavigatorListener.PAGE_JUMP_WHEN_GUIDE_END == jumpTiming){
                finish();
            }else if(IBNavigatorListener.PAGE_JUMP_WHEN_ROUTE_PLAN_FAIL == jumpTiming){
                finish();
            }
        }

        @Override
        public void notifyGPSStatusData(int arg0) {
        }

        @Override
        public void notifyLoacteData(LocData arg0) {
        }

        @Override
        public void notifyNmeaData(String arg0) {
        }

        @Override
        public void notifySensorData(SensorData arg0) {
        }

        @Override
        public void notifyStartNav() {
            BaiduNaviManager.getInstance().dismissWaitProgressDialog();
        }

        @Override
        public void notifyViewModeChanged(int arg0) {
        }
    };

    @Override
    public void onResume() {
        BNavigator.getInstance().resume();
        super.onResume();
        BNMapController.getInstance().onResume();
    };

    @Override
    public void onPause() {
        BNavigator.getInstance().pause();
        super.onPause();
        BNMapController.getInstance().onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        BNavigator.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    public void onBackPressed(){
        BNavigator.getInstance().onBackPressed();
    }

    @Override
    public void onDestroy(){
        BNavigator.destory();
        BNRoutePlaner.getInstance().setObserver(null);
        super.onDestroy();
    }
}
