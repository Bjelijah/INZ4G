package com.inz.action;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


import com.inz.activity.fragment.VodFragment;
import com.inz.bean.CamFactory;
import com.inz.bean.CameraItemBean;
import com.inz.bean.VODRecord;
import com.inz.player.ICam;
import com.inz.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/1/4.
 */

public class PlayBackVideoListAction {
    private static PlayBackVideoListAction mInstance = null;
    public static PlayBackVideoListAction getInstance(){
        if (mInstance == null){
            mInstance = new PlayBackVideoListAction();
        }
        return mInstance;
    }
    private PlayBackVideoListAction(){}
    boolean mSearchUseTurn = true;
    Context mContext;
    CameraItemBean mBean;
    Handler mHandler;
    ICam mCam;

    int mCurPage;
    int mTotalPage;
    boolean needInitDelay = false;
    ArrayList<VODRecord> mVodList = null;
    String mLastVODTime = "";
    String mStartTime=null,mEndTime=null;


    public ArrayList<VODRecord> getVodList(){
        Log.e("123","mCam="+mCam);
        mVodList = mCam.getVideoList();
        Log.e("123"," mVODLISt size="+mVodList.size());
        if (mVodList.size()!=0){
            mTotalPage = mVodList.size();
        }
        return mVodList;
    }
    public void setNeedInitDelay(boolean b){
        needInitDelay = b;
    }
    public boolean getNeedInitDelay(){
        return needInitDelay;
    }

    public PlayBackVideoListAction setHandler(Handler h){
        mHandler = h;
        if (mCam!=null){
            mCam.setHandler(h);
        }
        return this;
    }
    public PlayBackVideoListAction setCam(ICam c){
        mCam = c;
        return this;
    }

    public PlayBackVideoListAction initAgain(){
        mCam.init(mContext,mBean);
        return reset();
    }

    public PlayBackVideoListAction init(Context context,CameraItemBean bean){
        mContext = context;
        mBean = bean;
        mCam = CamFactory.buildCam(bean.getType());
        mCam.init(mContext,bean);
        return reset();
    }

    public void deInit(){
        mBean = null;
        mCam.deInit();
        mCam = null;
        Log.e("123","play back video list action   deInit");
        mHandler = null;
    }

    public PlayBackVideoListAction reset(){
        mCurPage = 1;//从1开始计算
        mTotalPage = 1;
        mLastVODTime = "";
        return this;
    }

    public void setSearchTime(String startTime,String endTime){
        mStartTime = startTime;
        mEndTime = endTime;
    }

    private void initNowTime(){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        Date begDate = null;
        if (mSearchUseTurn){
            begDate = calendar.getTime();
        }else{
            begDate = new Date(1970 - 1900, 1 - 1, 1, 0, 0, 0);
        }

        //FIXME  时间用     1天前到当前时间
        mStartTime = Util.Date2ISODate(begDate);
        mEndTime = Util.Date2ISODate(endDate);
//        mStartTime = sdf.format(begDate);
//        mEndTime = sdf.format(endDate);
    }
    public boolean hasVod(){
        if (mCam==null)return false;
        return mCam.hasVideoList();
    }

    public void searchVODList(final boolean useTurn){
        if (mStartTime==null||mEndTime==null){
            initNowTime();
        }
        mSearchUseTurn = useTurn;

        new AsyncTask<Void,Void,Boolean>(){

            private void listFun(ArrayList<VODRecord> list){
                if (list==null)return;
                for (VODRecord v:list){
                    if (!mLastVODTime.equals(v.getTimeZoneStartTime().substring(0,10))){
                        mLastVODTime = v.getTimeZoneStartTime().substring(0,10);
                        v.setHasTitle(true);
                    }
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                if (mCam==null){
                    Log.e("123","mCam=null");
                    return false;
                }

                if (mCurPage>mTotalPage){
                    Log.e("123","mCurPage>mTotalPage  mCurPage="+mCurPage+"  mTotalPage="+mTotalPage+"   useTurn="+useTurn);
                    if (!useTurn){
                        return false;
                    }
                }
                Log.i("123","mCurpage    mStartTime="+mStartTime+"   mEndTime="+mEndTime);
                mCam.setVideoListTime(mStartTime,mEndTime);

                mTotalPage = mCam.getVideoListPageCount(mCurPage,useTurn?0:20);// fixme  turn  0
                if (mTotalPage==-1)return false;
//                Log.i("123","mTotalPage="+mTotalPage);
//                mVodList = mCam.getVideoList();
//                Log.e("123"," mVODLISt size="+mVodList.size());
//                mTotalPage = mVodList.size();
//                listFun(mVodList);
//                return mVodList==null?false:true;
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mHandler==null)return;
                if (aBoolean){
                    //fixme send in mgr
                   // mHandler.sendEmptyMessage(VodFragment.MSG_VIDEO_LIST_DATA_UPDATE);
                }else {
                    if (mTotalPage==-1){
                        mHandler.sendEmptyMessage(VodFragment.MSG_VIDEO_LIST_DATA_UPDATE_ERROR);
                        return;
                    }
                    mHandler.sendEmptyMessage((mCurPage>=mTotalPage+1)?VodFragment.MSG_VIDEO_LIST_DATA_LAST:VodFragment.MSG_VIDEO_LIST_DATA_UPDATE_ERROR);
                }
            }
        }.execute();
    }



    public void refreashVODList(){
//        init(mContext,mBean);
        reset();
        initNowTime();
        searchVODList(mSearchUseTurn);
    }

    public void loadVODList(){
        if (!mSearchUseTurn){
            ++mCurPage;
            searchVODList(mSearchUseTurn);
        }else{
            mHandler.sendEmptyMessage(VodFragment.MSG_VIDEO_LIST_DATA_LAST);
        }
    }
}
