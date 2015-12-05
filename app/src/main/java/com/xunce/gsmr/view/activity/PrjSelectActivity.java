package com.xunce.gsmr.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.DBHelper;
import com.xunce.gsmr.util.FileHelper;
import com.xunce.gsmr.util.VibrateHelper;
import com.xunce.gsmr.util.preference.PreferenceHelper;
import com.xunce.gsmr.util.view.ToastHelper;
import com.xunce.gsmr.util.view.ViewHelper;
import com.xunce.gsmr.view.activity.baidu.BaiduPrjEditActivity;
import com.xunce.gsmr.view.activity.gaode.GaodePrjEditActivity;
import com.xunce.gsmr.view.adapter.PrjLvAdapter;
import com.xunce.gsmr.view.style.TransparentStyle;

/**
 * 主界面选择工程的Activity
 * Created by ssthouse on 2015/7/17.
 */
public class PrjSelectActivity extends AppCompatActivity{
    private static String EXTRA_KEY_IS_CALLED = "is_called_by_prj_edit";

    private ListView lv;

    private PrjLvAdapter adapter;

    public static void start(Activity activity, boolean isCalledByPrjEditAty){
        Intent intent = new Intent(activity, PrjSelectActivity.class);
        intent.putExtra(EXTRA_KEY_IS_CALLED, isCalledByPrjEditAty);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prj_select);
        TransparentStyle.setTransparentStyle(this,R.color.color_primary);

        //监测是否为PrjEditActivity调用
        boolean isCalled = getIntent().getBooleanExtra(EXTRA_KEY_IS_CALLED, false);
        if(isCalled){
            initView();
            return;
        }

        //判断---如果有上次打开的Project---就直接跳转
        //判断是否有上次编辑的project
        if (PreferenceHelper.getInstance(this).hasLastEditPrjItem(this)) {
            PrjItem prjItem = new PrjItem(PreferenceHelper.getInstance(this).getLastEditPrjName(this));

            //判断MapType
            //判断地图类型--启动Activity
            if(PreferenceHelper.getInstance(PrjSelectActivity.this).getMapType()
                    == PreferenceHelper.MapType.BAIDU_MAP) {
                BaiduPrjEditActivity.start(PrjSelectActivity.this, prjItem);
            }else{
                GaodePrjEditActivity.start(PrjSelectActivity.this, prjItem);
            }
            finish();
        }

