package com.howell.protocol.turn;

/**
 * Created by Administrator on 2017/6/29.
 */

public class TurnUtil {
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
}
