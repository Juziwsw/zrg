package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.MessageAdapter;
import cn.com.cjland.zhirenguo.bean.Messagebean;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.views.SwipeMenu;
import cn.com.cjland.zhirenguo.views.SwipeMenuCreator;
import cn.com.cjland.zhirenguo.views.SwipeMenuItem;
import cn.com.cjland.zhirenguo.views.SwipeMenuListView;

/**
 * Created by Administrator on 2015/12/28.
 */

public class MsgDialogFragment extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private SwipeMenuListView mListMsg;
    private TextView mEmptyView;
    private List<Messagebean> messageList = new ArrayList<Messagebean>();
    private String mMsgId,mUserId,mName;
    private int mPosition;
    private MessageAdapter mAdapter;
    private ImageView mImgColse,mImgdesClose;
    private ItemCallBack callBack;
    private RelativeLayout mRelMsgList,mRelMsgDes;
    private TextView mTvDesTitle,mTvDesContent;
    private Typeface typeface;
    private View mView;
    LinearLayout mLinVisitPart;
    Button mBtnIgnore, mBtnAgree;
    String postUrl;
    String postParams;
    int mMsgType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mMsgType = -1;
        postUrl = mContext.getResources().getString(R.string.urlheader) + "/Garden/doGardenMessage";
        postParams = "UserId=" + SharePreService.getUserId(mContext);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.fragment_msg_dialog, container);
        mView = view;
        findview();
        callBack = new ItemCallBack();
        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Font1.ttf");
        setListMenu();
        new GetMsgData().execute();
        return view;
    }
    private void findview(){
        mListMsg = (SwipeMenuListView)mView.findViewById(R.id.list_messages);
        mEmptyView = (TextView)mView.findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);
        mImgColse = (ImageView)mView.findViewById(R.id.img_msg_colse);
        mRelMsgList = (RelativeLayout)mView.findViewById(R.id.layout_msg_list);
        mRelMsgDes = (RelativeLayout)mView.findViewById(R.id.layout_msg_des);
        mImgdesClose = (ImageView)mView.findViewById(R.id.img_msgdes_close);
        mTvDesTitle = (TextView)mView.findViewById(R.id.tv_des_title);
        mTvDesContent = (TextView)mView.findViewById(R.id.tv_msgdes_content);
        mLinVisitPart = (LinearLayout) mView.findViewById(R.id.lin_visit_part);
        mBtnIgnore = (Button) mView.findViewById(R.id.btn_visit_no);
        mBtnAgree = (Button) mView.findViewById(R.id.btn_visit_yes);
        mBtnIgnore.setOnClickListener(this);
        mBtnAgree.setOnClickListener(this);
        mTvDesTitle.setTypeface(typeface);
        mTvDesContent.setTypeface(typeface);
        mImgColse.setOnClickListener(this);
        mImgdesClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_msg_colse:
                getDialog().dismiss();
                break;
            case R.id.img_msgdes_close:
                mRelMsgDes.setVisibility(View.GONE);
                mRelMsgList.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_visit_no:
                mMsgType = -1;
                postParams = postParams + "&PushZt=0";
                new PostDataTask().execute();
                break;
            case R.id.btn_visit_yes:
                postParams = postParams + "&PushZt=1";
                new PostDataTask().execute();
                break;
        }
    }
    //设置listview左滑删除
    private void setListMenu(){
        // step 1. 设置menu
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mContext);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(dp2px(60));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        // 将创建的menu放在listview中
        mListMsg.setMenuCreator(creator);

        // step 2. listener item click event
        mListMsg.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Messagebean bean = messageList.get(position);
                switch (index) {
                    case 0:
                        // 左滑 删除列表
                        mMsgType = -1;
                        mMsgId = bean.msgId;
                        mPosition = position;
//                        messageList.remove(mPosition);
//                        mAdapter.notifyDataSetChanged();
                        postParams = postParams + "&PushId=" + mMsgId;
                        postParams = postParams + "&PushZt=0";
                        new PostDataTask().execute();
                        break;
                }
                return false;
            }
        });
    }
    /**
     * 实现网络的异步访问
     * @author Zhutt
     */
    class GetMsgData extends AsyncTask<Void, Void, List<Messagebean>> {
        @Override
        protected List<Messagebean> doInBackground(Void... params) {
            return getJsonData();
        }
        @Override
        protected void onPostExecute(List<Messagebean> result) {
            super.onPostExecute(result);
            if (null == result|| 0 == result.size()) {
                mEmptyView.setVisibility(View.VISIBLE);
                mListMsg.setVisibility(View.GONE);
            }
            mAdapter = new MessageAdapter(mContext, result, callBack);
            mListMsg.setAdapter(mAdapter);
        }
    }
    /**
     * 将url对应的JSON格式数据转化为我们所封装的NewsBean
     * @param //url
     * @return
     */
    private List<Messagebean> getJsonData() {
        String url = getResources().getString(R.string.urlheader)+"/Message/getMessageList";
        String params = "pl_userid="+SharePreService.getUserId(mContext);
        String resultdata = null;
        messageList.clear();
        //转化JSON数据
        JSONObject jsonobject,jsuser,jsgroup;
        Messagebean msgbean;
        try {
            resultdata = HttpUtils.PostString(mContext, url, params);
            try {
                jsonobject = new JSONObject(resultdata);
                String event = jsonobject.getString("event");
                if(event.equals("0")){
                    JSONArray jsonarray = jsonobject.getJSONArray("objList");
                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonobject = jsonarray.getJSONObject(i);
                        msgbean = new Messagebean();
                        switch (jsonobject.getInt("pl_type")){
                            case 1:
                            case 4:
                                jsgroup = jsonobject.getJSONObject("gardenlist");
                                if(jsgroup != null){
                                    //发起邀请人群消息
                                    msgbean.groupId = jsgroup.getString("zg_id");//群id
                                    msgbean.groupName = jsgroup.getString("zg_name");//群名称
                                }
                            case 0:
                                //发起邀请人个人消息
                                jsuser = jsonobject.getJSONObject("userlist");
                                msgbean.userId = jsuser.getString("user_id");//id
                                msgbean.imgUrl = jsuser.getString("user_favicon");//头像
                                msgbean.userName = jsuser.getString("user_nickname");//名称
                                msgbean.userSignature = jsuser.getString("user_signature");//签名
                            case 2:
                            case 3:
                                msgbean.msgId = jsonobject.getString("pl_id");//消息id
                                msgbean.msgContent = jsonobject.getString("pl_content");//消息内容
                                msgbean.msgTpye = jsonobject.getInt("pl_type");//0是群 1是好友
                                break;
                        }
                        messageList.add(msgbean);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return messageList;
    }
    // 回调
    public interface ListItemCallBack {
        public void ListItemListener(Messagebean msg);
    }
    public class ItemCallBack implements ListItemCallBack {
        public void ListItemListener(Messagebean msg) {
            mRelMsgList.setVisibility(View.GONE);
            mRelMsgDes.setVisibility(View.VISIBLE);
            //设置详细信息页面
            mTvDesContent.setText(msg.msgContent);
            switch (msg.msgTpye) {
                case 1://1被邀请加入果园
                    mTvDesTitle.setText(msg.userName);
                    postParams = "UserId=" + SharePreService.getUserId(mContext);
                    postParams = postParams + "&PushId=" + msg.msgId;
                    mMsgType = 1;
                    break;
                case 0://0被邀请成为果友(处理加好友请求)
                    mTvDesTitle.setText(msg.userName);
                    postParams = "UserId=" + SharePreService.getUserId(mContext);
                    postParams = postParams + "&PushId=" + msg.msgId;
                    mMsgType = 0;
                    break;
                case 4://4邀请果友加入果园(处理果友申请入果园)
                    mTvDesTitle.setText(msg.userName);
                    postParams = "UserId=" + SharePreService.getUserId(mContext);
                    postParams = postParams + "&PushId=" + msg.msgId;
                    mMsgType = 4;
                    break;
                case 2://2乐投获取多少果币
                    mTvDesTitle.setText("系统消息");
                    break;
                case 3://3果袋容量不足
                    mTvDesTitle.setText("系统消息");
                    break;
            }
        }
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    private class PostDataTask extends AsyncTask<Void, Void, String> {
        String result;
        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job.
            try {
                result = HttpUtils.PostString(mContext, postUrl, postParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (null == result || result.equals("")) {
                System.out.println("数据为空");
                return;
            }
            JSONObject data = null;
            try {
                data = new JSONObject(result);
                String event = data.getString("event");
                String msg = data.getString("msg");
                if (event.equals("0")) {
                    mRelMsgDes.setVisibility(View.GONE);
                    mRelMsgList.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(SumConstants.INTENTACTION);
                    switch (mMsgType){
                        case 0:
                            MainFragment.mainhandler.sendMessage(MainFragment.mainhandler.obtainMessage(0x206));
                            break;
                        case 1:
                            intent.putExtra("index", 1);
                            mContext.sendBroadcast(intent);
                            break;
                        case 4:
                            intent.putExtra("index", 4);
                            mContext.sendBroadcast(intent);
                            break;
                    }
                    new GetMsgData().execute();
                }
                //Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
}

