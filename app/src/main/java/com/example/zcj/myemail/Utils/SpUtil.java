package com.example.zcj.myemail.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zcj on 2017/2/10.
 */
public class SpUtil {

    //存储首页信息
    private static final String SP_CONFIG = "config";
    private static final String SPKEY_REMENBER = "isRbPwd";
    private static final String SPKEY_ADD = "address";
    private static final String SPKEY_PWD = "password";
    private static final String SP_NAME = "name";
    private static final String SPKEY_NAME = "nameKey";

    //是否记住密码
    public static void putIsRbPwd(Context context, Boolean b) {
        SharedPreferences sp = context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE);
        sp.edit().putBoolean(SPKEY_REMENBER, b);
    }

    public static Boolean getIsRbPwd(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE);
        return sp.getBoolean(SPKEY_REMENBER, false);
    }
    //记住地址
    public static void putAddress(Context context, String str) {
        SharedPreferences sp = context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE);
        sp.edit().putString(SPKEY_ADD, str);
    }

    public static String getAddress(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE);
        return sp.getString(SPKEY_ADD, "");
    }

    //记住密码
    public static void putPwd(Context context, String str) {
        SharedPreferences sp = context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE);
        sp.edit().putString(SPKEY_PWD, str);
    }

    public static String getPwd(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE);
        return sp.getString(SPKEY_PWD, "");
    }
    //记住文件夹名字
    public static void putName(Context context, String str) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SPKEY_NAME, str);
    }

    public static String getName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(SPKEY_NAME, "");
    }
}
