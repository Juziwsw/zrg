package cn.com.cjland.zhirenguo.bean;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.utils.HttpUtils;

/**
 * Created by Administrator on 2015/11/27.
 */
public class DataServer {
    public static File mCache;
    //
    public static List<String> getImgurls(String path) throws Exception{
        // TODO Auto-generated method stub
        if (null == path) {
            return null;
        }
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200){
            return parseJson2String(conn.getInputStream());
        }
        return null;
    }
    private static List<String> parseJson2String(InputStream inputStream) throws Exception{
        // TODO Auto-generated method stub
        List<String> imgurls = new ArrayList<String>();
        String imgurl = null;
        String strData = changeInputString(inputStream);
        JSONArray arr = new JSONArray(strData);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject temp = (JSONObject) arr.get(i);
            imgurl = temp.getString("imgurl");
            imgurls.add(imgurl);
        }
        return imgurls;
    }
    //////////////////////////////////////////////////////////
    public static List<FruitFriend> getFruitFriends(Context context) throws Exception{
        // TODO Auto-generated method stub
        if (!HttpUtils.isNetworkAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.txt_network_waring), Toast.LENGTH_SHORT).show();
            return null;
        }
        String path = context.getResources().getString(R.string.urlheader)+"/friend/getfriends";
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        //post请求的参数
        String data="user_id=" + SharePreService.getShaedPrerence(context, SumConstants.USERID);
        OutputStream out=conn.getOutputStream();
        out.write(data.getBytes());
        out.flush();
        out.close();
        conn.connect();
        if (conn.getResponseCode() == 200){
            Log.e("wu", "1111111111111111 " );
            return parseJson2friends(conn.getInputStream());
        }
        return null;
    }
    private static List<FruitFriend> parseJson2friends(InputStream inputStream) throws Exception{
        // TODO Auto-generated method stub
        List<FruitFriend> friendList = null;
        FruitFriend fruitFriend = null;
        String strData = changeInputString(inputStream);
        Log.e("wu", "strData== "+strData );
        JSONObject data = new JSONObject(strData);
        if (data.getString("event").equals("0")) {
            friendList = new ArrayList<FruitFriend>();
            JSONArray arr = new JSONArray(data.getString("objList"));
            //System.out.println("数据leng:"+arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject temp = (JSONObject) arr.get(i);
                fruitFriend = new FruitFriend();
                fruitFriend.imgurl = temp.getString("user_favicon");
                fruitFriend.id = temp.getString("userid");
                fruitFriend.title = temp.getString("user_nickname");
                fruitFriend.summary = temp.getString("user_signature");
                friendList.add(fruitFriend);
            }
        }
        return friendList;
    }

    public static String changeInputString(InputStream inputStream) {

        String jsonString="";
        ByteArrayOutputStream outPutStream=new ByteArrayOutputStream();
        byte[] data=new byte[1024];
        int len=0;
        try {
            while((len=inputStream.read(data))!=-1){
                outPutStream.write(data, 0, len);
            }
            jsonString=new String(outPutStream.toByteArray());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonString;
    }
    /**
     * 获取网络图片,如果图片存在于缓存中，就返回该图片，否则从网络中加载该图片并缓存起来
     * @param path 图片路径
     * @return
     */
    public static Uri getImage(String path, File cacheDir) throws Exception{// path -> MD5 ->32字符串.jpg
        File localFile = new File(cacheDir, MD5.getMD5(path)+ path.substring(path.lastIndexOf(".")));
        if(localFile.exists()){
            return Uri.fromFile(localFile);
        }else{
            HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                FileOutputStream outStream = new FileOutputStream(localFile);
                InputStream inputStream = conn.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while( (len = inputStream.read(buffer)) != -1){
                    outStream.write(buffer, 0, len);
                }
                inputStream.close();
                outStream.close();
                return Uri.fromFile(localFile);
            }
        }
        return null;
    }
    public static void asyncImageLoad(ImageView imageView, String imageurl) {
        // TODO Auto-generated method stub
        mCache = new File(Environment.getExternalStorageDirectory() + "/"
                + "zhirenguo");
        if (!mCache.exists()) {
            mCache.mkdirs();
        }
        AsyncImageTask asyncImageTask = new AsyncImageTask(imageView);
        asyncImageTask.execute(imageurl);
    }
    private static class AsyncImageTask extends AsyncTask<String, Integer, Uri> {
        private ImageView imageView;
        public AsyncImageTask(ImageView imageView) {
            this.imageView = imageView;
        }
        protected Uri doInBackground(String... params) {//子线程中执行的
            try {
                return DataServer.getImage(params[0], mCache);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Uri result) {//运行在主线程
            if(result!=null && imageView!= null)
                imageView.setImageURI(result);
        }
    }
    public static int dip2px(Context context, float dipValue)
    {
        float m=context.getResources().getDisplayMetrics().density ;
        return (int)(dipValue * m + 0.5f) ;
    }

    public static int px2dip(Context context, float pxValue)
    {
        float m=context.getResources().getDisplayMetrics().density ;
        return (int)(pxValue / m + 0.5f) ;
    }
}
