package cn.com.cjland.zhirenguo.acticity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.utils.DownAddress;

/**
 *
 * @{# SplashActivity.java Create on 2013-5-2 下午9:10:01
 *
 *     class desc: 启动画面 (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 *     (2)是，则进入GuideActivity；否，则进入MainActivity (3)3s后执行(2)操作
 *
 */
public class SplashActivity extends Activity {
    boolean isNotFirstIn = false;
    boolean isLoadStatus = false;
    boolean isAddressDown = false;
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private static final int GO_LOAD = 1002;

    // 延迟1秒
    private static final long SPLASH_DELAY_MILLIS = 2000;
    private SharedPreferences preferences;

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
                case GO_LOAD:
                    goLoad();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //设置全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 读取SharedPreferences中需要的数据
        preferences = getSharedPreferences(
                SumConstants.SHAREDPREFERENCES_NAME, MODE_PRIVATE);
        // 使用SharedPreferences来记录程序的使用次数
        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isNotFirstIn = preferences.getBoolean(SumConstants.ISFIRSTIN, false);
        isLoadStatus = preferences.getBoolean(SumConstants.ISLOADSTATUS, false);
        isAddressDown = preferences.getBoolean(SumConstants.ISADDRESSDOWN,false);//地址信息是否下载，没有下载则开启下载
        if (!isAddressDown){
             DownAddress downAddress = new DownAddress(SplashActivity.this);
             downAddress.getAddress();
        }
        init();
    }

    private void init() {
        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
        if (isNotFirstIn) {
            if(isLoadStatus){// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
                mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
            }else{// 使用Handler的postDelayed方法，3秒后执行跳转到登录页面Loginactivity
                mHandler.sendEmptyMessageDelayed(GO_LOAD, SPLASH_DELAY_MILLIS);
            }
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
        }

    }
    private void goLoad() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
    /**
     * 退出应用
     * @param keyCode
     * @param event
     * @return
     */
    private long mExitTime = 0L;// 控制关闭程序的变量
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, getResources().getString(R.string.toast_quit_again), Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid());
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