package com.xunce.gsmr.view.activity.baidu;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.CommonParams;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.mapcontrol.MapParams;
import com.baidu.navisdk.comapi.routeguide.RouteGuideParams;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.routeplan.IRouteResultObserver;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;
import com.baidu.navisdk.comapi.setting.SettingParams;
import com.baidu.navisdk.comapi.tts.BNTTSPlayer;
import com.baidu.navisdk.comapi.tts.BNavigatorTTSPlayer;
import com.baidu.navisdk.comapi.tts.IBNTTSPlayerListener;
import com.baidu.navisdk.model.NaviDataEngine;
import com.baidu.navisdk.model.RoutePlanModel;
import com.baidu.navisdk.model.datastruct.RoutePlanNode;
import com.baidu.navisdk.ui.routeguide.BNavConfig;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.util.common.PreferenceHelper;
import com.baidu.navisdk.util.common.ScreenUtil;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.util.FileHelper;
import com.xunce.gsmr.util.L;
import com.xunce.gsmr.util.view.ToastHelper;
import com.xunce.gsmr.view.style.TransparentStyle;

import java.util.ArrayList;

/**
 * 选择路线的Activity---将在选择成功的回调方法中---启动真正的导航界面
 * Created by ssthouse on 2015/7/17.
 */
public class RoutePlanActivity extends Activity {
    private static final String TAG = "RouteActivity";

    private RoutePlanModel mRoutePlanModel = null;

    private MapGLSurfaceView mMapView = null;

    public static int requestCodeStart = 1001;
    public static int requestCodeEnd = 1002;

    private LatLng latLngStart, latLngEnd;

    private EditText etLatitudeStart, etLongitudeStart;
    private EditText etLatitudeEnd, etLongitudeEnd;

