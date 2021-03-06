package com.howell.protocol.http;

import android.util.Log;

/**
 * Created by Administrator on 2017/5/15.
 */

public class Util {
    public static String transformItemId2DeviceId(String itemId){
        StringBuffer str = new StringBuffer(itemId);
        str.setCharAt(24,'0');
        str.setCharAt(25,'0');
        str.setCharAt(28,'0');
        str.setCharAt(29,'0');
        str.setCharAt(30,'0');
        str.setCharAt(31,'0');
        return str.toString();
    }

    public static int getChannelFromItemId(String itemId){
        String str = itemId.substring(itemId.length()-4);

        return Integer.valueOf(str)-1;
    }

    public static boolean isTypeId(int type,String id){//type = 68 车
        StringBuffer str = new StringBuffer(id);
        char [] typeChs = new char[2];
        str.getChars(8,9,typeChs,0);
        return type==Integer.valueOf(typeChs.toString());
    }

}
