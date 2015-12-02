package com.xunce.gsmr.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xunce.gsmr.R;

/**
 * 显示图标下拉列表的Adapter
 * Created by ssthouse on 2015/12/1.
 */
public class MarkerIconSpinnerAdapter extends BaseAdapter {

    public Context context;

    //使用的数据都是已经定义好了的
    //只有5中颜色

    public MarkerIconSpinnerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.view_sp_item_marker_icon, null);
        ImageView iv = (ImageView) v.findViewById(R.id.id_iv);
        switch (position){
            case 0:
                iv.setImageResource(R.drawable.icon_marker_blue);
                break;
            case 1:
                iv.setImageResource(R.drawable.icon_marker_green);
                break;
            case 2:
                iv.setImageResource(R.drawable.icon_marker_orange);
                break;
            case 3:
                iv.setImageResource(R.drawable.icon_marker_purple);
                break;
            case 4:
                iv.setImageResource(R.drawable.icon_marker_red);
                break;
        }
        return v;
    }
}
