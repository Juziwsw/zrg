package cn.com.cjland.zhirenguo.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/30.
 */
public class JsonHelper {
    /**
     * 从JSON对象中取得字符串
     *
     * @param o
     * @param name
     * @return
     */
    public static String getString(JSONObject o, String name) {
        String ret = "";
        try {
            if ((o != null) && (!o.isNull(name))) {
                ret = o.getString(name);
                if(ret==null||"null".equals(ret))
                    return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }
    /**
     * 从JSON数组中取得字符串
     */
    public static List<Map<String, String>> getQueryList(JSONArray arr) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                JSONArray names = o.names();
                Map<String, String> m = new HashMap<String, String>();
                for (int j = 0; j < names.length(); j++) {
                    m.put(names.getString(j), getString(o, names.getString(j)));
                }
                list.add(m);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 从JSON对象中取得字符串
     */
    public static List<Map<String, String>> getQueryList(JSONObject obj) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray names = obj.names();
            Map<String, String> m = new HashMap<String, String>();
            for (int j = 0; j < names.length(); j++) {
                m.put(names.getString(j), getString(obj, names.getString(j)));
            }
            list.add(m);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
