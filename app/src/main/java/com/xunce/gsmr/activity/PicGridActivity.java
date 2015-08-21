package com.xunce.gsmr.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.R;
import com.xunce.gsmr.view.adapter.PicGridAdapter;
import com.xunce.gsmr.model.BitmapItem;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.view.style.TransparentStyle;
import com.xunce.gsmr.util.FileHelper;
import com.xunce.gsmr.util.PictureHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 用于显示一个MarkItem对应的所有的照片
 * Created by ssthouse on 2015/7/20.
 */
public class PicGridActivity extends AppCompatActivity {
    private static final String TAG = "PicGridActivity";

    private MarkerItem markerItem;

    private List<BitmapItem> selectedList = new ArrayList<>();

    private GridView gv;
    private ImageButton btnAdd;
    private LinearLayout ll;
    private Button btnDelete;
    private Button btnShare;

    private PicGridAdapter adapter;

    private boolean isInSelectMode = false;

    public static void start(Activity activity, MarkerItem markerItem) {
        //从Marker中获取信息--找到Picture的目录---展示所有的图片
        Intent intent = new Intent(activity, PicGridActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_MARKER_ITEM, markerItem);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_grid);
        TransparentStyle.setTransparentStyle(this,R.color.color_primary);

        markerItem = (MarkerItem) getIntent().getSerializableExtra(Constant.EXTRA_KEY_MARKER_ITEM);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new PicGridAdapter(this, markerItem.getFilePath());
        gv.setAdapter(adapter);
    }

    private void initView() {
        btnAdd = (ImageButton) findViewById(R.id.id_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureHelper.getPictureFromCamera(PicGridActivity.this, markerItem.getFilePath());
            }
        });

        ll = (LinearLayout) findViewById(R.id.id_ll_options);

        btnDelete = (Button) findViewById(R.id.id_btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        btnShare = (Button) findViewById(R.id.id_btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileHelper.sendPicture(PicGridActivity.this, selectedList);
            }
        });

        gv = (GridView) findViewById(R.id.id_gv);


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果在编辑状态---添加选中图片
                if (isInSelectMode) {
                    //获取点中的bitmapItem
                    BitmapItem bitmapItem = adapter.getBitmapItemList().get(gv.indexOfChild(view));
                    //如果已经选中了的又被点击---剔除
                    if (selectedList.contains(bitmapItem)) {
                        selectedList.remove(bitmapItem);
                        view.findViewById(R.id.id_iv_pic_delete).setVisibility(View.INVISIBLE);
                        if (selectedList.size() == 0) {
                            isInSelectMode = false;
                        }
                    } else {
                        selectedList.add(bitmapItem);
                        view.findViewById(R.id.id_iv_pic_delete).setVisibility(View.VISIBLE);
                    }
                } else {
                    //否则---开启系统图库查看图片
                    PictureHelper.showPictureInAlbum(PicGridActivity.this,
                            adapter.getBitmapItemList().get(position).getPath());
                }
                updateView();
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
                updateView();
                return true;
            }
        });
    }

    private void removeAllSelected() {
        isInSelectMode = false;
        selectedList.clear();
        for (int i = 0; i < gv.getChildCount(); i++) {
            View child = gv.getChildAt(i);
            child.findViewById(R.id.id_iv_pic_delete).setVisibility(View.GONE);
        }
    }

    private void updateView() {
        if (isInSelectMode) {
            btnAdd.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setVisibility(View.VISIBLE);
            ll.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isInSelectMode) {
            removeAllSelected();
            updateView();
            return;
        }
        super.onBackPressed();
    }

    private void showAlbumOrCamera() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        View.OnClickListener confirmListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (BitmapItem item : selectedList) {
                    PictureHelper.deletePicture(item.getPath());
                }
                adapter.notifyDataSetChanged();
                selectedList.clear();
                removeAllSelected();
                updateView();
                dialogBuilder.dismiss();
            }
        };
        View.OnClickListener cancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        };
        dialogBuilder.withTitle("确认删除?")             //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("照片将从工程中删除")//.withMessage(null)  no Msg
                .withMessageColor("#FFFFFF")
                .withDialogColor(this.getResources().getColor(R.color.dialog_color))
                .withEffect(Effectstype.Slidetop)       //def Effectstype.Slidetop
                .withButton1Text("确认")                 //def gone
                .withButton2Text("取消")                 //def gone
                .isCancelableOnTouchOutside(false)
                .setButton1Click(confirmListener)
                .setButton2Click(cancelListener)
                .show();
    }

    private void showConfirmDialog() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        View.OnClickListener confirmListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PicGridActivity.this.findViewById(R.id.id_pb_empty).setVisibility(View.VISIBLE);
                for (BitmapItem item : selectedList) {
                    PictureHelper.deletePicture(item.getPath());
                }
                adapter.notifyDataSetChanged();
                selectedList.clear();
                removeAllSelected();
                updateView();
                dialogBuilder.dismiss();
            }
        };
        View.OnClickListener cancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        };
        dialogBuilder.withTitle("确认删除?")             //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")
                .withDividerColor("#11000000")
                .withMessage("照片将从工程中删除")//.withMessage(null)  no Msg
                .withMessageColor("#FFFFFF")
                .withDialogColor(this.getResources().getColor(R.color.dialog_color))
                .withEffect(Effectstype.Slidetop)       //def Effectstype.Slidetop
                .withButton1Text("确认")                 //def gone
                .withButton2Text("取消")                 //def gone
                .isCancelableOnTouchOutside(false)
                .setButton1Click(confirmListener)
                .setButton2Click(cancelListener)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //判断是图库---还是照相机
        if (requestCode == Constant.REQUEST_CODE_ALBUM && null != data) {
            //根据uri获取图片路径
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String srcPath = cursor.getString(columnIndex);
            cursor.close();
            //将照片保存到指定文件夹
            PictureHelper.saveImage(srcPath, markerItem.getFilePath() +
                    Calendar.getInstance().getTimeInMillis() + ".jpeg");
            //更新界面
            adapter.notifyDataSetChanged();
            return;
        } else if (requestCode == Constant.REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            PicGridActivity.this.findViewById(R.id.id_pb_empty).setVisibility(View.VISIBLE);
            //更新界面
            adapter.addPicture();
            return;
        }
//        removeAllSelected();
//        updateView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
