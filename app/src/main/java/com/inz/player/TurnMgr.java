package com.inz.player;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.howell.bean.httpbean.Fault;
import com.howell.bean.httpbean.UserClientCredential;
import com.howell.bean.httpbean.UserNonce;
import com.howell.bean.soap.VodSearchReq;
import com.howell.bean.soap.VodSearchRes;
import com.howell.bean.turnbean.TurnDisSubscribeAckBean;
import com.howell.bean.turnbean.TurnGetRecordedFileAckBean;
import com.howell.bean.turnbean.TurnGetRecordedFilesBean;
import com.howell.bean.turnbean.TurnSubScribe;
import com.howell.bean.turnbean.TurnSubScribeAckBean;
import com.howell.jni.JniUtil;
import com.howell.player.BasePlayer;
import com.howell.protocol.http.HttpManager;
import com.howell.protocol.http.Util;
import com.howell.protocol.soap.SoapManager;
import com.howell.protocol.turn.TurnJsonUtil;
import com.howell.protocol.turn.TurnManager;
import com.inz.action.LoginAction;
import com.inz.activity.BasePlayActivity;
import com.inz.activity.fragment.VodFragment;
import com.inz.bean.CameraItemBean;
import com.inz.bean.VODRecord;
import com.inz.utils.IConfig;
import com.inz.utils.ServerConfigSp;
import com.inz.utils.ThreadUtil;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/28.
 */

public class TurnMgr extends BasePlayer implements ICam,IConfig, LoginAction.IloginRes {
    public static final int MSG_RECONNECT = 0x00;
    Context mContext;
    CameraItemBean mBean;
    Handler mHandler;
    private String mTurnServiceIP = null;
    private int mTurnServicePort = -1;

    @Override
    public void onLoginSuccess() {

    }

    @Override
    public void onLoginError(int e) {

    }

    public enum PlayCmd{
        PLAY_VIEW,
        PLAY_BACK,
        RECORDED_LIST,
        NULL_CMD
    }
    private PlayCmd mCmdType;
    TurnManager mgr;
    private int dialogId = 0;
    private int getDialogId(){
        return dialogId++;
    }
    ICam.IStream mStreamCB = null;
    private boolean mIsSub = false;
    boolean misPlayback;

    boolean mIsRePlay = false;

    private static final int F_TIME = 1;
    private String mLastVODTime="";
    private Timer timer = null;
    private MyTimerTask myTimerTask = null;
    private int mUnexpectNoFrame = 0;
    TurnResult mTurnResultCb = new TurnResult();
    String mStartTime,mEndTime;
    String mVodStartTime,mVodEndTime;
    ArrayList<VODRecord> mVodRecords = new ArrayList<>();
    VodSearchRes mVodSearchRes = null;
    int totalPage = 0;

