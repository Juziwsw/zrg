package cn.com.cjland.zhirenguo.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.views.RoundCornerProgressBar;


public class MainFragment extends Fragment implements View.OnClickListener{
    private static Context context;
    private RelativeLayout mRelFruitBag;
    private static RelativeLayout mRelGrowthValue;
    private ImageView mImgLeft,mImgShare,mImgHelp,mImgSign,mImgNews;
    private LinearLayout mLayoutLeft;
    private View mView;
    private static TextView mTvNum01;
    private static TextView mTvNum02;
    private static TextView mTvNum03;
    private static TextView mTvGrowthValue;
    private Boolean isLeftShow = false;//左边栏状态值 默认为：false
    private Bundle mSavedInstanceState;
    private static ImageView mUseravatar;
    private static RoundCornerProgressBar mRounfGProgress;
    private static Map<String,String> maps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        mSavedInstanceState = savedInstanceState;
        View gardenview = inflater.inflate(R.layout.fragment_main_title,container, false);
        mView = gardenview;
        Findview();//获取所需控件
        new getHeadData().start();
        //设置广播器
        IntentFilter filter = new IntentFilter();
        filter.addAction(SumConstants.INTENTACTION);
        getActivity().registerReceiver(broadcastReceiver, filter);
        return gardenview;
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getExtras().getInt("index");
            switch (index){
                case 1:
                case 4:
                case 2:
                    int num = Integer.parseInt(mTvNum02.getText().toString())+1;
                    mTvNum02.setText(""+num);
                    break;
                case 3:
                    break;
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    //获取所需控件
    private void Findview(){
        mRelFruitBag = (RelativeLayout) mView.findViewById(R.id.rel_fruit_bag);
        mRelFruitBag.setOnClickListener(this);
        mImgLeft = (ImageView)mView.findViewById(R.id.img_main_left);
        mImgShare = (ImageView)mView.findViewById(R.id.img_main_share);
        mImgHelp = (ImageView)mView.findViewById(R.id.img_main_help);
        mImgSign = (ImageView)mView.findViewById(R.id.img_main_sign);
        mLayoutLeft = (LinearLayout)mView.findViewById(R.id.layout_main_left);
        mTvNum01 = (TextView)mView.findViewById(R.id.tv_main_01);
        mTvNum02 = (TextView)mView.findViewById(R.id.tv_main_02);
        mTvNum03 = (TextView)mView.findViewById(R.id.tv_main_03);
        mUseravatar = (ImageView)mView.findViewById(R.id.user_avatar);
        mRelGrowthValue = (RelativeLayout) mView.findViewById(R.id.layout_garden_growth_value);//果树成长值
        mRounfGProgress = (RoundCornerProgressBar)mView.findViewById(R.id.progress_garden_growth_value);//进度条
        mTvGrowthValue = (TextView)mView.findViewById(R.id.tv_garden_growth_value);//果树成长值
        SetTextRType();//调用字体样式方法
        mImgNews = (ImageView)mView.findViewById(R.id.img_main_message);
        mImgLeft.setOnClickListener(this);
        mImgShare.setOnClickListener(this);
        mImgNews.setOnClickListener(this);
        mImgSign.setOnClickListener(this);
        mUseravatar.setOnClickListener(this);
        mImgHelp.setOnClickListener(this);
    }
    //设置字体样式
    private void SetTextRType(){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Font1.ttf");
        mTvNum01.setTypeface(typeface);
        mTvNum02.setTypeface(typeface);
        mTvNum03.setTypeface(typeface);
        mTvGrowthValue.setTypeface(typeface);
    }
    //监听事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_main_left:
                ChangeLeft();
                break;
            case R.id.img_main_share:
                //showInoutDialog();
                Toast.makeText(context, "我是分享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_main_message:
                showEditDialog();
                break;
            case R.id.img_main_sign:
                showSignDialog();
                break;
            case R.id.rel_fruit_bag:
                showBagDialog();
                break;
            case R.id.user_avatar:
                showSelfDialog();
                break;
            case R.id.img_main_help:
                showFeekDialog();
                break;
        }
    }
    //显示消息
    public void showEditDialog() {
        if(mSavedInstanceState == null){
            MsgDialogFragment editNameDialog = new MsgDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name","CC");
            editNameDialog.setArguments(bundle);//将activity的值传给DialogFragmentDemo
            editNameDialog.show(getFragmentManager(), "DialogFragmentDemo");
        }
    }
    //果袋
    private void showBagDialog(){
        if(mSavedInstanceState == null){
            FruitBagDialog fruitBagDialog = new FruitBagDialog();
            fruitBagDialog.show(getFragmentManager(), "FruitBagDialog");
        }
    }
    //果币收支
    private void showInoutDialog(){
        if(mSavedInstanceState == null){
            CoinInOutDialog fruitBagDialog = new CoinInOutDialog();
            fruitBagDialog.show(getFragmentManager(), "fruitBagDialog");
        }
    }

