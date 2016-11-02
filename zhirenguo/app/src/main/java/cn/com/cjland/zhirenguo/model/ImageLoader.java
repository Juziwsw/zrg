package cn.com.cjland.zhirenguo.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader {
	private ImageView mImageView;
	private String mUrl;
	private LruCache<String, Bitmap> mCaches;
	//构造方法
	public ImageLoader() {
		//获取当前应用可用空间大小
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory/4;
		mCaches = new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				//在每次存入缓存的时候调用
				return value.getByteCount();
			}
		};
	}
	//增加到缓存
	public void addBitmapToCache(String url,Bitmap bitmap){
		if(getBitmapFromCache(url) == null){
			mCaches.put(url, bitmap);
		}
	}
	//从缓存中获取数据
	public Bitmap getBitmapFromCache(String url){
		return mCaches.get(url);
	}
	/**
	 * 多线程加载图片
	 * @param imageview
	 * @param url
	 */
	public void showImageByThread(ImageView imageview, final String url){
		mImageView = imageview;
		mUrl = url;
		new Thread(){
			@Override
			public void run() {
				super.run();
				Bitmap bitmap = getBitmapFromURL(mUrl);
				Message msg = Message.obtain();
				msg.obj = bitmap;
				mHandler.sendMessage(msg);
			}
		}.start();
	}
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(mImageView.getTag().equals(mUrl)){
				mImageView.setImageBitmap((Bitmap) msg.obj);//取出线程传过了的bitmap
			}
		};
	};
	//通过URL获取图片
	public Bitmap getBitmapFromURL(String urlstring){
		Bitmap bitmap;
		InputStream is;
		try {
			URL mUrl = new URL(urlstring);
			HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
			is = new BufferedInputStream(connection.getInputStream());
			bitmap = BitmapFactory.decodeStream(is);
			connection.disconnect();
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * AsyncTask 异步加载图片
	 * @param imageview
	 * @param url
	 */
	public void showImageByAsycnTask(ImageView imageview,String url){
		//从缓存中获取对应的图片
		Bitmap bitmap = getBitmapFromCache(url);
		//如果缓存中没有，那么必须去下载
		if(bitmap == null){
			new NewsAsycntask(imageview,url).execute(url);
		}else{
			imageview.setImageBitmap(bitmap);
		}

	}
	private class NewsAsycntask extends AsyncTask<String, Void, Bitmap>{
		private ImageView mIgview;
		private String asycnurl;
		public NewsAsycntask (ImageView imageview,String url){
			mIgview = imageview;
			asycnurl = url;
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			//从网络获取图片
			Bitmap bitmap = getBitmapFromURL(params[0]);
			if(bitmap!=null){
				//将不再缓存的图片加入缓存
				addBitmapToCache(params[0], bitmap);
			}
			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			if(mIgview.getTag().equals(asycnurl)){
				mIgview.setImageBitmap(bitmap);
			}
		}
	}
}