    @Override
    public void init(Context context, CameraItemBean bean) {
        mContext = context;
        mBean = bean;

        String ip = ServerConfigSp.loadTurnIP(context);
        if (ip==null){
            //第一次登入
            ServerConfigSp.saveTurnServerInfo(context,IConfig.T_IP,IConfig.T_PORT_SSL);
            ip = T_IP;
        }


        int port = ServerConfigSp.loadTurnPort(context);
        boolean bSSL = ServerConfigSp.loadServerSSL(context);

        TurnManager t =TurnManager.getInstance()
                .turnInit(context)
                .setIP(IS_TEST?TEST_TURN_IP:ip)
                .setPort(port)
                .setIsSSL(bSSL)
                .setName(IS_TEST?TEST_ACCOUNT: LoginAction.getInstance().getmInfo().getAccount())
                .setPassword(IS_TEST?TEST_PASSWORD:LoginAction.getInstance().getmInfo().getPassword())
                .setIMEI(((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId())
                .setType(101);
        if (IS_TEST){
            bean.setDeviceId("00310101031111111000001000000000");
            bean.setChannelNo(8);
        }

        init(t,bean.getDeviceId(),bean.getChannelNo(),true);
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void registStreamLenCallback(IStream cb) {
        mStreamCB = cb;
    }

    @Override
    public void unregistStreamLenCallback() {
        mStreamCB = null;
    }

    @Override
    public void setStreamBSub(boolean isSub) {
        mIsSub = isSub;
    }

    @Override
    public void setPlayBack(boolean isPlayback) {
        misPlayback = isPlayback;
    }

    @Override
    public void setPlayBackTime(String startTime, String endTime) {
        mStartTime = startTime;
        mEndTime = endTime;

    }

    @Override
    public boolean bind() {
        return true;
    }

    @Override
    public boolean unBind() {
        return true;
    }

    @Override
    public boolean loginCam() {

        if(IS_TEST) {
            Log.i("123","before deviceId="+mBean.getDeviceId()+"   channel no="+mBean.getChannelNo());
            mBean.setDeviceId("00310101031111111000001000000000");
            mBean.setChannelNo(8);
            Log.i("123","after deviceId="+mBean.getDeviceId()+"   channel no="+mBean.getChannelNo());
        }


        init(TurnManager.getInstance(),mBean.getDeviceId(),mBean.getChannelNo(),mIsSub);
        startTimeTask();

        return true;
    }

    @Override
    public boolean logoutCam() {
        stopTimeTask();
        deInit();
        return true;
    }

    @Override
    public boolean playViewCam() {
        Log.i("123","turnMgr playview cam playback="+misPlayback);
        if (misPlayback){
            playback(mStartTime,mEndTime);
        }else{
            playView();
        }

        return true;
    }

    @Override
    public boolean stopViewCam() {
        mCmdType = PlayCmd.NULL_CMD;
        stopView();
        return true;
    }

    @Override
    public boolean reLinkServer() {

        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                Log.i("123","relinkserver mIsreplay = true");
//                mIsRePlay = true;
//                mgr.disconnect();

                mgr.turnDeinit();//close socket
                mgr.turnInit(mContext);
                mgr.connect();
            }
        });
        return true;
    }

    @Override
    public boolean reLink() {
        //TODO do relink
        ThreadUtil.cachedThreadStart(new Runnable() {
            @Override
            public void run() {
                stopViewCam();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (misPlayback){
                    Log.e("123","replay back!!!!!!");
                    rePlayback(mStartTime,mEndTime);
                }else{
                    playView();
                }
            }
        });
        return true;
    }

    @Override
    public boolean playBackReplay(long begOffset, long curProgress) {
        rePlayback(mStartTime,mEndTime);
        return true;
    }

    @Override
    public boolean playBackPause(boolean bPause, long begOffset, long curProgress) {
        JniUtil.pauseAndPlayView();
        return true;
    }

    @Override
    public boolean catchPic(String path) {
        JniUtil.catchPic(path);
        return true;
    }

    @Override
    public boolean ptzSetInfo(String account, String loginSession, String devID, int channelNo) {
        return false;
    }

    @Override
    public boolean soundSetData(byte[] buf, int len) {
        return false;
    }

    @Override
    public boolean zoomTeleStart() {
        return false;
    }

    @Override
    public boolean zoomTeleStop() {
        return false;
    }

    @Override
    public boolean zoomWideStart() {
        return false;
    }

    @Override
    public boolean zoomWideStop() {
        return false;
    }

    @Override
    public boolean ptzMoveStart(String direction) {
        return false;
    }

    @Override
    public boolean ptzMoveStop() {
        return false;
    }

    @Override
    public boolean hasVideoList() {
        if (IS_TEST)return true;
        return mBean.isStore();
    }

    @Override
    public void setVideoListTime(String startTime, String endTime) {
        mVodStartTime = startTime;
        mVodEndTime = endTime;
    }

