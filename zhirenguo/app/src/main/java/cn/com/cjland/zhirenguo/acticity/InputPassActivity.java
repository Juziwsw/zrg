package cn.com.cjland.zhirenguo.acticity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.utils.HttpUtils;

/**
 * Created by Administrator on 2015/12/29.
 */
public class InputPassActivity extends BaseActivity{
    private final static String TAG = "ChangerActivity";
    private TextView mTvError,mTvForget;
    private EditText mEditPassword;
    private Button mBtnSubmit;
    private String mPass,mPhoneNum,mUrl,mPhoneImei,mPhoneVer;
    private SharedPreferences preferences;
    private TelephonyManager mTm;
    private String userID;
    private Context mContext;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mContext = getBaseContext();
        preferences = getSharedPreferences(
                SumConstants.SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mPhoneImei = mTm.getDeviceId();
        mPhoneVer = android.os.Build.MODEL;
        mPhoneNum = preferences.getString(SumConstants.PHONENUM, null);
        mUrl = getResources().getString(R.string.urlheader)+"/user/login";
        findview();
        mTvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(InputPassActivity.this,ChangerActivity.class);
                startActivity(intent);
            }
        });
    }
    private void findview(){
        mTvError = (TextView)findViewById(R.id.txt_load_error);
        mEditPassword = (EditText)findViewById(R.id.edt_load_password);
        mTvForget = (TextView)findViewById(R.id.txt_load_forget);
        mBtnSubmit = (Button)findViewById(R.id.btn_login_submit);
        imgBack = (ImageView) findViewById(R.id.img_self_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InputPassActivity.this,LoginActivity.class));
                InputPassActivity.this.finish();
            }
        });
        setTextStyle();
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPass = mEditPassword.getText().toString();
                if (HttpUtils.isNetworkAvailable(InputPassActivity.this)) {
                    if (!mPass.equals("") && !mPhoneNum.equals("")) {
                        if (mPass.length() < 6 || mPass.length() > 12){
                            Toast.makeText(InputPassActivity.this, getResources().getString(R.string.toast_passrule_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mBtnSubmit.setEnabled(false);
                        final String params = "PassWord=" + mPass + "&Mobile=" + mPhoneNum;
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    String recode = HttpUtils.PostString(mContext, mUrl, params);
                                    JSONObject jslosd = new JSONObject(recode);
                                    Log.e(TAG, "recode==" + recode);
                                    String event = jslosd.getString("event");
                                    String backmsg = jslosd.getString("msg");
                                    if (event.equals("0")) {
                                        JSONObject dbj = jslosd.getJSONObject("objList");
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString(SumConstants.USERID, dbj.getString("user_id"));
                                        editor.commit();
                                    }
                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("event", event);
                                    bundle.putString("backmsg", backmsg);
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else {
                        Toast.makeText(InputPassActivity.this, getResources().getString(R.string.toast_cannotempty_Password), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(InputPassActivity.this,""+getResources().getString(R.string.txt_network_waring),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            Log.e(TAG,"event="+bundle.getString("event"));
            if(bundle.getString("event").equals("0")){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SumConstants.ISLOADSTATUS,true);
                editor.putString(SumConstants.PHONEIMEI, mPhoneImei);
                editor.putString(SumConstants.PHONEVER,mPhoneVer);
                editor.commit();
                Toast.makeText(InputPassActivity.this, getResources().getString(R.string.toast_enter_succeed), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(InputPassActivity.this,MainActivity.class));
                InputPassActivity.this.finish();
            }else{
                mBtnSubmit.setEnabled(true);
                mTvError.setVisibility(View.VISIBLE);
                mTvError.setText(""+bundle.getString("backmsg"));
            }
        }
    };
    private void setTextStyle(){
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Font1.ttf");
        mTvError.setTypeface(typeface);
        mEditPassword.setTypeface(typeface);
        mTvForget.setTypeface(typeface);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            startActivity(new Intent(InputPassActivity.this,LoginActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
