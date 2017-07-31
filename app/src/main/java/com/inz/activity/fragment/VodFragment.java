package com.inz.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.inz.action.PlayAction;
import com.inz.action.PlayBackVideoListAction;
import com.inz.activity.PlayBackActivity;
import com.inz.activity.RecycleViewDivider;
import com.inz.adapter.VideoListRecyclerAdapter;
import com.inz.bean.CameraItemBean;
import com.inz.bean.VODRecord;
import com.inz.inz4g.R;
import com.inz.utils.AlerDialogUtils;

import java.util.ArrayList;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;
import pullrefreshview.support.view.LockFooterView;
import pullrefreshview.support.view.LockHeaderView;

/**
 * Created by Administrator on 2017/1/6.
 */

public class VodFragment extends Fragment implements VideoListRecyclerAdapter.OnItemClick,LockHeaderView.OnRefreshListener,LockFooterView.OnLoadListener{

    public static final int MSG_VIDEO_LIST_DATA_UPDATE          = 0xfe00;
    public static final int MSG_VIDEO_LIST_DATA_UPDATE_ERROR    = 0xfe01;
    public static final int MSG_VIDEO_LIST_DATA_REFREASH        = 0xfe02;
    public static final int MSG_VIDEO_LIST_DATA_LAST            = 0xfe03;
    public static final int MSG_VIDEO_GET_DATA_START            = 0xfe04;
    public static final int MSG_VOD_LIST_NONE                   = 0xfe05;

    RecyclerView mRv;
    VideoListRecyclerAdapter mAdapter;
    View mView;
    LockHeaderView mlhv;
    LockFooterView mlfv;
    ArrayList<VODRecord> mList = new ArrayList<>();
    CameraItemBean mBean;
    boolean mSearchUseTurn = false;
    boolean mISDestory = false;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mISDestory){Log.e("123","已经退出了");return;}
            switch (msg.what){
                case MSG_VIDEO_LIST_DATA_UPDATE:
                    Log.i("123","MSG_VIDEO_LIST_DATA_UPDATE");
                    mList.addAll(PlayBackVideoListAction.getInstance().getVodList());
                    mAdapter.setData(mList);
                    mlfv.stopLoad();
                    break;
                case MSG_VIDEO_LIST_DATA_UPDATE_ERROR:
                    Log.e("123","MSG_VIDEO_LIST_DATA_UPDATE_ERROR");
                    Snackbar.make(mRv,getResources().getString(R.string.vod_page_error),Snackbar.LENGTH_LONG).show();
                    break;
                case MSG_VIDEO_LIST_DATA_REFREASH:
                    mAdapter.setData(mList);
                    mlhv.stopRefresh();
                    break;
                case MSG_VIDEO_LIST_DATA_LAST:
                    try {//maybe fragment not attach to activity
                        Snackbar.make(mRv,getResources().getString(R.string.vod_last_page),Snackbar.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case MSG_VIDEO_GET_DATA_START:
                    getData(mSearchUseTurn);
                    break;
                case MSG_VOD_LIST_NONE:
                    Log.i("123","MSG_VOD_LIST_NONE");
                    mSearchUseTurn = false;
                    getData(mSearchUseTurn);
                    mSearchUseTurn = true;
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_vod_list,container,false);
        initView();
        initFun();
        return mView;
    }

    @Override
    public void onDestroyView() {
        mISDestory = true;
        super.onDestroyView();
    }

    private void initView(){
        mRv = (RecyclerView) mView.findViewById(R.id.fragment_vod_rv);
        mlhv = (LockHeaderView) mView.findViewById(R.id.fragment_vod_header);
        mlhv.setOnRefreshListener(this);
        mlfv = (LockFooterView) mView.findViewById(R.id.fragment_vod_footer);
        mlfv.setOnLoadListener(this);
        mISDestory = false;
    }

    private void initFun(){
        mSearchUseTurn = false;
        mAdapter = new VideoListRecyclerAdapter(this);
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(new RecycleViewDivider(getContext(),LinearLayoutManager.HORIZONTAL,2,getResources().getColor(R.color.black)));
        PlayBackVideoListAction.getInstance().setHandler(mHandler);
        if (PlayBackVideoListAction.getInstance().hasVod()) {
            Log.e("123","vod fragment init fun get fun");
            boolean b = PlayBackVideoListAction.getInstance().getNeedInitDelay();
            mHandler.sendEmptyMessageDelayed(MSG_VIDEO_GET_DATA_START,b?1000:0);
        }else{
            AlerDialogUtils.postDialogMsg(getContext(),
                    getResources().getString(R.string.no_estore),
                    getResources().getString(R.string.no_sdcard),null);
        }
    }

    private void getData(boolean bTurn){

        if (PlayBackVideoListAction.getInstance().getNeedInitDelay()){
            PlayBackVideoListAction.getInstance().initAgain();
        }

        PlayBackVideoListAction.getInstance().searchVODList(bTurn);
    }

    public void searchList(String startTime,String endTime){
        mList.clear();
        PlayBackVideoListAction.getInstance().reset();
        PlayBackVideoListAction.getInstance().setSearchTime(startTime,endTime);
        if (PlayBackVideoListAction.getInstance().hasVod()) {
            Log.i("123","has vod so get data ~~~~~~~~~~~~~");
            getData(mSearchUseTurn);
        }else{
            AlerDialogUtils.postDialogMsg(getContext(),
                    getResources().getString(R.string.no_estore),
                    getResources().getString(R.string.no_sdcard),null);
        }
    }

    public void setBean(CameraItemBean b){
        mBean = b;
    }


    private void getData(int n){
        for (int i=0;i<n;i++){
            VODRecord record = new VODRecord();
            record.setStartTime("2017-01-04 13:57:00  00:00:00");
            record.setEndTime("2017-01-04 13:57:00   11:11:11");
            record.setTimeZoneStartTime("2017-01-04 13:57:00  00:00:00  ");
            record.setTimeZoneEndTime("2017-01-04 13:57:00   11:11:11");
            if (mList==null){
                mList = new ArrayList<>();
            }
            mList.add(record);
        }
        mHandler.sendEmptyMessage(MSG_VIDEO_LIST_DATA_UPDATE);
    }


    @Override
    public void onItemClickListener(View v, int pos) {
        VODRecord record = mList.get(pos);
        String startTime = record.getStartTime();
        String endTime = record.getEndTime();
        Log.i("123","startTime="+startTime+"  zoneTime="+record.getTimeZoneStartTime());
        PlayAction.getInstance().setPlayBean(mBean);

        Intent intent = new Intent(getContext(), PlayBackActivity.class);
        intent.putExtra("startTime",startTime);
        intent.putExtra("endTime",endTime);

        getContext().startActivity(intent);
    }

    @Override
    public void onLoad(BaseFooterView baseFooterView) {
        PlayBackVideoListAction.getInstance().loadVODList();
        baseFooterView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mlfv.stopLoad();
            }
        },1000);
    }

    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {
         mList.clear();
         PlayBackVideoListAction.getInstance().refreashVODList();
         baseHeaderView.postDelayed(new Runnable() {
             @Override
             public void run() {
                 mlhv.stopRefresh();
             }
         },1000);
    }
}
