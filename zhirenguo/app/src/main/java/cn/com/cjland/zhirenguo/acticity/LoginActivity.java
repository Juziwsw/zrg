package cn.com.cjland.zhirenguo.acticity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.utils.HttpUtils;

/**
 * Created by Administrator on 2015/12/28.
 */
public class LoginActivity extends Activity {
    private Button mBtnWX;
    private SharedPreferences preferences;
    // 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(SumConstants.DESCRIPTOR);
    //微信
    private String mOpenId,mParams,mUrlSubmit,mPhoneImei,mPhoneVer;
    private int mSanType;
    private Dialog mDialog;
    private TelephonyManager mTm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences(
                SumConstants.SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        mUrlSubmit = getResources().getString(R.string.urlheader)+"/user/TheLogin";
        mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mPhoneImei = mTm.getDeviceId();
        mPhoneVer = android.os.Build.MODEL;
        //---------------------- QQ、微信登录测试初始化 ----------------------
//        mParams = "mSanType=" + mSanType + "&mOpenId=4sdt34tdgr6ytfgh&nike=haha&urlheader=www.baidu.com";

        addWXPlatform();
        mBtnWX = (Button)findViewById(R.id.btn_login_WX);
        mBtnWX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSanType = 1;
                login(SHARE_MEDIA.WEIXIN);
//                Message msg = new Message();
//                msg.what = 0x123;
//                loaginHandler.sendMessage(msg);
            }
        });
    }
    //------------------------- QQ、微信登录测试 初始化 ----------------------
    private void addWXPlatform() {
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this, SumConstants.WXappId, SumConstants.WXappSecret);
        wxHandler.addToSocialSDK();
    }
    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(LoginActivity.this, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                progressDialog();
//                Toast.makeText(LoginActivity.this, "start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                mDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
//                Toast.makeText(LoginActivity.this, "onComplete", Toast.LENGTH_SHORT).show();
                mOpenId = value.getString("uid");
                if (!TextUtils.isEmpty(mOpenId)) {
                    getUserInfo(platform);
                } else {
                    Toast.makeText(LoginActivity.this, "授权失败...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                mDialog.dismiss();
                Toast.makeText(LoginActivity.this, "取消登录", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(SHARE_MEDIA platform) {
        mController.getPlatformInfo(LoginActivity.this, platform, new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (info != null) {
                    mParams = "mSanType=" + mSanType + "&mOpenId=" + mOpenId + "&nike="
                            + info.get("nickname").toString() + "&urlheader=" + info.get("headimgurl").toString();
//                    Toast.makeText(LoginActivity.this, "mParams=" + mParams, Toast.LENGTH_SHORT).show();
                    //将微信获取的数据传给后台
                    Message msg = new Message();
                    msg.what = 0x123;
                    loaginHandler.sendMessage(msg);
                } else {
                    mDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "获取用户信息失败...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //将微信获取的数据传给后台
    private void submitWXData(){
//        Toast.makeText(LoginActivity.this,"+++++++++++++++++",Toast.LENGTH_SHORT).show();
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String result = new HttpUtils().PostString(LoginActivity.this, mUrlSubmit, mParams);
                    Log.i("LoginActivity","result="+result);
                    //处理返回数据
                    JSONObject resultJs = new JSONObject(result);
                    Message msg = new Message();
                    msg.what = 100;
                    Bundle bundle = new Bundle();
                    String event = resultJs.getString("event");
                    bundle.putString("event", event);
                    if (event.equals("0")) {
                        JSONObject dbj = resultJs.getJSONObject("objList");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SumConstants.USERID, dbj.getString("user_id"));
                        editor.putBoolean(SumConstants.ISLOADSTATUS, true);
                        editor.putString(SumConstants.PHONEIMEI, mPhoneImei);
                        editor.putString(SumConstants.PHONEVER, mPhoneVer);
                        editor.commit();
                    }
                    bundle.putString("msg", resultJs.getString("msg"));
                    msg.setData(bundle);
                    loaginHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    //处理后台返回的数据
    Handler loaginHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle;
            switch (msg.what){
                case 0x123:
                    submitWXData();
                    break;
                case 100:
                    bundle = msg.getData();
                    String event = bundle.getString("event");
                    if(event.equals("0")){
                        Toast.makeText(LoginActivity.this,"登录成功!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        LoginActivity.this.finish();
                    }else{
                        mDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+bundle.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    //进度条
    private void progressDialog(){
        //消息按钮监听事件
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialog_custom_progress_bar, null );
        //对话框
        mDialog = new AlertDialog.Builder(LoginActivity.this).create();
        mDialog.show();
        mDialog.getWindow().setContentView(layout);
        mDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
    }



    private long mExitTime = 0L;// 控制关闭程序的变量
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, getResources().getString(R.string.toast_quit_again), Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            }
            return true;
        }
        // 拦截MENU按钮点击事件，让他无任何操作
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
