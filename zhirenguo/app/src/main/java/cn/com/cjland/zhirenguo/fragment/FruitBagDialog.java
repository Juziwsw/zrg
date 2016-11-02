package cn.com.cjland.zhirenguo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.views.RoundCornerProgressBar;


public class FruitBagDialog extends BaseFragment implements View.OnClickListener, Runnable{
    private final static String TAG = "FruitBagDialog";
    private Context mContext;
    private ImageView mBagClose;
    private Button mBtnCharge,mBtnEncash,mBtnExtra;
    private TextView mTvSysAcount;
    private View BagView;
    private TextView txtAllMoney ,txtMaxMoney;
    private Map<String,String> maps;
    private RoundCornerProgressBar progressBar;
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
        BagView = inflater.inflate(R.layout.fruit_bag_layout, container);
        bagfindview();
        new  Thread(this).start();
        return BagView;
    }
    //获取所需控件
    private void bagfindview(){
        mBtnExtra = (Button) BagView.findViewById(R.id.btn_extra_bag);
        mTvSysAcount = (TextView)BagView.findViewById(R.id.tv_friend_sysacount);
        mTvSysAcount.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );//设置下划线
        mBagClose = (ImageView) BagView.findViewById(R.id.iv_bag_close);
        mBtnCharge = (Button) BagView.findViewById(R.id.btn_charge);
        mBtnEncash = (Button) BagView.findViewById(R.id.btn_encash);
        txtAllMoney = (TextView) BagView.findViewById(R.id.tv_fruit_money);
        txtMaxMoney = (TextView) BagView.findViewById(R.id.tv_friend_title);
        progressBar = (RoundCornerProgressBar) BagView.findViewById(R.id.progessbar_main_gd);
        mBagClose.setOnClickListener(this);
        mBtnCharge.setOnClickListener(this);
        mBtnExtra.setOnClickListener(this);
        mBtnEncash.setOnClickListener(this);
        mTvSysAcount.setOnClickListener(this);
    }
    /**
     * 成功处理
     */
    protected void successResponse(Message msg) {
        progressBar.setMax(Integer.parseInt(maps.get("ua_bag_space")));
        progressBar.setProgress(Integer.parseInt(maps.get("ua_fc_balance")));
        txtAllMoney.setText(maps.get("ua_fc_balance")+"果币");
        txtMaxMoney.setText(maps.get("ua_bag_space")+"果币");
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_extra_bag:
                FruitBagExtraDialog fruitBagExtraDialog = new FruitBagExtraDialog();
                fruitBagExtraDialog.show(getFragmentManager(), "FruitBagExtraDialog");
                getDialog().dismiss();
                break;
            case R.id.iv_bag_close:
                getDialog().dismiss();
                break;
            case R.id.btn_charge:
                FruitChargeDialog fruitChargeDialog = new FruitChargeDialog();
                fruitChargeDialog.show(getFragmentManager(), "FruitChargeDialog");
                getDialog().dismiss();
                break;
            case R.id.btn_encash:
                FruitEncashDialog fruitEncashDialog = new FruitEncashDialog();
                fruitEncashDialog.show(getFragmentManager(), "FruitEncashDialog");
                getDialog().dismiss();
                break;
            case R.id.tv_friend_sysacount:
                SysAcountDialog sysAcountDialog = new SysAcountDialog();
                sysAcountDialog.show(getFragmentManager(), "SysAcountDialog");
                getDialog().dismiss();
                break;
        }
    }

    @Override
    public void run() {
        try{
            String parmas= "user_id=" + SharePreService.getUserId(mContext);
            //String parmas= "user_id=" + "134";
            Log.e(TAG, "parmas="+parmas );
            String loginResult= HttpUtils.PostString(mContext, getResources().getString(R.string.urlheader) + "/Garden/getFruitBag", parmas);
            Log.e(TAG, "loginResult="+loginResult );
            if (loginResult == null)return;
            JSONObject jsonObject = new JSONObject(loginResult);
            String is_ok = jsonObject.getString("event");
            if (is_ok.equals("0")){
                JSONObject object = jsonObject.getJSONObject("obj");
                maps = new HashMap<String ,String>();
                maps.put("ua_bag_space",object.getString("ua_bag_space"));
                maps.put("ua_fc_balance", object.getString("ua_fc_balance"));
                baseHander.sendEmptyMessage(SUCCESS);
            }else{
                Message message = new Message();
                message.what = Integer.valueOf(is_ok);
                message.obj = jsonObject.getString("msg");
                baseHander.sendMessage(message);
            }
        }catch (MalformedURLException e){
            Log.e(TAG, "e="+e );
        }catch (JSONException e){
            Log.e(TAG, "e="+e );
        }finally {

        }

    }
}
