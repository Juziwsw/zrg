package cn.com.cjland.zhirenguo.bean;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/11/27.
 */
public class SharePreService {
    //
    private static SharedPreferences preferences;

    public static  String getShaedPrerence(Context context,String preferencesName){
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(preferencesName,"");
    }

    public static String getUserId(Context context) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(SumConstants.USERID,"");
    }
    public static void setUserId(Context context, String userId) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SumConstants.USERID, userId);
        editor.commit();
    }

    public static String getUserPhone(Context context) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(SumConstants.PHONENUM,"");
    }
    public static void setUserPhone(Context context, String phone) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SumConstants.PHONENUM, phone);
        editor.commit();
    }

    public static String getUserAvatar(Context context) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString("userAvatarUrl","");
    }
    public static void setUserAvatar(Context context, String avatarUrl) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userAvatarUrl", avatarUrl);
        editor.commit();
    }

    public static Boolean getUserStatus(Context context) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(SumConstants.ISLOADSTATUS, false);
    }

    public static void setUserStatus(Context context, Boolean status) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SumConstants.ISLOADSTATUS, status);
        editor.commit();
    }
    public static void clearData(Context context) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
    public static void clearSomeData(Context context) {
        preferences = context.getSharedPreferences(SumConstants.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SumConstants.ISLOADSTATUS, false);
        editor.putString(SumConstants.USERID, "");
        editor.commit();
    }
}
