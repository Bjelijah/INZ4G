package com.inz.player;

import android.content.Context;
import android.os.Handler;

import com.inz.bean.CameraItemBean;
import com.inz.bean.VODRecord;

import java.util.ArrayList;

/**
 * Created by howell on 2016/12/6.
 */

public interface ICam {
    void init(Context context, CameraItemBean bean);
    void deInit();
    void setHandler(Handler handler);
    void registStreamLenCallback(IStream cb);
    void unregistStreamLenCallback();
    void setStreamBSub(boolean isSub);
    void setPlayBack(boolean isPlayback);
    void setPlayBackTime(String startTime, String endTime);



    boolean bind();//添加相机  将相机绑定到当前帐号
    boolean unBind();//删除相机  将相机解绑

    boolean loginCam();
    boolean logoutCam();

    boolean playViewCam();
    boolean stopViewCam();
    boolean reLink();
    boolean reLinkServer();
    boolean playBackReplay(long begOffset, long curProgress);
    boolean playBackPause(boolean bPause, long begOffset, long curProgress);

    //功能
    boolean catchPic(String path);
    //PTZ
    boolean ptzSetInfo(String account, String loginSession, String devID, int channelNo);
    boolean soundSetData(byte[] buf, int len);
    boolean zoomTeleStart();
    boolean zoomTeleStop();
    boolean zoomWideStart();
    boolean zoomWideStop();
    boolean ptzMoveStart(String direction);
    boolean ptzMoveStop();



    //回放列表

    boolean hasVideoList();
    void setVideoListTime(String startTime, String endTime);
    int getVideoListPageCount(int nowPage, int pageSize);//nowPage:当前第几页,pageSize:每页多少条 返回：从当前页到结束页共多少页
//    boolean getVideoList(String start,String end,int streamType);
    ArrayList<VODRecord> getVideoList();
    boolean playPause(boolean b);
    boolean isPlayBackCtrlAllow();

    interface IStream{
        void showStreamSpeed(final int kbitPerSec,long time,long first);
    }


}
