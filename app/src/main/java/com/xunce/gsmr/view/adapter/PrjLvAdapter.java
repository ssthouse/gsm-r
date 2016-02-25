package com.xunce.gsmr.view.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xunce.gsmr.R;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 工程的adapter
 * Created by ssthouse on 2015/7/18.
 */
public class PrjLvAdapter extends BaseAdapter {

    private List<PrjItem> prjItemList;
    private List<PrjItem> selectList;
    private LayoutInflater inflater;
    private int anim = 0;
    private Animation MoveIn;
    private Animation MoveOut;
    private Animation MoveOutTv;


    public PrjLvAdapter(Context context, List<PrjItem> prjItemList){
        this.prjItemList = prjItemList;
        inflater = LayoutInflater.from(context);
        MoveIn = AnimationUtils.loadAnimation(context,R.anim.translate_right);
        MoveOut = AnimationUtils.loadAnimation(context,R.anim.translate_left);
        MoveOutTv = AnimationUtils.loadAnimation(context,R.anim.translate_textview_left);
        selectList = new ArrayList<>();
        MoveIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                anim = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        MoveOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                anim = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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
            convertView = inflater.inflate(R.layout.view_lv_item_prj_select, null);
            //初始化ViewHoler
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView) convertView.findViewById(R.id.id_tv_prj_name);
            viewHolder.cb = (CheckBox) convertView.findViewById(R.id.id_cb_prj_name);
            //set to tag
            convertView.setTag(viewHolder);
            //set  data
            viewHolder.tv.setText(prjItemList.get(position).getPrjName());
            viewHolder.cb.setChecked(isinselectList(prjItemList.get(position)));
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.tv.setText(prjItemList.get(position).getPrjName());
            viewHolder.cb.setChecked(isinselectList(prjItemList.get(position)));
            if(anim == 1)
            {
                viewHolder.cb.setVisibility(View.VISIBLE);
                viewHolder.cb.startAnimation(MoveIn);
                viewHolder.tv.startAnimation(MoveIn);
            }else if(anim == 2 ){
                viewHolder.cb.startAnimation(MoveOut);
                viewHolder.cb.setVisibility(View.GONE);
                viewHolder.tv.startAnimation(MoveOutTv);
            }
        }
        return convertView;
    }

    public void CheckBox_Movein(){
        anim = 1;
        notifyDataSetChanged();
    }

    public void CheckBox_Moveout(){
        anim = 2;
        notifyDataSetChanged();
    }
    private boolean isinselectList(PrjItem prjItem)
    {
        if(selectList == null || selectList.size() == 0){
            return false;
        }
        if (selectList.contains(prjItem)){
            return true;
        }
        return false;
    }
    class ViewHolder {
        CheckBox cb;
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

    public List<PrjItem> getSelectList() {
        return selectList;
    }

    public void cleanSelectList(){
        selectList.clear();
    }

    public void setSelectList(List<PrjItem> selectList) {
        this.selectList = selectList;
    }
}
