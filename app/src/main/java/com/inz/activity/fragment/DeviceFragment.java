package com.inz.activity.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.howell.bean.soap.GetNATServerReq;
import com.howell.bean.soap.GetNATServerRes;
import com.howell.protocol.soap.SoapManager;
import com.inz.action.HomeAction;
import com.inz.action.LoginAction;
import com.inz.action.PlayAction;
import com.inz.activity.PlayViewActivity;
import com.inz.activity.VideoListActivity;
import com.inz.adapter.INZDeviceRecyclerViewAdapter;
import com.inz.adapter.SimpleDeviceRecyclerViewAdapter;
import com.inz.bean.CameraItemBean;
import com.inz.bean.NodeDetails;
import com.inz.bean.PlayType;
import com.inz.inz4g.R;
import com.inz.utils.AlerDialogUtils;
import com.inz.utils.IConfig;
import com.zys.brokenview.BrokenCallback;
import com.zys.brokenview.BrokenTouchListener;
import com.zys.brokenview.BrokenView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pullrefreshview.layout.BaseFooterView;
import pullrefreshview.layout.BaseHeaderView;
import pullrefreshview.layout.PullRefreshLayout;

/**
 * Created by howell on 2016/11/11.
 */

public class DeviceFragment extends HomeBaseFragment implements BaseHeaderView.OnRefreshListener,BaseFooterView.OnLoadListener, INZDeviceRecyclerViewAdapter.OnItemClickListener,HomeAction.QueryDeviceCallback,IConfig, LoginAction.IloginRes {
    public static final int MSG_RECEIVE_SIP = 0x0000;
    public static final int MSG_DEVICE_LIST_UPDATA = 0x0001;
    public static final int MSG_NET_SERVER_OK = 0x0002;
    public static final int MSG_NET_SERVER_ERROR = 0x0003;


    View mView;
    RecyclerView mRV;
    BaseHeaderView mbhv;
//    BaseFooterView mbfv;

    List<CameraItemBean> mList = new ArrayList<CameraItemBean>();
    int page = 1;
//    SimpleDeviceRecyclerViewAdapter adapter;
    INZDeviceRecyclerViewAdapter adapter;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_RECEIVE_SIP:
                    break;
                case MSG_DEVICE_LIST_UPDATA:
                    Log.e("123","list updata");
                    mbhv.stopRefresh();

                    adapter.setData(mList);
//                    mBrokenView.reset();
                    break;
                case MSG_NET_SERVER_OK:
                    doPlay(msg.arg1);
                    break;
                case MSG_NET_SERVER_ERROR:
                    Snackbar.make(mView,"网络连接失败！",Snackbar.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onDestroyView() {
        mHandler = null;
        LoginAction.getInstance().unRegLoginResCallback();
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_device,container,false);
        ((PullRefreshLayout)mView).setHasFooter(true);
        mRV = (RecyclerView) mView.findViewById(R.id.device_rv);
        mbhv = (BaseHeaderView) mView.findViewById(R.id.device_header);
//        mbfv = (BaseFooterView) mView.findViewById(R.id.device_footer);
        mbhv.setOnRefreshListener(this);
//        mbfv.setOnLoadListener(this);




//        adapter = new DeviceRecyclerViewAdapter(getContext(),this,getActivity());
//        adapter = new SimpleDeviceRecyclerViewAdapter(getContext(),this);
        adapter = new INZDeviceRecyclerViewAdapter(getContext(),this);
//        adapter = new DeviceRecyclerViewAdapter(getContext(),this);
        mRV.setLayoutManager(new LinearLayoutManager(getContext()));
        mRV.setAdapter(adapter);
        mRV.setItemAnimator(new DefaultItemAnimator());
        LoginAction.getInstance().regLoginResCallback(this);
        Log.i("123","on create get data");
        getData();

//        getData();
        return mView;
    }



