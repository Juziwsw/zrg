package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import cn.com.cjland.zhirenguo.R;

/**
 * Created by Administrator on 2016/1/11.
 */
public class MedalWallDialog extends DialogFragment{
    private Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View signView = inflater.inflate(R.layout.medal_wall_dialog, container);
      /*  new GetUserInfo().start();
        mSelfView = signView;
        //获取用户基本信息
        infifindview();*/
        return signView;
    }
}
