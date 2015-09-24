package com.xunce.gsmr.view.activity.gaode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.xunce.gsmr.R;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.view.adapter.GaodeDownloadedCityAdapter;
import com.xunce.gsmr.view.style.TransparentStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 高德离线地图Activity
 * Created by ssthouse on 2015/9/17.
 */
public class GaodeOfflineActivity extends AppCompatActivity implements
        OfflineMapManager.OfflineMapDownloadListener {
    private static final String TAG = "GaodeOfflineActivity";

    /**
     * viewpager相关
     */
    private Button btnDownloadList;
    private Button btnCityList;
    //viewpager承放器
    private List<View> viewList;
    private View leftView, rightView;

    /**
     * 离线地图下载器
     */
    private OfflineMapManager amapManager;
    // 保存一级目录的省直辖市
    private List<OfflineMapProvince> provinceList = new ArrayList<>();
    // 保存二级目录的市
    private HashMap<Object, List<OfflineMapCity>> cityMap = new HashMap<>();
    private int groupPosition = -1;// 记录一级目录的position
    private int childPosition = -1;// 记录二级目录的position
    private boolean isStart = false;// 判断是否开始下载,true表示开始下载，false表示下载失败
    private boolean[] isOpen;// 记录一级目录是否打开

    /**
     * 已经下载好了的地图
     */
    private GaodeDownloadedCityAdapter downloadedCityAdapter;

    /**
     * 启动当前Activity
     *
     * @param activity
     */
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, GaodeOfflineActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaode_offline);
        TransparentStyle.setTransparentStyle(this, R.color.color_primary);

        //初始化VIew
        initView();
    }

    /**
     * 处理界面更新的Handler
     */
    private final static int UPDATE_LIST = 0;
    private final static int DISMISS_INIT_DIALOG = 1;
    private final static int SHOW_INIT_DIALOG = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LIST:
                    ((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
                    break;
                case DISMISS_INIT_DIALOG:
                    handler.sendEmptyMessage(UPDATE_LIST);
                    break;
                case SHOW_INIT_DIALOG:
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化VIew
     */
    private void initView() {
        //填充view
        viewList = new ArrayList<>();
        leftView = View.inflate(this, R.layout.activity_gaode_offline_left, null);
        rightView = View.inflate(this, R.layout.activity_gaode_offline_right, null);
        viewList.add(leftView);
        viewList.add(rightView);

        //填充viewpager
        final ViewPager viewPager = (ViewPager) findViewById(R.id.id_view_pager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });

        //初始化滑动切换事件
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    //改变按钮颜色
                   chooseLeftPage();
                } else {
                    //改变按钮颜色
                    chooseRightPage();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //界面切换按钮
        btnDownloadList = (Button) findViewById(R.id.id_btn_download_list);
        btnDownloadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0, true);
                //改变按钮颜色
                chooseLeftPage();
            }
        });
        btnCityList = (Button) findViewById(R.id.id_btn_city_list);
        btnCityList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1, true);
                //改变按钮颜色
                chooseRightPage();
            }
        });

        //初始化管理器
        amapManager = new OfflineMapManager(this, this);
        provinceList = amapManager.getOfflineMapProvinceList();

        //填充左右view
        initLeftView();
        initRightView();
    }

    /**
     * 选中左边的Pager
     */
    private void chooseLeftPage(){
        //改变按钮颜色
        btnDownloadList.setTextColor(getResources().getColor(R.color.color_primary));
        btnDownloadList.setBackgroundColor(getResources().getColor(R.color.white));
        btnCityList.setTextColor(getResources().getColor(R.color.white));
        btnCityList.setBackgroundColor(getResources().getColor(R.color.color_primary));
    }

    /**
     * 选中右边的Pager
     */
    private void chooseRightPage(){
        //改变按钮颜色
        btnDownloadList.setTextColor(getResources().getColor(R.color.white));
        btnDownloadList.setBackgroundColor(getResources().getColor(R.color.color_primary));
        btnCityList.setTextColor(getResources().getColor(R.color.color_primary));
        btnCityList.setBackgroundColor(getResources().getColor(R.color.white));
    }

    /**
     * 初始化左边视图
     */
    private void initLeftView() {
        //初始化列表
        ExpandableListView expandableListView = (ExpandableListView)
                leftView.findViewById(R.id.id_expandable_view);
        downloadedCityAdapter = new GaodeDownloadedCityAdapter(this, amapManager);
        expandableListView.setAdapter(downloadedCityAdapter);
    }

    /**
     * 初始化右边视图
     */
    private void initRightView() {
        //初始化列表数据
        List<OfflineMapProvince> bigCityList = new ArrayList<>();// 以省格式保存直辖市、港澳、全国概要图
        List<OfflineMapCity> cityList = new ArrayList<>();// 以市格式保存直辖市、港澳、全国概要图
        List<OfflineMapCity> gangaoList = new ArrayList<>();// 保存港澳城市
        List<OfflineMapCity> gaiyaotuList = new ArrayList<>();// 保存概要图
        for (int i = 0; i < provinceList.size(); i++) {
            OfflineMapProvince offlineMapProvince = provinceList.get(i);
            List<OfflineMapCity> city = new ArrayList<>();
            OfflineMapCity aMapCity = getCity(offlineMapProvince);
            if (offlineMapProvince.getCityList().size() != 1) {
                city.add(aMapCity);
                city.addAll(offlineMapProvince.getCityList());
            } else {
                cityList.add(aMapCity);
                bigCityList.add(offlineMapProvince);
            }
            cityMap.put(i + 3, city);
        }
        //加入三个title
        OfflineMapProvince title = new OfflineMapProvince();
        title.setProvinceName("概要图");
        provinceList.add(0, title);
        title = new OfflineMapProvince();
        title.setProvinceName("直辖市");
        provinceList.add(1, title);
        title = new OfflineMapProvince();
        title.setProvinceName("港澳");
        provinceList.add(2, title);
        provinceList.removeAll(bigCityList);

        //对列表进行一些细节的处理
        for (OfflineMapProvince aMapProvince : bigCityList) {
            if (aMapProvince.getProvinceName().contains("香港")
                    || aMapProvince.getProvinceName().contains("澳门")) {
                gangaoList.add(getCity(aMapProvince));
            } else if (aMapProvince.getProvinceName().contains("全国概要图")) {
                gaiyaotuList.add(getCity(aMapProvince));
            }
        }
        try {
            cityList.remove(4);// 从List集合体中删除香港
            cityList.remove(4);// 从List集合体中删除澳门
            cityList.remove(4);// 从List集合体中删除澳门
        } catch (Throwable e) {
            e.printStackTrace();
        }
        cityMap.put(0, gaiyaotuList);// 在HashMap中第0位置添加全国概要图
        cityMap.put(1, cityList);// 在HashMap中第1位置添加直辖市
        cityMap.put(2, gangaoList);// 在HashMap中第2位置添加港澳
        isOpen = new boolean[provinceList.size()];

        ExpandableListView expandableListView = (ExpandableListView)
                rightView.findViewById(R.id.id_expandable_view);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                isOpen[groupPosition] = false;
            }
        });
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                isOpen[groupPosition] = true;
            }
        });

        //设置下载点击事件
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                LogHelper.Log(TAG, "我点击了");
                try {
                    String name = cityMap.get(groupPosition).get(childPosition).getCity();
//                    String url = amapManager.getItemByCityName(name).getUrl();

                    // 下载全国概要图、直辖市、港澳离线地图数据
                    if (groupPosition == 0 || groupPosition == 1
                            || groupPosition == 2) {
                        amapManager.downloadByProvinceName(cityMap
                                .get(groupPosition).get(childPosition)
                                .getCity());
                    }
                    // 下载各省的离线地图数据
                    else {
                        // 下载各省列表中的省份离线地图数据
                        if (childPosition == 0) {
                            amapManager.downloadByProvinceName(provinceList
                                    .get(groupPosition).getProvinceName());
                        }
                        // 下载各省列表中的城市离线地图数据
                        else if (childPosition > 0) {
                            amapManager.downloadByCityName(cityMap
                                    .get(groupPosition).get(childPosition)
                                    .getCity());
                        }
                    }
                } catch (AMapException e) {
                    e.printStackTrace();
                    Log.e("离线地图下载", "离线地图下载抛出异常" + e.getErrorMessage());
                }
                // 保存当前正在正在下载省份或者城市的position位置
                if (isStart) {
                    GaodeOfflineActivity.this.groupPosition = groupPosition;
                    GaodeOfflineActivity.this.childPosition = childPosition;
                }
                handler.sendEmptyMessage(UPDATE_LIST);
                return false;
            }
        });
    }

    /**
     * 右边主界面的Adapter
     */
    final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {

        @Override
        public int getGroupCount() {
            return provinceList.size();
        }

        /**
         * 获取一级标签内容
         */
        @Override
        public Object getGroup(int groupPosition) {
            return provinceList.get(groupPosition).getProvinceName();
        }

        /**
         * 获取一级标签的ID
         */
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        /**
         * 获取一级标签下二级标签的总数
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            return cityMap.get(groupPosition).size();
        }

        /**
         * 获取一级标签下二级标签的内容
         */
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return cityMap.get(groupPosition).get(childPosition).getCity();
        }

        /**
         * 获取二级标签的ID
         */
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /**
         * 指定位置相应的组视图
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        /**
         * 对一级标签进行设置
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            TextView group_text;
            ImageView group_image;
            if (convertView == null) {
                convertView = RelativeLayout.inflate(getBaseContext(),
                        R.layout.view_offline_group, null);
            }
            group_text = (TextView) convertView.findViewById(R.id.group_text);
            group_image = (ImageView) convertView
                    .findViewById(R.id.group_image);
            group_text.setText(provinceList.get(groupPosition)
                    .getProvinceName());
            if (isOpen[groupPosition]) {
                group_image.setImageDrawable(getResources().getDrawable(
                        R.drawable.btn_back));
            } else {
                group_image.setImageDrawable(getResources().getDrawable(
                        R.drawable.btn_back));
            }
            return convertView;
        }

        /**
         * 对一级标签下的二级标签进行设置
         */
        @Override
        public View getChildView(final int groupPosition, final int childPosition
                , boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = RelativeLayout.inflate(
                        getBaseContext(), R.layout.view_offline_child, null);
                holder.cityName = (TextView) convertView
                        .findViewById(R.id.id_tv_name);
                holder.citySize = (TextView) convertView
                        .findViewById(R.id.id_tv_size);
                holder.cityDown = (TextView) convertView
                        .findViewById(R.id.download_progress_status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.cityName.setText(cityMap.get(groupPosition)
                    .get(childPosition).getCity());
            holder.citySize.setText((cityMap.get(groupPosition).get(
                    childPosition).getSize())
                    / (1024 * 1024f) + "MB");
            OfflineMapCity mapCity = cityMap.get(groupPosition).get(
                    childPosition);
            // 通过getItem方法获取最新的状态
            if (groupPosition == 0 || groupPosition == 1 || groupPosition == 2) {
                // 全国，直辖市，港澳，按照城市处理
                mapCity = amapManager.getItemByCityName(mapCity.getCity());
            } else {
                if (childPosition == 0) {
                    // 省份
                    mapCity = getCity(amapManager.getItemByProvinceName(mapCity
                            .getCity()));
                } else {
                    // 城市
                    mapCity = amapManager.getItemByCityName(mapCity.getCity());
                }
            }
            int state = mapCity.getState();
            int completeCode = mapCity.getcompleteCode();
            if (state == OfflineMapStatus.SUCCESS) {
                holder.cityDown.setText("安装完成");
            } else if (state == OfflineMapStatus.LOADING) {
                holder.cityDown.setText("正在下载" + completeCode + "%");
            } else if (state == OfflineMapStatus.WAITING) {
                holder.cityDown.setText("等待中");
            } else if (state == OfflineMapStatus.UNZIP) {
                holder.cityDown.setText("正在解压" + completeCode + "%");
            } else if (state == OfflineMapStatus.LOADING) {
                holder.cityDown.setText("下载");
            } else if (state == OfflineMapStatus.PAUSE) {
                holder.cityDown.setText("暂停中");
            } else {
                holder.cityDown.setText("未下载");
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    };

    /**
     * 把一个省的对象转化为一个市的对象
     */
    public OfflineMapCity getCity(OfflineMapProvince aMapProvince) {
        OfflineMapCity aMapCity = new OfflineMapCity();
        aMapCity.setCity(aMapProvince.getProvinceName());
        aMapCity.setSize(aMapProvince.getSize());
        aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
        aMapCity.setState(aMapProvince.getState());
        aMapCity.setUrl(aMapProvince.getUrl());
        return aMapCity;
    }

    //地图下载监听器
    @Override
    public void onDownload(int state, int completeCode, String downName) {
        switch (state) {
            case OfflineMapStatus.SUCCESS:
                downloadedCityAdapter.notifyDataSetChanged();
                break;
            case OfflineMapStatus.LOADING:
                Log.e("amap-download", "download: " + completeCode + "%" + ","
                        + downName);
                break;
            case OfflineMapStatus.UNZIP:
                Log.e("amap-unzip", "unzip: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.WAITING:
                break;
            case OfflineMapStatus.PAUSE:
                Log.e("amap-unzip", "pause: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.STOP:
                break;
            case OfflineMapStatus.ERROR:
                Log.e("amap-download", "download: " + " ERROR " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_AMAP:
                Log.e("amap-download", "download: " + " EXCEPTION_AMAP " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
                Log.e("amap-download", "download: " + " EXCEPTION_NETWORK_LOADING "
                        + downName);
                Toast.makeText(GaodeOfflineActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                amapManager.pause();
                break;
            case OfflineMapStatus.EXCEPTION_SDCARD:
                Log.e("amap-download", "download: " + " EXCEPTION_SDCARD "
                        + downName);
                break;
            default:
                break;
        }
        handler.sendEmptyMessage(UPDATE_LIST);
    }

    @Override
    public void onCheckUpdate(boolean b, String s) {

    }

    @Override
    public void onRemove(boolean b, String s, String s1) {
        handler.sendEmptyMessage(UPDATE_LIST);
    }

    class ViewHolder {
        TextView cityName;
        TextView citySize;
        TextView cityDown;
    }

    //生命周期----------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(amapManager != null){
            amapManager.destroy();
        }
    }
}
