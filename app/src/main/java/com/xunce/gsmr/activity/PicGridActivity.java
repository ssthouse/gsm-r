package com.xunce.gsmr.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.xunce.gsmr.Constant;
import com.xunce.gsmr.R;
import com.xunce.gsmr.adapter.PicGridAdapter;
import com.xunce.gsmr.model.BitmapItem;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.util.LogHelper;
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
    private Button btnDelete;

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

        markerItem = (MarkerItem) getIntent().getSerializableExtra(Constant.EXTRA_KEY_MARKER_ITEM);

        initView();
    }


    private void initView() {
        btnAdd = (ImageButton) findViewById(R.id.id_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureHelper.getPictureFromCamera(PicGridActivity.this);
            }
        });

        btnDelete = (Button) findViewById(R.id.id_btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        gv = (GridView) findViewById(R.id.id_gv);
        adapter = new PicGridAdapter(this, markerItem.getFilePath());
        gv.setAdapter(adapter);
        gv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

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
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
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

    private void showAlbumOrCamera(){
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
        //TODO---回调
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
        } else if (requestCode == Constant.REQUEST_CODE_CAMERA && null != data) {
            Uri uri = data.getData();
            String picPath = markerItem.getFilePath() + Calendar.getInstance().getTimeInMillis() + ".jpeg";
            if (uri == null) {
                LogHelper.Log(TAG, "拍照的uri是空的!!!");
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap photo = (Bitmap) bundle.get("data"); //get bitmap
                    //直接将Bitmap保存到指定路径
                    PictureHelper.saveImage(photo, picPath);
                    //更新界面
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "该照片获取失败!", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                LogHelper.Log(TAG, "拍照的uri不是空的");
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
                PictureHelper.saveImage(srcPath, picPath);
                //更新界面
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