    private Button btnGetStartPoint, btnGetEndPOint;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, RoutePlanActivity.class));
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rout_select);
        TransparentStyle.setTransparentStyle(this, R.color.color_primary);

        latLngStart = new LatLng(0, 0);
        latLngEnd = new LatLng(0, 0);
        initNavi(this);
        initView();
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
    }


    private void initView() {

        btnGetStartPoint = (Button) findViewById(R.id.id_btn_choose_start);
        btnGetStartPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaiduGetLatLngActivity.start(RoutePlanActivity.this, requestCodeStart);
            }
        });

        btnGetEndPOint = (Button) findViewById(R.id.id_btn_choose_end);
        btnGetEndPOint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaiduGetLatLngActivity.start(RoutePlanActivity.this, requestCodeEnd);
            }
        });

        etLatitudeStart = (EditText) findViewById(R.id.id_et_begin_latitude);
        etLongitudeStart = (EditText) findViewById(R.id.id_et_begin_longitude);
        etLatitudeEnd = (EditText) findViewById(R.id.id_et_end_latitude);
        etLongitudeEnd = (EditText) findViewById(R.id.id_et_end_longitude);

        //初始化mapView
        initMapView();

        //计算路线
        findViewById(R.id.id_btn_calculate).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startCalcRoute(CommonParams.NL_Net_Mode.NL_Net_Mode_OnLine);
            }
        });

        //开始模拟导航
        findViewById(R.id.id_btn_simulate).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        startNavi(false);
                    }
                });

        //开启真实导航
        findViewById(R.id.id_btn_real_guide).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                PreferenceHelper.getInstance(getApplicationContext())
                        .putBoolean(SettingParams.Key.SP_TRACK_LOCATE_GUIDE,
                                false);
                startNavi(true);
            }
        });
    }

    private void updateEditText(){
        etLatitudeStart.setText(latLngStart.latitude+"");
        etLongitudeStart.setText(latLngStart.longitude+"");
        etLatitudeEnd.setText(latLngEnd.latitude+"");
        etLongitudeEnd.setText(latLngEnd.longitude+"");
    }


    private void initMapView() {
        if (Build.VERSION.SDK_INT < 14) {
            BaiduNaviManager.getInstance().destroyNMapView();
        }

        mMapView = BaiduNaviManager.getInstance().createNMapView(this);
        BNMapController.getInstance().setLevel(14);
        BNMapController.getInstance().setLayerMode(
                MapParams.Const.LayerMode.MAP_LAYER_MODE_BROWSE_MAP);
        updateCompassPosition();
    }

    /**
     * 初始化导航功能
     */
    public static void initNavi(final Activity context) {
        BNaviEngineManager.NaviEngineInitListener naviListener = new BNaviEngineManager.NaviEngineInitListener() {

            @Override
            public void engineInitStart() {
                L.log(TAG, "初始化导航-----");
            }

            @Override
            public void engineInitSuccess() {
                L.log(TAG, "初始化成功");

                BNMapController.getInstance().locateWithAnimation(
                        (int) (113.97348 * 1e5), (int) (22.53951 * 1e5));
            }

            @Override
            public void engineInitFail() {
                L.log(TAG, "初始化失败");
            }
        };

        LBSAuthManagerListener authListener = new LBSAuthManagerListener() {

            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
//                    ToastHelper.showSnack(context, "key校验成功-----");
                } else {
//                    ToastHelper.showSnack(context, "key校验失败-----");
                }
            }
        };

        BaiduNaviManager.getInstance().initEngine(context, FileHelper.getSDPath(),
                naviListener, authListener);
    }

    /**
     * 更新指南针位置
     */
    private void updateCompassPosition() {
        int screenW = this.getResources().getDisplayMetrics().widthPixels;
        BNMapController.getInstance().resetCompassPosition(
                screenW - ScreenUtil.dip2px(this, 30),
                ScreenUtil.dip2px(this, 126), -1);
    }

    private void startCalcRoute(int netmode) {
        //获取输入的起终点
        double sX = 0, sY = 0, eX = 0, eY = 0;
        try {
            sX = latLngStart.latitude;
            sY = latLngStart.longitude;
            eX = latLngEnd.latitude;
            eY = latLngEnd.longitude;
        } catch (Exception e) {
            ToastHelper.showToast(this, "计算失败");
            e.printStackTrace();
        }

        //起点
        RoutePlanNode startNode = new RoutePlanNode((int) (sX * 1e5), (int) (sY * 1e5),
                RoutePlanNode.FROM_MAP_POINT, "", "");
        //终点
        RoutePlanNode endNode = new RoutePlanNode((int) (eX * 1e5), (int) (eY * 1e5),
                RoutePlanNode.FROM_MAP_POINT, "", "");
        //将起终点添加到nodeList
        ArrayList<RoutePlanNode> nodeList = new ArrayList<RoutePlanNode>(2);
        nodeList.add(startNode);
        nodeList.add(endNode);
        BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this, null));
        //设置算路方式
        BNRoutePlaner.getInstance().setCalcMode(RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME);
        // 设置算路结果回调
        BNRoutePlaner.getInstance().setRouteResultObserver(mRouteResultObserver);
        // 设置起终点并算路
        boolean ret = BNRoutePlaner.getInstance().setPointsToCalcRoute(
                nodeList, CommonParams.NL_Net_Mode.NL_Net_Mode_OnLine);
        if (!ret) {
            ToastHelper.showToast(this, "计算成功");
        }
    }

    private void startNavi(boolean isReal) {
        if (mRoutePlanModel == null) {
            Toast.makeText(RoutePlanActivity.this, "请先算路", Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取路线规划结果起点
        RoutePlanNode startNode = mRoutePlanModel.getStartNode();
        // 获取路线规划结果终点
        RoutePlanNode endNode = mRoutePlanModel.getEndNode();
        if (null == startNode || null == endNode) {
            return;
        }
        // 获取路线规划算路模式
        int calcMode = BNRoutePlaner.getInstance().getCalcMode();
        Bundle bundle = new Bundle();
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_VIEW_MODE,
                BNavigator.CONFIG_VIEW_MODE_INFLATE_MAP);
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_DONE,
                BNavigator.CONFIG_CLACROUTE_DONE);
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_X,
                startNode.getLongitudeE6());
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_Y,
                startNode.getLatitudeE6());
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_X, endNode.getLongitudeE6());
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_Y, endNode.getLatitudeE6());
        bundle.putString(BNavConfig.KEY_ROUTEGUIDE_START_NAME,
                mRoutePlanModel.getStartName(this, false));
        bundle.putString(BNavConfig.KEY_ROUTEGUIDE_END_NAME,
                mRoutePlanModel.getEndName(this, false));
        bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_MODE, calcMode);
        if (!isReal) {
            // 模拟导航
            bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE,
                    RouteGuideParams.RGLocationMode.NE_Locate_Mode_RouteDemoGPS);
        } else {
            // GPS 导航
            bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE,
                    RouteGuideParams.RGLocationMode.NE_Locate_Mode_GPS);
        }

        Intent intent = new Intent(RoutePlanActivity.this, NaviActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private IRouteResultObserver mRouteResultObserver = new IRouteResultObserver() {

        @Override
        public void onRoutePlanYawingSuccess() {
            ToastHelper.showSnack(RoutePlanActivity.this, mMapView, "算路成功!");
        }

        @Override
        public void onRoutePlanYawingFail() {
            ToastHelper.showSnack(RoutePlanActivity.this, mMapView, "抱歉,算路失败");
        }

        @Override
        public void onRoutePlanSuccess() {
            // T算路成功---导出算路数据---为开启导航做好准备
            BNMapController.getInstance().setLayerMode(
                    MapParams.Const.LayerMode.MAP_LAYER_MODE_ROUTE_DETAIL);
            mRoutePlanModel = (RoutePlanModel) NaviDataEngine.getInstance()
                    .getModel(CommonParams.Const.ModelName.ROUTE_PLAN);
        }

        @Override
        public void onRoutePlanFail() {
        }

        @Override
        public void onRoutePlanCanceled() {
        }

        @Override
        public void onRoutePlanStart() {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCodeStart && resultCode == RESULT_OK && data != null) {
            latLngStart = new LatLng(data.getDoubleExtra(Constant.EXTRA_KEY_LATITUDE, 0),
                    data.getDoubleExtra(Constant.EXTRA_KEY_LONGITUDE, 0));
        } else if (requestCode == requestCodeEnd) {
            latLngEnd = new LatLng(data.getDoubleExtra(Constant.EXTRA_KEY_LATITUDE, 0),
                    data.getDoubleExtra(Constant.EXTRA_KEY_LONGITUDE, 0));
        }
        updateEditText();
        super.onActivityResult(requestCode, resultCode, data);
    }

    //生命周期***********************************************************
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        BNRoutePlaner.getInstance().setRouteResultObserver(null);
        ((ViewGroup) (findViewById(R.id.id_map_view))).removeAllViews();
        BNMapController.getInstance().onPause();
    }

    @Override
    public void onResume() {
        ((ViewGroup) (findViewById(R.id.id_map_view))).addView(mMapView);
        BNMapController.getInstance().onResume();
        super.onResume();
    }
}
