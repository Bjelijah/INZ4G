package com.inz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.inz.bean.MediaItemBean;
import com.inz.inz4g.R;
import com.inz.utils.PhoneConfig;
import com.inz.utils.ScaleImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/11/25.
 */

public class MediaRecyclerViewAdapter extends RecyclerView.Adapter<MediaRecyclerViewAdapter.ViewHolder> {

    List<MediaItemBean>mList;
    Context mContext;
    OnItemClickListener mListener;
    List<Pair<Integer,Integer>> mRects;//width,height

    public MediaRecyclerViewAdapter(Context context, List<MediaItemBean>list, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
        setData(list);
    }

    public MediaRecyclerViewAdapter(Context context, OnItemClickListener listener){
        this.mContext = context;
        this.mListener = listener;
    }

    public List<?>getData(){
        return mList;
    }
    public void setData(List<MediaItemBean>l){
        this.mList = l;
        getRandomHeight();
    }

    private void getRandomHeight(){
        if (mList==null)return;
        int width = PhoneConfig.getPhoneWidth(mContext) / 3 ;
        int height = width *3/4;

        mRects = new ArrayList<Pair<Integer, Integer>>();
        for (int i=0;i<mList.size();i++){
            mRects.add(new Pair<Integer, Integer>(width,height ));
        }


    }


    private void init(ViewHolder holder,MediaItemBean item,int pos ){

        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = mRects.get(pos).first;
        params.height = mRects.get(pos).second;
        holder.itemView.setLayoutParams(params);
        String path = item.getPicturePath();
      //  Log.e("123"," width= "+params.width+"   height= "+params.height+"   path="+path);

        Bitmap bm = ScaleImageUtils.decodeFile(params.width,
                params.height,new File(path));
        holder.iv.setImageBitmap(bm);
    }


    private void initClick(ViewHolder holder,final int pos){
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClickListener(view,pos);
            }
        });
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MediaItemBean item = mList.get(position);
        init(holder,item,position);
        initClick(holder,position);
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        public ViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.item_media_iv);
        }
    }
    public interface OnItemClickListener{
        void onItemClickListener(View v, int pos);
    }


}
