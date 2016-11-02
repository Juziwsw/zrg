package cn.com.cjland.zhirenguo.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.DataServer;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;

/**
 * 乐投
 */
public class LeToVoteFragment extends Fragment implements View.OnClickListener{
    private Context context;
    static int num = 0;
    Animation[] alphaAnimations = new Animation[15];
    private ImageView[] alphaView = new ImageView[15];
    private int[] alphaId = new int[]{R.id.img_light01, R.id.img_light02, R.id.img_light03, R.id.img_light04, R.id.img_light05,
            R.id.img_light06, R.id.img_light07, R.id.img_light08, R.id.img_light09, R.id.img_light10, R.id.img_light11, R.id.img_light12,
            R.id.img_light13, R.id.img_light14, R.id.img_light15};
    private TextView mTvTime,mTvTimeU,mTvMoney,mTvPerson;
    private ImageView mImagLV,mImgRank,mImgCoin,mImgInOut;
    private LinearLayout mLayoutTime;
    private View mView;
    private TimeCount time;// 倒计时对象
    private PopupWindow popup;
    private Button mBtnOK,mBtnNO;
    private static String isLeVote = "ok";
    private static boolean isGetguo = false;
    private static boolean isAnimRun = false;
    private Message levotemsg;
    private String levoteUrl,votegoldUrl,mStrVoteID,votegetGoldUrl,getHighestUrl,mHightestdata="";
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            cancelAnimation();
            mImagLV.setClickable(true);
            mImagLV.setImageResource(R.drawable.ic_home_pick);
        }
    };
    private ExecutorService cachedThreadPool;
    private int StartTime,StartPerson,StartMoney,UnitPrice,StartRandom;
    private Bundle mSavedInstanceState;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        levotemsg = new Message();
        cachedThreadPool = Executors.newCachedThreadPool();
        levoteUrl = getResources().getString(R.string.urlheader)+"/investment/getMachine";//获得乐投机器
        votegoldUrl = getResources().getString(R.string.urlheader)+"/investment/setMachineBet";//投币
        votegetGoldUrl = getResources().getString(R.string.urlheader)+"/investment/OpenReward";//采摘
        getHighestUrl = getResources().getString(R.string.urlheader)+"/investment/automaticMachine";//获取最高值
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
        levotemsg.what = 100;
        MainFragment.mainhandler.sendMessage(levotemsg);
        context = getActivity();
        View letovoteview = inflater.inflate(R.layout.fragment_letovote, container, false);
        mView = letovoteview;
        mLefindview();
        setPopup();
        return letovoteview;
    }
    //获取所需控件
    private void mLefindview(){
        mTvTime = (TextView)mView.findViewById(R.id.tv_levote_time);
        mTvTimeU = (TextView)mView.findViewById(R.id.tv_levote_time_unit);
        mTvMoney = (TextView)mView.findViewById(R.id.tv_levote_monery);
        mTvPerson = (TextView)mView.findViewById(R.id.tv_levote_person);
        mImgRank = (ImageView)mView.findViewById(R.id.img_levote_ranklist);
        mImgInOut = (ImageView) mView.findViewById(R.id.img_levote_packge);
        mImgCoin = (ImageView)mView.findViewById(R.id.img_levote_coin);
        changeTvStyle();
        for (int i = 0; i < 15; i++) {
            alphaAnimations[i]  = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha);
            alphaView[i] = (ImageView)mView.findViewById(alphaId[i]);
        }
        mImagLV = (ImageView)mView.findViewById(R.id.img_levote_pop);
        mLayoutTime = (LinearLayout)mView.findViewById(R.id.layout_levote_time);
        mImagLV.setOnClickListener(this);
        mImgRank.setOnClickListener(this);
        mImgInOut.setOnClickListener(this);
    }
    private void startCoinAnimation(final ImageView iv) {
        RotateAnimation animation =new RotateAnimation(0f, 80f, DataServer.dip2px(context, -7), DataServer.dip2px(context, 120));
        animation.setDuration(500);
        animation.setRepeatMode(RotateAnimation.REVERSE);
//        Animation animation = AnimationUtils.loadAnimation(context, R.anim.coin_rotate);
        iv.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.setVisibility(View.GONE);
            }
        });
    }
    private void startAnimation() {
        isAnimRun = true;
        alphaView[num].startAnimation(alphaAnimations[num]);
        bindAnim();

    }
    private void cancelAnimation() {
        isAnimRun = false;
        for (int i = 14; i >= 0; i--) {
            alphaView[i].clearAnimation();
        }

    }
    //跑马灯动画效果
    public void bindAnim(){
        alphaAnimations[num].setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                num++;
                if (num < 15) {
                    alphaView[num].startAnimation(alphaAnimations[num]);
                } else {
                    num = 0;
                    alphaView[0].startAnimation(alphaAnimations[0]);
                }
                if (isAnimRun) {
                    bindAnim();
                } else {
                    cancelAnimation();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    //修改字体样式
    private void changeTvStyle(){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Font2.ttf");
        mTvTime.setTypeface(typeface);
        mTvTimeU.setTypeface(typeface);
        mTvMoney.setTypeface(typeface);
        mTvPerson.setTypeface(typeface);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_levote_pop:
                if (isLeVote.equals("ok")) {
                    //投币
                    voteGoldSubmit();
                    //将PopupWindow显示在指定位置
//                    popup.showAtLocation(getActivity().findViewById(R.id.img_levote_pop), Gravity.CENTER, 0, 0);
                }else if(isLeVote.equals("get")){
                    //采摘
                    voteGetGold();
                }
                break;
            case R.id.img_levote_ranklist://排名列表
                showRankDialog();
                break;
            case R.id.img_levote_packge://果币收支
                showInoutDialog();
                break;
        }
    }
    /**
     * 时间控件
     */
    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次是总时长和计时的时间间隔
        }
        @Override
        public void onFinish() {// 计时完毕时触发
//            if(!isGetguo){
//                Toast.makeText(context,"系统已为你采摘果币，请在邮件查收！",Toast.LENGTH_SHORT).show();
//            }
            //获取后台数据（本轮最高获取者+获取果币）
            getHighestValue();
//            Toast.makeText(context,"本轮王小明获取999果币，不亏为手气之王",Toast.LENGTH_SHORT).show();
            mTvTime.setVisibility(View.VISIBLE);
            mImagLV.setClickable(true);
            mImagLV.setImageResource(R.drawable.ic_home_pay);
            //获取后台数据（下一轮）
            GetLovoteData();
        }
        @Override
        public void onTick(long millisInFuture) {// 计时过程显示
            int curtime = (int) (millisInFuture / 1000);
            if(curtime<=70 && curtime >= 10){
                mTvTime.setText(String.format("%d", curtime-10));
                StartPerson += StartRandom;
                StartMoney += StartPerson*UnitPrice;
                mTvMoney.setText(""+StartMoney);
                mTvPerson.setText(""+StartPerson);
                isLeVote = "ok";
            }else if(curtime<10 && curtime >0){
                if(isAnimRun){
                    isAnimRun = false;
                    handler.sendMessage(handler.obtainMessage());
                }
                mTvTime.setVisibility(View.INVISIBLE);
                isLeVote = "get";

            }
        }
    }
    /*  弹出popupWindow的应用   */
    private void setPopup(){
        //1.加载R.layout.popup对应的页面布局文件
        View root = getActivity().getLayoutInflater().inflate(R.layout.popwaring,null,false);
        //创建PopupWindow对象
        popup = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        popup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置PopupWindow的焦点
        popup.setFocusable(true);
        //为PopupWindow设置动画效果
        popup.setAnimationStyle(R.style.Anim_popup);
        mBtnOK = (Button)root.findViewById(R.id.btn_kuoc_now);
        mBtnNO = (Button)root.findViewById(R.id.btn_kuoc_close);
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FruitBagExtraDialog fruiExtraDialog = new FruitBagExtraDialog();
                fruiExtraDialog.show(getFragmentManager(), "FruitBagExtraDialog");
                popup.dismiss();
            }
        });
        mBtnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        //获取乐投数据
        GetLovoteData();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(time!=null){
            time.cancel();
        }
        cancelAnimation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!cachedThreadPool.isShutdown()){
            cachedThreadPool.shutdownNow();
        }
    }

    Handler firstHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle;
            switch (msg.what){
                case 0:
                    bundle = msg.getData();
                    StartTime = bundle.getInt("time") + 10;
                    StartMoney = bundle.getInt("m_amount");
                    StartPerson = StartMoney/UnitPrice;
                    //1.设置参与人数、投入果币
                    mTvMoney.setText(""+StartMoney);
                    mTvPerson.setText(""+StartPerson);
                    //2.设置跑秒
                    time = new TimeCount(StartTime*1000,1000);// 构造CountDownTimer对象
                    time.start();
                    //3.跑马灯开始
                    startAnimation();
                    break;
                case 1:
                    bundle = msg.getData();
                    if(bundle.getString("event").equals("0")){
                        Message filemsg = new Message();
                        filemsg.what = 201;
                        filemsg.obj = bundle.getString("obj");
                        MainFragment.mainhandler.sendMessage(filemsg);
                        mImagLV.setClickable(false);
                        mImagLV.setImageResource(R.drawable.ic_home_unpick);
                    }else if(bundle.getString("event").equals("103")){
                        mImagLV.setClickable(false);
                        mImagLV.setImageResource(R.drawable.ic_home_unpick);
                    }
                    Toast.makeText(context,""+bundle.getString("msg"),Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    bundle = msg.getData();
                    if(bundle.getString("event").equals("0")){
                        startCoinAnimation(mImgCoin);
                        mImagLV.setClickable(false);
                        mImagLV.setImageResource(R.drawable.ic_home_unpick);
                        isGetguo = true;
                        Toast.makeText(context,"好手气恭喜你获取"+bundle.getString("money")+"果币",Toast.LENGTH_SHORT).show();
                        Message filemsg = new Message();
                        filemsg.what = 202;
                        filemsg.obj = bundle.getString("acountMoney");
                        MainFragment.mainhandler.sendMessage(filemsg);
                    }else if(bundle.getString("event").equals("105")||bundle.getString("event").equals("107")){
                        isGetguo = true;
                        mImagLV.setClickable(false);
                        mImagLV.setImageResource(R.drawable.ic_home_unpick);
                        Toast.makeText(context,""+bundle.getString("msg"),Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "" + bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:
                    bundle = msg.getData();
                    String grab_name = bundle.getString("grab_name");
                    String grab_money = bundle.getString("grab_money");
                    if(!grab_name.equals("")||!grab_money.equals("")){
                        Toast.makeText(context,"本轮乐投:"+grab_name+"获取"+grab_money+"果币，不亏为手气之王!",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 104:
                    GetLovoteData();
                    break;
            }

        }
    };
    //获取乐投数据方法
    private void GetLovoteData(){
        final String params = "params=hah";
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String firstdata = new HttpUtils().PostString(context, levoteUrl, params);
                    Log.i("GetLovoteData", firstdata);
                    JSONObject firstJs = new JSONObject(firstdata);
                    String event = firstJs.getString("event");
                    if (event.equals("0")) {
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        JSONObject Jsdata = firstJs.getJSONObject("objList");
                        bundle.putInt("time", Jsdata.getInt("time"));//剩余秒数
                        UnitPrice = Jsdata.getInt("m_each");//单价
                        bundle.putInt("m_amount", Jsdata.getInt("m_amount"));//总金额
                        mStrVoteID = String.valueOf(Jsdata.getInt("m_id"));//乐投ID
                        StartRandom = Jsdata.getInt("m_random");//随机数
                        msg.setData(bundle);
                        msg.what = 0;
                        firstHandler.sendMessage(msg);
                    }else if(event.equals("104")){
                        GetLovoteData();
                        Log.i("GetLovoteData",firstJs.getString("msg"));
                    }else {
                        Toast.makeText(context, "" + firstJs.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //投币方法
    private void voteGoldSubmit(){
        final String params = "UserId="+ SharePreService.getUserId(context)+"&Mid="+mStrVoteID;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String votedata = null;
                try {
                    votedata = new HttpUtils().PostString(context, votegoldUrl, params);
                    Log.i("voteGoldSubmit","votedata="+votedata);
                    JSONObject voteJs = new JSONObject(votedata);
                    String event = voteJs.getString("event");
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("event", event);
                    bundle.putString("msg",voteJs.getString("msg"));
                    if(event.equals("0")){
                        bundle.putString("obj", voteJs.getString("obj"));
                    }
                    msg.setData(bundle);
                    msg.what = 1;
                    firstHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //采摘方法
    private void voteGetGold(){
        final String params = "UserId="+ SharePreService.getUserId(context)+"&Mid="+mStrVoteID;
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String getgolddata = new HttpUtils().PostString(context,votegetGoldUrl, params);
                    Log.i("voteGoldSubmit","getgolddata="+getgolddata);
                    JSONObject firstJs = new JSONObject(getgolddata);
                    String event = firstJs.getString("event");
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("event",event);
                    bundle.putString("msg",firstJs.getString("msg"));
                    if (event.equals("0")) {
                        JSONObject Jsdata = firstJs.getJSONObject("objList");
                        bundle.putString("money", Jsdata.getString("money"));//采摘的果币
                        bundle.putString("acountMoney", Jsdata.getString("acountMoney"));
                    }
                    msg.setData(bundle);
                    msg.what = 2;
                    firstHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //获取最大值
    private void getHighestValue(){
        cachedThreadPool.execute(new Runnable() {
            String params = "UserId="+SharePreService.getUserId(context)+"&Mid="+mStrVoteID;
            @Override
            public void run() {
                try {
                    mHightestdata = new HttpUtils().PostString(context,getHighestUrl,params);
                    Log.i("getHighestValue",mHightestdata);
                    JSONObject heightest = new JSONObject(mHightestdata);
                    String event = heightest.getString("event");
                    if(event.equals("0")){
                        JSONObject js = heightest.getJSONObject("objList");
                        if(js!=null){
                            JSONObject heightJs = js.getJSONObject("Highest");
                            Message msg = new Message();
                            msg.what = 3;
                            Bundle bundle = new Bundle();
                            bundle.putString("grab_money",heightJs.getString("grab_money"));
                            bundle.putString("grab_name", heightJs.getString("user_nickname"));
                            msg.setData(bundle);
                            firstHandler.sendMessage(msg);
                        }
                    }else{
                        Toast.makeText(context,""+heightest.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
    //排行方法
    private void showRankDialog(){
        if(mSavedInstanceState == null){
            LevoteRankDialog rankDialog = new LevoteRankDialog();
            Bundle bundle = new Bundle();
            bundle.putString("mHightestdata",mHightestdata);
            rankDialog.setArguments(bundle);
            rankDialog.show(getFragmentManager(), "LevoteRankDialog");
        }
    }
    //果币收支
    private void showInoutDialog(){
        if(mSavedInstanceState == null){
            CoinInOutDialog fruitBagDialog = new CoinInOutDialog();
            fruitBagDialog.show(getFragmentManager(), "fruitBagDialog");
        }
    }
}
