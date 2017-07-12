package com.inz.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;


import com.howell.bean.httpbean.Fault;
import com.inz.action.HomeAction;
import com.inz.inz4g.R;
import com.inz.utils.AlerDialogUtils;
import com.inz.utils.IConfig;
import com.inz.utils.ServerConfigSp;
import com.inz.utils.Util;

/**
 * Created by howell on 2016/12/6.
 */

public class ServerSetActivity extends AppCompatActivity implements IConfig{
    public static final int MSG_SERVER_SET_WAIT = 0x00;
    Toolbar mTb;
    AutoCompleteTextView mIPView,mPortView,mTurnIPView,mTurnPortView;
    Button mbtnSave,mbtnDefault;
    Switch mswSSL;
    private ProgressDialog mPd;
    private boolean mIsSSL = false;
    private RadioGroup mRecordStreamGroup;
    private RadioButton mRecordStreamMain,mRecordStreamSub;
    private int mRecordStream = 0;
//    ImageButton mBack;

    private String mServerIP,mTurnIP;
    private int mServerPort,mTurnPort;




    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SERVER_SET_WAIT:
                    mPd.dismiss();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_address);
        initView();
        initToolbar();
        initSetting();
    }

    private void initView(){
        mIPView = (AutoCompleteTextView) findViewById(R.id.server_set_et_ip);
        mPortView = (AutoCompleteTextView) findViewById(R.id.server_set_et_port);
        mTurnIPView = (AutoCompleteTextView) findViewById(R.id.server_set_t_ip);
        mTurnPortView = (AutoCompleteTextView) findViewById(R.id.server_set_t_port);

        mbtnSave = (Button) findViewById(R.id.server_set_btn);
        mbtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeInput(view);
                fun();
            }
        });
        mbtnDefault = (Button) findViewById(R.id.server_set_default_btn);
        mbtnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIPView.setText(S_IP);
                mPortView.setText(S_PORT_SSL+"");
                mswSSL.setChecked(IS_SSL);
                mTurnIPView.setText(T_IP);
                mTurnPortView.setText(T_PORT_SSL+"");
                mIsSSL = IS_SSL;
                mRecordStream = RECORD_SUB;
                mRecordStreamMain.setChecked(RECORD_SUB==0?true:false);
                mRecordStreamSub.setChecked(RECORD_SUB==1?true:false);
            }
        });

        mRecordStreamGroup = (RadioGroup) findViewById(R.id.server_set_record_stream);
        mRecordStreamGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.server_set_record_stream_main:
                        mRecordStream = 0;
                        break;
                    case R.id.server_set_record_stream_sub:
                        mRecordStream = 1;
                        break;
                    default:
                        break;
                }
            }
        });
        mRecordStreamMain = (RadioButton) findViewById(R.id.server_set_record_stream_main);
        mRecordStreamSub = (RadioButton) findViewById(R.id.server_set_record_stream_sub);




        mswSSL = (Switch) findViewById(R.id.server_set_ssl);
        mswSSL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsSSL = isChecked;
            }
        });

//        mBack = (ImageButton) findViewById(R.id.server_set_ib_back);
//        mBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
    }

    private void initToolbar(){
        mTb = (Toolbar) findViewById(R.id.server_set_toolbar);
//        mTb.setNavigationIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_chevron_left).actionBar().color(Color.WHITE));

//        mTb.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_theaters_white_24dp));
        // mTb.showOverflowMenu();
        mTb.setTitle(getString(R.string.server_setting_title));
        setSupportActionBar(mTb);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        mTb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeInput(view);
                doSaveDialog();
//                finish();
            }
        });
    }

    private void initSetting(){

        mIPView.setText(ServerConfigSp.loadServerIP(this));
        mPortView.setText(ServerConfigSp.loadServerPort(this)+"");
        mTurnIPView.setText(ServerConfigSp.loadTurnIP(this));
        mTurnPortView.setText(ServerConfigSp.loadTurnPort(this)+"");
        mswSSL.setChecked(ServerConfigSp.loadServerSSL(this));
        if(ServerConfigSp.loadTurnRecordStream(this)==0){
            mRecordStreamMain.setChecked(true);
            mRecordStreamSub.setChecked(false);
            mRecordStream = 0 ;
        }else{
            mRecordStreamMain.setChecked(false);
            mRecordStreamSub.setChecked(true);
            mRecordStream = 1;
        }


    }
    private void closeInput(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }

    private void fun(){
        mIPView.setError(null);
        mPortView.setError(null);
        mTurnIPView.setError(null);
        mTurnPortView.setError(null);
        String ip = mIPView.getText().toString();
        String port = mPortView.getText().toString();
        String ipT = mTurnIPView.getText().toString();
        String portT = mTurnPortView.getText().toString();


        View v = null;
        if (!Util.isInteger(portT)){
            mTurnPortView.setError(getString(R.string.add_ap_port_error));
            v = mTurnPortView;
        }
        if (portT.equals("")){
            mTurnPortView.setError(getString(R.string.reg_field_empty));
            v= mTurnPortView;
        }
        if (!Util.hasDot(ipT)){
            mTurnIPView.setError(getString(R.string.add_ap_ip_error));
            v = mTurnIPView;
        }
        if (ipT.equals("")){
            mTurnIPView.setError(getString(R.string.reg_field_empty));
            v = mTurnIPView;
        }


        if (!Util.isInteger(port)){
            mPortView.setError(getString(R.string.add_ap_port_error));
            v = mPortView;
        }
        if (port.equals("")){
            mPortView.setError(getString(R.string.reg_field_empty));
            v= mPortView;
        }
        if (!Util.hasDot(ip)){
            mIPView.setError(getString(R.string.add_ap_ip_error));
            v = mIPView;
        }
        if (ip.equals("")){
            mIPView.setError(getString(R.string.reg_field_empty));
            v = mIPView;
        }





        if (v!=null){
            Log.i("123","v!=null");
            v.requestFocus();
            return;
        }

        mServerIP = ip;
        mServerPort = Integer.valueOf(port);
        mTurnIP = ipT;
        mTurnPort = Integer.valueOf(portT);

        save();
    }

    private void waitShow(String title,String msg,long autoDismissMS){
        mPd = new ProgressDialog(this);
        mPd.setTitle(title);
        mPd.setMessage(msg);
        mPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPd.show();
        if (autoDismissMS>0){
            mHandler.sendEmptyMessageDelayed(MSG_SERVER_SET_WAIT,autoDismissMS);
        }
    }

    private void doSaveDialog(){

        AlerDialogUtils.postDialogMsg(this,
                getResources().getString(R.string.server_setting_save_exit_title),
                getResources().getString(R.string.server_setting_save_exit_msg),
                getResources().getString(R.string.server_setting_save_exit_ok),
                getResources().getString(R.string.server_setting_save_exit_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("123","save it");
                        fun();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("123","no save it");
                        finish();
                    }
                });
    }



    private void save(){
        String _ip;
        if (Util.isIP(mServerIP)){
            _ip = mServerIP;
        }else{
            _ip = Util.parseIP(mServerIP);
        }
        HomeAction.getInstance().setServiceIPAndPort(_ip,mServerPort);
        ServerConfigSp.saveServerInfo(this,_ip,mServerPort,mIsSSL);
        ServerConfigSp.saveTurnServerInfo(this,mTurnIP,mTurnPort);
        ServerConfigSp.saveTurnRecordStream(this,mRecordStream);
        waitShow(getResources().getString(R.string.camera_setting_save_title),getResources().getString(R.string.camera_setting_save_msg),1000);
    }



}
