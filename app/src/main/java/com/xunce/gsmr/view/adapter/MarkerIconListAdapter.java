package com.xunce.gsmr.view.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.MarkerIconCons;
import com.xunce.gsmr.util.preference.PreferenceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备类型图标设置界面listview的Adapter
 * Created by ssthouse on 2015/12/1.
 */
public class MarkerIconListAdapter extends BaseSwipeAdapter {

    private Context context;

    /**
     * 接下来界面的操作---都是处理的这个数据
     */
    private List<String> keyList = new ArrayList<>();
    private List<String> valueList = new ArrayList<>();

    private MarkerIconSpinnerAdapter spinnerAdapter;

    /**
     * 构造方法
     *
     * @param context
     */
    public MarkerIconListAdapter(Context context) {
        this.context = context;
        //获取Map数据
        Map<String, String> dataMap = PreferenceHelper.getInstance(context).getMarkerIconMap();
        //map转化为List数据
        for (Map.Entry<String, String> entity : dataMap.entrySet()) {
            keyList.add(entity.getKey());
            valueList.add(entity.getValue());
        }
        //初始化一个spinner的adapter
        spinnerAdapter = new MarkerIconSpinnerAdapter(context);
    }


    /**
     * 增加新的List成员
     */
    public void addNewMarkerIcon() {
        //添加key成员
        keyList.add("");
        //增加value成员(默认是蓝色)
        valueList.add(MarkerIconCons.ColorName.BLUE);
        //更新view
        notifyDataSetChanged();
    }

    /**
     * 获取编辑后的markerIcon的map数据
     *
     * @return
     */
    public Map<String, String> getEditedMarkerIconMap() {
        Map<String, String> map = new HashMap<>();
        //将List中的数据填充进map
        for (int i = 0; i < keyList.size(); i++) {
            map.put(keyList.get(i), valueList.get(i));
        }
        return map;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.id_swipe_layout;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_lv_item_marker_icon_set, null);
        //设置点击事件
        final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.id_swipe_layout);
        //删除按钮
        swipeLayout.findViewById(R.id.id_ll_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO---删除当前item
                //删除key
                keyList.remove(position);
                valueList.remove(position);
                //更新view
                notifyDataSetChanged();
            }
        });
        //overflow按钮弹出删除按钮
        view.findViewById(R.id.id_btn_show_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.toggle();
            }
        });
        return view;
    }

    @Override
    public void fillValues(final int positionInLv, View convertView) {
        //填充view的数据
        Spinner sp = (Spinner) convertView.findViewById(R.id.id_sp);
        sp.setAdapter(spinnerAdapter);
        //设置spinner改变选项的点击事件
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionInSp, long id) {
                //TODO---改变List中的数据(只用改变value列表中的数据)
                switch (positionInSp) {
                    case 0:
                        valueList.set(positionInLv, MarkerIconCons.ColorName.BLUE);
                        break;
                    case 1:
                        valueList.set(positionInLv, MarkerIconCons.ColorName.GREEN);
                        break;
                    case 2:
                        valueList.set(positionInLv, MarkerIconCons.ColorName.ORANGE);
                        break;
                    case 3:
                        valueList.set(positionInLv, MarkerIconCons.ColorName.PURPLE);
                        break;
                    case 4:
                        valueList.set(positionInLv, MarkerIconCons.ColorName.RED);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //设置Edittext的文字---文字变化事件
        EditText et = (EditText) convertView.findViewById(R.id.id_et);
        et.setText(keyList.get(positionInLv));
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO---文字改变后---改keylist
                keyList.set(positionInLv, s.toString());
            }
        });
        //设置图标的颜色
        ImageView iv = (ImageView) convertView.findViewById(R.id.id_iv);
        switch (valueList.get(positionInLv)) {
            case MarkerIconCons.ColorName.BLUE:
                sp.setSelection(0);
                break;
            case MarkerIconCons.ColorName.GREEN:
                sp.setSelection(1);
                break;
            case MarkerIconCons.ColorName.ORANGE:
                sp.setSelection(2);
                break;
            case MarkerIconCons.ColorName.PURPLE:
                sp.setSelection(3);
                break;
            case MarkerIconCons.ColorName.RED:
                sp.setSelection(4);
                break;
        }
    }

    @Override
    public int getCount() {
        return keyList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
