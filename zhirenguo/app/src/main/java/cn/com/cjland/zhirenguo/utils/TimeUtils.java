package cn.com.cjland.zhirenguo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * @author Msquirrel
 */
public class TimeUtils {

    private static SimpleDateFormat sf = null;

    /*获取系统时间 格式为："yyyy/MM/dd "*/
    public static String getCurrentTime() {
        Date d = new Date();
        sf = new SimpleDateFormat("HH:mm");
        return sf.format(d);
    }
    /*获取系统时间 格式为："yyyyMM "*/
    public static String getTodayDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyyMMdd");
        return sf.format(d);
    }
    /*获取系统时间 格式为："yyyy/MM/dd "*/
    public static String getCurrentDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString2(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time,int type) {
       switch (type){
           case 0:
               sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
               break;
           case 1:
               sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               break;
       }
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime()/1000;
    }
    //获取当前时间直接转化为时间戳
    public static long getTimeDate(){
        String data = getCurrentDate();
        Long timedate =  getStringToDate(data,1);
        return timedate;
    }
    /*时间戳转换成字符窜 yyyy-mm-dd hh:mm*/
    public static String getDateToStringAll(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return sf.format(d);
    }
}