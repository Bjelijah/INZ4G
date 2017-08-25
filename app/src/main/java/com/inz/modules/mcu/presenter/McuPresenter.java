package com.inz.modules.mcu.presenter;

import android.content.Context;

import com.howell.bean.soap.LoginRequest;
import com.howell.bean.soap.LoginResponse;
import com.howell.protocol.soap.SoapManager;
import com.inz.modules.BasePresenter;
import com.inz.modules.ImpBaseView;
import com.inz.modules.mcu.IMcuContract;
import com.inz.modules.mcu.bean.Type;
import com.inz.utils.DecodeUtils;
import com.inz.utils.RxUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Administrator on 2017/8/15.
 */

public class McuPresenter extends BasePresenter implements IMcuContract.IPresenter{
    private IMcuContract.IView mView;

    @Override
    public void bindView(ImpBaseView view) {
        mView = (IMcuContract.IView) view;
    }

    @Override
    public void unbindView() {
        dispose();
        mView = null;
    }


    @Override
    public IMcuContract.IPresenter initURL(Context c, String ip, int port, boolean isSSL) {
        SoapManager.getInstance().initURL(c,ip,port,isSSL);
        return this;
    }

    @Override
    public void login(final String account, final String pwd) {

        addDisposable(RxUtil.doRxTask(new RxUtil.CommonTask<Boolean>() {
            LoginResponse res;
            @Override
            public void doInIOThread() {
                try {
                    res = SoapManager.getInstance()
                            .getUserLoginRes(new LoginRequest(
                                    account,
                                    "Common",
                                    DecodeUtils.getEncodedPassword(pwd),
                                    "1.0.0.1"));
                } catch (IOException e) {
                    e.printStackTrace();
                    setT(false);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    setT(false);
                }
                setT(true);
                //need save info


            }

            @Override
            public void doInUIThread() {
                mView.loginRes(res.getResult().equalsIgnoreCase("OK")?Type.OK: Type.ERROR);
            }

        }));
    }

    public void logout(){

    }










}
