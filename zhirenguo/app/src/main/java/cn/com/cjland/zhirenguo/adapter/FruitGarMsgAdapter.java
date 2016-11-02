package cn.com.cjland.zhirenguo.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.DataServer;
import cn.com.cjland.zhirenguo.bean.FruitGarden;

public class FruitGarMsgAdapter extends ImagesBaseAdapter {
	Dialog mDialog;
	public FruitGarMsgAdapter(Context mContext, List<?> mListData, int mItem) {
		super(mContext, mListData, mItem);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(mItem, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.tv_msg_title);
			viewHolder.summary = (TextView) convertView.findViewById(R.id.tv_msg_summary);
			viewHolder.legendView = (ImageView) convertView.findViewById(R.id.iv_msg_legend);
			viewHolder.linHeader = (LinearLayout) convertView.findViewById(R.id.lin_msg_content);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		FruitGarden fg = (FruitGarden) mListData.get(position);
		if (0 == position) {
			viewHolder.legendView.setImageResource(R.drawable.ic_garden_msg_header);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, DataServer.dip2px(mContext, 50), 0, 0);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			viewHolder.linHeader.setLayoutParams(lp);
		} else {
			viewHolder.legendView.setImageResource(R.drawable.ic_garden_msg_line);
		}
		viewHolder.title.setText(fg.time);
		viewHolder.summary.setText(fg.content);
		return convertView;
	}
	private final static class ViewHolder{
		public ImageView legendView;
		public TextView title;
		public TextView summary;
		public LinearLayout linHeader;
	}
}
