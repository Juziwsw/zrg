package cn.com.cjland.zhirenguo.acticity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.utils.HttpUtils;


/**
 * Created by Administrator on 2015/12/29.
 */
public class BaseActivity extends Activity{
    // 数据成功返回
    public static final int SUCCESS = 0;
    // 服务器繁忙
    protected static final int UNKONWN_EXCEPTION = 110;
    // 用户ID不存在
    protected static final int DATA_EXCEPTION = 102;
    // 验证码错误
    protected static final int ERR_NO_AUTO = 105;

    // 服务器端无法连接
    protected static final int UNKONWN_SERVER = 110;
    // 手机网络异常
    protected static final int NETWORK_EXCEPTION = 111;
    // 用户不存在
    public static final int ERR_USER_NOTEXIST = 201;
    // 用户被锁定
    public static final  int ERR_USER_LOCKED = 202;
    // 用户密码错误
    public static final  int ERR_PASSWORD = 203;
    // 参数传递错误
    public static final  int ERR_PARAMS = 301;
    // 无记录错误
    public static final  int ERR_NO_RECORD = 302;

    // 其他未知错误
    public static final  int ERR_UNKNOW = 999;
    //昵称不能为空
    public static final  int CANNOT_EMPTY_NIKENAME = 401;
    //验证码不能为空
    public static final  int CANNOT_EMPTY_AUTO = 402;
    //密码不能为空
    public static final  int CANNOT_EMPTY_PASS = 403;
    //密码大于6位小于12位
    public static final  int CANNOT_EMPTY_RULEPASS = 404;

    //请输入手机号
    public static final  int ERR_NO_PHONO = 501;
    //请输入正确手机号
    public static final  int ERR_NO_TUREPHONO = 502;

    public Handler baseHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case SUCCESS:
                    successResponse(msg);
                    break;
                case DATA_EXCEPTION:
                case UNKONWN_EXCEPTION:
                case ERR_NO_AUTO:
                    errorResponse(msg);
                    toastLongINfo(msg.obj.toString());
                    break;
                case NETWORK_EXCEPTION:
                    toastShortINfo(getResources().getString(R.string.toast_open_network));
                    break;
                case ERR_USER_NOTEXIST:
                case ERR_USER_LOCKED:
                case ERR_PASSWORD:
                case ERR_PARAMS:
                case ERR_NO_RECORD:
                case ERR_UNKNOW:
                    //showException(msg);
                    break;
                case ERR_NO_TUREPHONO:
                case ERR_NO_PHONO:
                    errorResponse(msg);
                    toastLongINfo(msg.obj.toString());
                    break;
                case CANNOT_EMPTY_NIKENAME:
                    errorResponse(msg);
                    toastLongINfo(getResources().getString(R.string.toast_cannotempty_nikename));
                    break;
                case CANNOT_EMPTY_AUTO:
                    errorResponse(msg);
                    toastLongINfo(getResources().getString(R.string.toast_auto_cannotempty));
                    break;
                case CANNOT_EMPTY_PASS:
                    errorResponse(msg);
                    toastLongINfo(getResources().getString(R.string.toast_cannotempty_Password));
                    break;
                case CANNOT_EMPTY_RULEPASS:
                    errorResponse(msg);
                    toastLongINfo(getResources().getString(R.string.toast_passrule_error));
                    break;


            }
        }

    };
    /**
     * 成功处理
     */
    protected void successResponse(Message msg) {
    }
    /**
     * 异常处理
     */
    protected void errorResponse(Message msg) {
    }
    /**
     * 隐藏软键盘
     */
    public void hiddenInputMethod() {
        View focusView = this.getCurrentFocus();
        if (focusView != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(focusView.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    /**
     * 点击空白处隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null
                    && getCurrentFocus().getWindowToken() != null) {
                hiddenInputMethod();
            }
        }
        return super.onTouchEvent(event);
    }
    public boolean isNetWork(){
        if (!HttpUtils.isNetworkAvailable(this) && !HttpUtils.isWifiConnected(this)){
            baseHander.sendEmptyMessage(NETWORK_EXCEPTION);
            return  false;
        }
        return true;
    }
    /**
     * 短 Toast
     * @param msg
     */
    public void toastShortINfo(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长 Toast
     * @param msg
     */
    public void toastLongINfo(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
