package cn.com.cjland.zhirenguo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/1/10.
 */
public class NoslidingGridView extends GridView {
    public NoslidingGridView(Context context) {
        super(context);
    }

    public NoslidingGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoslidingGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override

    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {

            return true;  //禁止GridView滑动

        }
        return super.dispatchTouchEvent(ev);

    }
}
