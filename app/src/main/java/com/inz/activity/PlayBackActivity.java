package com.inz.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;


import com.howell.jni.JniUtil;
import com.inz.action.PlayAction;
import com.inz.inz4g.R;
import com.inz.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/1/10.
 */

public class PlayBackActivity extends BasePlayActivity implements View.OnClickListener,PlayAction.IPlayBackFun,SeekBar.OnSeekBarChangeListener{


    private long mCurBeg = 0;
    private boolean mIsPause = false;
    private long mLastProgressOffset = 0;

    private long mAllSec;
    private Date mBegDate,mEndDate;
    private long mNewBeg=0;
    private String mStartTime,mEndTime;
    private boolean isUserControl=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPlayBackView();
        initFun();
        start();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MSG_PLAY_PLAY_BACK_FUN);
        PlayAction.getInstance().unregistPlayBackCallback();
        super.onDestroy();
    }

    private void initPlayBackView(){
//        boolean isAllow = PlayAction.getInstance().isSeekBarAllow();
        boolean isAllow = true;
        mReplaySeekBar.setVisibility(isAllow?View.VISIBLE:View.GONE);
        mPause.setVisibility(isAllow?View.VISIBLE:View.GONE);
        mStreamChange.setVisibility(View.GONE);
        mVodList.setVisibility(View.GONE);
    }


    private void initFun(){
        mStartTime = getIntent().getStringExtra("startTime");
        mEndTime = getIntent().getStringExtra("endTime");
        mBack.setOnClickListener(this);
        mCatchPicture.setOnClickListener(this);

        mReplaySeekBar.setOnSeekBarChangeListener(this);
        mPause.setOnClickListener(this);
        PlayAction.getInstance().setPlayBack(true).setPlayBackTime(mStartTime,mEndTime).registPlayBackCallback(this);

        mBegDate = Util.ISODateString2ISODate(mStartTime);
        mEndDate = Util.ISODateString2ISODate(mEndTime);
        mAllSec  = mEndDate.getTime()-mBegDate.getTime();
        Log.i("123","mAllsec="+mAllSec);
        mReplaySeekBar.setMax((int) mAllSec);
    }

    private String translateTime(int progress){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        String text = sdf.format(mCurBeg*1000 + progress);
//        Log.i("123"," cur progress+offset= "+mCurBeg*1000 + progress+"                 progress="+progress);
        return text;
    }




    private void start(){
        mIsPause = false;
        PlayAction.getInstance().setPlayBackProgressByUser(false);
        this.camConnect();
    }

    private void pause(){
        if (mIsPause){//现在播放
            mIsPause = false;
            mPause.setImageDrawable(getResources().getDrawable(R.mipmap.img_pause));
            mHandler.sendEmptyMessageDelayed(MSG_PLAY_PLAY_BACK_FUN,200);
        }else{//现在暂停
            mIsPause = true;
            mPause.setImageDrawable(getResources().getDrawable(R.mipmap.img_play));
            mHandler.removeMessages(MSG_PLAY_PLAY_BACK_FUN);
        }
        PlayAction.getInstance().camPlayBackPause(mIsPause,mCurBeg,mReplaySeekBar.getProgress());
    }


    @Override
    protected void camConnect() {
        super.camConnect();
    }

    @Override
    protected void camDisconnect() {
        super.camDisconnect();
    }

    @Override
    protected void camPlay() {
        super.camPlay();
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PLAY_BACK_FUN,200);
    }

    @Override
    protected void camStop() {
        super.camStop();
    }

    @Override
    protected void reLinkServer(){
        super.reLinkServer();
        Log.e("123","relink Server");
        PlayAction.getInstance().reLinkServerAndLink();
        mHandler.sendEmptyMessage(MSG_PLAY_PLAY_WAIT);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.player_imagebutton_back:
                finish();
                break;
//            case R.id.sound:
//                soundFun();
//                break;
            case R.id.catch_picture:
                PlayAction.getInstance().catchPic();
                break;
            case R.id.ib_pause:
                pause();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayBackBegEnd(long beg, long end) {//秒数
        //fixme no used
        int max = (int)((end-beg)*1000);//开始到结束时间的毫秒
        mCurBeg = beg;
        Log.i("123","!!!!!!!!!!onPlayback beg="+beg+" end="+end+"    max="+max);

        mReplaySeekBar.setMax((int)((end-beg)*1000));
    }

    @Override
    protected void playBackFun() {
        //FIXME



        /*
        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PLAY_BACK_FUN,200);
        if (PlayAction.getInstance().getPlayBackKeepProgress())return;
        long begTimestamp = JniUtil.getBegPlayTimestamp();
        if (begTimestamp == 0) {
            return;
        }
        long curTimestamp = JniUtil.getCurPlayTimestamp();
        long offset = curTimestamp - begTimestamp;

        Log.i("123","offset=            "+offset);
        if (mLastProgressOffset!=offset) {
            mReplaySeekBar.setProgress((int) offset);
            mLastProgressOffset = offset;
        }
        */

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        mReplaySeekBar.setSeekBarText(translateTime(progress));

        if (fromUser){
//            Log.e("123","progress="+progress);
            try {
                long time = progress + mBegDate.getTime();
                Date date = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String str = sdf.format(date);
                mReplaySeekBar.setSeekBarText(str);
            }catch (Exception e){e.printStackTrace();}
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isUserControl = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        try{ isUserControl = false;
            mNewBeg = mBegDate.getTime()+seekBar.getProgress();
            Date data = new Date(mNewBeg);
            String str = Util.Date2ISODate(data);

            PlayAction.getInstance().setPlayBackTime(str,mEndTime);
            PlayAction.getInstance().playBackRePlay(0,0);
        }catch (Exception e){e.printStackTrace();}



//        PlayAction.getInstance().setPlayBackProgressByUser(false);
//        int progress = seekBar.getProgress();
//        mLastProgressOffset = progress;
//        PlayAction.getInstance().playBackRePlay(mCurBeg,progress);
//        mHandler.sendEmptyMessageDelayed(MSG_PLAY_PLAY_BACK_FUN,1000);
    }

    @Override
    public void showStreamSpeed(int kbitPerSec, long time, long first) {
        super.showStreamSpeed(kbitPerSec, time, first);
        if (!isUserControl){
            long o = mNewBeg-mBegDate.getTime();
            int progress = (int)((o>0?o:0) + (time-first));
//            Log.e("123","progress="+progress+"     max="+mReplaySeekBar.getMax()
//            +"  newBeg="+mNewBeg+"  begTime="+mBegDate.getTime()+"  time="+time+  "  first="+first+"  offset="+(time-first));
            mReplaySeekBar.setProgress(progress);
        }

    }
}
