package cn.com.cjland.zhirenguo.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.FruitGarMsgAdapter;
import cn.com.cjland.zhirenguo.bean.FruitGarden;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.utils.TimeUtils;


public class FruitGarMsgDialog extends DialogFragment implements View.OnClickListener,Runnable{
    private final static String TAG = "FruitBagDialog";
    private Context mContext;
    private ListView mMsgList;
    private List<FruitGarden> mFruitGardenDatas = new ArrayList<FruitGarden>();
    private String gardenId;
    FruitGarMsgAdapter mFruitGarMsgAdapter;
    FragmentManager mFragmentManager;
    Dialog mDialog;
    private Button btnSubmit;
    /**
     * 处理activity传递过来的值
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            gardenId = getArguments().getString("gardenId");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mFragmentManager = getFragmentManager();
        mDialog = getDialog();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.fruit_garden_msg, container);
        mMsgList = (ListView) view.findViewById(R.id.list_garden_msg);
        mFruitGarMsgAdapter = new FruitGarMsgAdapter(mContext, mFruitGardenDatas, R.layout.fruit_garden_msg_item);
        mMsgList.setAdapter(mFruitGarMsgAdapter);
        new Thread(this).start();
        return view;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mFruitGarMsgAdapter.notifyDataSetChanged();
                    break;
                case 101:
                    FruitGarden fruitGarden1 = new FruitGarden();
                    fruitGarden1.time = "很遗憾";
                    fruitGarden1.content = msg.obj.toString();
                    Log.e(TAG, "fruitGarden1.content=="+fruitGarden1.content );
                    mFruitGardenDatas.add(fruitGarden1);
                    mFruitGarMsgAdapter.notifyDataSetChanged();
                    break;
                default:
                    FruitGarden fruitGarden2 = new FruitGarden();
                    fruitGarden2.time = "很遗憾";
                    fruitGarden2.content = msg.obj.toString();
                    Log.e(TAG, "fruitGarden2.content=="+fruitGarden2.content );
                    mFruitGardenDatas.add(fruitGarden2);
                    mFruitGarMsgAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    public void onClick(View v) {

    }

    @Override
    public void run() {
        try{
            String parmas= "pl_garden=" + gardenId ;
            Log.e(TAG, "parmas=" + parmas);
            String loginResult= HttpUtils.PostString(mContext, getResources().getString(R.string.urlheader) + "/Message/getGardenEvent", parmas);
            Log.e(TAG, "loginResult="+loginResult );
            if (loginResult == null)return;
            JSONObject jsonObject = new JSONObject(loginResult);
            String is_ok = jsonObject.getString("event");
            if (is_ok.equals("0")){
                JSONArray object = jsonObject.getJSONArray("objList");
                for (int i = 0 ;i < object.length() ; i++){
                    JSONObject obj = object.getJSONObject(i);
                    String content =obj.getString("pl_content");
                    String time =obj.getString("pl_addsj");
                    FruitGarden fruitGarden = new FruitGarden();
                    fruitGarden.time = TimeUtils.getDateToStringAll(Long.valueOf(time) * 1000);
                    fruitGarden.content = content;
                    mFruitGardenDatas.add(fruitGarden);
                }
                handler.sendEmptyMessage(0);
            }else{
                Message message = new Message();
                message.what = Integer.valueOf(is_ok);
                message.obj = jsonObject.getString("msg");
                handler.sendMessage(message);
            }
        }catch (MalformedURLException e){
            Log.e(TAG, "e="+e );
        }catch (JSONException e){
            Log.e(TAG, "e="+e );
        }finally {

        }

    }
}
