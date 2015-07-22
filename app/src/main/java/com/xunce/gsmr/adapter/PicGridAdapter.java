package com.xunce.gsmr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xunce.gsmr.R;
import com.xunce.gsmr.model.BitmapItem;
import com.xunce.gsmr.model.widget.CustomImageView;
import com.xunce.gsmr.util.PictureHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片相关工具类
 * Created by ssthouse on 2015/7/20.
 */
public class PicGridAdapter extends BaseAdapter {
    private static String TAG = "PicGridAdaoter";

    private Context context;
    private List<BitmapItem> bitmapItemList;

    private String path;

    /**
     * 根据path自己获取图片
     * @param path
     */
    public PicGridAdapter(Context context,String path){
        this.context = context;
        bitmapItemList = new ArrayList<>();
        new Task().execute(path);
        this.path = path;
    }

    @Override
    public int getCount() {
//        LogHelper.Log(TAG, bitmapItemList.size()+"");
        return bitmapItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmapItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_view_item, null);
            viewHolder.ivPic = (CustomImageView) convertView.findViewById(R.id.id_iv_pic);
            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.id_iv_pic_delete);
            viewHolder.ivPic.setImageBitmap(bitmapItemList.get(position).getBitmap());
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.ivPic.setImageBitmap(bitmapItemList.get(position).getBitmap());
        }
        return convertView;
    }

    class ViewHolder {
        CustomImageView ivPic;
        ImageView ivDelete;
    }

    public List<BitmapItem> getBitmapItemList() {
        return bitmapItemList;
    }

    public void setBitmapItemList(List<BitmapItem> bitmapItemList) {
        this.bitmapItemList = bitmapItemList;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void resetBitmap(){
        new Task().execute(path);
    }

    public void addPicture(){
        File dir = new File(path);
        File files[] = dir.listFiles();
        PictureHelper.sortFileArray(files);
        //获取缩略图
        Bitmap bitmap = PictureHelper.getSmallBitmap(files[files.length-1].getAbsolutePath(), 240, 240);
        bitmapItemList.add(new BitmapItem(bitmap, files[files.length-1].getAbsolutePath()));
        notifyDataSetChanged();
    }

    class Task extends AsyncTask<String, Void, List<BitmapItem>>{
        @Override
        protected List<BitmapItem> doInBackground(String... params) {
            //要返回的数据
            List<BitmapItem> bitmapList = new ArrayList<>();
            //列出picture文件
            File[] files;
            File dir = new File(params[0]);
            if (dir.exists()) {
                files = dir.listFiles();
            } else {
                dir.mkdirs();
                files = dir.listFiles();
            }
            //整理顺序
            PictureHelper.sortFileArray(files);
            //将每个文件转化为bitmap
            for (File file : files) {
                //获取缩略图
                Bitmap bitmap = PictureHelper.getSmallBitmap(file.getAbsolutePath(), 240, 240);
                bitmapList.add(new BitmapItem(bitmap, file.getAbsolutePath()));
            }
            return bitmapList;
        }

        @Override
        protected void onPostExecute(List<BitmapItem> bitmapItems) {
            bitmapItemList = bitmapItems;
            PicGridAdapter.this.notifyDataSetInvalidated();
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            appCompatActivity.findViewById(R.id.id_pb_empty).setVisibility(View.GONE);
            super.onPostExecute(bitmapItems);
        }
    }
}
