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
import cn.com.cjland.zhirenguo.bean.FruitFriend;
import cn.com.cjland.zhirenguo.fragment.FruitFriFragment;

public class FruitFriendAdapter extends ImagesBaseAdapter {
	Handler mHandler;
	public FruitFriendAdapter(Context mContext, List<?> mListData, int mItem) {
		super(mContext, mListData, mItem);
		// TODO Auto-generated constructor stub
	}
	public FruitFriendAdapter(Context mContext, Handler handler, List<?> mListData, int mItem) {
		super(mContext, mListData, mItem);
		mHandler = handler;
		// TODO Auto-generated constructor stub
	}
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(mItem, null);
			viewHolder.avatarView = (ImageView) convertView.findViewById(R.id.iv_friend_avatar);
			viewHolder.title = (TextView) convertView.findViewById(R.id.tv_friend_title);
			viewHolder.summary = (TextView) convertView.findViewById(R.id.tv_friend_summary);
			viewHolder.invite = (Button) convertView.findViewById(R.id.btn_invite);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final FruitFriend fg = (FruitFriend) mListData.get(position);
		viewHolder.title.setText(fg.title);
		viewHolder.summary.setText(fg.summary);
		if (null != fg.imgurl) {
			DataServer.asyncImageLoad(viewHolder.avatarView, fg.imgurl);
		}
		if (2 == FruitFriFragment.flag) {
			viewHolder.invite.setVisibility(View.VISIBLE);
		}
		viewHolder.invite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHandler.sendMessage(mHandler.obtainMessage(0x58, fg.id));
			}
		});
		return convertView;
	}

	private final static class ViewHolder{
		public ImageView avatarView;
		public TextView title;
		public TextView summary;
		public Button invite;
	}
}
