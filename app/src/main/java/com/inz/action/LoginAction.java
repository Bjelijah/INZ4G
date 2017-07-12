package com.inz.action;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.howell.bean.httpbean.Fault;
import com.howell.bean.httpbean.UserClientCredential;
import com.howell.bean.httpbean.UserNonce;
import com.howell.bean.soap.AccountResponse;
import com.howell.bean.soap.GetAccountRes;
import com.howell.bean.soap.LoginRequest;
import com.howell.bean.soap.LoginResponse;
import com.howell.bean.soap.Request;
import com.howell.protocol.http.HttpManager;
import com.howell.protocol.soap.SoapManager;
import com.inz.bean.Custom;
import com.inz.bean.UserLoginDBBean;
import com.inz.db.UserLoginDao;
import com.inz.utils.DecodeUtils;
import com.inz.utils.IConfig;
import com.inz.utils.ServerConfigSp;
import com.inz.utils.UserConfigSp;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


/**
 * Created by howell on 2016/11/9.
 */

public class LoginAction implements IConfig{
    public static final int ERROR_LOGIN_ACCOUNT = 0xe3;
    public static final int ERROR_LOGIN_PWD = 0xe1;
    public static final int ERROR_LOGIN_OTHER = 0xe2;
    public static final int ERROR_LINK_ERROR = 0xe0;
    private static LoginAction mInstance = null;
    public static LoginAction getInstance(){
        if (mInstance == null){
            mInstance = new LoginAction();
        }
        return mInstance;
    }
    private Context mContext;
    public LoginAction setContext(Context c){
        this.mContext = c;
        return this;
    }
    private UserInfo mInfo = new UserInfo();
    public UserInfo getmInfo() {
        return mInfo;
    }
    private boolean mIsGuest = true;

    public boolean ismIsGuest() {
        return mIsGuest;
    }

    public LoginAction setmIsGuest(boolean mIsGuest) {
        this.mIsGuest = mIsGuest;
        return this;
    }

    private SoapManager mSoapManager = SoapManager.getInstance();
    private IloginRes mCallback;
    public LoginAction regLoginResCallback(IloginRes i){
        mCallback = i;
        return this;
    }
    public void unRegLoginResCallback(){
        mCallback = null;
    }


    private LoginAction(){}

    public void reLogin(Context context){
        String ip = ServerConfigSp.loadServerIP(context);
        int port = ServerConfigSp.loadServerPort(context);
        boolean ssl = ServerConfigSp.loadServerSSL(context);
        String account = UserConfigSp.loadUserName(context);
        String pwd = UserConfigSp.loadUserPwd(context);
        Custom c = new Custom();
        c.setCustom(false);
        c.setCustomIP(ip);
        c.setCustomPort(port);
        c.setSSL(ssl);
        Login(account,pwd,c);
    }



