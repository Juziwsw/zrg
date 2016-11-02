package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.MessageAdapter;
import cn.com.cjland.zhirenguo.bean.Messagebean;
import cn.com.cjland.zhirenguo.views.SwipeMenu;
import cn.com.cjland.zhirenguo.views.SwipeMenuCreator;
import cn.com.cjland.zhirenguo.views.SwipeMenuItem;
import cn.com.cjland.zhirenguo.views.SwipeMenuListView;

/**
 * Created by Administrator on 2015/12/28.
 */

public class UserTermsDialogFragment extends DialogFragment implements View.OnClickListener{
    private Context context;
    private TextView mTitle,mTv01,mTv02,mTv03,mTv04;
    private Button mBtnGoon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        View view = inflater.inflate(R.layout.fragment_userterms_dialog, container);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Font1.ttf");
        mTitle = (TextView)view.findViewById(R.id.txt_user_terms_title);
        mTv01 = (TextView)view.findViewById(R.id.txt_user_terms01);
        mTv02 = (TextView)view.findViewById(R.id.txt_user_terms02);
        mTv03 = (TextView)view.findViewById(R.id.txt_user_terms03);
        mTv04 = (TextView)view.findViewById(R.id.txt_user_terms04);
        mBtnGoon = (Button)view.findViewById(R.id.btn_terms_goon);
        mBtnGoon.setOnClickListener(this);
        mTitle.setTypeface(typeface);
        mTv01.setTypeface(typeface);
        mTv02.setTypeface(typeface);
        mTv03.setTypeface(typeface);
        mTv04.setTypeface(typeface);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_terms_goon){
            getDialog().dismiss();
        }
    }

}

