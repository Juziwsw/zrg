package cn.com.cjland.zhirenguo.acticity;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.fragment.UserTermsDialogFragment;
import cn.com.cjland.zhirenguo.utils.HttpUtils;

/**
 * Created by Administrator on 2015/12/29.
 * 注册界面
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "RegisterActivity";
    private EditText editText_passWord,editText_auto, editText_invite;//密码，昵称，验证码,邀请码
    private String string_passWord, string_auto, string_invite;
    private Button button_verify, button_register;
    private TextView txt_encoll_waring;
    private Bundle mSavedInstanceState;
    private TimeCount timeCount;
    private Context mContext;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        mContext = getBaseContext();
        setContentView(R.layout.activity_register);
        timeCount = new TimeCount(60000, 1000);
        findview();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    button_register.setClickable(true);
                    button_register.setBackgroundResource(R.drawable.ic_register_btn);
                }else{
                    button_register.setClickable(false);
                    button_register.setBackgroundResource(R.drawable.no_register_btn);
                }
            }
        });
    }
    /**
     * 成功处理
     */
    protected void successResponse(Message msg) {
        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.toast_register_succeed), Toast.LENGTH_LONG).show();
        finish();
    }

    private void findview() {
        checkBox = (CheckBox) findViewById(R.id.checkbox_encoll_waring);
        button_register = (Button) findViewById(R.id.button_register);
        editText_auto = (EditText) findViewById(R.id.editText_auto);
        editText_passWord = (EditText) findViewById(R.id.editText_password);
        editText_invite = (EditText) findViewById(R.id.editText_invite_code);
        button_verify = (Button) findViewById(R.id.button_verify);
        txt_encoll_waring = (TextView) findViewById(R.id.txt_encoll_waring);
        txt_encoll_waring.setOnClickListener(this);
        setTextTyle();
    }

    private void setTextTyle() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Font1.ttf");
//        editText_auto.setTypeface(typeface);
//        editText_passWord.setTypeface(typeface);
//        button_verify.setTypeface(typeface);
//        txt_encoll_waring.setTypeface(typeface);
//        editText_invite.setTypeface(typeface);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_verify://获取验证码
                new GetAuthCode().start();
                break;
            case R.id.img_self_back://返回
                this.finish();
                break;
            case R.id.txt_encoll_waring://用户注册协议
                if (mSavedInstanceState == null) {
                    UserTermsDialogFragment editNameDialog = new UserTermsDialogFragment();
                    editNameDialog.show(getFragmentManager(), "UserTermsDialogFragment");
                }
                break;
            case R.id.button_register://注册
                string_passWord = editText_passWord.getText().toString().trim();
                string_auto = editText_auto.getText().toString().trim();
                string_invite = editText_invite.getText().toString().trim();
                button_register.setEnabled(false);
                new Register().start();
                break;
        }
    }
    /**
     * 异常处理
     */
    protected void errorResponse(Message msg) {
        button_register.setEnabled(true);
    }

    /**
     * 注册
     */
    private class Register extends Thread {
        @Override
        public void run() {
            super.run();
            if (string_auto == null || string_auto.length() == 0) {
              baseHander.sendEmptyMessage(CANNOT_EMPTY_AUTO);
                return;
            }
            if (string_passWord == null || string_passWord.length() == 0) {
               baseHander.sendEmptyMessage(CANNOT_EMPTY_PASS);
                return;
            }
            if (string_passWord.length() < 6 || string_passWord.length() > 12){
                baseHander.sendEmptyMessage(CANNOT_EMPTY_RULEPASS);
            }
            try {
                Log.e(TAG, "string_passWord=="+string_passWord);
                Log.e(TAG, "string_auto=="+string_auto);
//                String parmas= "Mobile="+ URLEncoder.encode(LoginActivity.phoneNumber)+"&"+
//                               "PassWord=" + URLEncoder.encode(string_passWord)+"&"+
//                               "Code=" + URLEncoder.encode(string_auto)+"&"+
//                               "Relation=" +  URLEncoder.encode(string_invite);
//                String loginResult= HttpUtils.EncollPostString(mContext, getResources().getString(R.string.urlheader) + "/user/register", parmas);
//                if (loginResult == null) return;
//                Log.e(TAG, "loginResult111==" + loginResult);
//                JSONObject jsonObject = new JSONObject(loginResult);
//                String is_ok = jsonObject.getString("event");
//                if (is_ok.equals("0")) {
//                    timeCount.cancel();
//                    baseHander.sendEmptyMessage(SUCCESS);
//                    return;
//                } else {
//                    Message message = new Message();
//                    message.what = Integer.valueOf(is_ok);
//                    message.obj = jsonObject.getString("msg");
//                    baseHander.sendMessage(message);
//                    return;
//                }
            } catch (Exception e) {

            }
        }
    }

    private class GetAuthCode extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                timeCount.start();
//                String parmas= "Mobile="+ URLEncoder.encode(LoginActivity.phoneNumber);
//                String loginResult= HttpUtils.CodePostString(mContext, getResources().getString(R.string.urlheader) + "/user/code", parmas);
//                Log.e(TAG, "loginResult==" + loginResult);
//                final JSONObject jsonObject = new JSONObject(loginResult);
//                String is_ok = jsonObject.getString("event");
//                final int code =jsonObject.getInt("code");
//                if (is_ok.equals("0")) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(RegisterActivity.this, String.valueOf(code), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * 验证码点击计时
     */
    private class TimeCount extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次是总时长和计时的时间间隔
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            button_verify.setText((int) millisUntilFinished / 1000 + "S");
            button_verify.setClickable(false);
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            button_verify.setText("验证码");
            button_verify.setClickable(true);
        }
    }
}
