package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import cn.com.cjland.zhirenguo.R;

/**
 * Created by Administrator on 2016/1/12.
 */
public class BaseFragment extends DialogFragment{
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
    // 数据成功返回
    public static final int SUCCESS = 0;
    // 服务器繁忙
    protected static final int UNKONWN_EXCEPTION = 101;
    // 用户ID不存在
    protected static final int DATA_EXCEPTION = 102;
    // json结果解析异常
    protected static final int JSON_EXCEPTION = 103;

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

    //昵称不能为空
    public static final  int CANNOT_EMPTY_NIKENAME = 401;

    // 其他未知错误
    public static final  int ERR_UNKNOW = 999;

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
                    toastLongINfo(msg.obj.toString());
                    break;
                case JSON_EXCEPTION:
                case NETWORK_EXCEPTION:
                    toastShortINfo(getResources().getString(R.string.toast_open_network));
                    break;
                case UNKONWN_SERVER:
                case ERR_USER_NOTEXIST:
                case ERR_USER_LOCKED:
                case ERR_PASSWORD:
                case ERR_PARAMS:
                case ERR_NO_RECORD:
                case ERR_UNKNOW:
                    //showException(msg);
                    break;
                case CANNOT_EMPTY_NIKENAME:
                    toastLongINfo(mContext.getResources().getString(R.string.toast_cannotempty_nikename));
                    break;
            }
        }
    };
    /**
     * 成功处理
     */
    protected void successResponse(Message msg) {
        /*toastShortINfo("操作成功!");
        dismissDig(msg);*/
    }
    /**
     * 短 Toast
     * @param msg
     */
    public void toastShortINfo(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长 Toast
     * @param msg
     */
    public void toastLongINfo(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }
}
