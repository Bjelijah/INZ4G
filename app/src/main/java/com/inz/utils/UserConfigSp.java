package com.inz.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by howell on 2016/11/29.
 */

public class UserConfigSp {

    private static final String SP_NAME = "user_set";
    private static final String SET_SP_NAME = "set";
    private static final String USER_LIKE_SP = "like_set";
    public static void saveUserInfo(Context context,String name,String pwd,boolean isCustom){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_name",name);
        editor.putString("user_pwd",pwd);
        editor.putBoolean("user_custom",isCustom);
        editor.commit();


        SharedPreferences sp2 = context.getSharedPreferences("set",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sp2.edit();
        editor2.putString("account",name);
        editor2.putString("password",pwd);
        editor2.putBoolean("custom",isCustom);
        editor2.putBoolean("isFirstLogin",false);
        editor2.commit();

    }
    public static String loadUserName(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
       return sp.getString("user_name",null);
    }

    public static String loadUserPwd(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getString("user_pwd",null);
    }

    public static boolean loadUserIsCustom(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean("user_custom",false);
    }

    public static void saveSoundState(Context context,boolean isOpen){
        SharedPreferences sp = context.getSharedPreferences(SET_SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("sound_mode",isOpen);
        edit.commit();
    }

    public static boolean loadSoundState(Context context){
        SharedPreferences sp = context.getSharedPreferences(SET_SP_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean("sound_mode",true);

    }

    public static void saveLike(Context context,boolean isLike){
        SharedPreferences sp = context.getSharedPreferences(USER_LIKE_SP,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLike",isLike);
        editor.commit();
    }
    public static boolean loadLike(Context context){
        SharedPreferences sp = context.getSharedPreferences(USER_LIKE_SP,Context.MODE_PRIVATE);
        return sp.getBoolean("isLike",false);
    }


}
