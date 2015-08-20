package com.xunce.gsmr.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xunce.gsmr.R;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.gps.DBHelper;

import java.util.List;

/**
 * 工程的adapter
 * Created by ssthouse on 2015/7/18.
 */
public class PrjLvAdapter extends BaseAdapter {

    private List<PrjItem> prjItemList;

    private LayoutInflater inflater;


    public PrjLvAdapter(Context context, List<PrjItem> prjItemList){
        this.prjItemList = prjItemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return prjItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return prjItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){
            //初始话ConvertView
            convertView = inflater.inflate(R.layout.lv_item_prj_select, null);
            //初始化ViewHoler
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView) convertView.findViewById(R.id.id_tv_prj_name);
            //set to tag
            convertView.setTag(viewHolder);
            //set  data
            viewHolder.tv.setText(prjItemList.get(position).getPrjName());
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.tv.setText(prjItemList.get(position).getPrjName());
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }

    @Override
    public void notifyDataSetChanged() {
        //刷新数据
        prjItemList = DBHelper.getPrjItemList();
        //刷新数据库
        super.notifyDataSetChanged();
    }

    //getter----------and-----------setter

    public List<PrjItem> getPrjItemList() {
        return prjItemList;
    }

    public void setPrjItemList(List<PrjItem> prjItemList) {
        this.prjItemList = prjItemList;
    }
}
