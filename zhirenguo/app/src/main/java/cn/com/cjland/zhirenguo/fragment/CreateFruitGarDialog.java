package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.utils.HttpUtils;


public class CreateFruitGarDialog extends DialogFragment implements View.OnClickListener{
    private Context context;
    private EditText mGardenName;
    private EditText mGardenNote;
    private ImageView mImgClose;
    private Button mBtnSubmit;
    private String gardenName,gardenContext;
    private String mSourceType;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    Toast.makeText(context, context.getResources().getString(R.string.toast_newgarden_congratulation), Toast.LENGTH_SHORT).show();
                    if(mSourceType.equals("gardenList")){
                        Message jumpMsg = new Message();
                        Bundle bundle = new Bundle();
                        jumpMsg.what = 10;
                        bundle.putString("gardenName", gardenName);
                        bundle.putInt("gardenId", msg.arg1);
                        bundle.putInt("user_level", msg.arg2);
                        jumpMsg.setData(bundle);
                        FruitGarFragment.JumpHandler.sendMessage(jumpMsg);
                        Intent intent = new Intent(SumConstants.INTENTACTION);
                        intent.putExtra("index", 2);
                        context.sendBroadcast(intent);
                    }
                    break;
                case 110:
                    Toast.makeText(context, context.getResources().getString(R.string.toast_servicer_error), Toast.LENGTH_SHORT).show();
                    break;
                case 105:
                    getDialog().dismiss();
                    Toast.makeText(context, context.getResources().getString(R.string.toast_submit_succeed), Toast.LENGTH_SHORT).show();
                    break;
                case 111:
                    Toast.makeText(context, context.getResources().getString(R.string.toast_open_network), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    /**
     * 处理activity传递过来的值
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            mSourceType = bundle.getString("sourceType");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.create_fruit_garden, container);
        mGardenName = (EditText) view.findViewById(R.id.edt_garden_name);
        mGardenNote = (EditText) view.findViewById(R.id.edt_garden_note);
        mBtnSubmit = (Button) view.findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);
        mImgClose = (ImageView) view.findViewById(R.id.iv_close);
        mImgClose.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                String name = mGardenName.getText().toString();
                String note = mGardenNote.getText().toString();
                if(name.equals("")){
                    Toast.makeText(getActivity(), context.getResources().getString(R.string.toast_earnest_write), Toast.LENGTH_SHORT).show();
                    mGardenName.requestFocus();
                }else{
                    new CreateGarden(name,note).start();
                    getDialog().dismiss();
                }
                break;
            case R.id.iv_close:
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }
    /**
     * 新建果园
     */
    private class CreateGarden extends Thread {

        public CreateGarden (String name,String context){
            gardenName = name;
            gardenContext = context;
        }
        @Override
        public void run() {
            super.run();
            try {
                String parmas= "user_id="+ SharePreService.getShaedPrerence(context, SumConstants.USERID)+
                        "&zg_name="+gardenName+
                        "&zg_content="+gardenContext;
                Log.e("wu", "parmas=" + parmas);
                String loginResult= HttpUtils.PostString(context, getResources().getString(R.string.urlheader) + "/Garden/buildGarden", parmas);
                Log.e("wu", "loginResult=" + loginResult);
                handleCreate(loginResult);
            } catch (Exception e) {
            }
        }
    }
    private void handleCreate(String loginResult){
        if (loginResult == null) return;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(loginResult);
            String is_ok = jsonObject.getString("event");
            Log.e("wu", "is_ok=" + is_ok);
            if (is_ok.equals("0")) {
                JSONObject obj = jsonObject.getJSONObject("obj");
                Message msg = new Message();
                msg.what = 200;
                msg.arg1 = obj.getInt("zg_id");
                msg.arg2 = Integer.parseInt(obj.getString("user_level"));
                handler.sendMessage(msg);
            } else if (is_ok.equals("110")) {
                handler.sendEmptyMessage(110);
            } else if (is_ok.equals("105")) {
                handler.sendEmptyMessage(105);
            }
            getDialog().dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
