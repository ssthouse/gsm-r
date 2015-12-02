package com.xunce.gsmr.view.activity.baidu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.xunce.gsmr.R;
import com.xunce.gsmr.util.view.ToastHelper;

import java.util.ArrayList;

/**
 * 管理离线地图的Activity
 */
public class BaiduOfflineActivity extends Activity implements MKOfflineMapListener {
    private static final String TAG = "OffLineActivity";

    //离线地图管理器
    private MKOfflineMap mOffline = null;

    private Button btnDownloadList;
    private Button btnCityList;

    /**
     * 当前搜索的City的list
     */
    private ArrayList<MKOLSearchRecord> currentCityList = null;
    private CurrentCityAdapter currentCityAdapter = null;

    /**
     * 已下载的离线地图信息列表
     */
    private ArrayList<MKOLUpdateElement> localMapList = null;
    private LocalMapAdapter localMapAdapter = null;

    public static void start(Context context) {
        context.startActivity(new Intent(context, BaiduOfflineActivity.class));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_offline);

        //初始化一个离线地图管理器
        mOffline = new MKOfflineMap();
        mOffline.init(this);

        initView();
    }

    private void initView() {
        //界面切换按钮
        btnDownloadList = (Button) findViewById(R.id.id_btn_download_list);
        btnDownloadList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
                LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
                lm.setVisibility(View.VISIBLE);
                cl.setVisibility(View.GONE);
                //改变按钮颜色
                btnDownloadList.setTextColor(getResources().getColor(R.color.white));
                btnDownloadList.setBackgroundColor(getResources().getColor(R.color.color_primary));
                btnCityList.setTextColor(getResources().getColor(R.color.color_primary));
                btnCityList.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
        btnCityList = (Button) findViewById(R.id.id_btn_city_list);
        btnCityList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
                LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
                lm.setVisibility(View.GONE);
                cl.setVisibility(View.VISIBLE);
                //改变按钮颜色
                btnDownloadList.setTextColor(getResources().getColor(R.color.color_primary));
                btnDownloadList.setBackgroundColor(getResources().getColor(R.color.white));
                btnCityList.setTextColor(getResources().getColor(R.color.white));
                btnCityList.setBackgroundColor(getResources().getColor(R.color.color_primary));
            }
        });

        //初始化将city查询列表设置为所有的可以离线的列表
        ListView allCityList = (ListView) findViewById(R.id.lv_all_city);
        // 获取所有支持离线地图的城市
        ArrayList<String> allCities = new ArrayList<>();
        currentCityList = mOffline.getOfflineCityList();
        for (MKOLSearchRecord r : currentCityList) {
            allCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
                    + this.formatDataSize(r.size));
        }
        currentCityAdapter = new CurrentCityAdapter();
        allCityList.setAdapter(currentCityAdapter);

        //城市搜索框的EditText监听事件
        EditText etSearchCity = (EditText) findViewById(R.id.id_et_city);
        etSearchCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null) {
                    return;
                }
                if (TextUtils.isEmpty(s)) {
                    currentCityList = mOffline.getOfflineCityList();
                    return;
                }
                //一旦文字发生变化----更新下面的listView中的数据--
                if (mOffline.searchCity(s.toString()) == null) {
                    currentCityList.clear();
                } else {
                    currentCityList = mOffline.searchCity(s.toString());
                }
                currentCityAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
        LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
        lm.setVisibility(View.GONE);
        cl.setVisibility(View.VISIBLE);

        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<>();
        }

        //本地已经下载的地图文件
        ListView localMapListView = (ListView) findViewById(R.id.lv_local_maps);
        localMapAdapter = new LocalMapAdapter();
        localMapListView.setAdapter(localMapAdapter);
    }


    /**
     * 开始下载
     *
     * @param mkolSearchRecord
     */
    private void startDownload(MKOLSearchRecord mkolSearchRecord) {
        if (mkolSearchRecord == null) {
            return;
        }
        mOffline.start(mkolSearchRecord.cityID);
        btnDownloadList.performClick();
        updateView();
        ToastHelper.showSnack(this, btnDownloadList, mkolSearchRecord.cityName + "开始下载");
    }

    /**
     * 从SD卡导入离线地图安装包
     *
     * @param view
     */
    public void importFromSDCard(View view) {
        int num = mOffline.importOfflineData();
        String msg;
        if (num == 0) {
            msg = "没有导入离线包，这可能是离线包放置位置不正确，或离线包已经导入过";
        } else {
            msg = String.format("成功导入 %d 个离线包，可以在下载管理查看", num);
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        updateView();
    }

    /**
     * 更新状态显示
     */
    public void updateView() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<>();
        }
        localMapAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    @Override
    protected void onDestroy() {
        /**
         * 退出时，销毁离线地图模块
         */
        mOffline.destroy();
        super.onDestroy();
    }

    /**
     * 更新下载状态
     *
     * @param type
     * @param state
     */
    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {
                    updateView();
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("BaiduOfflineActivity", String.format("add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);
                break;
        }
    }

    /**
     * 离线地图管理列表适配器
     */
    public class LocalMapAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localMapList.size();
        }

        @Override
        public Object getItem(int index) {
            return localMapList.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            MKOLUpdateElement updateElement = (MKOLUpdateElement) getItem(index);
            view = View.inflate(BaiduOfflineActivity.this,
                    R.layout.view_lv_offline_localmap, null);
            initViewItem(view, updateElement);
            return view;
        }

        void initViewItem(View view, final MKOLUpdateElement e) {
            Button display = (Button) view.findViewById(R.id.display);
            Button remove = (Button) view.findViewById(R.id.remove);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView update = (TextView) view.findViewById(R.id.update);
            TextView ratio = (TextView) view.findViewById(R.id.ratio);
            ratio.setText(e.ratio + "%");
            title.setText(e.cityName);
            if (e.update) {
                update.setText("可更新");
            } else {
                update.setText("最新");
            }
            if (e.ratio != 100) {
                display.setEnabled(false);
            } else {
                display.setEnabled(true);
            }
            remove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mOffline.remove(e.cityID);
                    updateView();
                }
            });
//            display.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.putExtra("y", e.geoPt.latitude);
//                    intent.putExtra("x", e.geoPt.longitude);
//                    intent.setClass(BaiduOfflineActivity.this, BaseMapDemo.class);
//                    startActivity(intent);
//                }
//            });
        }
    }

    //当前搜索的City的Adapter
    public class CurrentCityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return currentCityList.size();
        }

        @Override
        public Object getItem(int position) {
            return currentCityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(BaiduOfflineActivity.this, R.layout.view_lv_item_current_city, null);
                viewHolder = new ViewHolder();
                viewHolder.tv = (TextView) convertView.findViewById(R.id.id_tv);
                viewHolder.btn = (Button) convertView.findViewById(R.id.id_btn_down_load);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //填充数据
            MKOLSearchRecord r = currentCityList.get(position);
            viewHolder.tv.setText(r.cityName + "(" + r.cityID + ")" + "   --"
                    + formatDataSize(r.size));
            viewHolder.btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击下载
                    startDownload(currentCityList.get(position));
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView tv;
            Button btn;
        }
    }
}