    @Override
    public int getVideoListPageCount(int nowPage, int pageSize) {
        Log.e("123","getVideoListPageCount  now getRecoredeFile ");
        if (pageSize==0) {
            getRecoredeFile(mVodStartTime, mVodEndTime);
            return 0;
        }else{
            return getVideoListPageCountEcam(nowPage,pageSize);
        }

    }

    private int getVideoListPageCountEcam(final int nowPage,final int pageSize){
        Log.e("123","getVideoListPageCountEcam");
        final String account = LoginAction.getInstance().getmInfo().getAccount();
        final String session = LoginAction.getInstance().getmInfo().getLr().getLoginSession();
        final String devID = mBean.getDeviceId();
        final int channel = mBean.getChannelNo();
        int stream = ServerConfigSp.loadTurnRecordStream(mContext);
        try {
            mVodSearchRes = SoapManager.getInstance().vodSearch(new VodSearchReq(
                    account,
                    session,
                    devID,
                    channel,
                    stream,
                    mVodStartTime,
                    mVodEndTime,
                    nowPage,
                    null,
                    pageSize
            ));
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return -1;
        } catch (NullPointerException e){
            e.printStackTrace();
            return -1;
        }
        if (mVodSearchRes.getResult().equalsIgnoreCase("SessionExpired")){
            try {
                LoginAction.getInstance().regLoginResCallback(this);
                LoginAction.getInstance().reLogin(mContext);
            }catch (Exception e){}
        }
        ArrayList<VodSearchRes.Record>fileList = mVodSearchRes.getRecord();
        if (fileList==null || fileList.size()==0){
            Log.e("123","fileList==null or size=0");
            mVodRecords.clear();
            mHandler.sendEmptyMessage(VodFragment.MSG_VIDEO_LIST_DATA_UPDATE);
            return 0;
        }
        mVodRecords.clear();
        Log.i("123","fileList="+fileList.toString());

        for (VodSearchRes.Record rf:fileList){
            VODRecord record = new VODRecord();
            record.setStartTime(rf.getStartTime());
            record.setEndTime(rf.getEndTime());
            record.setTimeZoneStartTime(rf.getStartTime());
            record.setTimeZoneEndTime(rf.getEndTime());
            String time = com.inz.utils.Util.ISODateString2Date(record.getTimeZoneStartTime());
            if (!mLastVODTime.equals(time.substring(0,10))){
                mLastVODTime = time.substring(0,10);
                record.setHasTitle(true);
            }
            String startY = record.getTimeZoneStartTime().substring(0,4);
            String endY = record.getTimeZoneEndTime().substring(0,4);
            Log.i("123","startY="+startY+"   endY="+endY);
            if (startY.equals(endY)) {
                mVodRecords.add(record);
            }
        }
        mHandler.sendEmptyMessage(VodFragment.MSG_VIDEO_LIST_DATA_UPDATE);
        return mVodSearchRes.getPageCount();
    }



    @Override
    public ArrayList<VODRecord> getVideoList() {
        return mVodRecords;
    }

    @Override
    public boolean playPause(boolean b) {
        JniUtil.pauseAndPlayView();
        return true;
    }

    @Override
    public boolean isPlayBackCtrlAllow() {
        Log.e("123","misPlayback="+misPlayback);
        return misPlayback;
    }

    /**************************************************/




