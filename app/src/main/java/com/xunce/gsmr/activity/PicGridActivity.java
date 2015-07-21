package com.xunce.gsmr.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.xunce.gsmr.Constant;
import com.xunce.gsmr.R;
import com.xunce.gsmr.adapter.PicGridAdapter;
import com.xunce.gsmr.model.BitmapItem;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.PictureHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示一个MarkItem对应的所有的照片
 * Created by ssthouse on 2015/7/20.
 */
public class PicGridActivity extends AppCompatActivity {
    private static final String TAG = "PicGridActivity";

    /**
     * 当前用于调试的path
     * TODO---应该是根据MarkerItem获取的
     */
    private String path = "/storage/sdcard0/picture/";

    private List<BitmapItem> selectedList = new ArrayList<>();

    private GridView gv;

    private PicGridAdapter adapter;

    private boolean isInSelectMode = false;

    public static void start(Context context, MarkerItem markerItem) {
        //从Marker中获取信息--找到Picture的目录---展示所有的图片
        Intent intent = new Intent(context, PicGridActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_MARKER_ITEM, markerItem);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_grid);

        initView();

        int a = new File(path).list().length;
        LogHelper.Log(TAG, a + "个文件");
    }


    private void initView() {
        gv = (GridView) findViewById(R.id.id_gv);
        adapter = new PicGridAdapter(this, path);
        gv.setAdapter(adapter);
        gv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "shenmegui");
                //如果在编辑状态---添加选中图片
                if(isInSelectMode){
                    //获取点中的bitmapItem
                    BitmapItem bitmapItem = adapter.getBitmapItemList().get(gv.indexOfChild(view));
                    //如果已经选中了的又被点击---剔除
                    if (selectedList.contains(bitmapItem)) {
//                        LogHelper.Log(TAG, "将之隐藏");
//                        selectedList.remove(bitmapItem);
//                        view.findViewById(R.id.id_iv_pic_delete).setVisibility(View.GONE);
                        if (selectedList.size() == 0) {
                            isInSelectMode = false;
                        }
                    } else {
                        LogHelper.Log(TAG, "我添加进来了");
                        selectedList.add(bitmapItem);
                        view.findViewById(R.id.id_iv_pic_delete).setVisibility(View.VISIBLE);
                        gv.postInvalidate();
                        gv.clearChildFocus(view);
                        gv.clearChoices();
                        gv.clearFocus();
                    }
                }
                if (isInSelectMode) {
                    LogHelper.Log(TAG, "我进来饿了");
                    //获取点中的bitmapItem
                    BitmapItem bitmapItem = adapter.getBitmapItemList().get(gv.indexOfChild(view));
                    //如果已经选中了的又被点击---剔除
                    if (selectedList.contains(bitmapItem)) {
                        LogHelper.Log(TAG, "将之隐藏");
                        selectedList.remove(bitmapItem);
                        view.findViewById(R.id.id_iv_pic_delete).setVisibility(View.INVISIBLE);
                        if (selectedList.size() == 0) {
                            isInSelectMode = false;
                        }
                    } else {
                        LogHelper.Log(TAG, "我添加进来了");
                        selectedList.add(bitmapItem);
                        view.findViewById(R.id.id_iv_pic_delete).setVisibility(View.VISIBLE);
                    }
                } else {
                    //否则---开启系统图库查看图片
                    PictureHelper.showPictureInAlbum(PicGridActivity.this,
                            adapter.getBitmapItemList().get(position).getPath());
                }
            }
        });

        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //如果不在编辑状态---选中---并进入编辑状态
                if (!isInSelectMode) {
                    view.findViewById(R.id.id_iv_pic_delete).setVisibility(View.VISIBLE);
                    isInSelectMode = true;
                    BitmapItem bitmapItem = adapter.getBitmapItemList().get(gv.indexOfChild(view));
                    selectedList.add(bitmapItem);
                }
                return false;
            }
        });
    }

    private void removeAllSelected() {
        isInSelectMode = false;
        selectedList.clear();
        for (int i = 0; i < gv.getChildCount(); i++) {
            View child = gv.getChildAt(i);
            child.findViewById(R.id.id_iv_pic_delete).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isInSelectMode) {
            removeAllSelected();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO---回调
        super.onActivityResult(requestCode, resultCode, data);
    }
}
