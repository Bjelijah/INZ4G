package com.inz.activity.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.inz.activity.BigImagesActivity;
import com.inz.adapter.MediaRecyclerViewAdapter;
import com.inz.bean.MediaItemBean;
import com.inz.inz4g.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by howell on 2016/11/11.
 */

public class MediaFragment extends HomeBaseFragment implements MediaRecyclerViewAdapter.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener {
    private final static int MSG_MEDIA_UPDATA = 0x10;
    SwipeRefreshLayout mSrl;
    View mView;
    RecyclerView mRv;
    List<MediaItemBean> mList = new ArrayList<MediaItemBean>();
    ArrayList<String> mPicPath ;
    MediaRecyclerViewAdapter mAdapter;
    LinearLayout noImageBg;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_MEDIA_UPDATA:
                    mAdapter.setData(mList);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }

        }
    };



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_media,container,false);
        mSrl = (SwipeRefreshLayout) mView.findViewById(R.id.media_srl);
        mSrl.setOnRefreshListener(this);
        mRv = (RecyclerView) mView.findViewById(R.id.media_rv);
        mAdapter = new MediaRecyclerViewAdapter(getContext(),this);
        mRv.setAdapter(mAdapter);
        mRv.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));

        noImageBg = (LinearLayout)mView.findViewById(R.id.meida_ll_loacl_file_no_image);
        getData();
        return mView;
    }




    private ArrayList<String> getFileName(File file){
        File[] fileArray = file.listFiles();
        ArrayList<String>   list = new ArrayList<String>();
        Log.i("123","fileArray="+fileArray);
        if (fileArray==null)return list;
        for (File f : fileArray) {
            if(f.isFile() && !list.contains(f.getPath())){
                list.add(f.getPath());
            }
        }
        Collections.sort(list, new SortByDate());
        return list;
    }

    @Override
    public void getData(){
        mList.clear();
        File f = new File("/sdcard/eCamera");
        mPicPath = getFileName(f);
        noImageBg.setVisibility(mPicPath.size()==0?View.VISIBLE:View.GONE);
        for (String s:mPicPath){
            MediaItemBean b = new MediaItemBean(s);
            mList.add(b);
        }

        mHandler.sendEmptyMessage(MSG_MEDIA_UPDATA);
    }


    @Override
    public void onItemClickListener(View view,int pos) {
        Log.i("123","media on item click  pos="+pos);
        //TODO : show big picture;
        Intent intent = new Intent(getContext(), BigImagesActivity.class);
        intent.putExtra("position", pos);
        intent.putStringArrayListExtra("arrayList", mPicPath);



        //startActivity(intent);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(),view,"myImage").toBundle());
        //overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mList.clear();
                getData();
                mSrl.setRefreshing(false);
            }
        },1000);
    }

    class SortByDate implements Comparator {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s2.compareTo(s1);
        }
    }
}
