package com.howell.protocol.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2017/4/25.
 */

public class Util {

    public static String mixIds(ArrayList<String> ids){
        String str = "";
        for (int i=0;i<ids.size();i++){
            if (i>0){
                str += ",";
            }
            str += ids.get(i);
        }
        return str;
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




}
