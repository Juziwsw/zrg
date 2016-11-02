package cn.com.cjland.zhirenguo.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.RankingBean;
import cn.com.cjland.zhirenguo.fragment.SysAcountDialog;

/**
 * Created by Administrator on 2015/11/30.
 */
public class SysAcountAdapter extends BaseAdapter {
    private List<RankingBean> mList;
    private LayoutInflater mInflaier;
    private Context mContext;
    private Typeface typeface;
    private SysAcountDialog.ListItemCallBack callBack;
    public SysAcountAdapter(Context context, List<RankingBean> data,SysAcountDialog.ListItemCallBack callBack) {
        this.mContext = context;
        mList = data;
        mInflaier = LayoutInflater.from(context);
        this.callBack = callBack;
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
            convertView = mInflaier.inflate(R.layout.fragment_sysacount_item, null);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.img_sysacount_linetime);
            viewHolder.tvTime.setTypeface(typeface);
            viewHolder.ivmoney = (TextView)convertView.findViewById(R.id.img_sysacount_money);
            viewHolder.ivmoney.setTypeface(typeface);
            viewHolder.btnExtract = (Button)convertView.findViewById(R.id.btn_sysacount_extract);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTime.setText(!mList.get(position).rankTime.equals("") ? mList.get(position).rankTime : "");
        viewHolder.ivmoney.setText(!mList.get(position).rankMoney.equals("") ? mList.get(position).rankMoney : "");
        int rankStatus = Integer.parseInt(mList.get(position).rankStatus);
        switch (rankStatus){//0未提取1已提取2已过期
            case 0:
                viewHolder.btnExtract.setText("");
                viewHolder.btnExtract.setClickable(true);
                viewHolder.btnExtract.setBackgroundResource(R.drawable.ic_sysacount_extract);
                break;
            case 1:
                viewHolder.btnExtract.setText("已提取");
                viewHolder.btnExtract.setClickable(false);
                viewHolder.btnExtract.setBackgroundResource(R.color.transparent);
                break;
            case 2:
                viewHolder.btnExtract.setText("已过期");
                viewHolder.btnExtract.setClickable(false);
                viewHolder.btnExtract.setBackgroundResource(R.color.transparent);
                break;
        }
        viewHolder.btnExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btnExtract) {
                callBack.ListItemListener(btnExtract,mList.get(position).rankId,mList.get(position).rankMoney);
            }
        });
        return convertView;
    }
    //文意思
    class ViewHolder{
//        public TextView tvExtract;
        public TextView tvTime;
        public TextView ivmoney;
        public Button btnExtract;
    }
}
