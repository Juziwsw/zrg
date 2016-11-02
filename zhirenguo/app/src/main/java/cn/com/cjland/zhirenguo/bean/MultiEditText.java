package cn.com.cjland.zhirenguo.bean;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/1/7.
 * 多行
 */
public class MultiEditText extends EditText{


    public MultiEditText(Context context) {
        super(context);
    }

    public MultiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
//这里调用父类方法来初始化必要部分
        InputConnection connection = super.onCreateInputConnection(outAttrs);
        if (connection == null) return null;
//移除EditorInfo.IME_FLAG_NO_ENTER_ACTION标志位
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return connection;
    }
}
