package com.example.navigationdrawertest.utils;

import android.content.Context;

/**
 * sp工具类
 */
public class SharedPrefsUtil {

    private final static String SETTING = "Setting";

    /**
     * 存int类型的值
     *
     * @param context 上下文
     * @param key     上下文
     * @param value   int类型的值
     */
    public static void putValue(Context context, String key, int value) {
        context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit()
                .putInt(key, value)
                .apply();
    }

    /**
     * 存boolean类型的值
     *
     * @param context 上下文
     * @param key     键
     * @param value   boolean类型的值
     */
    public static void putValue(Context context, String key, boolean value) {
        context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    /**
     * 存String类型的值
     *
     * @param context 上下文
     * @param key     键
     * @param value   String类型的值
     */
    public static void putValue(Context context, String key, String value) {
        context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
    }

    /**
     * 取int类型的值
     *
     * @param context  上下文
     * @param key      键
     * @param defValue 需要设置一个默认值
     * @return int值
     */
    public static int getValue(Context context, String key, int defValue) {
        return context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).getInt(key, defValue);
    }

    /**
     * 取boolean类型的值
     *
     * @param context  上下文
     * @param key      键
     * @param defValue 需要设置一个默认值
     * @return boolean值
     */
    public static boolean getValue(Context context, String key, boolean defValue) {
        return context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).getBoolean(key, defValue);
    }

    /**
     * 取String类型的值
     *
     * @param context  上下文
     * @param key      键
     * @param defValue 需要设置一个默认值
     * @return String值
     */
    public static String getValue(Context context, String key, String defValue) {
        return context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).getString(key, defValue);
    }
}
