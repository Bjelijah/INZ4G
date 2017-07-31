package com.inz.adapter;

/**
 * Created by Administrator on 2017/7/27.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inz.bean.CameraItemBean;
import com.inz.inz4g.R;
import com.inz.utils.IConfig;
import com.inz.utils.PhoneConfig;
import com.inz.utils.ScaleImageUtils;

import java.io.File;
import java.util.List;


/**
 * Created by Administrator on 2017/6/27.
 */

public class INZDeviceRecyclerViewAdapter extends RecyclerView.Adapter<INZDeviceRecyclerViewAdapter.ViewHolder> implements IConfig {

    List<CameraItemBean> mList;
    private OnItemClickListener mClickListener;
    Activity mActivity;
    Context mContext;
    int imageWidth,imageHeight;


    public INZDeviceRecyclerViewAdapter(){}


    public INZDeviceRecyclerViewAdapter(Context c,OnItemClickListener o){
        mContext = c;
        imageWidth = PhoneConfig.getPhoneWidth(c)/2;
        imageHeight = imageWidth *10 / 16;
        mClickListener = o;
    }


    public void setData(List<CameraItemBean >l){
        mList = l;
        notifyDataSetChanged();
    }
    public List<?>getData(){return mList;}


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera_inz,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    private void init(ViewHolder holder,CameraItemBean item){
//        holder.ivCamera.setLayoutParams(new FrameLayout.LayoutParams(imageWidth,imageHeight));
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 2;
//        Bitmap bm = null;
//        try{  bm = ScaleImageUtils.decodeFile(imageWidth,imageHeight,new File(item.getPicturePath()));}catch (Exception e){}
//        if (bm==null){
//            holder.ivCamera.setImageResource(R.mipmap.card_camera_default_image2);
////            holder.ivCamera.setImageBitmap(null);
////            holder.ivCamera.setBackgroundColor(mContext.getResources().getColor(R.color.item_camera_video));
//        }else{
//            holder.ivCamera.setImageBitmap(bm);
//        }
//        holder.tvName.setText(item.getCameraName());

        if (item.isOnline()){

//            holder.ivInOffLine.setImageResource( R.mipmap.card_online_image_blue);
            holder.tvInOff.setText(mContext.getString(R.string.camera_online));
            Log.e("123","!!!!!!indensity="+item.getIndensity());
            if (item.getIndensity()>75){
                holder.ivWifi.setImageResource(R.mipmap.wifi_4);
            }else if(item.getIndensity()>50){
                holder.ivWifi.setImageResource(R.mipmap.wifi_3);
            }else if(item.getIndensity()>25){
                holder.ivWifi.setImageResource(R.mipmap.wifi_2);
            }else if(item.getIndensity()>0){
                holder.ivWifi.setImageResource(R.mipmap.wifi_1);
            }else{
                holder.ivWifi.setImageResource(R.mipmap.wifi_0);
            }
        }else{
//            holder.ivInOffLine.setImageResource( R.mipmap.card_offline_image_gray);
            holder.ivWifi.setImageResource(R.mipmap.wifi_0);
            holder.tvInOff.setText(mContext.getString(R.string.camera_offline));
        }
    }

    private void initClick(final ViewHolder holder,final int pos,final CameraItemBean item){
        holder.llplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("123","onclick   !!");
                mClickListener.onItemVideoClickListener(v,holder.getItemView(),pos);
            }
        });

        holder.llback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemVideoClickListener(v,holder.getItemView(),pos);
                }
        });


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CameraItemBean item = mList.get(position);
        init(holder,item);
        initClick(holder,position,item);
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    public interface OnItemClickListener{
        void onItemVideoClickListener(View v,View itemView,int pos);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView tvInOff;

        LinearLayout llback,llplay;
        ImageView ivWifi;
        public View getItemView(){return itemView;}
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvInOff = (TextView) itemView.findViewById(R.id.item_camera_tv_onoffline);
            llplay = (LinearLayout) itemView.findViewById(R.id.item_camera_ll_play);
            llback = (LinearLayout) itemView.findViewById(R.id.item_camera_info);
            ivWifi = (ImageView) itemView.findViewById(R.id.item_camera_iv_wifi_idensity);
        }
    }
}

