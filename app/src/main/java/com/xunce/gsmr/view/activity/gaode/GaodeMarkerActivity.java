package com.xunce.gsmr.view.activity.gaode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.lib.xmlparser.XmlParser;
import com.xunce.gsmr.lib.digitalmap.DigitalMapHolder;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.event.DrawMapDataEvent;
import com.xunce.gsmr.model.event.MarkerEditEvent;
import com.xunce.gsmr.model.event.MarkerInfoSaveEvent;
import com.xunce.gsmr.model.gaodemap.GaodeMapCons;
import com.xunce.gsmr.util.DBHelper;
import com.xunce.gsmr.util.gps.MarkerHelper;
import com.xunce.gsmr.util.gps.PositionUtil;
import com.xunce.gsmr.util.view.ToastHelper;
import com.xunce.gsmr.util.view.ViewHelper;
import com.xunce.gsmr.view.style.TransparentStyle;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 开启本Activity需要一个MarkerItem
 * 高德选取Marker的Activity
 * Created by ssthouse on 2015/9/15.
 */
public class GaodeMarkerActivity extends GaodeBaseActivity {
    /**
     * 开启本Activity需要的数据
     */
    private MarkerItem markerItem;
    /**
     * 用于判断---是修改还是新增
     */
    private int requestCode;

    /**
     * 经纬度输入框
     */
    private EditText etLatitude, etLongitude;

    private DigitalMapHolder digitalMapHolder;
    private XmlParser xmlParser;

    private boolean isXmlTextShowed = false;
    private boolean isDigitalMapTextShowed = false;


    /**
     * 启动当前Activity
     *
     * @param activity 上下文
     * @param markerItem 编辑的数据
     * @param requestCode 启动code
     */
    public static void start(Activity activity, MarkerItem markerItem,LatLng latLng, int requestCode) {
        //填充intent进去markerItem和requestCode
        Intent intent = new Intent(activity, GaodeMarkerActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_MARKER_ITEM, markerItem);
        intent.putExtra(Constant.EXTRA_KEY_REQUEST_CODE, requestCode);
        //之前在prjEditAc的界面中心坐标
        intent.putExtra(Constant.EXTRA_KEY_LATITUDE, latLng.latitude);
        intent.putExtra(Constant.EXTRA_KEY_LONGITUDE, latLng.longitude);
        //启动Activity
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_gaode_mark);
        TransparentStyle.setTransparentStyle(this, R.color.color_primary);
        super.init(savedInstanceState);

        //获取数据
        MarkerItem wrongItem = (MarkerItem) getIntent()
                .getSerializableExtra(Constant.EXTRA_KEY_MARKER_ITEM);
        markerItem = DBHelper.getMarkerItemInDB(wrongItem);
        requestCode = getIntent().getIntExtra(Constant.EXTRA_KEY_REQUEST_CODE,
                GaodePrjEditActivity.REQUEST_CODE_MARKER_ACTIVITY);

        //如果是编辑---定位到编辑的点
        if (markerItem != null && markerItem.getLatitude() != 0 && markerItem.getLongitude() != 0) {
            super.animateToPoint(markerItem.getGaodeLatLng());
        } else {
            initLocate();
//            animateToMyLocation();
            Intent intent = getIntent();
            LatLng latLng = new LatLng(intent.getDoubleExtra(Constant.EXTRA_KEY_LATITUDE, 0),
                    intent.getDoubleExtra(Constant.EXTRA_KEY_LONGITUDE, 0));
            animateToPoint(latLng);
        }

