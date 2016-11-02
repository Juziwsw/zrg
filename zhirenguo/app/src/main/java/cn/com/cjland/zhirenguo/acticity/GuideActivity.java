package cn.com.cjland.zhirenguo.acticity;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.views.CannotSlidingView;

public class GuideActivity extends Activity implements View.OnClickListener{
    private CannotSlidingView mCanotSlid;
    private SharedPreferences preferences;
    private ImageView mImgDian01,mImgDian02,mImgDian03,mImgDian04,mImgGole01,
            mImgGole02,mImgGole03,mImgGole04;
    private ImageView mImgStart;
    private View view1,view2,view3,view4;
    private TextView mTxt01,mTxt02,mTxt03,mTxt04;
    ObjectAnimator yxBouncer01,yxBouncer02,yxBouncer03,yxBouncer04;
    ViewPager.OnPageChangeListener mPCListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        //设置全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findview();
        setViewPager();
        mCanotSlid.setCurrentItem(0);
        mPCListener.onPageSelected(0);

    }
    private void findview(){
        mCanotSlid = (CannotSlidingView)findViewById(R.id.viewpager_guide);
        mCanotSlid.setScrollble(false);
        mImgDian01 = (ImageView)findViewById(R.id.dian_01);
        mImgDian02 = (ImageView)findViewById(R.id.dian_02);
        mImgDian03 = (ImageView)findViewById(R.id.dian_03);
        mImgDian04 = (ImageView)findViewById(R.id.dian_04);
    }
    //设置ViewPager
    private void setViewPager(){
        // 将要分页显示的View装入数组中
        LayoutInflater mLi = LayoutInflater.from(this);
        view1 = mLi.inflate(R.layout.activity_guide_01, null);
        view2 = mLi.inflate(R.layout.activity_guide_02, null);
        view3 = mLi.inflate(R.layout.activity_guide_03, null);
        view4 = mLi.inflate(R.layout.activity_guide_04, null);
        // 每个页面的view数据
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        //获取所需控件
        mImgStart = (ImageView)view4.findViewById(R.id.img_guide_end);
        mImgGole01 = (ImageView)view1.findViewById(R.id.gold_coin_01);
        mImgGole02 = (ImageView)view2.findViewById(R.id.gold_coin_02);
        mImgGole03 = (ImageView)view3.findViewById(R.id.gold_coin_03);
        mImgGole04 = (ImageView)view4.findViewById(R.id.gold_coin_04);
        mTxt01 = (TextView)view1.findViewById(R.id.tv_coin_01);
        mTxt02 = (TextView)view2.findViewById(R.id.tv_coin_02);
        mTxt03 = (TextView)view3.findViewById(R.id.tv_coin_03);
        mTxt04 = (TextView)view4.findViewById(R.id.tv_coin_04);
        mImgStart.setOnClickListener(this);
        setObjectAnimator();
        // 填充ViewPager的数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(views.get(position));
                Log.i("GuideActivity","position="+position);
                switch (position){
                    case 0:
                        mImgGole01.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mImgGole02.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        mImgGole03.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mImgGole04.setVisibility(View.VISIBLE);
                        break;
                }
                return views.get(position);
            }
        };
        mCanotSlid.setAdapter(mPagerAdapter);//为viewpager配置适配器
        //设置viewpager监听器
         mPCListener =  new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mImgDian01.setImageResource(R.drawable.img01);
                        mImgDian02.setImageResource(R.drawable.img02);
                        mImgDian03.setImageResource(R.drawable.img02);
                        mImgDian04.setImageResource(R.drawable.img02);
                        yxBouncer01.start();
                        break;
                    case 1:
                        mImgDian01.setImageResource(R.drawable.img02);
                        mImgDian02.setImageResource(R.drawable.img01);
                        mImgDian03.setImageResource(R.drawable.img02);
                        mImgDian04.setImageResource(R.drawable.img02);
                        yxBouncer02.start();
                        break;
                    case 2:
                        mImgDian01.setImageResource(R.drawable.img02);
                        mImgDian02.setImageResource(R.drawable.img02);
                        mImgDian03.setImageResource(R.drawable.img01);
                        mImgDian04.setImageResource(R.drawable.img02);
                        yxBouncer03.start();
                        break;
                    case 3:
                        mImgDian01.setImageResource(R.drawable.img02);
                        mImgDian02.setImageResource(R.drawable.img02);
                        mImgDian03.setImageResource(R.drawable.img02);
                        mImgDian04.setImageResource(R.drawable.img01);
                        yxBouncer04.start();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mCanotSlid.setOnPageChangeListener(mPCListener);
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.img_guide_end ){
            preferences = getSharedPreferences(
                    SumConstants.SHAREDPREFERENCES_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstIn",true);
            editor.commit();
            startActivity(new Intent(GuideActivity.this, LoginActivity.class));
            finish();
        }
    }
    //动画效果
    //分300步进行移动动画
    final int count = 300;
    /**
     * 要start 动画的那张图片的ImageView
     * @param imageView
     */
    private void startAnimation(final ImageView imageView,int index) {
    }
    private void setObjectAnimator(){
        Keyframe[] keyframes = new Keyframe[count];
        final float keyStep = 1f / (float) count;
        float key = keyStep;
        for (int i = 0; i < count; ++i) {
            keyframes[i] = Keyframe.ofFloat(key, 0);
            key += keyStep;
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe("translationX", keyframes);
        key = keyStep;
        for (int i = 0; i < count; ++i) {
            keyframes[i] = Keyframe.ofFloat(key, -getY(i + 1));
            key += keyStep;
        }
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("translationY", keyframes);
        yxBouncer01 = ObjectAnimator.ofPropertyValuesHolder(mImgGole01, pvhY, pvhX).setDuration(1500);
        yxBouncer01.setInterpolator(new BounceInterpolator());
        yxBouncer02 = ObjectAnimator.ofPropertyValuesHolder(mImgGole02, pvhY, pvhX).setDuration(1500);
        yxBouncer02.setInterpolator(new BounceInterpolator());
        yxBouncer03 = ObjectAnimator.ofPropertyValuesHolder(mImgGole03, pvhY, pvhX).setDuration(1500);
        yxBouncer03.setInterpolator(new BounceInterpolator());
        yxBouncer04 = ObjectAnimator.ofPropertyValuesHolder(mImgGole04, pvhY, pvhX).setDuration(1500);
        yxBouncer04.setInterpolator(new BounceInterpolator());
        yxBouncer01.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTxt01.setVisibility(View.VISIBLE);
                mImgGole01.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        yxBouncer02.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mTxt02.setVisibility(View.VISIBLE);
                mImgGole02.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        yxBouncer03.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mTxt03.setVisibility(View.VISIBLE);
                mImgGole03.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        yxBouncer04.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mTxt04.setVisibility(View.VISIBLE);
                mImgGole04.setVisibility(View.INVISIBLE);
                mImgStart.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
    final float a = -1f / 75f;
    /**
     * 这里是根据三个坐标点{（0,0），（300,0），（150,300）}计算出来的抛物线方程
     *
     * @param x
     * @return
     */
    private float getY(float x) {
        return a * x * x + 4 * x;
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
