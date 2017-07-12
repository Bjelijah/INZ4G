package com.inz.action;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import com.howell.bean.soap.QueryDeviceRes;
import com.howell.bean.soap.Request;
import com.howell.protocol.soap.SoapManager;
import com.inz.bean.CamFactory;
import com.inz.bean.CameraItemBean;
import com.inz.bean.Custom;
import com.inz.bean.NodeDetails;
import com.inz.bean.UserLoginDBBean;
import com.inz.db.UserLoginDao;
import com.inz.player.ICam;
import com.inz.utils.ServerConfigSp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howell on 2016/11/18.
 */

public class HomeAction {
    private static HomeAction mInstance = null;
    public static HomeAction getInstance(){
        if (mInstance == null){
            mInstance = new HomeAction();
        }
        return mInstance;
    }
    private Context mContext;
    private String serviceIP;
    private int servicePort,videoPort;
    private boolean isUseTurn;
    private boolean isUseCrypto;
    public HomeAction setServiceIPAndPort(String ip,int port){
        this.serviceIP = ip;
        this.servicePort = port;
        return this;
    }
    public HomeAction setServiceVideoPort(int videoPort){
        this.videoPort = videoPort;
        return this;
    }
    public int getServiceVideoPort(){
        return videoPort;
    }

    public HomeAction init(){
        isUseTurn = ServerConfigSp.loadServerIsTurn(mContext);
        isUseCrypto = ServerConfigSp.loadServerIsCrypto(mContext);
        serviceIP = ServerConfigSp.loadServerIP(mContext);
        servicePort = ServerConfigSp.loadServerPort(mContext);
        return this;
    }

    public boolean isUseTurn() {
        return isUseTurn;
    }

    public HomeAction setUseTurn(boolean useTurn) {
        isUseTurn = useTurn;
        return this;
    }

    public boolean isUseCrypto() {
        return isUseCrypto;
    }

    public void setUseCrypto(boolean useCrypto) {
        isUseCrypto = useCrypto;
    }

    public String getServiceIP(){
        return this.serviceIP;
    }
    public int getServicePort(){
        return servicePort;
    }

    public HomeAction setContext(Context c){
        this.mContext = c;
        return this;
    }
    private HomeAction(){}

    private QueryDeviceCallback mQueryDeviceCallback;
    private ChangeUser mChangeUserCallback;

    public HomeAction registChangerUserCallback(ChangeUser cb){
        this.mChangeUserCallback = cb;
        return this;
    }

    public void unregistChangerUserCallback(){
        this.mChangeUserCallback = null;
    }

    public QueryDeviceCallback unregistQueryDeviceCallback() {
        mQueryDeviceCallback = null;
        return mQueryDeviceCallback;
    }

    public HomeAction registQueryDeviceCallback(QueryDeviceCallback mQueryDeviceCallback) {
        this.mQueryDeviceCallback = mQueryDeviceCallback;
        return this;
    }

    private SoapManager mSoapManager = SoapManager.getInstance();
    private ArrayList<NodeDetails> mList;










    public boolean sort(ArrayList<CameraItemBean> list){
        if (list==null)return false;
        for (int i=0;i<list.size();i++){
            if (list.get(i).isOnline() ){
                list.add(0, list.get(i));
                list.remove(i+1);
            }
        }
        return true;
    }

    public boolean removeCam(Context context,CameraItemBean bean){
        Log.e("123","type="+bean.getType());
        ICam cam = CamFactory.buildCam(bean.getType());
        if (null==cam)return false;
        cam.init(context,bean);
        return cam.unBind();
    }


//    boolean doOnce = true;

