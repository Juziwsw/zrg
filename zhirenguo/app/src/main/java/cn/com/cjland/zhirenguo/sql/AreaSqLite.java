package cn.com.cjland.zhirenguo.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/12/31.
 */
public class AreaSqLite extends SQLiteOpenHelper{
    public AreaSqLite(Context context) {
        super(context, "AREA", null, 1);
    }
    public AreaSqLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String  sql="create table address("
                + "_id integer  primary key ,"
                + "provinces varchar(50),"
                + "pcode varchar(50),"
                + "city varchar(50),"
                + "ccode varchar(50),"
                + "twon varchar(50),"
                + "tcode varchar(50))";
        db.execSQL(sql);
    }
    //打开数据库
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