    private void getData(int n) {
        //// FIXME: 2016/11/18 for test
        List<String> datas = new ArrayList<>(n);
        mList.clear();

        for (int i=0;i<n;i++){
            CameraItemBean b = new CameraItemBean()
                    .setCameraName("test   "+i)
                    .setIndensity(100)
                    .setOnline(true)
                    .setPtz(true)
                    .setStore(true)
                    .setPicturePath(null);
            mList.add(b);
        }
        mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);
    }

    @Override
    public void getData(){

        if (mList==null)return;
        mList.clear();

        LoginAction.UserInfo info = LoginAction.getInstance().getmInfo();
        //get ap list

//        HomeAction.getInstance().addApCam2List(getContext(),info.getAccount(),this);
        try {
            HomeAction.getInstance().setContext(getContext()).setUseTurn(true).registQueryDeviceCallback(this).queryDevice(info.getAccount(), info.getLr().getLoginSession());
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onLoad(BaseFooterView baseFooterView) {
        Log.i("123","onLoad");
        baseFooterView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mbfv.stopLoad();
            }
        },3000);
    }

    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {
        Log.i("123","onRefresh");
        getData();
        baseHeaderView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mbhv.stopRefresh();
            }
        },1500);
    }




    @Override
    public void onQueryDeviceSuccess(ArrayList<NodeDetails> l) {

//        mList.clear();
        if (l==null){
            mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);
            return;
        }
        for (NodeDetails n:l){

            CameraItemBean b = new CameraItemBean()
                    .setType(HomeAction.getInstance().isUseTurn()?PlayType.TURN:PlayType.ECAM)//FIXME ME  should be ecam when test ,is 5198
                    .setCameraName(n.getName())
                    .setCameraDescription(null)
                    .setIndensity(n.getIntensity())
                    .setDeviceId(n.getDevID())
                    .setOnline(n.isOnLine())
                    .setPtz(n.isPtzFlag())
                    .setStore(n.iseStoreFlag())
                    .setUpnpIP(n.getUpnpIP())
                    .setUpnpPort(n.getUpnpPort())
                    .setMethodType(n.getMethodType())
                    .setPicturePath(n.getPicturePath());
//            Log.e("123","~~~~~~~~~~~~~~~~~~~~~~~~~~~n.getMethod type="+n.getMethodType()+"  name="+n.getName()+"  upnpIP="+n.getUpnpIP());
            mList.add(b);
        }
        //TODO 重新排列： 1ecam OnLine 2ap 3ecam Offline
        HomeAction.getInstance().sort((ArrayList<CameraItemBean>) mList);//online 倒序添加
        mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);//updata ecam list and ap list
    }



    @Override
    public void onQueryDeviceError() {
        mHandler.sendEmptyMessage(MSG_DEVICE_LIST_UPDATA);//for updata ap list
    }

    @Override
    public void onQueryOverTime() {
        LoginAction.getInstance().reLogin(getContext());

    }

    @Override
    public void onItemVideoClickListener(View v, View itemView, int pos) {
        //long click || click
        Log.i("123","on item vied long click || click");
      //  itemView.setOnTouchListener(mColorfulListener);
        //TODO get net server res
        CameraItemBean bean = mList.get(pos);
        Log.i("123","bean type="+bean.getType());

        if (!bean.isOnline() ){
            AlerDialogUtils.postDialogMsg(this.getContext(),
                    getResources().getString(R.string.not_online),
                    getResources().getString(R.string.not_online_message),null);
            return;
        }
        if (v.getId()==R.id.item_camera_iv_picture || v.getId()==R.id.item_camera_ll_play) {
            getNetServer(pos);
        } else if(v.getId()==R.id.item_camera_info){
            //todo test vod
            Log.e("123","click");
            if (IS_TEST || RECORDE_FILE_TEST) {

                Log.i("123", "do play Type=" + bean.getType());
                PlayAction.getInstance().setPlayBean(bean);
                Intent intent = new Intent(getContext(), VideoListActivity.class);
                intent.putExtra("bean", PlayAction.getInstance().getPlayBean());
                intent.putExtra("playview", false);
                startActivity(intent);
            }
        }
    }

    private void getNetServer(final int pos){
        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                SoapManager soapManager = SoapManager.getInstance();
                GetNATServerReq req = new GetNATServerReq(LoginAction.getInstance().getmInfo().getAccount(),
                        LoginAction.getInstance().getmInfo().getLr().getLoginSession());

                GetNATServerRes res = null;
                try {
                    res = soapManager.getGetNATServerRes(req);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    return false;
                } catch (NullPointerException e){
                    e.printStackTrace();
                    return false;
                }
                Log.e("123","GetNATServerRes="+res.toString());
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean){
                    Message msg = new Message();
                    msg.what = MSG_NET_SERVER_OK;
                    msg.arg1 = pos;
                    mHandler.sendMessage(msg);
                }else{
                    mHandler.sendEmptyMessage(MSG_NET_SERVER_ERROR);
                }
            }
        }.execute();
    }

    private void doPlay(int pos){
        CameraItemBean bean = getUpdataBean(pos);
        Log.i("123","do play Type="+bean.getType());
        PlayAction.getInstance().setPlayBean(bean);
        Intent intent = new Intent(getContext(), PlayViewActivity.class);
        getContext().startActivity(intent);
    }

    private CameraItemBean getUpdataBean(int pos){
        CameraItemBean b = mList.get(pos);
        if (b.getType()== PlayType.ECAM || b.getType() == PlayType.TURN){
            b.setType(HomeAction.getInstance().isUseTurn()?PlayType.TURN:PlayType.ECAM);
            mList.set(pos,b);
        }
        return b;
    }

    public void updataAllBeanType(){
        Log.i("123","updata All bean type");
        for (CameraItemBean b:mList){
            if (b.getType()==PlayType.ECAM||b.getType()==PlayType.TURN){
                b.setType(HomeAction.getInstance().isUseTurn()?PlayType.TURN:PlayType.ECAM);
            }
        }

    }


    @Override
    public void onLoginSuccess() {
        Log.i("123","login success!!");
        getData();
    }

    @Override
    public void onLoginError(int e) {

    }
}
