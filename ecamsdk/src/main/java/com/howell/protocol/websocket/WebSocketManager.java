package com.howell.protocol.websocket;

import android.util.Log;

import com.howell.bean.httpbean.WSRes;
import com.howell.protocol.utils.SDKDebugLog;

import org.json.JSONException;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

/**
 * Created by Administrator on 2017/4/6.<br/>
 * WS Manager : client get alarm push,notice push,event push from server<br/>
 * long link ,keep alive by heartbeat<br/>
 * @author howell
 */
public class WebSocketManager {
    private static final String TAG = WebSocketManager.class.getName();
    public static final int ERROR_SEND = 0x01;
    public static final int ERROR_RECEIVE = 0x02;


    private String wsuri;
    private WebSocketConnection mConnect;
    ArrayList<IMessage> mCallback=null;
    private boolean mIsOpen = false;
    public WebSocketManager registMessage(IMessage c){
        if (mCallback==null)mCallback = new ArrayList<IMessage>();
        mCallback.add(c);
        return this;
    }
    public void unregistMessage(IMessage c){
        if (mCallback==null)return;
        mCallback.remove(c);
    }

    private void sendOpen(){
        if (mCallback==null)return;
        for (IMessage msg:mCallback){
            msg.onWebSocketOpen();
        }
    }

    private void sendClose(){
        if (mCallback==null)return;
        for (IMessage msg:mCallback){
            msg.onWebSocketClose();
        }
    }

    private void sendError(int error){
        if (mCallback==null)return;
        for (IMessage msg:mCallback){
            msg.onError(error);
        }
    }

    private void sendMessage(WSRes res){
        if (mCallback==null)return;
        for (IMessage msg:mCallback){
            msg.onGetMessage(res);
        }
    }

    /**
     * init url
     * @param serverIP sever ip
     * @return this Manager
     * @throws WebSocketException
     */
    public WebSocketManager initURL(String serverIP) throws WebSocketException {
        mIsOpen = false;
        wsuri = "ws://"+serverIP+":8803/howell/ver10/ADC";
        mConnect = new WebSocketConnection();
        mConnect.connect(wsuri,new WebSocketConnectionHandler(){
            @Override
            public void onOpen() {
                super.onOpen();
                mIsOpen = true;
                sendOpen();
            }

            @Override
            public void onClose(int code, String reason) {
                super.onClose(code, reason);
                mIsOpen = false;
                sendClose();
            }

            @Override
            public void onTextMessage(String payload) {
                super.onTextMessage(payload);
                try {
                    handleMessageJsonString(payload);
                } catch (JSONException e) {
                    sendError(ERROR_RECEIVE);
                    e.printStackTrace();
                }
            }
        });
        return this;
    }

    /**
     * deInit<br/>
     * disconnect
     */
    public void deInit(){
        mConnect.disconnect();
        mIsOpen = false;

    }

    /**
     * send link information to server (like login protocol)
     * @param cseq
     * @param session the session u login in to server
     * @param username the username u login in to server
     * @throws JSONException
     */
    public void alarmLink(int cseq,String session,String username) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createAlarmPushConnectJsonObject(cseq,session,username).toString());
    }

    /**
     * send heartbeat to server
     * @param cseq
     * @param systemUpTime system time: how long has this app run (can be 0)
     * @param longitude longitude (can be 0)
     * @param latitude latitude (can be 0)
     * @param useLongitudeOrLatitude if use it (if u set longitude or latitude 0:false; else true)
     * @throws JSONException
     */
    public void alarmAlive(int cseq,long systemUpTime,double longitude,double latitude,boolean useLongitudeOrLatitude) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createAlarmAliveJsonObject(cseq,systemUpTime,longitude,latitude,useLongitudeOrLatitude).toString());
    }

    /**
     * after get event push from server send handle result back
     * @param cseq same as event cseq that u got in event push
     * @throws JSONException
     */
    public void ADCEventRes(int cseq) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createADCEventResJsonObject(cseq).toString());
    }

    /**
     * after get notice push from server,send handle result back
     * @param cseq same as cseq that u got in notice push
     * @throws JSONException
     */
    public void ADCNoticeRes(int cseq) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createADCNoticeResJsonObject(cseq).toString());
    }

    /**
     * client send notice to server
     * @deprecated not support in current
     * @param n notice
     * @throws JSONException
     */
    public void MCUSendNotice(WSRes.AlarmNotice n) throws JSONException {
        if (!mIsOpen)sendError(ERROR_SEND);
        mConnect.sendTextMessage(JsonUtil.createMCUSendMessage(n).toString());
    }



    private void handleMessageJsonString(String jsonStr) throws JSONException {
        SDKDebugLog.logI(TAG+":handleMessageJsonString","jsonStr="+jsonStr);
        sendMessage(JsonUtil.parseResJsonString(jsonStr));
    }



    public interface IMessage{
        void onWebSocketOpen();
        void onWebSocketClose();
        void onGetMessage(WSRes res);
        void onError(int error);
    }
}
