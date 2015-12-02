package com.xunce.gsmr.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.xunce.gsmr.R;
import com.xunce.gsmr.util.preference.PreferenceHelper;
import com.xunce.gsmr.util.view.ViewHelper;
import com.xunce.gsmr.view.adapter.MarkerIconListAdapter;

import java.util.Map;

/**
 * 标记点图标设置的Activity
 * Created by ssthouse on 2015/12/1.
 */
public class MarkerIconSetActivity extends AppCompatActivity {

    private MarkerIconListAdapter adapter;

    /**
     * 启动当前Activity
     */
    public static void start(Context context){
        context.startActivity(new Intent(context, MarkerIconSetActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_icon_set);
        initView();
    }

    private void initView(){
        ViewHelper.initActionBar(this, getSupportActionBar(), "标记点图标设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lv = (ListView) findViewById(R.id.id_lv);
        adapter = new MarkerIconListAdapter(this);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_marker_icon_set, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            //增加markerIcon
            case R.id.id_action_add:
                adapter.addNewMarkerIcon();
                break;
            //保存修改
            case R.id.id_action_save_change:
                Map<String, String> mapData = adapter.getEditedMarkerIconMap();
                PreferenceHelper.getInstance(this).setMarkerIconMap(mapData);
                //保存修改后---退出activity
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
