package com.inz.utils;


import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.databind.util.ISO8601Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yangyu
 *	��������������������
 */
public class Util {
	/**
	 * �õ��豸��Ļ�Ŀ��
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * �õ��豸��Ļ�ĸ߶�
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * �õ��豸���ܶ�
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * ���ܶ�ת��Ϊ����
	 */
	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}
	
	
	private static long lastTotalRxBytes = 0;
	private static long lastTimeStamp = 0;
	
	private static long getTotalRxBytes(Context context){
		return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED?0:(TrafficStats.getTotalRxBytes()/1024);
	}
	
	public static String getDownloadSpeed(Context context){
		long nowTotalRxBytes = getTotalRxBytes(context);
		long nowTimeStemp = System.currentTimeMillis();
		long speed = (nowTotalRxBytes - lastTotalRxBytes)*1000 / (nowTimeStemp - lastTimeStamp);
		lastTimeStamp = nowTimeStemp;
		lastTotalRxBytes = nowTotalRxBytes;
//		if(speed == 0 ){
//			if(!isNetConnect(context)){
//				return null;
//			}
//		}
		
		return String.valueOf(speed) + "kb/s";
	}
	

	public static boolean isIP(String addr){
		if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
		{
			return false;
		}
		/**
		 * 判断IP格式和范围
		 */
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pat = Pattern.compile(rexp);
		Matcher mat = pat.matcher(addr);
		boolean ipAddress = mat.find();
		return ipAddress;
	}

	public static boolean hasDot(String addr){
		if ("".equals(addr))return false;
		return addr.contains(".");
	}

	public static String parseIP(String domainName){
		InetAddress addr=null;
		try {
			addr =InetAddress.getByName(domainName);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
		return addr.getHostAddress();
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	public static boolean isNewApi(){
		return android.os.Build.VERSION.SDK_INT>22;
	}

	public static String Date2ISODate(Date date){
		ISO8601DateFormat isoDate = new ISO8601DateFormat();
		String isoString = isoDate.format(date);
		Log.i("123", "isoDate:"+isoString);
		return isoString;
	}

	public static String ISODateString2ISOString(String isoDate){
		String str = null;
		try{
			Date date = ISO8601Utils.parse(isoDate,new ParsePosition(0));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			str = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;


	}

	public static String ISODateString2Date(String isoDate){
		String str = null;
		try {
			Date date = ISO8601Utils.parse(isoDate,new ParsePosition(0));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			str = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;

		//		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		//		DateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//		try {
		//			return sd.format(sdf.parse(isoDate));
		//		} catch (ParseException e) {
		//			e.printStackTrace();
		//			return null;
		//		}
	}

	public static String createClientNonce(int length){
		String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<length;i++){
			int number =random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}


	public static Date ISODateString2ISODate(String isoDate){
		return org.codehaus.jackson.map.util.ISO8601Utils.parse(isoDate);
	}




}
