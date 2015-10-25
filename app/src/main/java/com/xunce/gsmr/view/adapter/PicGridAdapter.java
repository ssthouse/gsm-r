package com.xunce.gsmr.view.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xunce.gsmr.R;
import com.xunce.gsmr.model.BitmapItem;
import com.xunce.gsmr.view.widget.CustomImageView;
import com.xunce.gsmr.util.PictureHelper;

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
        this.path = path;
        bitmapItemList = new ArrayList<>();
        new Task().execute(path);
    }

    @Override
    public int getCount() {
//        LogHelper.log(TAG, bitmapItemList.size()+"");
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
            convertView = LayoutInflater.from(context).inflate(R.layout.view_grid_item, null);
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
        new Task().execute(path);
        super.notifyDataSetChanged();
    }

    public void addPicture(){
        notifyDataSetChanged();
    }

    class Task extends AsyncTask<String, Void, List<BitmapItem>>{
        @Override
        protected List<BitmapItem> doInBackground(String... params) {
            return PictureHelper.getBitmapItemList(path);
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
