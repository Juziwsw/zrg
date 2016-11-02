package cn.com.cjland.zhirenguo.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.FruitFriendAdapter;
import cn.com.cjland.zhirenguo.bean.FruitFriend;


public class FruitFriendSearchDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private ListView mFriendList;
    private List<FruitFriend> mFruitFriendDatas = new ArrayList<FruitFriend>();
    FruitFriendAdapter mFruitFriendAdapter;
    FragmentManager mFragmentManager;
    Dialog mDialog;
    ImageView mImgBack;
    EditText mEdtSearchValue;
    Button mBtnSearch;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mFragmentManager = getFragmentManager();
        mDialog = getDialog();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        View view = inflater.inflate(R.layout.fruit_friend_search, container);
        mImgBack = (ImageView) view.findViewById(R.id.iv_gf_back);
        mImgBack.setOnClickListener(this);
        mEdtSearchValue = (EditText) view.findViewById(R.id.edt_friend_num);
        mBtnSearch = (Button) view.findViewById(R.id.btn_friend_search);
        mBtnSearch.setOnClickListener(this);
        mFriendList = (ListView) view.findViewById(R.id.list_search_friend);

        mFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FruitFriendCenterDialog fruitFriendCenterDialog = new FruitFriendCenterDialog();
                fruitFriendCenterDialog.show(getFragmentManager(), "FruitFriendCenterDialog");
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_gf_back:
                getDialog().dismiss();
                break;
            case R.id.btn_friend_search:
                showData();
                break;
        }
    }
    public void showData() {
        for (int i = 0; i < 6; i++) {
            FruitFriend fruitFriend = new FruitFriend();
            fruitFriend.imgurl = "http://1.ieasy.sinaapp.com/image/chat_big.png";
            fruitFriend.title = "花千骨";
            fruitFriend.id="20151111";
            fruitFriend.summary = "邀您一起修仙问道";
            mFruitFriendDatas.add(fruitFriend);
        }

        mFruitFriendAdapter = new FruitFriendAdapter(mContext, mFruitFriendDatas, R.layout.fruit_friend_item);
        mFriendList.setAdapter(mFruitFriendAdapter);
    }
}
