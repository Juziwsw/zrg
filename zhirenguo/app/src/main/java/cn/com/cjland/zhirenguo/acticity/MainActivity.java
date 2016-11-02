package cn.com.cjland.zhirenguo.acticity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.fragment.FruitFriFragment;
import cn.com.cjland.zhirenguo.fragment.FruitGarFragment;
import cn.com.cjland.zhirenguo.fragment.LeToVoteFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private ImageView mBtnLetovote,mBtnFruitgar,mBtnFruitfir;
    private LeToVoteFragment mFragLeVote;
    private FruitGarFragment mFragFruitG;
    private FruitFriFragment mFragFruitF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainfinview();
        // 设置默认的Fragment即初始化页面
        setDefaultFragment();
    }
    
    /**
     * 默认进入应用的页面
     * 默认：首页
     */
    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mFragFruitG = new FruitGarFragment();
        ChangeTab(1);
        transaction.replace(R.id.id_content, mFragFruitG);
        transaction.commit();
    }
    private void mainfinview(){
        mBtnLetovote = (ImageView)findViewById(R.id.btn_Letovote);
        mBtnFruitgar = (ImageView)findViewById(R.id.btn_fruit_garden);
        mBtnFruitfir = (ImageView)findViewById(R.id.btn_fruit_friend);
        mBtnLetovote.setOnClickListener(this);
        mBtnFruitgar.setOnClickListener(this);
        mBtnFruitfir.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (v.getId()){
            case R.id.btn_Letovote:
                if (mFragLeVote == null)
                {
                    mFragLeVote = new LeToVoteFragment();
                }
                ChangeTab(0);
                transaction.replace(R.id.id_content, mFragLeVote);
                break;
            case R.id.btn_fruit_garden:
                if (mFragFruitG == null)
                {
                    mFragFruitG = new FruitGarFragment();
                }
                ChangeTab(1);
                transaction.replace(R.id.id_content, mFragFruitG);
                break;
            case R.id.btn_fruit_friend:
                if (mFragFruitF == null)
                {
                    mFragFruitF = new FruitFriFragment();
                }
                ChangeTab(2);
                transaction.replace(R.id.id_content, mFragFruitF);
                break;
        }
        transaction.commit();
    }
    /**
     * 变化tab的图片
     * @param i
     */
    private void ChangeTab(int i){
        switch (i){
            case 0:
                mBtnLetovote.setImageResource(R.drawable.ic_home_bottom_btn011);
                mBtnFruitgar.setImageResource(R.drawable.ic_home_bottom_btn02);
                mBtnFruitfir.setImageResource(R.drawable.ic_home_bottom_btn03);
                break;
            case 1:
                mBtnLetovote.setImageResource(R.drawable.ic_home_bottom_btn01);
                mBtnFruitgar.setImageResource(R.drawable.ic_home_bottom_btn022);
                mBtnFruitfir.setImageResource(R.drawable.ic_home_bottom_btn03);
                break;
            case 2:
                mBtnLetovote.setImageResource(R.drawable.ic_home_bottom_btn01);
                mBtnFruitgar.setImageResource(R.drawable.ic_home_bottom_btn02);
                mBtnFruitfir.setImageResource(R.drawable.ic_home_bottom_btn033);
                break;
        }
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
