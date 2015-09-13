package com.xunce.gsmr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.xunce.gsmr.R;
import com.xunce.gsmr.activity.baidu.PrjEditActivity;
import com.xunce.gsmr.util.DialogHelper;
import com.xunce.gsmr.util.FileHelper;
import com.xunce.gsmr.util.PreferenceHelper;
import com.xunce.gsmr.util.ToastHelper;
import com.xunce.gsmr.util.VibrateHelper;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.util.gps.DBHelper;
import com.xunce.gsmr.view.adapter.PrjLvAdapter;
import com.xunce.gsmr.view.style.TransparentStyle;


/**
 * 主界面选择工程的Activity
 * Created by ssthouse on 2015/7/17.
 */
public class PrjSelectActivity extends AppCompatActivity{
    private static final String TAG = "PrjSelectActivity";

    private ListView lv;

    private PrjLvAdapter adapter;

    public static void start(Activity activity){
        activity.startActivity(new Intent(activity, PrjSelectActivity.class));
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prj_select);
        TransparentStyle.setTransparentStyle(this,R.color.color_primary);

        initView();
    }

    private void initView(){
        ViewHelper.initActionBar(this, getSupportActionBar(), "基址勘察");

        lv = (ListView) findViewById(R.id.id_lv);
        adapter = new PrjLvAdapter(this, DBHelper.getPrjItemList());
        lv.setAdapter(adapter);
        //开启工程编辑Acvivity
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击某一个prjImte的时候跳转到---具体的编辑界面(一个地图---很多按钮)
                //保存当前要编辑的Prjname到preference
                PreferenceHelper.setLastEditPrjName(PrjSelectActivity.this,
                        adapter.getPrjItemList().get(position).getPrjName());
                finish();
                PrjEditActivity.start(PrjSelectActivity.this, adapter.getPrjItemList().get(position));
            }
        });
        //长按监听事件
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //震动
                VibrateHelper.shortVibrate(PrjSelectActivity.this);
                //显示长按的菜单Dialog
                DialogHelper.showLvLongClickDialog(PrjSelectActivity.this,
                        adapter.getPrjItemList().get(position), adapter);
                return true;
            }
        });

        FloatingActionButton btnAdd = (FloatingActionButton) findViewById(R.id.id_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //会自动回调----刷新界面
                DialogHelper.showPrjNameDialog(PrjSelectActivity.this, adapter);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_prj_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_action_export_data:
                //
                FileHelper.sendDbFile(PrjSelectActivity.this);
                break;
            case R.id.id_action_setting:
                ToastHelper.showSnack(PrjSelectActivity.this, lv, "设置");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
