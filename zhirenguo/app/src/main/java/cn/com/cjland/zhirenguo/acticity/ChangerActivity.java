package cn.com.cjland.zhirenguo.acticity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.utils.HttpUtils;

/**
 * Created by Administrator on 2015/12/30.
 */
public class ChangerActivity extends BaseActivity{
    private final static String TAG = "ChangerActivity";
    private TextView txtPhoneNumber,mTxtPhono;
    private EditText ediPhoneAuto,ediPassWord;
    private Button btnGetPhoneAuto;
    private String strPassWord,strAuto;
    private TimeCount timeCount;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changer_password);
        mContext = getBaseContext();
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Font1.ttf");
        txtPhoneNumber = (TextView)findViewById(R.id.phone_number);
        mTxtPhono = (TextView)findViewById(R.id.tv_forget_phono);
        txtPhoneNumber.setTypeface(typeface);
        mTxtPhono.setTypeface(typeface);
//        txtPhoneNumber.setText(LoginActivity.phoneNumber);
        ediPassWord = (EditText)findViewById(R.id.editText_password);
        ediPhoneAuto = (EditText)findViewById(R.id.editText_auto);
        btnGetPhoneAuto = (Button) findViewById(R.id.button_verify);
        timeCount = new TimeCount(60000,1000);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_self_back:
                ChangerActivity.this.finish();
                break;
            case R.id.button_verify:
                new GetAuthCode().start();
                break;
            case R.id.button_submit:
                findViewById(R.id.button_submit).setEnabled(false);
                strAuto = ediPhoneAuto.getText().toString().toString();
                strPassWord = ediPassWord.getText().toString().toString();
                new Submit().start();
                break;
        }
    }
    /**
     * 成功处理
     */
    protected void successResponse(Message msg) {
        Toast.makeText(ChangerActivity.this, getResources().getString(R.string.toast_change_succeed), Toast.LENGTH_LONG).show();
        finish();

    }
    /**
     * 异常处理
     */
    protected void errorResponse(Message msg) {
        findViewById(R.id.button_submit).setEnabled(true);
    }

    /**
     * 获取验证码
     */
    private class GetAuthCode extends  Thread{
        @Override
        public void run() {
            super.run();
            if (!isNetWork()) return;
            try {
                timeCount.start();
//                String parmas= "Mobile="+ URLEncoder.encode(LoginActivity.phoneNumber);
//                String loginResult= HttpUtils.CodePostString(mContext, getResources().getString(R.string.urlheader) + "/user/code", parmas);
//                Log.e(TAG, "loginResult=="+loginResult);
            }catch ( Exception e){
            }
        }
    }
    /**
     * 修改密码
     */
    private class Submit extends  Thread{
        @Override
        public void run() {
            super.run();
            try {
                Log.e(TAG, "string_passWord=="+strPassWord);
                Log.e(TAG, "string_auto=="+strAuto);
                if (strAuto == null || strAuto.length() == 0) {
                    baseHander.sendEmptyMessage(CANNOT_EMPTY_AUTO);
                    return;
                }
                if (strPassWord == null || strPassWord.length() == 0) {
                    baseHander.sendEmptyMessage(CANNOT_EMPTY_PASS);
                    return;
                }
                if (strPassWord.length() < 6 || strPassWord.length() > 12){
                    baseHander.sendEmptyMessage(CANNOT_EMPTY_RULEPASS);
                    return;
                }
//                String parmas= "Mobile="+ URLEncoder.encode(LoginActivity.phoneNumber)+"&"+
//                        "PassWord=" + URLEncoder.encode(strPassWord)+"&"+
//                        "Code=" + URLEncoder.encode(strAuto);
//                String loginResult= HttpUtils.EncollPostString(mContext, getResources().getString(R.string.urlheader)+"/user/password", parmas);
//                Log.e(TAG, "loginResult111=="+loginResult);
//                JSONObject jsonObject = new JSONObject(loginResult);
//                String is_ok = jsonObject.getString("event");
//                if (is_ok.equals("0")){
//                    timeCount.cancel();
//                    baseHander.sendEmptyMessage(SUCCESS);
//                    return;
//                }else {
//                    Message message = new Message();
//                    message.what = Integer.valueOf(is_ok);
//                    message.obj = jsonObject.getString("msg");
//                    baseHander.sendMessage(message);
//                    return;
//                }
            }catch ( Exception e){

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
            btnGetPhoneAuto.setText((int)millisUntilFinished/1000 + "S");
            btnGetPhoneAuto.setClickable(false);
        }
        @Override
        public void onFinish() {// 计时完毕时触发
            btnGetPhoneAuto.setText("验证码");
            btnGetPhoneAuto.setClickable(true);
        }
    }
}
