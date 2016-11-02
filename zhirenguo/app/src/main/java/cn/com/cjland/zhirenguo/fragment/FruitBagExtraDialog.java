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
import android.widget.EditText;
import android.widget.ImageView;

import cn.com.cjland.zhirenguo.R;


public class FruitBagExtraDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private ImageView mExtraClose;
    private View bagExtraView;
    private Button mBtnCreateGarden,mBtnInviteFri;
    private Bundle mSavedInstanceState;
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
        mSavedInstanceState = savedInstanceState;
        mContext = getActivity();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        bagExtraView = inflater.inflate(R.layout.fragment_extra_dialog, container);
        bagExtrafindview();
        return bagExtraView;
    }
    private void bagExtrafindview(){
        mExtraClose = (ImageView) bagExtraView.findViewById(R.id.img_extra_close);
        mBtnCreateGarden = (Button) bagExtraView.findViewById(R.id.btn_new_garden);
        mBtnInviteFri = (Button) bagExtraView.findViewById(R.id.btn_invite_friend);
        mExtraClose.setOnClickListener(this);
        mBtnCreateGarden.setOnClickListener(this);
        mBtnInviteFri.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_extra_close:
                FruitBagDialog fruitBagDialog = new FruitBagDialog();
                fruitBagDialog.show(getFragmentManager(), "FruitBagDialog");
                getDialog().dismiss();
                break;
            case R.id.btn_new_garden:
                showCreateGarden();
                break;
            case R.id.btn_invite_friend:
                showInvistDialog();
                break;
        }
    }
    //邀请
    private void showInvistDialog(){
        if(mSavedInstanceState == null){
            FinviteDialogFragment inviteDialog = new FinviteDialogFragment();
            inviteDialog.show(getFragmentManager(), "FinviteDialogFragment");
            getDialog().dismiss();
        }
    }
    //新建果园
    private void showCreateGarden(){
        CreateFruitGarDialog createGardenDialog = new CreateFruitGarDialog();
        Bundle bundle = new Bundle();
        bundle.putString("sourceType","bagExtra");
        createGardenDialog.setArguments(bundle);
        createGardenDialog.show(getFragmentManager(), "CreateFruitGarDialog");
        getDialog().dismiss();
    }
}
