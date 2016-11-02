package cn.com.cjland.zhirenguo.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.sql.AreaSqLite;

/**
 * Created by Administrator on 2015/12/31.
 */
public class DownAddress {
    private final static String TAG = "DownAddress";
    private final String MY_ACTION = "android.com.example.broadcastreceiver.action.MYACTION";
    private SQLiteDatabase db;
    private Context context;
    private SharedPreferences preferences;
    public  DownAddress(Context context1){
        context = context1;
    }
    public void getAddress(){
        AreaSqLite areaSqLite = new AreaSqLite(context);
        db=areaSqLite.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from address",null);
        Log.e("ww", "cursor111111的大小" + cursor.getCount());
        if (cursor!=null||cursor.getCount()!=0) {
            db.execSQL("delete from address");
            new getArea().start();
        }else{
            new getArea().start();
        }
    }
    /**
     * 得到域信息
     */
    private class getArea extends  Thread{
        @Override
        public void run() {
            super.run();
            try {
                int count = 0;
                String parmas= "";
                String loginResult= HttpUtils.PostString(context, "http://192.168.1.64/zr/index.php/user/getArea", parmas);
                Log.e("we", "area==" + loginResult);
                JSONObject jsonObject = new JSONObject(loginResult);
                JSONArray jsonArray = jsonObject.getJSONArray("objList");
                ContentValues values=new ContentValues();
                for (int i =0 ;i<jsonArray.length();i++){
                    values.clear();
                    JSONObject provincesObj = jsonArray.getJSONObject(i);
                    String provinces = provincesObj.getString("name");
                    String pCode = provincesObj.getString("code");
                    JSONArray cityJSONA = provincesObj.getJSONArray("city");
                    if (cityJSONA.length()==0){
                        values.put("provinces", provinces);
                        values.put("pcode", pCode);
                        values.put("city", "");
                        values.put("ccode","");
                        values.put("twon", "");
                        values.put("tcode", "");
                        long m=db.insert("address", null, values);
                        if(m>0){

                        }else{
                            count++;
                            Log.e("we1", "保存失败");
                            return;
                        }
                    }else{
                        for (int j = 0 ;j<cityJSONA.length();j++){
                            JSONObject cityObject = cityJSONA.getJSONObject(j);
                            String city = cityObject.getString("name");
                            String cCode = cityObject.getString("code");
                            JSONArray twonArray = cityObject.getJSONArray("twon");
                            for (int k = 0;k<twonArray.length();k++){
                                JSONObject twonObject = twonArray.getJSONObject(k);
                                String twon = twonObject.getString("name");
                                String tCode = twonObject.getString("code");
                                values.put("provinces", provinces);
                                values.put("pcode", pCode);
                                values.put("city", city);
                                values.put("ccode",cCode);
                                values.put("twon", twon);
                                values.put("tcode", tCode);
                                long m=db.insert("address", null, values);
                                if(m>0){

                                }else{
                                    count++;
                                    Log.e("we1", "保存失败");
                                    return;
                                }
                            }
                        }
                    }
                }
                if (count == 0){
                    Log.e(TAG, "wu=== "+"成功" );
                    preferences = context.getSharedPreferences(
                            SumConstants.SHAREDPREFERENCES_NAME, context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(SumConstants.ISADDRESSDOWN,true);
                    editor.commit();
                }
            }catch ( Exception e){
                Log.e(TAG, "e: "+e);
            }finally {
                if (db.isOpen()){
                    db.close();
                }
            }
        }
    }
}
