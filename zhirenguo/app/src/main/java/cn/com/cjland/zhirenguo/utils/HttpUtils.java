package cn.com.cjland.zhirenguo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.com.cjland.zhirenguo.R;

/**
 * Created by Administrator on 2015/11/23.
 * 网络连接工具类，用于判断网络类型，网络是否可用，以及与web服务器交互
 * @author ztt
 */
public class HttpUtils {
    private static String[] sessionId;
    public HttpUtils() {

    }
    /**
     * 判断是否有网络连接
     * @param context
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        //获取网络管理对象
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            //获取所有代表联网状态的NetWorkInfo对象
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null){
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 判断当前是否连接上wifi网络
     * @param context
     * @return boolean
     */
    public static boolean isWifiConnected(Context context){
        //获取网络管理对象
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null != cm){
            //获取当前代表联网状态的NetWorkInfo对象
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if(null != netInfo){
                int netType = netInfo.getType();
                if(ConnectivityManager.TYPE_WIFI == netType){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 判断当前是否连接上2G/3G网络
     * @param context
     * @return boolean
     */
    public static boolean isMobileConnected(Context context){
        //获取网络管理对象
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null != cm){
            //获取当前代表联网状态的NetWorkInfo对象
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if(null != netInfo){
                int netType = netInfo.getType();
                if(ConnectivityManager.TYPE_MOBILE == netType){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 手机自带网络连接
     * @param url
     * @return
     */
    public static String PostString(Context context, String url,String parmas) throws MalformedURLException {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.txt_network_waring),Toast.LENGTH_SHORT).show();
            return null;
        }
        URL mUrl = new URL(url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) mUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
            OutputStream out = conn.getOutputStream();//获取输入流
            String content = parmas;//需要传递的参数
            out.write(content.getBytes());//向服务器传递数据
            InputStream is = conn.getInputStream();
            return readStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }
    /**
     * 手机自带网络连接   注册 保存session
     * @param url
     * @return
     */
    public static String EncollPostString(Context context, String url,String parmas) throws MalformedURLException {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.txt_network_waring),Toast.LENGTH_SHORT).show();
            return null;
        }
        URL mUrl = new URL(url);
        HttpURLConnection conn = null;
        String session_value;
        try {
            conn = (HttpURLConnection) mUrl.openConnection();
            //保存session信息
            conn.setRequestProperty("Cookie", sessionId[0]);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
//            conn.connect();
            OutputStream out = conn.getOutputStream();//获取输入流
            String content = parmas;//需要传递的参数
            out.write(content.getBytes());//向服务器传递数据
            InputStream is = conn.getInputStream();
            return readStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return null;
    }
    /**
     * 手机自带网络连接 -- 注册 获取session
     * @param url
     * @return
     */
    public static String CodePostString(Context context, String url,String parmas) throws MalformedURLException {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.txt_network_waring),Toast.LENGTH_SHORT).show();
            return null;
        }
        URL mUrl = new URL(url);
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) mUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
//            conn.connect();
            OutputStream out = conn.getOutputStream();//获取输入流
            String content = parmas;//需要传递的参数
            out.write(content.getBytes());//向服务器传递数据
            InputStream is = conn.getInputStream();
            if(is!=null){
                return readStream(is);
            }else{
                Toast.makeText(context, context.getResources().getString(R.string.toast_servicer_error_01),Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                //获得session信息
                String session_value = conn.getHeaderField("Set-Cookie");
                sessionId = session_value.split(";");
                conn.disconnect();
            }
        }
        return null;
    }
    //将JSON语句转换为我们需要的字符串
    /**
     * 通过is解析网页返回的数据
     * @param isresult
     * @return
     */
    public static String readStream(InputStream isresult){
        InputStreamReader ism;
        String result = "";
        String line = "";
        try {
            ism = new InputStreamReader(isresult,"utf-8");
            BufferedReader br = new BufferedReader(ism);
            while((line = br.readLine())!=null){
                result+=line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 上传文件至Server的方法 */
    public String uploadFile (Context context, String mUrl,String filepath){
        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.txt_network_waring),Toast.LENGTH_SHORT).show();
            return null;
        }
        String boundary = "---------------------------7de2c25201d48";
        String prex = "--";
        String end = "\r\n";
        URL url;
        try {
            url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);//允许向服务器写入数据
            conn.setDoInput(true);//允许服务器输出数据
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(prex+boundary+end);
            out.writeBytes("Content-Disposition:form-data;"+"name=\"filename\";filename=\""+"Header.jpg"+"\""+end+"Content-Type:image/jpeg"+end);
            out.writeBytes(end);
            FileInputStream fileinputstream = new FileInputStream(new File(filepath));
            byte[] b = new byte[1024*4];
            int len;
            while((len=fileinputstream.read(b))!=-1){
                out.write(b,0,len);
            }
//            out.write(parmas.getBytes());
            out.writeBytes(end);

            out.writeBytes(prex+boundary+prex+end);
            out.flush();
            Log.e("SelfDialogFragment", "2222222222222222222");
            /* 取得Response内容 */
            InputStream is = conn.getInputStream();
            Log.e("SelfDialogFragment", "55555555555555");
            return readStream(is);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
