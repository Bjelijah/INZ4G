package com.inz.activity;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inz.action.PlayAction;
import com.inz.activity.view.MySeekBar;
import com.inz.activity.view.PlayGLTextureView;
import com.inz.activity.view.ZoomableTextureView;
import com.inz.bean.CamFactory;
import com.inz.inz4g.R;
import com.inz.player.ICam;
import com.inz.player.gles.GLESTextureView;
import com.inz.utils.AlerDialogUtils;
import com.inz.utils.MessageUtiles;
import com.inz.utils.PhoneConfig;
import com.inz.utils.UserConfigSp;


/**
 * Created by Administrator on 2016/12/16.
 */

public class BasePlayActivity extends FragmentActivity implements SurfaceHolder.Callback,View.OnTouchListener,ICam.IStream{

    public static final int MSG_PTZ_SHAKE               = 0xff00;
    public final static int MSG_PLAY_SOUND_MUTE         = 0xff01;
    public final static int MSG_PLAY_SOUND_UNMUTE       = 0xff02;
    public final static int MSG_PLAY_SAVE_PICTURE       = 0xff03;
    public final static int MSG_PLAY_LOGIN_CAM_OK       = 0xff04;
    public final static int MSG_PLAY_LOGIN_CAM_ERROR    = 0xff05;
    public final static int MSG_PLAY_PLAY_CAM_OK        = 0xff06;
    public final static int MSG_PLAY_PLAY_CAM_ERROR     = 0xff07;
    public final static int MSG_PLAY_PLAY_WAIT          = 0xff08;
    public final static int MSG_PLAY_PLAY_UNWAIT        = 0xff09;
    public final static int MSG_PLAY_STOP_CAM_OK        = 0xff0a;
    public final static int MSG_PLAY_STOP_CAM_ERROR     = 0xff0b;
    public final static int MSG_PLAY_LOGOUT_CAM_OK      = 0xff0c;
    public final static int MSG_PLAY_LOGOUT_CAM_ERROR   = 0xff0d;
    public final static int MSG_PLAY_RELINK_OK          = 0xff0e;
    public final static int MSG_PLAY_PLAY_BACK_FUN      = 0xff0f;
    public final static int MSG_PLAY_RELINK_START       = 0xff10;
    public final static int MSG_PLAY_RELINK_SERVER      = 0xff11;
    public final static int MSG_PLAY_PLAY_START         = 0xff12;



    //控件
//    protected GLSurfaceView mGlView;
    protected PlayGLTextureView mGlView;

    protected ImageButton mVodList,mCatchPicture,mPause,mBack;
    protected FrameLayout mTitle;
    protected TextView mStreamChange;
    protected MySeekBar mReplaySeekBar;
    protected LinearLayout mSurfaceIcon,mHD,mSD;
    protected ProgressBar mWaitProgressBar;

    protected PopupWindow mPopupWindow;
    protected TextView mStreamLen;
    protected boolean isShowSurfaceIcon = true;
    protected ICam mPlayMgr;
    protected boolean mIsAudioOpen = false;
    protected boolean mIsTalk;