    //签到
    private void showSignDialog(){
        if(mSavedInstanceState == null){
            SignDialogFragment editNameDialog = new SignDialogFragment();
            editNameDialog.show(getFragmentManager(), "SignDialogFragment");
        }
    }
    //个人信息
    private void showSelfDialog(){
        if(mSavedInstanceState == null){
            SelfDialogFragment selfinfoDialog = new SelfDialogFragment();
            selfinfoDialog.show(getFragmentManager(), "SelfDialogFragment");
        }
    }
    //意见反馈
    private void showFeekDialog(){
        if(mSavedInstanceState == null){
            FeekBackDialogFragment feekbackDialog = new FeekBackDialogFragment();
            feekbackDialog.show(getFragmentManager(), "FeekBackDialogFragment");
        }
    }

    //左边栏 修改布局样式
    private void ChangeLeft(){
        if(isLeftShow){
            isLeftShow = false;
            mImgLeft.setImageResource(R.drawable.ic_home_left_btn01);
            mLayoutLeft.setVisibility(View.GONE);
        }else {
            isLeftShow = true;
            mImgLeft.setImageResource(R.drawable.ic_home_left_btn01_up);
            mLayoutLeft.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_right_in));
            mLayoutLeft.setVisibility(View.VISIBLE);
        }
    }
    //当进入果园-单个果园时 显示成长值
    static Handler mainhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x123://显示成长值 并设置成长值
                    mRelGrowthValue.setVisibility(View.VISIBLE);
                    mRounfGProgress.setProgress(msg.arg1);
                    mTvGrowthValue.setText(""+msg.arg1);
                    break;
                case 100://隐藏成长值
                    mRelGrowthValue.setVisibility(View.GONE);
                    break;
                case 200://修改个人头像
                    String picPath = msg.obj.toString();
                    File file = new File(picPath);
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                        mUseravatar.setScaleType(ImageView.ScaleType.FIT_XY);
                        mUseravatar.setImageBitmap(bitmap);
                    }
                    break;
                case 201:
                    String obj = (String) msg.obj;
                    mTvNum01.setText(obj);
                    break;
                case 202:
                    String objoo = (String) msg.obj;
                    mTvNum01.setText(objoo);
                    break;
                case 203:
                    int money = (int) msg.obj;
                    String mCurrentMoney = mTvNum01.getText().toString();
                    int newMoney = money+Integer.parseInt(mCurrentMoney);
                    mTvNum01.setText(""+newMoney);
                    break;
                case 0x205:
                    maps.put("friend",msg.obj.toString());
                    mTvNum03.setText(maps.get("friend"));
                    break;
                case 0x206:
                    int num = Integer.parseInt(mTvNum03.getText().toString())+1;
                    maps.put("friend",""+num);
                    mTvNum03.setText(maps.get("friend"));
                    break;
                default:
                    Toast.makeText(context,msg.obj.toString(),Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 203://修改个人头像
                    mTvNum01.setText(maps.get("balance"));
                    mTvNum02.setText(maps.get("garden"));
                    mTvNum03.setText(maps.get("friend"));
                    String picUrl = msg.obj.toString();
                    if(!picUrl.equals("")){
                        mUseravatar.setTag(picUrl);
                        getGroupHeader(mUseravatar, picUrl);
                    }else {
                        mUseravatar.setImageResource(R.drawable.ic_userinfo_header_default);
                    }
                    break;
            }
        }
    };
    //获取头数据
    private class getHeadData extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                String parmas = "UserId=" + SharePreService.getUserId(context);
                String loginResult = HttpUtils.PostString(context, getResources().getString(R.string.urlheader) + "/user/UserSundry", parmas);
                Log.e("wu","loginResult=="+loginResult);
                if (loginResult == null) return;
                JSONObject jsonObject = new JSONObject(loginResult);
                String is_ok = jsonObject.getString("event");
                Message msg = new Message();
                if (is_ok.equals("0")) {
                    JSONObject obj = jsonObject.getJSONObject("ObjectList");
                    maps = new HashMap<String, String>();
                    maps.put("friend",obj.getString("friend"));
                    maps.put("balance",obj.getString("balance"));
                    maps.put("garden",obj.getString("garden"));
                    String picUrl = obj.getString("user_favicon");
                    if (!picUrl.equals("")){
                        mUseravatar.setTag(picUrl);
                        Message filemsg = new Message();
                        filemsg.what = 203;
                        filemsg.obj = picUrl;
                        mHandler.sendMessage(filemsg);
                    }
                } else {
                    Message message = new Message();
                    message.what = Integer.valueOf(is_ok);
                    msg.obj = jsonObject.getString("msg");
                    mHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //获取头像
    private static void getGroupHeader(ImageView iamgeview,String url){
        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(context);
        imageLoader.displayImage(url, iamgeview);
    }
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove
                .build();
        ImageLoader.getInstance().init(config);
    }
}