    public void Login(final String account, final String password, final Custom custom){
        if (custom==null){
            if(mCallback!=null)mCallback.onLoginError(ERROR_LOGIN_OTHER);
            Log.e("123","custom==null");
            return;
        }
        mInfo.setAccount(account).setPassword(password);
        setmIsGuest(account.equals("100868"));
        Log.e("123","account="+account+"  password="+password+"  custom="+custom.isCustom()+
        "  ip="+custom.getCustomIP()+"  port="+custom.getCustomPort()+"   ssl="+custom.isSSL());
        mSoapManager.initURL(mContext,custom.getCustomIP(),custom.getCustomPort(),custom.isSSL());
        new AsyncTask<Void,Void,Boolean>(){
            int mError;
            LoginResponse mLoginRes = null;
            GetAccountRes mAccountResponse = null;
            private boolean checkLoginRes(LoginResponse res){
                if (res==null)return false;
                String str = res.getResult().toString();
                if (str.equalsIgnoreCase("OK")){
                    return true;
                }else if(str.equalsIgnoreCase("AccountNotExist")){
                    mError = ERROR_LOGIN_ACCOUNT;
                    return false;
                }else if(str.equalsIgnoreCase("Authencation")){
                    mError = ERROR_LOGIN_PWD;
                    return false;
                }else{
                    mError = ERROR_LOGIN_OTHER;
                    return false;
                }
            }
            private boolean checkAccountRes(GetAccountRes res){
                if (res==null)return false;
                String str = res.getResult().toString();
                if (str.equalsIgnoreCase("OK")){
                    return true;
                }else{
                    return false;
                }
            }
            private boolean login(String account, String password){
                String encodedPassword = DecodeUtils.getEncodedPassword(password);
                LoginRequest loginReq = new LoginRequest(account, "Common",encodedPassword, "1.0.0.1");
                try {
                    mLoginRes = mSoapManager.getUserLoginRes(loginReq);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
            private boolean account(String account, String session){
//                AccountRequest r = new AccountRequest(account,session);
                Request r = new Request(account,session);
                try {
                    mAccountResponse = mSoapManager.getAccount(r);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            void login2ServerTEST() throws JSONException, UnsupportedEncodingException, NoSuchAlgorithmException {
                HttpManager hMgr = HttpManager.getInstance();
                hMgr.initURL(mContext,TEST_S_IP,IS_SSL?8850:8800,IS_SSL);
                UserNonce sn = hMgr.userNonce(TEST_ACCOUNT);//从服务器获取随机码

                String clientNonce = com.inz.utils.Util.createClientNonce(32);//客户端  本地随机码Util
                //用户认证
                Fault fault = hMgr.doUserAuthenticate(new UserClientCredential(sn.getDomain(),TEST_ACCOUNT,TEST_PASSWORD,sn.getNonce(),clientNonce));
                Log.e("123","fault="+fault.toString());
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                LoginRequest loginReq = null;
                if (!login(account,password)){
                    mError = ERROR_LINK_ERROR;
                    return false;
                }
                mInfo.setCustom(custom);

                if (!checkLoginRes(mLoginRes)){
                    Log.e("123","checkLoginRes");
                    return false;
                }
                mInfo.setLr(mLoginRes);

                if(!account(account,mLoginRes.getLoginSession())){
                    Log.e("123","account");
                    return false;
                }
                if(checkAccountRes(mAccountResponse)){
                    mInfo.setAr(mAccountResponse);
                    saveLogin2DB();
                }
                if (IS_TEST){
                    try {
                        login2ServerTEST();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }

                return true;
            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                Log.i("123","login ="+aBoolean);
                if (mCallback==null)return;
                if (aBoolean){
                    saveLogin2Sp();
                    mCallback.onLoginSuccess();
                }else{
                    mCallback.onLoginError(mError);
                }
            }
        }.execute();
    }

    private void saveLogin2DB(){
        UserLoginDao dao = new UserLoginDao(mContext, "user.db", 1);
        if(dao.findByNameAndIP(mInfo.getAccount(),mInfo.getCustom().getCustomIP())){
            List<UserLoginDBBean> list = dao.queryByNameAndIP(mInfo.getAccount(),mInfo.getCustom().getCustomIP());
            for (UserLoginDBBean b : list){
                if (b.getUserNum()==0){
                    dao.close();
                    return;
                }
            }
            dao.insert(new UserLoginDBBean(0,mInfo.getAccount(),mInfo.getPassword(),mInfo.getAr().getEmail(), mInfo.getCustom()));
        }else{
            dao.insert(new UserLoginDBBean(0,mInfo.getAccount(),mInfo.getPassword(),mInfo.getAr().getEmail(), mInfo.getCustom()));
        }
        dao.close();
    }

    private void saveLogin2Sp(){
        UserConfigSp.saveUserInfo(mContext,mInfo.getAccount(),mInfo.getPassword(),mInfo.getCustom().isCustom());
    }


    public  interface IloginRes{
        void onLoginSuccess();
        void onLoginError(int e);
    }

    /**
     * login info
     */
    public class UserInfo{
        private String account;
        private String password;
        private LoginResponse lr;
        private GetAccountRes ar;
        private Custom custom;

        public Custom getCustom() {
            return custom;
        }

        public void setCustom(Custom custom) {
            this.custom = custom;
        }

        public LoginResponse getLr() {
            return lr;
        }
        public UserInfo setLr(LoginResponse lr) {
            this.lr = lr;
            return this;
        }
        public String getAccount() {
            return account;
        }
        public UserInfo setAccount(String account) {
            this.account = account;
            return this;
        }
        public String getPassword() {
            return password;
        }
        public UserInfo setPassword(String password) {
            this.password = password;
            return this;
        }

        public GetAccountRes getAr() {
            return ar;
        }

        public void setAr(GetAccountRes ar) {
            this.ar = ar;
        }
    }




}
