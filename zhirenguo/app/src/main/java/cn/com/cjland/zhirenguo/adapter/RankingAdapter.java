package cn.com.cjland.zhirenguo.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.Messagebean;
import cn.com.cjland.zhirenguo.bean.RankingBean;
import cn.com.cjland.zhirenguo.fragment.MsgDialogFragment;
import cn.com.cjland.zhirenguo.model.ImageLoader;

/**
 * Created by Administrator on 2015/11/30.
 */
public class RankingAdapter extends BaseAdapter {
    private List<RankingBean> mList;
    private LayoutInflater mInflaier;
    private ImageLoader imageloader;
    private Context mContext;
    Typeface typeface;
    public RankingAdapter(Context context, List<RankingBean> data) {
        this.mContext = context;
        mList = data;
        mInflaier = LayoutInflater.from(context);
        imageloader = new ImageLoader();
        this.typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Font1.ttf");
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflaier.inflate(R.layout.fragment_ranking_item, null);
            viewHolder.ivIcon = (ImageView)convertView.findViewById(R.id.iv_ranking_header);
            viewHolder.tvNum = (TextView)convertView.findViewById(R.id.txt_ranking_rank);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_ranking_name);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tv_ranking_time);
            viewHolder.tvTime.setTypeface(typeface);
            viewHolder.ivmoney = (TextView)convertView.findViewById(R.id.img_ranking_money);
            viewHolder.ivmoney.setTypeface(typeface);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivIcon.setImageResource(R.drawable.ic_person_header);//设置默认图片
        String url = mList.get(position).rankHeaderUrl;
        if(!url.equals("")){
            //多线程加载图片
            viewHolder.ivIcon.setTag(url);//给imageview设置一个名为url的tag,就是相对应的进行绑定
            imageloader.showImageByAsycnTask(viewHolder.ivIcon, url);
        }else{
            viewHolder.ivIcon.setImageResource(R.drawable.ic_userinfo_header_default);//设置默认图片
        }
        viewHolder.tvName.setText(!mList.get(position).rankName.equals("") ? mList.get(position).rankName : "无名");
        if(mList.get(position).oraderNo.equals("0")){
            viewHolder.tvNum.setVisibility(View.GONE);
        }else{
            if(position == 0){
                viewHolder.tvNum.setText("");
                viewHolder.tvNum.setBackgroundResource(R.drawable.ic_ranking_one);
            }else{
                viewHolder.tvNum.setText(""+(position+1));
                viewHolder.tvNum.setBackgroundResource(R.color.transparent);
            }
        }
        viewHolder.tvTime.setText(!mList.get(position).rankTime.equals("") ? mList.get(position).rankTime : "");
        viewHolder.ivmoney.setText(!mList.get(position).rankMoney.equals("") ? mList.get(position).rankMoney : "");
        return convertView;
    }
    //文意思
    class ViewHolder{
        public TextView tvName;
        public TextView tvTime;
        public ImageView ivIcon;
        public TextView tvNum;
        public TextView ivmoney;
    }
}
