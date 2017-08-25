package com.inz.modules.mcu;

import android.content.Context;

import com.inz.modules.ImpBasePresenter;
import com.inz.modules.ImpBaseView;
import com.inz.modules.mcu.bean.Type;

/**
 * Created by Administrator on 2017/8/15.
 */

public interface IMcuContract {
    interface IView extends ImpBaseView{
        void loginRes(Type type);
    }
    interface IPresenter extends ImpBasePresenter{
        IPresenter initURL(Context c, String ip, int port, boolean isSSL);
        void login(String account,String pwd);
    }
}
