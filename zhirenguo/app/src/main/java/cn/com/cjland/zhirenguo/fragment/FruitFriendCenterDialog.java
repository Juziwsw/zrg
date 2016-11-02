package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.DataServer;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.utils.TimeUtils;


public class FruitFriendCenterDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    String mFriendId;
    ImageView mUserAvatar;
    TextView mUserName;
    TextView mUserSex;
    TextView mNiceName;
    TextView mUserSign;
    TextView mRegistDate;
    TextView mUserId;
    TextView mUserLocate;
    private ImageView mClose;
    /**
     * 处理activity传递过来的值
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        if (null != getArguments()) {
            mFriendId = getArguments().getString("friendId");
        }
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.fruit_friend_center, container);
        mUserAvatar = (ImageView) view.findViewById(R.id.iv_friend_avatar);
        mNiceName = (TextView) view.findViewById(R.id.tv_friend_title);
        mUserName = (TextView) view.findViewById(R.id.tv_friend_name);
        mUserSex = (TextView) view.findViewById(R.id.tv_friend_sex);
        mUserSign = (TextView) view.findViewById(R.id.tv_friend_summary);
        mRegistDate = (TextView) view.findViewById(R.id.tv_regist_date);
        mUserId = (TextView) view.findViewById(R.id.tv_regist_id);
        mUserLocate = (TextView) view.findViewById(R.id.tv_user_locate);
        mClose = (ImageView) view.findViewById(R.id.iv_close);
        mClose.setOnClickListener(this);
        new GetDataTask().execute();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }
    private class GetDataTask extends AsyncTask<Void, Void, String> {
        String resultdata = null;
        String url = getResources().getString(R.string.urlheader)+"/friend/getuserlist";
        String param = "user_id=" + mFriendId;
        //后台处理部分
        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job.
            try {
                resultdata = new HttpUtils().PostString(mContext, url, param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultdata;
        }
        @Override
        protected void onPostExecute(String result) {
            if (null == result) {
                System.out.println("数据为空");
                return;
            }
            try {
                JSONObject dataUser = new JSONObject(result);
                if (dataUser.getString("event").equals("0")) {
                    JSONObject data = new JSONObject(dataUser.getString("obj"));
                    String imgUrl= data.getString("user_favicon");
                    mNiceName.setText(data.getString("user_nickname"));
                    mUserName.setText(data.getString("user_name"));
                    mUserSex.setText(data.getString("user_sex").equals("0") ? "男" : "女");
                    mUserSign.setText(data.getString("user_signature"));
                    mRegistDate.setText(TimeUtils.getDateToString2(1000*Long.valueOf(data.getString("add_sj"))));
                    mUserId.setText(data.getString("user_id"));
                    mUserLocate.setText(data.getString("user_pro")+"省"+data.getString("user_city")+"市"+data.getString("user_town")+"区");
                    DataServer.asyncImageLoad(mUserAvatar, imgUrl);
                } else {
                    Toast.makeText(mContext, dataUser.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }  catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
}
