package cn.com.cjland.zhirenguo.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.InOutCoinBean;

/**
 * Created by Administrator on 2016/1/11.
 */
public class CoinInoutAdapter  extends BaseAdapter {
    private List<InOutCoinBean> mList;
    private LayoutInflater mInflaier;
    private Context mContext;
    Typeface typeface;
    public CoinInoutAdapter(Context context, List<InOutCoinBean> data) {
        this.mContext = context;
        mList = data;
        mInflaier = LayoutInflater.from(context);
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
            convertView = mInflaier.inflate(R.layout.coin_in_out_item, null);
            viewHolder.txtName = (TextView)convertView.findViewById(R.id.in_out_name);
            viewHolder.txtName.setTypeface(typeface);
            viewHolder.txtTime = (TextView)convertView.findViewById(R.id.year_month_day);
            viewHolder.txtTime.setTypeface(typeface);
            viewHolder.txtCoinNum = (TextView)convertView.findViewById(R.id.inout_coin_number);
            viewHolder.txtCoinNum.setTypeface(typeface);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtName.setText(mList.get(position).coinName);
        viewHolder.txtTime.setText(mList.get(position).time);
        viewHolder.txtCoinNum.setText(mList.get(position).coinNumber);
        viewHolder.txtCoinNum.setTextColor(Integer.valueOf(mList.get(position).coinNumber) > 0 ?
                                         mContext.getResources().getColor(R.color.colorin) :
                                          mContext.getResources().getColor(R.color.colorout));
        return convertView;
    }
    //文意思
    class ViewHolder{
        public TextView txtName;//具体收支名称
        public TextView txtTime;//年月日
        //public TextView txtHM;//几点几分
        public TextView txtCoinNum;//收入支出
    }
}
