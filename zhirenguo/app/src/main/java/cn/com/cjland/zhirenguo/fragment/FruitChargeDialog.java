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
import android.widget.Button;
import android.widget.ImageView;

import cn.com.cjland.zhirenguo.R;


public class FruitChargeDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private ImageView mChargeClose;
    private Button mBtnCharge;
    /**
     * 处理activity传递过来的值
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.fruit_charge_layout, container);
        mBtnCharge = (Button) view.findViewById(R.id.btn_charge);
        mBtnCharge.setOnClickListener(this);
        mChargeClose = (ImageView) view.findViewById(R.id.iv_charge_close);
        mChargeClose.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_charge:
//                FruitBagExtraDialog fruitBagExtraDialog = new FruitBagExtraDialog();
//                fruitBagExtraDialog.show(getFragmentManager(), "FruitBagExtraDialog");
                getDialog().dismiss();
                break;
            case R.id.iv_charge_close:
                FruitBagDialog fruitBagDialog = new FruitBagDialog();
                fruitBagDialog.show(getFragmentManager(), "FruitBagDialog");
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }
}
