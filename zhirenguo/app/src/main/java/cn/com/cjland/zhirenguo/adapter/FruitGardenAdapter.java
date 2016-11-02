package cn.com.cjland.zhirenguo.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.FruitGarden;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.fragment.FruitFriendInviteDialog;
import cn.com.cjland.zhirenguo.fragment.FruitGarCenterDialog;
import cn.com.cjland.zhirenguo.fragment.FruitGarFragment;

public class FruitGardenAdapter extends ImagesBaseAdapter {
	FragmentManager mFragmentManager;
    Handler mHandler;
	String mUserId;
	public FruitGardenAdapter(Context mContext, FragmentManager mFragmentManager, Handler handler, List<?> mListData, int mItem) {
		super(mContext, mListData, mItem);
		this.mFragmentManager = mFragmentManager;
        this.mHandler = handler;
		mUserId = SharePreService.getUserId(mContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(mItem, null);
//			viewHolder.avatarView = (ImageView) convertView.findViewById(R.id.iv_garden_avatar);
			viewHolder.title = (TextView) convertView.findViewById(R.id.tv_garden_title);
//			viewHolder.summary = (TextView) convertView.findViewById(R.id.tv_garden_summary);
//			viewHolder.id = (TextView) convertView.findViewById(R.id.tv_garden_id);
//			viewHolder.msgView = (ImageView) convertView.findViewById(R.id.iv_garden_msg);
			viewHolder.invite = (Button) convertView.findViewById(R.id.btn_garden_invite);
			viewHolder.memberCount = (TextView) convertView.findViewById(R.id.tv_member_count);
//			viewHolder.holdings = (TextView) convertView.findViewById(R.id.tv_garden_holdings);
			viewHolder.gardenCenter = (LinearLayout) convertView.findViewById(R.id.lin_garden_center);
            viewHolder.treeView = (ImageView) convertView.findViewById(R.id.iv_garden_go);
			viewHolder.ball = (ImageView) convertView.findViewById(R.id.iv_default_ball);
			viewHolder.flag = (ImageView) convertView.findViewById(R.id.iv_default_flag);
			viewHolder.screen = (ImageView) convertView.findViewById(R.id.iv_garden_screen);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final FruitGarden fg = (FruitGarden) mListData.get(position);
		if (fg.masterId.equals(mUserId)) {
			viewHolder.screen.setBackgroundResource(R.drawable.ic_bg_garden_me);
		} else {
			viewHolder.screen.setBackgroundResource(R.drawable.ic_bg_garden_friend);
		}
		if (fg.id.equals(String.valueOf(FruitGarFragment.mDefaultgId))) {
			viewHolder.ball.setBackgroundResource(R.drawable.ic_bg_garden_selected);
			viewHolder.flag.setVisibility(View.VISIBLE);
		} else {
			viewHolder.ball.setBackgroundResource(R.drawable.ic_bg_garden_normal);
			viewHolder.flag.setVisibility(View.INVISIBLE);
		}
		viewHolder.title.setText(fg.title);
//		viewHolder.summary.setText(fg.summary);
//		viewHolder.id.setText(fg.id);
		viewHolder.memberCount.setText(fg.memberCount);
//		viewHolder.holdings.setText(fg.holdings);

//		if (null != fg.imgurl) {
//			DataServer.asyncImageLoad(viewHolder.avatarView, fg.imgurl);
//		}
		viewHolder.gardenCenter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FruitGarCenterDialog fruitGarCenterDialog = new FruitGarCenterDialog();
				Bundle bundle = new Bundle();
				bundle.putString("gardenId", fg.id);
				fruitGarCenterDialog.setArguments(bundle);
				fruitGarCenterDialog.show(mFragmentManager, "FruitGarCenterDialog");
			}
		});
		viewHolder.invite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FruitFriendInviteDialog fruitFriendInviteDialog = new FruitFriendInviteDialog();
				Bundle bundle = new Bundle();
				bundle.putString("gardenId", fg.id);
				fruitFriendInviteDialog.setArguments(bundle);
				fruitFriendInviteDialog.show(mFragmentManager, "FruitFriendInviteDialog");
			}
		});
        viewHolder.treeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				Log.e("wu","ID=="+fg.id);
                mHandler.sendMessage(mHandler.obtainMessage(0x5, fg));
            }
        });
		viewHolder.ball.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHandler.sendMessage(mHandler.obtainMessage(0x6, fg.id));
			}
		});
		return convertView;
	}

	private final static class ViewHolder{
		public ImageView avatarView;
		public TextView title;
		public TextView summary;
		public TextView id;
		public ImageView msgView;
		public Button invite;
		public TextView memberCount;
		public ImageView treeView;
		public ImageView ball;//设置默认
		public ImageView flag;//默认flag
		public ImageView screen;//果园电子屏
		public LinearLayout gardenCenter;
	}
}
