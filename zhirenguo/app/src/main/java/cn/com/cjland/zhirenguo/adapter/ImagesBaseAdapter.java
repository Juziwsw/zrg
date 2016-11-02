package cn.com.cjland.zhirenguo.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.com.cjland.zhirenguo.bean.ImageWithId;

public class ImagesBaseAdapter extends BaseAdapter {
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(false).cacheOnDisc(true).build();
	Context mContext;
	File mCache;
	List<?> mListData;
	int mItem;
	LayoutInflater mInflater;

    public ImagesBaseAdapter(Context mContext, List<?> mListData, int mItem) {
		this.mContext = mContext;
		mCache = new File(Environment.getExternalStorageDirectory() + "/"
				+ "zhirenguo");
		if (!mCache.exists()) {
			mCache.mkdirs();
		}
		this.mListData = mListData;
		this.mItem = mItem;
		this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initImageLoader(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(mItem, null);
			//viewHolder.image = (ImageView) convertView.findViewById(R.id.action_right);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ImageWithId mImageWithHref = (ImageWithId)mListData.get(position);
		//DataServer.asyncImageLoad(viewHolder.image, mImageWithHref.mImgurl, mCache);
		//imageLoader.displayImage(mImageWithHref.mImgUrl, viewHolder.image, options);
		return convertView;
	}
	private static class ViewHolder{
		public ImageView image;
	}
	/**
	 * ImageLoader 图片组件初始化
	 *
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