    protected boolean mIsDestory = false;


    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mIsDestory){Log.e("123","basePlayActivity 已经退出了  msg="+String.format("0x%x",msg.what));return;}
            switch (msg.what){
                case MSG_PTZ_SHAKE:
//                    PTZControlAction.getInstance().ptzShake(BasePlayActivity.this, (View)msg.obj);
                    break;
                case MSG_PLAY_RELINK_START:
                    camReLink();
                    break;
                case MSG_PLAY_RELINK_OK:
                    Log.i("123","get play re link ok");
                    if (msg.arg1==1){
                        mStreamChange.setText("标清");
                    }else if(msg.arg1==2){
                        mStreamChange.setText("高清");
                    }
                    break;
                case MSG_PLAY_SOUND_MUTE:
//                    mSound.setImageDrawable(getResources().getDrawable(R.mipmap.img_no_sound));
                    mIsAudioOpen = false;
                    break;
                case MSG_PLAY_SOUND_UNMUTE:
//                    mSound.setImageDrawable(getResources().getDrawable(R.mipmap.img_sound));
                    mIsAudioOpen = true;
                    break;
                case MSG_PLAY_SAVE_PICTURE:
                    MessageUtiles.postToast(getApplicationContext(),getResources().getString(R.string.save_picture), Toast.LENGTH_SHORT);
                    break;
                case MSG_PLAY_LOGIN_CAM_OK:
                    Log.e("123","MSG_PLAY_LOGIN_CAM_OK");
                    camPlay();
                    break;
                case MSG_PLAY_PLAY_CAM_ERROR:
                    Log.e("123","play cam error");
                    playErrorFun();

                    break;
                case MSG_PLAY_LOGIN_CAM_ERROR:
                    Log.e("123","MSG_PLAY_LOGIN_CAM_ERROR");
                    playErrorFun();
                    break;
                case MSG_PLAY_PLAY_WAIT:
                    mWaitProgressBar.setVisibility(View.VISIBLE);
                    break;
                case MSG_PLAY_PLAY_UNWAIT:
                  //  Log.e("123","~~~~~~~~~~~~MSG_PLAY_PLAY_UNWAIT");
                    mWaitProgressBar.setVisibility(View.GONE);
                    break;
                case MSG_PLAY_PLAY_BACK_FUN:
                    playBackFun();
                    break;
                case MSG_PLAY_RELINK_SERVER:
                    reLinkServer();
                    break;
                case MSG_PLAY_PLAY_START:
                    start();
                    break;
                default:
                    break;
            }
        }
    };

    protected void reLinkServer(){

    }


    private void playErrorFun(){
        mWaitProgressBar.setVisibility(View.GONE);
        AlerDialogUtils.postDialogMsg(BasePlayActivity.this,
                getResources().getString(R.string.play_play_error_msg_title),
                getResources().getString(R.string.play_play_error_msg_msg),null);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glsurface);
        initView();
        initViewFun();
        mHandler.sendEmptyMessage(MSG_PLAY_PLAY_START);
    }


    protected void start(){
        initPlayer();
    }

    protected void initView(){
        mIsDestory = false;
        mGlView = (PlayGLTextureView) findViewById(R.id.gl_texture_view);

        mCatchPicture = (ImageButton)findViewById(R.id.catch_picture);
        mVodList = (ImageButton) findViewById(R.id.vedio_list);
//        mSound = (ImageButton)findViewById(R.id.sound);
        mTitle = (FrameLayout)findViewById(R.id.player_title_bar);
        mStreamChange = (TextView)findViewById(R.id.player_change_stream);
        mPause = (ImageButton) findViewById(R.id.ib_pause);
        mBack = (ImageButton) findViewById(R.id.player_imagebutton_back);
        mReplaySeekBar = (MySeekBar) findViewById(R.id.replaySeekBar);
        mSurfaceIcon = (LinearLayout) findViewById(R.id.surface_icons);
        mWaitProgressBar = (ProgressBar) findViewById(R.id.waitProgressBar);
        mWaitProgressBar.setVisibility(View.VISIBLE);
        mStreamLen = (TextView) findViewById(R.id.tv_stream_len);

        initPopupWindow();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        mWaitProgressBar.setVisibility(View.VISIBLE);
    }

    protected void initViewFun(){
//        mGlView.setEGLContextClientVersion(2);
//        mGlView.setRenderer(new YV12Renderer(this,mGlView,mHandler));
//        mGlView.getHolder().addCallback(this);
//        mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

//        mGlView.setOnTouchListener(this);

//        mGlView.setFocusable(true);
//        mGlView.setClickable(true);
//        mGlView.setLongClickable(true);


        if (PhoneConfig.getPhoneHeight(this)<PhoneConfig.getPhoneWidth(this)){
            showSurfaceIcon(false);
        }
        mIsAudioOpen = UserConfigSp.loadSoundState(this);
//        mSound.setImageDrawable(getResources().getDrawable(mIsAudioOpen?R.mipmap.img_sound:R.mipmap.img_no_sound));
    }


    protected void initPopupWindow(){
        View v = LayoutInflater.from(this).inflate(R.layout.popup_window,null);
        mHD = (LinearLayout) v.findViewById(R.id.pop_layout_hd);
        mSD = (LinearLayout) v.findViewById(R.id.pop_layout_sd);
        int width = PhoneConfig.getPhoneWidth(this);
        int height = width * 5 / 3;
        mPopupWindow = new PopupWindow(v,width/4,height);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
    }

    protected void initPlayer(){

        mPlayMgr = CamFactory.buildCam(PlayAction.getInstance().getPlayBean().getType());
        mPlayMgr.init(this,PlayAction.getInstance().getPlayBean());
        mPlayMgr.setHandler(mHandler);
        mPlayMgr.registStreamLenCallback(this);
        PlayAction.getInstance().setHandler(mHandler).setCam(mPlayMgr);

    }


    protected void showSurfaceIcon(boolean bShow){
        if (!bShow){
            mTitle.setVisibility(View.GONE);
            mSurfaceIcon.setVisibility(View.GONE);
            mStreamLen.setVisibility(View.VISIBLE);
            isShowSurfaceIcon = false;
        }else{
            mTitle.setVisibility(View.VISIBLE);
            mSurfaceIcon.setVisibility(View.VISIBLE);
            isShowSurfaceIcon = true;
        }


    }





    protected void camConnect(){
        PlayAction.getInstance().camLogin();
    }




    protected void soundFun(){
        if (PlayAction.getInstance().isMute()){
            PlayAction.getInstance().unmute();
            mIsAudioOpen = true;
        }else{
            PlayAction.getInstance().mute();
            mIsAudioOpen = false;
        }
    }

    protected void camReLink(){
        PlayAction.getInstance().reLink();
    }

    protected void camPlay(){
        Log.i("123","base play cam play");
        PlayAction.getInstance().camViewPlay();
    }

    protected void camStop(){
        PlayAction.getInstance().camViewStop();
    }

    protected void camDisconnect(){
        PlayAction.getInstance().camLogout();
    }



    @Override
    protected void onPause() {
        mGlView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mGlView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mIsDestory = true;
        mPlayMgr.unregistStreamLenCallback();
        camStop();
        camDisconnect();

        mGlView.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){//2   heng

            showSurfaceIcon(false);
//            if (mIsTalk)

        } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){//1  shu
            showSurfaceIcon(true);
//            if (mIsTalk)

        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()){

            default:
                break;
        }

        return false;
    }

    protected void playBackFun(){}



    @Override
    public void showStreamSpeed(final int kbitPerSec, long time, long first) {
        if (mStreamLen!=null){
            mStreamLen.post(new Runnable() {
                @Override
                public void run() {
                    mStreamLen.setText(kbitPerSec+" kbit/s");
                }
            });
        }
    }
}
