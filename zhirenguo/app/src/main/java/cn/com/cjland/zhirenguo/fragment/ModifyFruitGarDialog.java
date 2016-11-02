package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.utils.HttpUtils;


public class ModifyFruitGarDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private EditText mGardenName;
    private EditText mGardenNote;
    private ImageView mImgClose;
    private Button mBtnSubmit;
    private String gardenName,gardenContext;
    private String FruitGarCenterId;
    private String mStrName,mStrContent;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_modify_garden_success), Toast.LENGTH_SHORT).show();
                    FruitGarCenterDialog fruitGarCenterDialog = new FruitGarCenterDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("gardenId", FruitGarCenterId);
                    fruitGarCenterDialog.setArguments(bundle);
                    fruitGarCenterDialog.show(getFragmentManager(), "FruitGarCenterDialog");
                    getDialog().dismiss();
                    break;
                case 110:
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_servicer_error), Toast.LENGTH_SHORT).show();
                    break;
                case 105:
                    getDialog().dismiss();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_submit_succeed), Toast.LENGTH_SHORT).show();
                    break;
                case 111:
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.toast_open_network), Toast.LENGTH_SHORT).show();
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
            FruitGarCenterId = getArguments().getString("gardenId");
            mStrName = getArguments().getString("gardenName");
            mStrContent = getArguments().getString("gardenDes");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.modify_fruit_garden, container);
        mGardenName = (EditText) view.findViewById(R.id.edt_garden_name);
        if (!mStrName.equals("")) {
            mGardenName.setText(mStrName);
            mGardenName.setSelection(mStrName.length());
        }
        mGardenNote = (EditText) view.findViewById(R.id.edt_garden_note);
        if (!mStrContent.equals("")) {
            mGardenNote.setText(mStrContent);
        }
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
                String name = mGardenName.getText().toString().trim();
                String note = mGardenNote.getText().toString().trim();
                if(name.equals("")){
                    Toast.makeText(getActivity(), "请输入果园名称！", Toast.LENGTH_SHORT).show();
                    mGardenName.requestFocus();
                }else if (mStrName.equals(name)&&mStrContent.equals(note)){
                    FruitGarCenterDialog fruitGarCenterDialog = new FruitGarCenterDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("gardenId", FruitGarCenterId);
                    fruitGarCenterDialog.setArguments(bundle);
                    fruitGarCenterDialog.show(getFragmentManager(), "FruitGarCenterDialog");
                    getDialog().dismiss();
                }else{
                    new CreateGarden(name,note).start();
                }
                break;
            case R.id.iv_close:
                FruitGarCenterDialog fruitGarCenterDialog = new FruitGarCenterDialog();
                Bundle bundle = new Bundle();
                bundle.putString("gardenId", FruitGarCenterId);
                fruitGarCenterDialog.setArguments(bundle);
                fruitGarCenterDialog.show(getFragmentManager(), "FruitGarCenterDialog");
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
                String url = "http://192.168.1.64/zr/index.php/Garden/updateGarden";
                Log.e("wu", "url=" + url);
                String parmas= "user_id="+ SharePreService.getShaedPrerence(mContext, SumConstants.USERID)+
                        "&zg_id="+FruitGarCenterId+
                        "&zg_name="+gardenName+
                        "&zg_content="+gardenContext;
                Log.e("wu", "parmas=" + parmas);
                String loginResult= HttpUtils.PostString(mContext, url, parmas);
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
                handler.sendEmptyMessage(200);
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