    public void queryDevice(final String account,final String session){
        new AsyncTask<Void,Void,Boolean>(){
            private void sort(ArrayList<NodeDetails> list){
                if(list != null){
                    int length = list.size();
                    for(int i = 0 ; i < length ; i++){
                        System.out.println(i+":"+list.get(i).toString());
                        if(list.get(i).isOnLine()){
                            list.add(0, list.get(i));
                            list.remove(i+1);
                        }else{
                            //System.out.println(i);
                        }
                    }
                }
            }

            private ArrayList<NodeDetails> transfor2NodeDetails(ArrayList<QueryDeviceRes.NodeList> l){
                if (l==null)return null;
                ArrayList<NodeDetails> details = new ArrayList<NodeDetails>();
                for (QueryDeviceRes.NodeList n:l){
                    NodeDetails d = new NodeDetails(n.getDevID(),n.getChannelID(),n.getName(),
                            n.getOnLIne(),n.getPtzFlag(),n.getSecurityArea(),n.geteStoreFlag(),
                            n.getUpnpIP(),n.getUpnpPort(),n.getDevVer(),n.getCurVideoNum(),
                            n.getLastUpdated(),n.getSmsSubscribedFlag(),n.geteMailSubscribedFlag(),
                            n.getSharingFlag(),n.getApplePushSubscribedFlag(),n.getAndroidPushSubscribedFlag(),
                            n.getInfraredFlag(),n.getWirelessFlag()
                    );
                    if (n.getWirelessFlag()==0||n.getWirelessFlag()==1){
                        d.setIntensity(0);
                    }else{
                        d.setIntensity(n.getIntensity());
                    }

                    details.add(d);
                }
                return details;
            }


            @Override
            protected Boolean doInBackground(Void... voids) {
                QueryDeviceRes res = null;
                try {
                   res = mSoapManager.queryDevice(new Request(account,session));
                   Log.i("123","res="+res.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

                if ( res!=null){
//                    res.setResult("SessionExpired");
                    if (res.getResult().equalsIgnoreCase("SessionExpired")){
                        //need reLogin
                        if (mQueryDeviceCallback!=null){
                            mQueryDeviceCallback.onQueryOverTime();
                        }
//                        doOnce = false;
                        return false;
                    }
                }
                mList = transfor2NodeDetails(res.getNodeLists());
//                mList =  mSoapManager.getNodeDetails();
                sort(mList);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (mQueryDeviceCallback==null)return;
                if (aBoolean){
                    mQueryDeviceCallback.onQueryDeviceSuccess(mList);
                }else{
                    mQueryDeviceCallback.onQueryDeviceError();
                }
            }
        }.execute();
    }

    public void changeUser(Context context, String userName,String email){
        Bundle bundle = getUserPwdByDB(userName,email);
        String userPwd = bundle.getString("pwd");
        Custom c = (Custom) bundle.getSerializable("custom");
        LoginRes res = new LoginRes();
        LoginAction.getInstance().setContext(context).regLoginResCallback(res).Login(userName,userPwd,c);
    }

    private Bundle getUserPwdByDB(String name,String email){
        String pwd = null;
        Custom c = null;
        UserLoginDao dao = new UserLoginDao(mContext, "user.db", 1);
        List<UserLoginDBBean> b = dao.queryByNameAndEmail(name,email);
        dao.close();
        if (b.size()>0){
            pwd = b.get(0).getUserPassword();
            c = b.get(0).getC();
        }
        Bundle bundle = new Bundle();
        bundle.putString("pwd",pwd);
        bundle.putSerializable("custom",c);
        return bundle;
    }

    class LoginRes implements LoginAction.IloginRes{

        @Override
        public void onLoginSuccess() {
            if (mChangeUserCallback!=null){
                mChangeUserCallback.onChangeOk();
            }
            LoginAction.getInstance().unRegLoginResCallback();
        }

        @Override
        public void onLoginError(int e) {
            if (mChangeUserCallback!=null){
                mChangeUserCallback.onChangeError();
            }
            LoginAction.getInstance().unRegLoginResCallback();
        }
    }


    public interface QueryDeviceCallback{
        void onQueryDeviceSuccess(ArrayList<NodeDetails> l);
        void onQueryDeviceError();
        void onQueryOverTime();
    }

    public interface ChangeUser{
        void onChangeOk();
        void onChangeError();
    }

}
