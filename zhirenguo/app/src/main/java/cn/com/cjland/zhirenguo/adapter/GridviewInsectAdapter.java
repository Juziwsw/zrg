package cn.com.cjland.zhirenguo.adapter;

import java.io.File;
import java.util.List;
import java.util.Random;

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

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.ImageWithId;

public class GridviewInsectAdapter extends BaseAdapter {
    Context mContext;
    List<?> mListData;
    int mItem;
    LayoutInflater mInflater;
    Random random = new Random();

    public GridviewInsectAdapter(Context mContext, List<?> mListData, int mItem) {
        this.mContext = mContext;
        this.mListData = mListData;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItem = mItem;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(mItem, null);
            viewHolder.image = (ImageView)convertView.findViewById(R.id.img_grid_insect);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageWithId mImageWithHref = (ImageWithId)mListData.get(position);
        if(mImageWithHref.ishas){
            switch (mImageWithHref.mInsect){
                case 0:
                    viewHolder.image.setImageResource(R.drawable.ic_garden_insect_01);
                    break;
                case 1:
                    viewHolder.image.setImageResource(R.drawable.ic_garden_insect_02);
                    break;
            }
            viewHolder.image.setVisibility(View.VISIBLE);
        }else {
            viewHolder.image.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
    private static class ViewHolder{
        public ImageView image;
    }
}
