package com.xunce.gsmr.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.xunce.gsmr.R;
import com.xunce.gsmr.util.LogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 所有离线城市的Adapter
 * Created by ssthouse on 2015/9/17.
 */
public class GaodeDownloadedCityAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "GaodeDownloadedCityAdapter";

    /**
     * 上下文
     */
    private Context context;

    /**
     * 离线地图管理器
     */
    private OfflineMapManager offlineMapManager;

    /**
     * 城市数据
     */
    // 保存一级目录的省----直辖市
    private List<OfflineMapProvince> provinceList = new ArrayList<>();
    // 保存二级目录的市
    private HashMap<Object, List<OfflineMapCity>> cityMap = new HashMap<>();

    /**
     * 构造方法
     *
     * @param context
     * @param offlineMapManager
     */
    public GaodeDownloadedCityAdapter(Context context, OfflineMapManager offlineMapManager) {
        this.context = context;
        this.offlineMapManager = offlineMapManager;

        //初始化数据
        initData();
    }

    /**
     * 初始化已经下载了的地图数据
     */
    private void initData() {
        //初始化数据
        cityMap.clear();
        provinceList = offlineMapManager.getDownloadOfflineMapProvinceList();
        LogHelper.Log(TAG, "数据一共有:  " + provinceList.size() + "条");
        for (int i = 0; i < provinceList.size(); i++) {
            cityMap.put(i, provinceList.get(i).getCityList());
            LogHelper.Log(TAG, "我找到了一个下载了的地图..................");
        }
    }

    @Override
    public void notifyDataSetChanged() {
        initData();
        super.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return provinceList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return cityMap.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return provinceList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return cityMap.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView group_text;
        ImageView group_image;
        if (convertView == null) {
            convertView = RelativeLayout.inflate(context, R.layout.view_offline_group, null);
        }
        group_text = (TextView) convertView.findViewById(R.id.group_text);
        group_image = (ImageView) convertView.findViewById(R.id.group_image);
        group_text.setText(provinceList.get(groupPosition).getProvinceName());
//        if (isOpen[groupPosition]) {
//            group_image.setImageDrawable(getResources().getDrawable(R.drawable.btn_back));
//        } else {
//            group_image.setImageDrawable(getResources().getDrawable(R.drawable.btn_back));
//        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.view_offline_child, null);
        TextView tvName = (TextView) view.findViewById(R.id.id_tv_name);
        tvName.setText(cityMap.get(groupPosition).get(childPosition).getCity());
        TextView tvSize = (TextView) view.findViewById(R.id.id_tv_size);
        tvSize.setText(cityMap.get(groupPosition).get(childPosition).getSize() / (1024 * 1024f) + "MB");
        view.findViewById(R.id.id_btn_delete_offline_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取城市名称
                OfflineMapCity city = cityMap.get(groupPosition).get(childPosition);
                String name = city.getCity();
                offlineMapManager.remove(name);
                LogHelper.Log(TAG, "我删除了----" + name);
                //删除后----刷新视图
                showWaitDialog();
            }
        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    //显示确认的Dialog
    private AlertDialog dialog;
    //显示等待Dialog
    private void showWaitDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //inflate出view---设置点击事件
        View contentView = View.inflate(context, R.layout.dialog_complete, null);
        contentView.findViewById(R.id.id_btn_ensure).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setView(contentView);
        dialog = builder.create();
        dialog.show();
    }
}