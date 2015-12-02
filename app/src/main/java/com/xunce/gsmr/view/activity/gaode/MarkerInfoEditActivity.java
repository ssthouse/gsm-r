package com.xunce.gsmr.view.activity.gaode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.util.view.ViewHelper;

/**
 * Marker文本信息编辑
 * Created by ssthouse on 2015/12/2.
 */
public class MarkerInfoEditActivity extends AppCompatActivity {

    /**
     * 当前编辑的数据
     */
    private MarkerItem markerItem;

    /**
     * 启动当前Activity需要一个markerItem
     * @param context
     * @param markerItem
     */
    public static void start(Context context, MarkerItem markerItem){
        Intent intent =new Intent(context, MarkerInfoEditActivity.class);
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

    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), "编辑标记点信息");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO---编辑事件---更新数据
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
            case R.id.id_action_save_change:
                //保存修改
                markerItem.save();
                //退出
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
