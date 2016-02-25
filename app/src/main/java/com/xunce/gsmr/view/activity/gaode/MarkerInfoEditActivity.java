package com.xunce.gsmr.view.activity.gaode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.event.MarkerInfoSaveEvent;
import com.xunce.gsmr.util.InputTools;
import com.xunce.gsmr.util.view.ToastHelper;
import com.xunce.gsmr.util.view.ViewHelper;

import de.greenrobot.event.EventBus;

/**
 * Marker文本信息编辑
 * Created by ssthouse on 2015/12/2.
 */
public class MarkerInfoEditActivity extends AppCompatActivity {

    private EditText etDeviceType;
    EditText etDistanceToRail;
    EditText etTowerHeight;

    /**
     * 当前编辑的数据
     */
    private MarkerItem markerItem;

    /**
     * 启动当前Activity需要一个markerItem
     *
     * @param context
     * @param markerItem
     */
    public static void start(Context context, MarkerItem markerItem) {
        Intent intent = new Intent(context, MarkerInfoEditActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_MARKER_ITEM, markerItem);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info_edit);

        //获取intent数据
        markerItem = (MarkerItem) getIntent().getSerializableExtra(Constant.EXTRA_KEY_MARKER_ITEM);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InputTools.hideKeyboard(etDeviceType);

    }

    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), "编辑标记点信息");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO---编辑事件---更新数据
        //设备类型
        etDeviceType = (EditText) findViewById(R.id.id_et_device_type);
        etDeviceType.setText(markerItem.getDeviceType());
        etDeviceType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                markerItem.setDeviceType(s.toString());
            }
        });

        //公里标
        EditText etKilometerMarker = (EditText) findViewById(R.id.id_et_kilometer_mark);
        etKilometerMarker.setText(markerItem.getKilometerMark());
        etKilometerMarker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                markerItem.setKilometerMark(s.toString());
            }
        });

        //下行侧向
        Spinner spSideDirection = (Spinner) findViewById(R.id.id_sp_side_direction);
        switch (markerItem.getSideDirection()){
            case MarkerItem.MarkerItemCons.sideDirectionLeft:
                spSideDirection.setSelection(0);
                break;
            case MarkerItem.MarkerItemCons.sideDirectionRight:
                spSideDirection.setSelection(1);
                break;
        }
        spSideDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        markerItem.setSideDirection(MarkerItem.MarkerItemCons.sideDirectionLeft);
                        break;
                    case 1:
                        markerItem.setSideDirection(MarkerItem.MarkerItemCons.sideDirectionRight);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //距离线路中心距离
        etDistanceToRail = (EditText) findViewById(R.id.id_et_distance_to_rail);
        etDistanceToRail.setText(markerItem.getDistanceToRail()+"");
        etDistanceToRail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                double distanceToRail = 0.0;
                try {
                    distanceToRail = Double.parseDouble(s.toString());
                } catch (Exception e) {
                    ToastHelper.show(MarkerInfoEditActivity.this, "请输入有效数据到: 距离线路中心距离");
                    //还原原来的文字
                    etDistanceToRail.setText("" + markerItem.getDistanceToRail());
                    return;
                }
                markerItem.setDistanceToRail(distanceToRail);
            }
        });

        //杆塔类型
        Spinner spTowerType = (Spinner) findViewById(R.id.id_sp_tower_type);
        if(markerItem.getTowerType() !=null) {
            switch (markerItem.getTowerType()) {
                case MarkerItem.MarkerItemCons.towerTypePole:
                    spTowerType.setSelection(0);
                    break;
                case MarkerItem.MarkerItemCons.towerTypeSingleTower:
                    spTowerType.setSelection(1);
                    break;
                case MarkerItem.MarkerItemCons.towerTypeFourTower:
                    spTowerType.setSelection(2);
                    break;
            }
        }else {spTowerType.setSelection(0);}
        spTowerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        markerItem.setTowerType(MarkerItem.MarkerItemCons.towerTypePole);
                        break;
                    case 1:
                        markerItem.setTowerType(MarkerItem.MarkerItemCons.towerTypeSingleTower);
                        break;
                    case 2:
                        markerItem.setTowerType(MarkerItem.MarkerItemCons.towerTypeFourTower);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //杆塔高度
        etTowerHeight = (EditText) findViewById(R.id.id_et_tower_height);
        etTowerHeight.setText(markerItem.getTowerHeight());
        etTowerHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //杆塔高度后续不用进行计算--直接赋为字符串就好
                markerItem.setTowerHeight(s.toString());
            }
        });

        //天线方向角1
        EditText etAntenna1 = (EditText) findViewById(R.id.id_et_antenna_direction_1);
        etAntenna1.setText(markerItem.getAntennaDirection1());
        etAntenna1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                markerItem.setAntennaDirection1(s.toString());
            }
        });

        //天线方向角2
        EditText etAntenna2 = (EditText) findViewById(R.id.id_et_antenna_direction_2);
        etAntenna2.setText(markerItem.getAntennaDirection2());
        etAntenna2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                markerItem.setAntennaDirection2(s.toString());
            }
        });

        //天线方向角3
        EditText etAntenna3 = (EditText) findViewById(R.id.id_et_antenna_direction_3);
        etAntenna3.setText(markerItem.getAntennaDirection3());
        etAntenna3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                markerItem.setAntennaDirection3(s.toString());
            }
        });

        //天线方向角4
        EditText etAntenna4 = (EditText) findViewById(R.id.id_et_antenna_direction_4);
        etAntenna4.setText(markerItem.getAntennaDirection4());
        etAntenna4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                markerItem.setAntennaDirection4(s.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_marker_info_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            case R.id.id_action_save_change:
                //发送event保存修改
                EventBus.getDefault().post(new MarkerInfoSaveEvent(markerItem));
                //退出
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
