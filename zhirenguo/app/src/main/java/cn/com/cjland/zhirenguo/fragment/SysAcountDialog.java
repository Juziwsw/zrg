package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.RankingAdapter;
import cn.com.cjland.zhirenguo.adapter.SysAcountAdapter;
import cn.com.cjland.zhirenguo.bean.RankingBean;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.utils.TimeUtils;


public class SysAcountDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private View mSysAcountView;
    private ImageView mImgSysClose,mImgSysBack;
    private SysAcountAdapter acountAdapter;
    private ListView mListSysacount;
    private List<RankingBean> aountList = new ArrayList<RankingBean>();
    private ItemCallBack callBack;
    private String mUrlSysAcount,mUrlSysExtract;
    private TextView mTvNonews;
    private ExecutorService cachedThreadPool;
    private Button mBtnExtract;
    private int mMoney;
    /**
     * 处理activity传递过来的值
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cachedThreadPool = Executors.newCachedThreadPool();
        mUrlSysAcount = getResources().getString(R.string.urlheader)+"/garden/getSystemAccount";
        mUrlSysExtract = getResources().getString(R.string.urlheader)+"/garden/getSysAcc";
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        mSysAcountView = inflater.inflate(R.layout.fragment_sysacount_dialog, container);
        callBack = new ItemCallBack();
        getfindview();
        getSysAdata();//获取系统账户数据
        return mSysAcountView;
    }
    //获取所需控件
    private void getfindview(){
        mImgSysClose = (ImageView)mSysAcountView.findViewById(R.id.img_sysacount_colse);
        mImgSysBack = (ImageView)mSysAcountView.findViewById(R.id.img_sysacount_back);
        mListSysacount = (ListView)mSysAcountView.findViewById(R.id.list_ranking);
        mTvNonews = (TextView)mSysAcountView.findViewById(R.id.tv_sysacount_no);
        mImgSysClose.setOnClickListener(this);
        mImgSysBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_sysacount_colse:
                getDialog().dismiss();
                break;
            case R.id.img_sysacount_back:
                FruitBagDialog bagDialog = new FruitBagDialog();
                bagDialog.show(getFragmentManager(), "FruitBagDialog");
                getDialog().dismiss();
                break;
        }
    }
    //获取系统账户数据
    private void getSysAdata(){
        final String params = "user_id="+ SharePreService.getUserId(mContext);
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String sysData = null;
                try {
                    sysData = new HttpUtils().PostString(mContext, mUrlSysAcount, params);
                    handlerData(sysData);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void handlerData(String data){
        RankingBean rankbean;
        try {
            JSONObject sysJs = new JSONObject(data);
            String event = sysJs.getString("event");
            if(event.equals("0")){
                JSONArray array = sysJs.getJSONArray("objList");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    rankbean = new RankingBean();
                    rankbean.rankId = obj.getString("sa_id");
                    rankbean.rankMoney = obj.getString("sa_num");
                    rankbean.rankStatus = obj.getString("sa_zt");
                    String time = obj.getString("sa_expir_sj");
                    if(!time.equals("")){
                        Long sysTime = Long.parseLong(time);
                        rankbean.rankTime = TimeUtils.getDateToStringAll(sysTime * 1000);
                    }
                    aountList.add(rankbean);
                }
                Message msg = new Message();
                msg.what = 0x123;
                mSysHandler.sendMessage(msg);
            }else if(event.equals("101")){
                Message msg = new Message();
                msg.what = 101;
                mSysHandler.sendMessage(msg);
            }else{
                Message msg = new Message();
                msg.what = 100;
                msg.obj = sysJs.getString("msg");
                mSysHandler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    Handler mSysHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle;
            switch (msg.what){
                case 0x123:
                    mTvNonews.setVisibility(View.GONE);
                    mListSysacount.setVisibility(View.VISIBLE);
                    acountAdapter = new SysAcountAdapter(mContext,aountList,callBack);
                    mListSysacount.setAdapter(acountAdapter);
                    break;
                case 101:
                    mListSysacount.setVisibility(View.GONE);
                    mTvNonews.setVisibility(View.VISIBLE);
                    break;
                case 100:
                    String msgwa = (String) msg.obj;
                    Toast.makeText(mContext,msgwa,Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    bundle = msg.getData();
                    String event = bundle.getString("event");
                    if(event.equals("0")){
                        mBtnExtract.setBackgroundResource(R.color.transparent);
                        mBtnExtract.setText("已提取");
                        Message sysmsg = new Message();
                        sysmsg.what = 203;
                        sysmsg.obj = mMoney;
                        MainFragment.mainhandler.sendMessage(sysmsg);
                    }else{
                        Toast.makeText(mContext,""+bundle.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    // 回调
    public interface ListItemCallBack {
        public void ListItemListener(View view,String aountId,String money);
    }
    public class ItemCallBack implements ListItemCallBack {
        public void ListItemListener(View view,String aountId,String money) {
            mBtnExtract = (Button) view;
            mMoney = Integer.parseInt(money);
            final String params = "user_id="+ SharePreService.getUserId(mContext)+"&sa_id="+aountId;
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    String sysData = null;
                    try {
                        sysData = new HttpUtils().PostString(mContext, mUrlSysExtract, params);
                        ExtractData(sysData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private void ExtractData(String data){
        try {
            JSONObject extract = new JSONObject(data);
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("event",extract.getString("event"));
            bundle.putString("msg",extract.getString("msg"));
            msg.setData(bundle);
            msg.what = 200;
            mSysHandler.sendMessage(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