        //初始化View
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), "选址");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //找到UI控件
        etLatitude = (EditText) findViewById(R.id.id_et_latitude);
        etLongitude = (EditText) findViewById(R.id.id_et_longitude);

        //确认按钮
        findViewById(R.id.id_btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MarkerHelper.isDataValid(etLatitude, etLongitude)) {
                    //保存数据---并改变原来的照片的文件夹的名称
                    double latitude = MarkerHelper.getLatitude(etLatitude);
                    double longitude = MarkerHelper.getLongitude(etLongitude);
                    //double wgsLatlng[] = PositionUtil.gcj_To_Gps84(latitude, longitude);
                    markerItem.changeData(new double[]{latitude, longitude});
                    //返回
                    EventBus.getDefault().post(new MarkerEditEvent(MarkerEditEvent.BackState.CHANGED));
                    //退出
                    finish();
                } else {
                    ToastHelper.showSnack(GaodeMarkerActivity.this, etLatitude, "请选择有效数据");
                }
            }
        });

        //定位按钮
        findViewById(R.id.id_ib_locate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaodeMarkerActivity.super.animateToMyLocation();
            }
        });

        //地图状态变化监听---用于监听选取的Marker位置
        getaMap().setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //抬手时更新输入框经纬度数据
                    LatLng latlng = getaMap().getCameraPosition().target;
                    double wgsLatlng[] = PositionUtil.gcj_To_Gps84(latlng.latitude, latlng.longitude);
                    etLatitude.setText(wgsLatlng[0] + "");
                    etLongitude.setText(wgsLatlng[1] + "");
                }
            }
        });

        //监测---地图的大小变化---画出/隐藏---文字
        getaMap().setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                //数字地图已经加载 且 switch为开
                if (digitalMapHolder != null) {
                    //如果放大到16以上
                    if (cameraPosition.zoom > GaodeMapCons.zoomLevel) {
                        //Timber.e("放大到16以上了");
                        if (!isDigitalMapTextShowed) {
                            digitalMapHolder.drawText(getaMap());
                            isDigitalMapTextShowed = true;
                        }
                    } else if (cameraPosition.zoom < GaodeMapCons.zoomLevel) {
                        //Timber.e("缩小到16以下了");
                        digitalMapHolder.hideText();
                        isDigitalMapTextShowed = false;
                    }
                }
                //如果 xml文件已经加载 且 switch为开
                if (xmlParser != null) {
                    //如果放大到16以上
                    if (cameraPosition.zoom > GaodeMapCons.zoomLevel && !isXmlTextShowed) {
                        xmlParser.drawText(getaMap());
                        isXmlTextShowed = true;
                        Timber.e(">>>> 16了");
                    } else if (cameraPosition.zoom < GaodeMapCons.zoomLevel && isXmlTextShowed) {
                        Timber.e("缩小到16以下了");
                        xmlParser.hideText();
                        isXmlTextShowed = false;
                    }
                }
            }
        });
    }

    /**
     * 画出PrjEditActivity上已有的地图数据
     *
     * @param drawMapDataEvent
     */
    public void onEventMainThread(DrawMapDataEvent drawMapDataEvent) {
        //复制一份holder到当前activity
        if(drawMapDataEvent.getDigitalMapHolder() != null) {
            digitalMapHolder = new DigitalMapHolder();
            digitalMapHolder.setTextList(drawMapDataEvent.getDigitalMapHolder().getTextList());
            digitalMapHolder.setVectorList(drawMapDataEvent.getDigitalMapHolder().getVectorList());
            digitalMapHolder.clearData();
        }
        if(drawMapDataEvent.getXmlParser() != null) {
            xmlParser = new XmlParser();
            xmlParser.setTextList(drawMapDataEvent.getXmlParser().getTextList());
            xmlParser.setLineList(drawMapDataEvent.getXmlParser().getLineList());
            xmlParser.setVectorList(drawMapDataEvent.getXmlParser().getVectorList());
            xmlParser.clearData();
        }
        if(digitalMapHolder != null){
            digitalMapHolder.drawLine(getaMap());
        }
        if(xmlParser != null){
            xmlParser.drawLine(getaMap());
        }
    }

    /**
     * 文本编辑的回调方法
     *
     * @param event
     */
    public void onEventMainThread(MarkerInfoSaveEvent event) {
        if (event != null) {
            //更新文本文件的数据
            markerItem.setDeviceType(event.getMarkerItem().getDeviceType());
            markerItem.setKilometerMark(event.getMarkerItem().getKilometerMark());
            markerItem.setSideDirection(event.getMarkerItem().getSideDirection());
            markerItem.setDistanceToRail(event.getMarkerItem().getDistanceToRail());
            markerItem.setTowerType(event.getMarkerItem().getTowerType());
            markerItem.setTowerHeight(event.getMarkerItem().getTowerHeight());
            markerItem.setAntennaDirection1(event.getMarkerItem().getAntennaDirection1());
            markerItem.setAntennaDirection2(event.getMarkerItem().getAntennaDirection2());
            markerItem.setAntennaDirection3(event.getMarkerItem().getAntennaDirection3());
            markerItem.setAntennaDirection4(event.getMarkerItem().getAntennaDirection4());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_mark_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //编辑文本信息
            case R.id.id_action_edit_info:
                MarkerInfoEditActivity.start(this, markerItem);
                break;
            case android.R.id.home:
                if (requestCode == GaodePrjEditActivity.REQUEST_CODE_MARKER_EDIT_ACTIVITY) {
                    finish();
                    return true;
                }
                markerItem.delete();
                EventBus.getDefault().post(new MarkerEditEvent(MarkerEditEvent.BackState.UNCHANGED));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (requestCode == GaodePrjEditActivity.REQUEST_CODE_MARKER_EDIT_ACTIVITY) {
            finish();
            return;
        }
        //如果直接想返回---需要删除提前在数据库中保存的数据
        markerItem.delete();
        EventBus.getDefault().post(new MarkerEditEvent(MarkerEditEvent.BackState.UNCHANGED));
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