        //初始化View
        initView();
    }

    /**
     * 初始化View
     */
    private void initView(){
        ViewHelper.initActionBar(this, getSupportActionBar(), "基址勘察");

        lv = (ListView) findViewById(R.id.id_lv);
        adapter = new PrjLvAdapter(this, DBHelper.getPrjItemList());
        lv.setAdapter(adapter);
        //开启工程编辑Activity
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击某一个prjItem的时候跳转到---具体的编辑界面(一个地图---很多按钮)
                //保存当前要编辑的PrjName到preference
                PreferenceHelper.getInstance(PrjSelectActivity.this)
                        .setLastEditPrjName(PrjSelectActivity.this,
                                adapter.getPrjItemList().get(position).getPrjName());
                finish();
                //判断地图类型--启动Activity
                if(PreferenceHelper.getInstance(PrjSelectActivity.this).getMapType()
                        == PreferenceHelper.MapType.BAIDU_MAP) {
                    BaiduPrjEditActivity.start(PrjSelectActivity.this, adapter.getPrjItemList()
                            .get(position));
                }else{
                    GaodePrjEditActivity.start(PrjSelectActivity.this, adapter.getPrjItemList()
                            .get(position));
                }
            }
        });
        //长按监听事件
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //震动
                VibrateHelper.shortVibrate(PrjSelectActivity.this);
                //显示长按的菜单Dialog
                showLvLongClickDialog(PrjSelectActivity.this,
                        adapter.getPrjItemList().get(position), adapter);
                return true;
            }
        });

        FloatingActionButton btnAdd = (FloatingActionButton) findViewById(R.id.id_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //会自动回调----刷新界面
                showPrjNameDialog(PrjSelectActivity.this, adapter);
            }
        });
    }

    /**
     * 长按显示的Menu的Dialog
     * @param context
     * @param prjItem
     * @param adapter
     */
    public void showLvLongClickDialog(final Context context, final PrjItem prjItem, final PrjLvAdapter adapter){
        //build出dialog
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        //inflate出View---配置点击事件
        LinearLayout ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_lv_item, null);
        ll.findViewById(R.id.id_menu_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
                prjItem.deletePrj(context);
                //刷新视图
                adapter.notifyDataSetChanged();
            }
        });
        ll.findViewById(R.id.id_menu_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
                //开启编辑PrjItem的Activity
                GaodePrjEditActivity.start((Activity) context, prjItem);
            }
        });
        ll.findViewById(R.id.id_menu_rename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
                //开启重命名的Dialog
                showChangeNameDialog(context, prjItem);
            }
        });
        dialogBuilder.withTitle(null)             //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage(null)//.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")
                .withDialogColor(context.getResources().getColor(R.color.dialog_color))
                .withEffect(Effectstype.Slidetop)       //def Effectstype.Slidetop
                .setCustomView(ll, context)
                .isCancelableOnTouchOutside(false)       //不可以点击外面取消
                .show();
    }

    /**
     * 重命名的Dialog
     * @param context
     * @param prjItem
     */
    public void showChangeNameDialog(final Context context, final PrjItem prjItem) {
        //导出View
        LinearLayout llPrjName = (LinearLayout) LayoutInflater.from(context).
                inflate(R.layout.dialog_prj_name, null);
        final EditText etPrjName = (EditText) llPrjName.findViewById(R.id.id_et);
        //导出Dialog
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        //创建监听事件
        View.OnClickListener cancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        };
        View.OnClickListener confirmListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prjName = etPrjName.getText().toString();
                if (prjName.equals("")) {
                    ToastHelper.showSnack(context, v, "工程名不可为空");
                } else {
                    if (DBHelper.isPrjExist(prjName)) {
                        ToastHelper.showSnack(context, v, "该工程已存在");
                    } else {
                        prjItem.changeName(context, prjName);
                        dialogBuilder.dismiss();
                        ToastHelper.showSnack(context, v, "重命名成功!");
                    }
                }
            }
        };
        dialogBuilder.withTitle("工程名")             //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage(null)//.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")
                .withDialogColor(context.getResources().getColor(R.color.dialog_color))
                .withEffect(Effectstype.Slidetop)       //def Effectstype.Slidetop
                .setCustomView(llPrjName, context)
                .withButton1Text("确认")                 //def gone
                .withButton2Text("取消")                 //def gone
                .isCancelableOnTouchOutside(false)
                .setButton1Click(confirmListener)
                .setButton2Click(cancelListener)
                .show();
    }

    /**
     * 显示新工程名输入的Dialog
     * @param context
     * @param adapter
     */
    public void showPrjNameDialog(final Context context, final PrjLvAdapter adapter) {
        LinearLayout llPrjName = (LinearLayout) LayoutInflater.from(context).
                inflate(R.layout.dialog_prj_name, null);
        final EditText etPrjName = (EditText) llPrjName.findViewById(R.id.id_et);
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        View.OnClickListener cancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        };
        View.OnClickListener confirmListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prjName = etPrjName.getText().toString();
                if (prjName.equals("")) {
                    ToastHelper.showSnack(context, v, "工程名不可为空");
                } else {
                    if (DBHelper.isPrjExist(prjName)) {
                        ToastHelper.showSnack(context, v, "该工程已存在");
                    } else {
                        //将新的prjItem保存进数据库
                        new PrjItem(prjName).save();
                        //重新加载工程视图
                        adapter.notifyDataSetChanged();
                        //消除Dialog
                        dialogBuilder.dismiss();
                        //Toast 提醒成功
                        ToastHelper.showSnack(context, v, "工程创建成功!");
                    }
                }
            }
        };
        dialogBuilder.withTitle("工程名")             //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage(null)//.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")
                .withDialogColor(context.getResources().getColor(R.color.dialog_color))
                .withEffect(Effectstype.Slidetop)       //def Effectstype.Slidetop
                .setCustomView(llPrjName, context)
                .withButton1Text("确认")                 //def gone
                .withButton2Text("取消")                 //def gone
                .isCancelableOnTouchOutside(false)
                .setButton1Click(confirmListener)
                .setButton2Click(cancelListener)
                .show();
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
