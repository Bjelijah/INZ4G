package com.inz.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.howell.player.AudioAction;
import com.howell.protocol.utils.SDKDebugLog;
import com.inz.action.PlayAction;
import com.inz.activity.view.ZoomableTextureView;
import com.inz.inz4g.R;
import com.inz.utils.AlerDialogUtils;
import com.inz.utils.PhoneConfig;
import com.inz.utils.UserConfigSp;


/**
 * Created by Administrator on 2016/12/16.
 */

public class PlayViewActivity extends BasePlayActivity implements GestureDetector.OnGestureListener,View.OnTouchListener,View.OnClickListener {

    private GestureDetector mGestureDetector;
    private boolean mIsShowPtz;


    ZoomableTextureView.OnTouchCb mcb = new ZoomableTextureView.OnTouchCb() {
        @Override
        public void onTouchCb(View view, MotionEvent motionEvent) {
            if (PhoneConfig.getPhoneHeight(PlayViewActivity.this)>PhoneConfig.getPhoneWidth(PlayViewActivity.this))return;
            if (isShowSurfaceIcon){
                showSurfaceIcon(false);
//            mPlayFun.setBottomView(null);
            }else{
                showSurfaceIcon(true);
//            mPlayFun.setBottomView(mSurfaceIcon);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKDebugLog.LogEnable(true);
        initPlayView();
        initFun();
        //start();
    }

    @Override
    protected void onDestroy() {
        AudioAction.getInstance().deInitAudio();
        super.onDestroy();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

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
        Log.i("123","play view cam play");
        super.camPlay();
    }


    @Override
    protected void camStop() {
        super.camStop();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (PhoneConfig.getPhoneHeight(this)>PhoneConfig.getPhoneWidth(this))return false;
        if (isShowSurfaceIcon){

            showSurfaceIcon(false);
//            mPlayFun.setBottomView(null);
        }else{

            showSurfaceIcon(true);
//            mPlayFun.setBottomView(mSurfaceIcon);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (!PlayAction.getInstance().getPlayBean().isPtz()){
           // return false;
        }

        final int hMax = PhoneConfig.getPhoneHeight(this);
        final int wMax = PhoneConfig.getPhoneWidth(this);
        if (hMax > wMax) {
            Log.e("123", "竖屏");
            return false;
        }


        final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;

        if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY && e1.getX() < (wMax/2)) {
            Log.e("123", "fling up");


            if (mIsShowPtz) {//当前显示  从当中到最上
                //要不显示显示

                mIsShowPtz = false;
            }else{//当前不显示 从最下到当中
                //要显示

                mIsShowPtz = true;
            }

        }else if(e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY && e1.getX() < (wMax/2)){
            Log.e("123", "fling down");

            if(mIsShowPtz){//当前显示  从当中到最下
                //要不显示

                mIsShowPtz = false;
            }else{//从最上到当中

                mIsShowPtz = true;
            }

        }
        return true;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouch(v,event);

        return mGestureDetector.onTouchEvent(event);
//        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.pop_layout_sd:
                mPopupWindow.dismiss();
                PlayAction.getInstance().rePlay(1);
                break;
            case R.id.pop_layout_hd:
                mPopupWindow.dismiss();
                PlayAction.getInstance().rePlay(0);
                break;
//            case R.id.sound:
//                this.soundFun();
//                break;
            case R.id.vedio_list:
                showVodFun();
                break;
            case R.id.catch_picture:
                PlayAction.getInstance().catchPic();
                break;
            case R.id.player_change_stream:
                mPopupWindow.showAsDropDown(v);
                break;
            case R.id.player_imagebutton_back:
                PlayAction.getInstance().catchPic("/sdcard/eCamera/cache");
                //TODO: stop play

                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            PlayAction.getInstance().catchPic("/sdcard/eCamera/cache");
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showVodFun(){
        //if
        if (!PlayAction.getInstance().getPlayBean().isStore()){
            AlerDialogUtils.postDialogMsg(this,
                    getResources().getString(R.string.no_estore),
                    getResources().getString(R.string.no_sdcard),null);
            return;
        }
        //stop
        PlayAction.getInstance().catchPic("/sdcard/eCamera/cache");
        finish();


        //goto activity
        Intent intent = new Intent(PlayViewActivity.this, VideoListActivity.class);
        intent.putExtra("bean",PlayAction.getInstance().getPlayBean());
        intent.putExtra("playview",true);
        startActivity(intent);
    }


    private void initPlayView(){
        mReplaySeekBar.setVisibility(View.GONE);
        mGlView.setOnTouchCallback(mcb);
        mGlView.setFocusable(true);
        mGlView.setClickable(true);
        mGlView.setLongClickable(true);

    }

    private void initFun(){
        mGestureDetector = new GestureDetector(this,this);


//        mSound.setOnClickListener(this);
        mVodList.setOnClickListener(this);
        mCatchPicture.setOnClickListener(this);
        mHD.setOnClickListener(this);
        mSD.setOnClickListener(this);
        mStreamChange.setOnClickListener(this);
        mBack.setOnClickListener(this);

    }









    private void setOrientation(boolean isTalking){
        int ro =getRequestedOrientation();
        if (isTalking) {
//            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){//2   shu
//
//                showSurfaceIcon(false);
////            if (mIsTalk)
//            } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){//1  heng
//                showSurfaceIcon(true);
////            if (mIsTalk)
//            }
            Log.i("123","orientation    = "+this.getResources().getConfiguration().orientation);
            setRequestedOrientation(this.getResources().getConfiguration().orientation);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        Log.i("123","ro="+ro);
    }


    @Override
    protected void start(){
        super.start();
        PlayAction.getInstance().setPlayBack(false);
        this.camConnect();
    }

    private void end(){

    }

    @Override
    protected void soundFun() {
        super.soundFun();
        UserConfigSp.saveSoundState(PlayViewActivity.this,mIsAudioOpen);
//        mPlayFun.updataAllView();
    }


    @Override
    public void showStreamSpeed(int kbitPerSec, long time, long first) {
        super.showStreamSpeed(kbitPerSec,time,first);



    }
}