    public void startTimeTask(){
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 0,F_TIME*1000);
    }

    public void stopTimeTask(){
        if (timer!=null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (myTimerTask!=null) {
            myTimerTask.cancel();
            myTimerTask = null;
        }
    }






    private void onConnectFun(String sessionId){

//        subcribePlayView(mDeviceId,0,misSub);
        Log.i("123","onConnectFun   mCmdType="+mCmdType);
        switch (mCmdType){
            case PLAY_VIEW:

                mgr.subscribe(new TurnSubScribe(sessionId,"media",new TurnSubScribe.media(getDialogId(),new TurnSubScribe.meta(mDeviceId,"live",mChannel,mIsSub?1:0))));
                break;
            case PLAY_BACK:
                Log.i("123","PLAY_BACK    m_is sub="+mIsSub);
                mgr.subscribe(new TurnSubScribe(sessionId,"media",new TurnSubScribe.media(getDialogId(),new TurnSubScribe.meta(mDeviceId,"playback",mChannel,mIsSub?1:0,mBeg,mEnd))));
                break;
            case RECORDED_LIST:
                Log.e("123","onConnectFun  do recodeFiles   mDeviceId="+mDeviceId+"  mChannel="+mChannel+" mBeg="+mBeg+" mEnd="+mEnd);
                mgr.getRecordedFiles(new TurnGetRecordedFilesBean(mDeviceId,mChannel,mBeg,mEnd));
                break;
            case NULL_CMD:
                //do nothing just return
                break;
            default:
                break;
        }
    }

    @Override
    public BasePlayer init(TurnManager mgr, String deviceId, int channel, boolean isSub) {
        super.init(mgr, deviceId, channel, isSub);
        this.mgr = mgr.registResultCallback(mTurnResultCb);
        return this;
    }

    @Override
    public void deInit() {
        Log.i("123","hash="+this.hashCode());
        if (mgr != null) {
            mgr.unregistResultCallback(mTurnResultCb);
            Log.i("123", "turn mgr deinit 000");
            mgr.disconnect();
            Log.i("123", "turn mgr deinit 1111");
        }
        Log.i("123", "turn mgr deinit 222");
        mgr.turnDeinit();
        Log.i("123", "turn mgr deinit 333");
        super.deInit();
        Log.i("123", "turn mgr deinit 4444  finish");
    }

    @Override
    public void playView() {
        super.playView();
        misPlayback = false;
        mCmdType = PlayCmd.PLAY_VIEW;
        mgr.connect();
    }

    @Override
    public void playback(String beg, String end) {
        super.playback(beg, end);
        misPlayback = true;
        mCmdType = PlayCmd.PLAY_BACK;
        Log.i("123","playback    mCmd="+mCmdType);
        mgr.connect();
    }

    @Override
    public void rePlayback(String beg, String end) {
        super.rePlayback(beg, end);
        misPlayback = true;
        mCmdType = PlayCmd.PLAY_BACK;
        mIsRePlay = true;
        stopView();
    }

    @Override
    public void getRecoredeFile(String beg, String end) {
        Log.e("123","getRecoredeFile!!!!!!!!");
        super.getRecoredeFile(beg, end);
        mCmdType = PlayCmd.RECORDED_LIST;
        mgr.connect();
    }

    @Override
    public void readyAndPlay(TurnSubScribeAckBean bean, boolean isPlayback) {
        super.readyAndPlay(bean, isPlayback);
    }

    @Override
    public void stopView() {
        Log.e("123","stop view unSubscribe");
        mgr.unSubscribe();
        super.stopView();
    }

    class TurnResult implements TurnManager.ITurn{

        @Override
        public void onConnect(String sessionId) {
            onConnectFun(sessionId);
        }

        @Override
        public void onDisconnect() {
            if (mIsRePlay){

//                mHandler.sendEmptyMessageDelayed(BasePlayActivity.MSG_PLAY_REPLAY,1000);
                ThreadUtil.cachedThreadStart(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mgr.connect();
                    }
                });
            }
        }

        @Override
        public void onDisconnectUnexpect(int flag) {

            /////TODO
            Log.e("123","disconnect unexpect  flag="+flag);
            switch (mCmdType){
                case RECORDED_LIST:
                    Log.e("123","RECORDED_LIST   need tell");
                    mHandler.sendEmptyMessage(VodFragment.MSG_VIDEO_LIST_DATA_UPDATE_ERROR);//
                    break;
                case PLAY_BACK:
                    ////0 socket 1 sync  2 packet receive false   3  http !=200
                    Log.e("123","play back need  return  flag="+flag);
                    if (flag==2){
                        //need disconnect socket and link
                        mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_RELINK_SERVER);
                    }else if(flag == 3) {
                        mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_CAM_ERROR);
                    }

                    break;
                case PLAY_VIEW:
                    Log.e("123","PLAY_VIEW   need relink");
                    //
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onRecordFileList(TurnGetRecordedFileAckBean fileList) {
            // on recordFileList
            Log.i("123","onRecordFileList");
            mgr.disconnect();
            if (fileList==null || fileList.getRecordedFiles().size()==0){
                Log.e("123","fileList==null or size=0");
                if (fileList.getRecordedFiles().size()==0){
                    mVodRecords.clear();
                    mHandler.sendEmptyMessage(VodFragment.MSG_VOD_LIST_NONE);
                }
                return;
            }
            mVodRecords.clear();
            Log.i("123","fileList="+fileList.toString());
            ArrayList<TurnGetRecordedFileAckBean.RecordedFile> recordedFiles = fileList.getRecordedFiles();
            for (TurnGetRecordedFileAckBean.RecordedFile rf:recordedFiles){
                VODRecord record = new VODRecord();
                record.setStartTime(rf.getBeginTime());
                record.setEndTime(rf.getEndTime());
                record.setTimeZoneStartTime(rf.getBeginTime());
                record.setTimeZoneEndTime(rf.getEndTime());
                String time = com.inz.utils.Util.ISODateString2Date(record.getTimeZoneStartTime());
                if (!mLastVODTime.equals(time.substring(0,10))){
                    mLastVODTime = time.substring(0,10);
                    record.setHasTitle(true);
                }
                String startY = record.getTimeZoneStartTime().substring(0,10);
                String endY = record.getTimeZoneEndTime().substring(0,10);
                Log.i("123","startY = "+startY+"   endY="+endY);

                mVodRecords.add(record);
            }
            Log.i("123","mVodRecords size="+mVodRecords.size());

            mHandler.sendEmptyMessage(VodFragment.MSG_VIDEO_LIST_DATA_UPDATE);



        }

        @Override
        public void onSubscribe(String jsonStr) {
            readyAndPlay(TurnJsonUtil.getTurnSubscribeAckAllFromJsonStr(jsonStr),misPlayback);
        }

        @Override
        public void onUnsubscribe(String jsonStr) {
            TurnDisSubscribeAckBean bean = TurnJsonUtil.getTurnDisSubscribeAckFromJsonStr(jsonStr);
            if (bean.getCode()==200){
               // Log.i("123","TurnMgr get onUnsubscribe  then we disconnect");
               // mgr.disconnect();
            }
        }


    }

    class MyTimerTask extends TimerTask{

        @Override
        public void run() {

            int streamLen = mgr.getStreamLen();

            int speed = streamLen*8/1024/F_TIME;
            long timeStamp = mgr.getTimeStamp();
            long firstTimeStamp = mgr.getFirstTimeStamp();
            if (mStreamCB!=null){
                mStreamCB.showStreamSpeed(speed,timeStamp,firstTimeStamp);
            }
            if (streamLen==0){
                mUnexpectNoFrame++;
            }else{

                mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_UNWAIT);
                mUnexpectNoFrame = 0;
            }

            if (!misPlayback) {//fixme  回放有暂停
                if (mUnexpectNoFrame == 3) {
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_PLAY_WAIT);
                }

                if (mUnexpectNoFrame == 10) {// 10s / 1000ms
//                mHandler.sendEmptyMessage(PlayerActivity.MSG_DISCONNECT_UNEXPECT);//FIXME
                    // TODO reLink
                    mHandler.sendEmptyMessage(BasePlayActivity.MSG_PLAY_RELINK_START);
                }
            }



        }
    }









}
