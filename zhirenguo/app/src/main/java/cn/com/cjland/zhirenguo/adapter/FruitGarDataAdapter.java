package cn.com.cjland.zhirenguo.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.DataServer;
import cn.com.cjland.zhirenguo.bean.FruitGarden;

public class FruitGarDataAdapter extends ImagesBaseAdapter {
	Handler mHandler;
	public FruitGarDataAdapter(Context mContext, Handler handler, List<?> mListData, int mItem) {
		super(mContext, mListData, mItem);
		// TODO Auto-generated constructor stub
		mHandler = handler;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(mItem, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.tv_garden_title);
			viewHolder.summary = (TextView) convertView.findViewById(R.id.tv_garden_summary);
			viewHolder.apply = (Button) convertView.findViewById(R.id.btn_garden_apply);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final FruitGarden fg = (FruitGarden) mListData.get(position);

		viewHolder.title.setText(fg.title);
		viewHolder.summary.setText(fg.summary);
		viewHolder.apply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHandler.sendMessage(mHandler.obtainMessage(0x68, fg.id));
			}
		});
		return convertView;
	}

	private final static class ViewHolder{
		public TextView title;
		public TextView summary;
		public Button apply;
	}
}
