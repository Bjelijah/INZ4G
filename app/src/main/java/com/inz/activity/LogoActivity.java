package com.inz.activity;

/**
 * @author 霍之昊 
 * 
 * 类说明：app登录页面
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.util.Log;


import com.howell.jni.JniUtil;
import com.howell.protocol.soap.SoapManager;
import com.inz.action.LoginAction;
import com.inz.bean.Custom;
import com.inz.bean.UserLoginDBBean;
import com.inz.db.UserLoginDao;
import com.inz.inz4g.R;
import com.inz.utils.IConfig;
import com.inz.utils.NetWorkUtils;
import com.inz.utils.ServerConfigSp;

import java.util.List;
import java.util.Set;



public class LogoActivity extends Activity implements LoginAction.IloginRes,IConfig{
	//与平台交互协议单例
	private SoapManager mSoapManager;

	//是否显示开场导航标志位，存于配置文件中
	private boolean isFirstLogin;

	private String account;
	private String password;
	private boolean mIsFromNotification;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().build());
		init();
		JniUtil.logEnable(true);
		//开启service

		//判断手机是否连接网络


		if (!NetWorkUtils.isNetworkConnected(this)) {
			LoginThread myLoginThread = new LoginThread(3);
			myLoginThread.start();
		}else{
			//清空存储设备信息单例对象
//			DeviceManager mDeviceManager = DeviceManager.getInstance();//FIXME
//			mDeviceManager.clearMember();

			//获取平台协议单例对象
			mSoapManager = SoapManager.getInstance();

			//从配置文件获取开场导航界面标志位不存在则为true，获取用户名和密码如果不存在则为空字符串
			SharedPreferences sharedPreferences = getSharedPreferences("set", Context.MODE_PRIVATE);
			isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", true);
//			isFirstLogin = true;
			account = sharedPreferences.getString("account", "");
			password = sharedPreferences.getString("password", "");
			Log.e("123","isFirstlogin="+isFirstLogin+"  account="+account+"   password="+password);

			//如果用户以前登录过app（配置文件中用户名，密码不为空）则直接登录
			if(!account.equals("") && !password.equals("")){
				LoginThread myLoginThread = new LoginThread(1);
				myLoginThread.start();
			}else{
				//如果用户以前没有登陆过app（用户名，密码为空字符串）则进入注册、登录、演示界面
				ServerConfigSp.saveServerInfo(this,S_IP,S_PORT_SSL,IS_SSL);
				ServerConfigSp.saveTurnServerInfo(this,T_IP,IS_SSL?T_PORT_SSL:T_PORT_NORMAL);
				ServerConfigSp.saveTurnRecordStream(this,RECORD_SUB);

				LoginThread myLoginThread = new LoginThread(2);
				myLoginThread.start();
			}
		}
	}

	private void init(){
		mIsFromNotification = getIntent().getBooleanExtra("notification",false);
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.finish();
	}

	@Override
	public void onLoginSuccess() {
		Intent intent = new Intent(LogoActivity.this, HomeActivity.class);
//		//TODO notification flag
		intent.putExtra("notification",mIsFromNotification);
		startActivity(intent);
		LoginAction.getInstance().unRegLoginResCallback();
		finish();
	}

	@Override
	public void onLoginError(int e) {
		Intent intent = new Intent(LogoActivity.this, LoginActivity.class);
		startActivity(intent);
		LoginAction.getInstance().unRegLoginResCallback();
		finish();
	}

	class LoginThread extends Thread{
		private int flag;
		public LoginThread(int flag) {
			// TODO Auto-generated constructor stub
			this.flag = flag;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Log.e("123","flag="+flag);
			try {
				Thread.sleep(1 * 1000);
				//第一次进入程序加载欢迎导航界面
				if(isFirstLogin){
					Intent intent = new Intent(LogoActivity.this,LoginActivity.class);
//						Intent intent = new Intent(LogoActivity.this,RegisterOrLogin.class);
					startActivity(intent);
				}else{
					switch(flag){
					case 1:
						Custom c = getCustomFromName(account,password);
						LoginAction.getInstance().setContext(LogoActivity.this).regLoginResCallback(LogoActivity.this)
								.Login(account,password,c);
						break;
					case 2:
						//如果用户以前没有登陆过app（用户名，密码为空字符串）则进入注册、登录、演示界面
						Intent intent = new Intent(LogoActivity.this,LoginActivity.class);
						startActivity(intent);
						break;
					case 3:
						Intent intent2 = new Intent(LogoActivity.this,LoginActivity.class);
						intent2.putExtra("intentFlag", 1);
						startActivity(intent2);
					default:break;
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Nullable
	private Custom getCustomFromName(String userName,String password){
		Custom c = null;
		UserLoginDao dao = new UserLoginDao(this, "user.db", 1);
		if(dao.findByName(userName)){
			List<UserLoginDBBean> list = dao.queryByName(userName);
			for(UserLoginDBBean b:list){
				if (b.getUserPassword().equals(password)){
					c = b.getC();
					break;
				}
			}
		}
		return c;
	}


}
