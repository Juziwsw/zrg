package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.FruitGardenAdapter;
import cn.com.cjland.zhirenguo.adapter.RankingAdapter;
import cn.com.cjland.zhirenguo.bean.FruitGarden;
import cn.com.cjland.zhirenguo.bean.RankingBean;


public class LevoteRankDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private View mRandingView;
    private ImageView mImgRankClose;
    private RankingAdapter rankAdapter;
    private ListView mListRank;
    private List<RankingBean> insectList = new ArrayList<RankingBean>();
    private String mHeightRank;
    private TextView mTvNomassge;
    /**
     * 处理activity传递过来的值
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            mHeightRank = bundle.getString("mHightestdata");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        mRandingView = inflater.inflate(R.layout.fragment_ranking_dialog, container);
        getfindview();
        data();
        return mRandingView;
    }
    //获取所需控件
    private void getfindview(){
        mImgRankClose = (ImageView)mRandingView.findViewById(R.id.img_rank_colse);
        mListRank = (ListView)mRandingView.findViewById(R.id.list_ranking);
        mTvNomassge = (TextView)mRandingView.findViewById(R.id.tv_rank_nonews);
        mImgRankClose.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_rank_colse:
                getDialog().dismiss();
                break;
        }
    }
    //获取排行数据
    private void data(){
        RankingBean rankbean;
        if(!mHeightRank.equals("")){
            mTvNomassge.setVisibility(View.GONE);
            mListRank.setVisibility(View.VISIBLE);
            try {
                JSONObject js = new JSONObject(mHeightRank);
                String event = js.getString("event");
                if(event.equals("0")){
                    JSONObject obj = js.getJSONObject("objList");
                    if(obj!=null){
                        JSONArray harray = obj.getJSONArray("TopTen");
                        for (int i =0;i<harray.length();i++){
                            JSONObject objone = harray.getJSONObject(i);
                            rankbean = new RankingBean();
                            rankbean.rankHeaderUrl = objone.getString("user_favicon");
                            rankbean.rankName = objone.getString("user_nickname");
                            rankbean.oraderNo = "1";
                            rankbean.rankTime = objone.getString("md_grab_sj");
                            rankbean.rankMoney = objone.getString("grab_money");
                            insectList.add(rankbean);
                        }
                        rankAdapter = new RankingAdapter(mContext,insectList);
                        mListRank.setAdapter(rankAdapter);
                    }
                }else{
                    Toast.makeText(mContext,""+js.getString("msg"),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            mListRank.setVisibility(View.GONE);
            mTvNomassge.setVisibility(View.VISIBLE);
        }

    }
}
