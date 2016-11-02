package cn.com.cjland.zhirenguo.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 可以设置禁止滑动的 ViewPager(单向禁止：左滑动)
 * 核心方法：setScrollble()
 */

public class CannotSlidingView extends ViewPager {
    //上一次x坐标
    private float beforeX;
    private boolean isCanScroll = true;
    public CannotSlidingView(Context context) {
        super(context);
    }

    public CannotSlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //-----禁止左滑-------左滑：上一次坐标 > 当前坐标
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(isCanScroll){
            return super.dispatchTouchEvent(ev);
        }else  {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN://按下如果‘仅’作为‘上次坐标’，不妥，因为可能存在左滑，motionValue大于0的情况（来回滑，只要停止坐标在按下坐标的右边，左滑仍然能滑过去）
                    beforeX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float motionValue = ev.getX() - beforeX;
                    if (motionValue > 0) {//禁止右滑
                        return true;
                    }
                    beforeX = ev.getX();//手指移动时，再把当前的坐标作为下一次的‘上次坐标’，解决上述问题
                    break;
                default:
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }
    }
    public boolean isScrollble() {
        return isCanScroll;
    }
/**
 * 设置 是否可以滑动
 * @param isCanScroll
 */
    public void setScrollble(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }
    //右滑就把motionValue判断改成大于0
    //if (motionValue > 0)
}
