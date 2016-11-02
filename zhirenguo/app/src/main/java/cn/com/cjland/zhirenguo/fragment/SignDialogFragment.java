package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.utils.TimeUtils;
import cn.com.cjland.zhirenguo.views.Rotate3dAnimation;


public class SignDialogFragment extends DialogFragment implements View.OnClickListener{
    private Context context;
    private TextView mTvToday,mTvGet;
    private ImageView mImgSignClose,mImgBao,mImgSunshine;
    private SharedPreferences preferences;
    private String mPhoneImei,mPhoneVer,mUserId,mSignUrl,mParams,mResutData,mSignDate,mTodayData;
    private int mSignGetGold,mSignGetNum;
    private LinearLayout mLayoutGet;
    private View mSignView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences(
                SumConstants.SHAREDPREFERENCES_NAME, getActivity().MODE_PRIVATE);
        mPhoneImei = preferences.getString(SumConstants.PHONEIMEI, null);
        mPhoneVer = preferences.getString(SumConstants.PHONEVER, null);
        mUserId = preferences.getString(SumConstants.USERID, null);
        mSignDate = preferences.getString(SumConstants.SIGNDATE, "19971230");
        mSignGetGold = preferences.getInt(SumConstants.SIGNGETGOLD,0);
        mSignGetNum = preferences.getInt(SumConstants.SIGNGETNUM, 0);
        mSignUrl = getResources().getString(R.string.urlheader)+"/user/userSgin";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        mSignView = inflater.inflate(R.layout.fragment_sign_dialog, container);
        signFindview();
        judgeWhetherSign();
        return mSignView;
    }
    //获取所需控件
    private void signFindview(){
        mImgBao = (ImageView)mSignView.findViewById(R.id.img_sign_bao);
        mTvToday = (TextView)mSignView.findViewById(R.id.tv_sign_today);
        mTvGet = (TextView)mSignView.findViewById(R.id.txt_sign_get);
        mImgSignClose = (ImageView)mSignView.findViewById(R.id.img_sign_close);
        mLayoutGet = (LinearLayout)mSignView.findViewById(R.id.layout_sign_get);
        mImgSunshine = (ImageView)mSignView.findViewById(R.id.rotating_sunshine);
        mImgBao.setOnClickListener(this);
        mImgSignClose.setOnClickListener(this);
    }
    //判断是否已签到
    private void judgeWhetherSign(){
        //1 获取当前日期
        mTodayData = TimeUtils.getTodayDate();
        //2 当前日期 与 数据库的签到日期 比较
        int comoareValue = mTodayData.compareTo(mSignDate);
        if (comoareValue == 0) {//已签到
            mImgBao.setClickable(false);
            mTvToday.setText("今日已签到:第"+mSignGetNum+"个");
            mTvGet.setText("已领取"+mSignGetGold+"果币");
            mTvToday.setVisibility(View.VISIBLE);
            mLayoutGet.setVisibility(View.VISIBLE);
        }else if (comoareValue > 0) {//当前日期大于数据库保存日期 可签到
            mImgBao.setClickable(true);
        }else if (comoareValue < 0) {//当前日期小于 数据库保存日期 错误提示
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_sign_bao:
                //获取当前时间戳
                Long currnetT = TimeUtils.getTimeDate();
                mParams = "user_id="+mUserId+"&sginTime="+currnetT+"&fc_mobile_imei="+mPhoneImei+"&fc_mobile_ver="+mPhoneVer;
                Log.i("SignDialog", mParams);
                if(HttpUtils.isNetworkAvailable(context)){
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                mResutData = HttpUtils.PostString(context, mSignUrl,mParams);
                                Log.i("SignDialog", mResutData);
                                handleSignData(mResutData);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }else{
                    Toast.makeText(context, "" + getResources().getString(R.string.txt_network_waring), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_sign_close:
                getDialog().dismiss();
                break;
        }
    }
    //处理数据
    private Handler signhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if(bundle.getString("event").equals("0")){
                mTvToday.setText("今日签到:第"+bundle.getInt("num")+"个");
                mTvGet.setText("领取" + bundle.getInt("getGuob") + "果币");
                mTvToday.setVisibility(View.VISIBLE);
                mLayoutGet.setVisibility(View.VISIBLE);
                rotating();//旋转开始
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(SumConstants.SIGNDATE, mTodayData);
                editor.putInt(SumConstants.SIGNGETNUM, bundle.getInt("num"));
                editor.putInt(SumConstants.SIGNGETGOLD,bundle.getInt("getGuob"));
                editor.commit();
            }
            Toast.makeText(context, "" + bundle.getString("resultmsg"), Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * 处理返回数据
     * @param jsonString
     */
    private void handleSignData(String jsonString) {
        //转化JSON数据
        JSONObject jsonobject;
        Message msg = new Message();
        Bundle bundle = new Bundle();
        try {
            jsonobject = new JSONObject(jsonString);
            String event = jsonobject.getString("event");
            String resultmsg = jsonobject.getString("msg");
            if(event.equals("0")){
                JSONObject signJs = jsonobject.getJSONObject("ObjectList");
                bundle.putInt("num",signJs.getInt("num"));
                bundle.putInt("getGuob",signJs.getInt("fc_number"));
            }
            bundle.putString("event", event);
            bundle.putString("resultmsg", resultmsg);
            msg.setData(bundle);
            signhandler.sendMessage(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void  rotating(){
        float centerX=mImgSunshine.getWidth()/2f;
        float centerY=mImgSunshine.getHeight()/2f;
        // 构建3D旋转动画对象，旋转角度为0到90度，这使得textview将会从可见变为不可见
        //把我文字界面看做一个平面，旋转90度后，平面的左边棱正对着我们，只能看到一条棱，相当于看不见了(从平面变成了一根线)
        Rotate3dAnimation animation=new Rotate3dAnimation(0,360*5, centerX, centerY, 100.0f, true);
        animation.setDuration(1000);//动画持续时间设为500毫秒
        animation.setFillAfter(true);//动画完成后保持完成的状态
        mImgSunshine.startAnimation(animation);
    }

}
