package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.DataServer;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.utils.TimeUtils;


public class FruitGarCenterDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private ImageView mImgClose;
    private ImageView mImgSetting;
    Button mBtnCoinEncash;
    //果园名称，果园简介，果园我的乐透，果园名片，果园园主，果园创建时间，果园成员数
    private TextView txtFGName,txtFGContent,txtFGCoin,txtFGCard,txtFGOwner,txtFGCTime,txtFGFNumber;
    private String FruitGarCenterId;
    /**
     * 处理activity传递过来的值
     */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            FruitGarCenterId = getArguments().getString("gardenId");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.fruit_garden_center, container);
        txtFGName = (TextView) view.findViewById(R.id.fruitGarCenterName);
        txtFGContent = (TextView) view.findViewById(R.id.fruitGarCenterContent);
        txtFGCoin = (TextView) view.findViewById(R.id.fav_coin);
        txtFGCard = (TextView) view.findViewById(R.id.fruitCard);
        txtFGOwner = (TextView) view.findViewById(R.id.fruitGarCenterOwner);
        txtFGCTime = (TextView) view.findViewById(R.id.createTime);
        txtFGFNumber = (TextView) view.findViewById(R.id.fruitFriNumber);
        mBtnCoinEncash = (Button) view.findViewById(R.id.btn_coin_encash);
        mBtnCoinEncash.setOnClickListener(this);
        mImgSetting = (ImageView) view.findViewById(R.id.iv_garden_setting);
        mImgSetting.setOnClickListener(this);
        mImgClose = (ImageView) view.findViewById(R.id.img_self_colse);
        mImgClose.setOnClickListener(this);
        new GetDataTask().execute();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_coin_encash:
                FruitFetchDialog fruitFetchDialog = new FruitFetchDialog();
                fruitFetchDialog.show(getFragmentManager(), "FruitFetchDialog");
                getDialog().dismiss();
                break;
            case R.id.img_self_colse:
                getDialog().dismiss();
                break;
            case R.id.iv_garden_setting:
                ModifyFruitGarDialog modifyFruitGarDialog = new ModifyFruitGarDialog();
                Bundle bundle = new Bundle();
                bundle.putString("gardenId", FruitGarCenterId);
                bundle.putString("gardenName", txtFGName.getText().toString());
                bundle.putString("gardenDes", txtFGContent.getText().toString());
                modifyFruitGarDialog.setArguments(bundle);
                modifyFruitGarDialog.show(getFragmentManager(), "ModifyFruitGarDialog");
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }
    private class GetDataTask extends AsyncTask<Void, Void, String> {
        String resultdata = null;
        String url = getResources().getString(R.string.urlheader)+"/Garden/getMyGarden";
        String param = "user_id=" + SharePreService.getUserId(mContext) + "&zg_id=" +FruitGarCenterId ;
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
            Log.e("wu", "result==" + result);
            try {
                JSONObject dataUser = new JSONObject(result);
                if (dataUser.getString("event").equals("0")) {
                    JSONObject data = dataUser.getJSONObject("objList");
                    String imgUrl= data.getString("user_favicon");
                    txtFGName.setText(data.getString("zg_name"));
                    txtFGContent.setText(data.getString("zg_content"));
                    txtFGCoin.setText(data.getString("user_account"));
                    txtFGCard.setText(data.getString("user_name"));
                    txtFGOwner.setText(data.getString("zg_username"));
                    txtFGCTime.setText(TimeUtils.getDateToString2(1000*Long.valueOf(data.getString("zg_addsj"))));
                    txtFGFNumber.setText(data.getString("count"));
                    JSONArray jarHeadPic = data.getJSONArray("faviconlist");
                    for (int i = 0; i<jarHeadPic.length();i++){
                        JSONObject jsonObject = jarHeadPic.getJSONObject(i);
                        String itemPicStr = jsonObject.getString("user_favicon");
                    }
                    //DataServer.asyncImageLoad(mUserAvatar, imgUrl);
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
