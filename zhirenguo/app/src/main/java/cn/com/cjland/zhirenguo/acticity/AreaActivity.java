package cn.com.cjland.zhirenguo.acticity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.sql.AreaSqLite;

/**
 * Created by Administrator on 2015/12/31.
 */
public class AreaActivity extends BaseActivity implements NumberPicker.OnValueChangeListener {
    private final static String TAG = "AreaActivity";
    private NumberPicker nbpProvince,nbpCity,nbpTwon;
    private SQLiteDatabase db;
    private List<String>listProvinces,listCity,listTwon;
    private int mProvinceId = 1;
    private static int oldCityMax,oldTwonMax;
    private int provinceVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        nbpProvince = (NumberPicker) findViewById(R.id.province);
        nbpCity = (NumberPicker) findViewById(R.id.city);
        nbpTwon = (NumberPicker) findViewById(R.id.twon);
        AreaSqLite areaSqLite = new AreaSqLite(this);
        db = areaSqLite.getWritableDatabase();
        listProvinces = queryProvinces();
        initProvinceData();
    }
    private void initProvinceData() {
        String[] provinceNames = new String[listProvinces.size()];
        for (int i = 0; i < listProvinces.size(); i++) {
            provinceNames[i] = listProvinces.get(i);
        }
        nbpProvince.setMinValue(0);
        nbpProvince.setMaxValue(provinceNames.length - 1);
        nbpProvince.setDisplayedValues(provinceNames);
        nbpProvince.setOnValueChangedListener(this);
        nbpProvince.setValue(mProvinceId);
        initCityData(nbpProvince.getValue());
    }
    private void initCityData(int pId) {
        listCity = queryCity(pId);
        if (listCity == null || listCity.size() == 0)return;
        String[] cityNames = new String[listCity.size()];
        for (int i = 0; i < listCity.size(); i++) {
            cityNames[i] = listCity.get(i);
        }
        int max=cityNames.length-1;
        try {
            if (max >= oldCityMax){
                nbpCity.setDisplayedValues(cityNames);
                nbpCity.setValue(0);
                nbpCity.setMinValue(0);
                nbpCity.setMaxValue(max);
            }else{
                nbpCity.setValue(0);
                nbpCity.setMinValue(0);
                nbpCity.setMaxValue(max);
                nbpCity.setDisplayedValues(cityNames);
            }
            oldCityMax = max;
        }catch (NumberFormatException e){
            Log.e(TAG,"e=="+e);
        }
        nbpCity.setOnValueChangedListener(this);
        initTwonData(pId, nbpCity.getValue());
    }
    /**
     * 加载twon数据
     * @param cId
     */
    private void initTwonData (int pId,int cId){
        listTwon = queryTwon(pId,cId);
        if (listTwon == null || listTwon.size() == 0)return;
        String[] twonNames = new String[listTwon.size()];
        for (int i = 0; i < listTwon.size(); i++) {
            twonNames[i] = listTwon.get(i);
        }
        int max= twonNames.length - 1;
        try {
            if (max >= oldTwonMax){
                nbpTwon.setDisplayedValues(twonNames);
                nbpTwon.setValue(0);
                nbpTwon.setMinValue(0);
                nbpTwon.setMaxValue(max);
            }else{
                nbpTwon.setValue(0);
                nbpTwon.setMinValue(0);
                nbpTwon.setMaxValue(max);
                nbpTwon.setDisplayedValues(twonNames);
            }
            oldTwonMax = max;
        }catch (NumberFormatException e){
            Log.e(TAG,"e=="+e);
        }
        nbpTwon.setOnValueChangedListener(this);
    }
    /**
     * 省的数据库查询
     * @return
     */
    private List<String> queryProvinces(){
        List<String> provincesList = new ArrayList<String>();
        Cursor cursor=db.rawQuery("select provinces from address  group by provinces", null);
        Log.e("we", "街路巷查询语句cursor的大小" + cursor.getCount());
        while (cursor.moveToNext()) {
            String provincesName=cursor.getString(cursor.getColumnIndex("provinces"));
            //String pCode=cursor.getString(cursor.getColumnIndex("pcode"));
            //Log.e(TAG, "pCode=== "+pCode );
            provincesList.add(provincesName);
        }
        return provincesList;
    }
    /**
     * 市的数据库查询city
     * @return
     */
    private List<String> queryCity(int pId){
        Log.e(TAG, "pId "+listProvinces.get(pId) );
        Log.e(TAG, "listProvinces.get(pId) "+listProvinces.get(pId) );
        List<String> cityList = new ArrayList<String>();
        Cursor cursor=db.rawQuery("select city from address where provinces='"+listProvinces.get(pId)+"' group by city", null);
        Log.e("we","街路巷查询语句cursor的大小" + cursor.getCount());
        while (cursor.moveToNext()) {
            String cityName=cursor.getString(cursor.getColumnIndex("city"));
            //String cCode=cursor.getString(cursor.getColumnIndex("ccode"));
            //Log.e(TAG, "pCode=== "+cCode );
            cityList.add(cityName);
        }
        return cityList;
    }

    /**
     * 县的数据库查询Twon
     * @param tId
     * @return
     */
    private List<String> queryTwon(int pId,int tId){
        Log.e(TAG, "listProvinces.get(pId) "+listProvinces.get(pId) );
        Log.e(TAG, "listCity.get(tId) "+listCity.get(tId) );
        List<String> twonList = new ArrayList<String>();
        Cursor cursor=db.rawQuery("select twon from address where provinces='"+listProvinces.get(pId)+"' and city='"+listCity.get(tId)+"' group by twon", null);
        Log.e("we","街路巷查询语句cursor的大小" + cursor.getCount());
        while (cursor.moveToNext()) {
            String twonName=cursor.getString(cursor.getColumnIndex("twon"));
            //String cCode=cursor.getString(cursor.getColumnIndex("ccode"));
            //Log.e(TAG, "pCode=== "+cCode );
            twonList.add(twonName);
        }
        return twonList;
    }
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (nbpProvince.equals(picker)){
            provinceVal = newVal;
            initCityData(newVal);
        }else if (nbpCity.equals(picker)){
            initTwonData(provinceVal,newVal);
        }else if (nbpTwon.equals(picker)){

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (db.isOpen())
        db.close();
        finish();
    }
}
