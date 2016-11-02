package cn.com.cjland.zhirenguo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.CoinInoutAdapter;
import cn.com.cjland.zhirenguo.bean.InOutCoinBean;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.utils.TimeUtils;

/**
 * Created by Administrator on 2016/1/11.
 */
public class CoinInOutDialog extends BaseFragment implements View.OnClickListener,Runnable{
    private final static String TAG = "CoinInOutDialog";
    private Context mContext;
    private View mSysAcountView;
    private ImageView mImgSysClose,mImgSysBack;
    private CoinInoutAdapter acountAdapter;
    private ListView mListSysacount;
    private List<InOutCoinBean> aountList = new ArrayList<InOutCoinBean>();
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
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        mSysAcountView = inflater.inflate(R.layout.coin_income_expenses_dialog, container);
        getfindview();
        acountAdapter = new CoinInoutAdapter(mContext,aountList);
        mListSysacount.setAdapter(acountAdapter);
        new Thread(this).start();
        //data();//模拟数据
        return mSysAcountView;
    }
    /**
     * 成功处理
     */
    protected void successResponse(Message msg) {
        acountAdapter.notifyDataSetChanged();
    }
    //获取所需控件
    private void getfindview(){
        mImgSysClose = (ImageView)mSysAcountView.findViewById(R.id.img_sysacount_colse);
        //mImgSysBack = (ImageView)mSysAcountView.findViewById(R.id.img_sysacount_back);
        mListSysacount = (ListView)mSysAcountView.findViewById(R.id.list_ranking);
        mImgSysClose.setOnClickListener(this);
        //mImgSysBack.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_sysacount_colse:
                getDialog().dismiss();
                break;
        }
    }
    //假数据
    private void data(){
        InOutCoinBean inOutCoinBean;
        for (int i = 0; i < 6; i++) {
            inOutCoinBean = new InOutCoinBean();
            inOutCoinBean.coinName = "大乐透"+i;
          /*  inOutCoinBean.timeYMD = "11.15";
            inOutCoinBean.timeHM = "12:30";*/
            inOutCoinBean.coinNumber = "1200";
            aountList.add(inOutCoinBean);
        }
        acountAdapter = new CoinInoutAdapter(mContext,aountList);
        mListSysacount.setAdapter(acountAdapter);
    }

    @Override
    public void run() {
        try{
            String parmas= "user_id="+ SharePreService.getUserId(mContext);
            String loginResult= HttpUtils.PostString(mContext, getResources().getString(R.string.urlheader) + "/garden/coinlist", parmas);
            Log.e(TAG, "loginResult="+loginResult );
            JSONObject jsonObject = new JSONObject(loginResult);
            String is_Ok = jsonObject.getString("event");
            if (loginResult == null)return;
            if (is_Ok.equals("0")){
                JSONArray array = jsonObject.getJSONArray("objList");
                for (int i = 0;i < array.length();i++){
                    JSONObject object = array.getJSONObject(i);
                    InOutCoinBean inOutCoinBean = new InOutCoinBean();
                    inOutCoinBean.coinName = object.getString("bt");
                    inOutCoinBean.time = TimeUtils.getDateToStringAll(Long.valueOf(object.getString("fc_rq")) * 1000);
                    inOutCoinBean.tyle = object.getString("fc_lx");;
                    inOutCoinBean.coinNumber = object.getString("fc_number");;
                    aountList.add(inOutCoinBean);
                }
                baseHander.sendEmptyMessage(SUCCESS);
            }else{
                Message message = new Message();
                message.what = Integer.valueOf(is_Ok);
                message.obj = jsonObject.getString("msg");
                baseHander.sendMessage(message);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
