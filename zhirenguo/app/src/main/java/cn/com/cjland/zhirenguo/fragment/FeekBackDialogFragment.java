package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.FeedbackAdapter;
import cn.com.cjland.zhirenguo.bean.ChatMsgEntity;
import cn.com.cjland.zhirenguo.bean.MultiEditText;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.utils.TimeUtils;

public class FeekBackDialogFragment extends DialogFragment implements View.OnClickListener{
    private Context context;
    private View feekbackView;
    private ImageView mImgClose;
    private ImageView mImgface1,mImgface2;
    private int FaceStatus = 0;
    private FeedbackAdapter mAdapter;
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
    private ListView mListChat;
    private String mUrlFeed,mParamsFeed,mUrlSub,mSubparams,mUserId;
    private MultiEditText mEditFeed;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        feekbackView = inflater.inflate(R.layout.fragment_feekback_dialog, container);
        feekbakfindview();
        mUrlFeed = getResources().getString(R.string.urlheader)+"/friend/getfeedbackview";
        mUrlSub = getResources().getString(R.string.urlheader)+"/friend/feedbackadd";
        mUserId = SharePreService.getUserId(context);
        //获取初始值
        new NewsAsycnTask().execute(mUrlFeed);
        return feekbackView;
    }
    private void feekbakfindview(){
        mImgClose = (ImageView)feekbackView.findViewById(R.id.img_feekback_close);
        mImgClose.setOnClickListener(this);
        mListChat = (ListView)feekbackView.findViewById(R.id.list_feedback_chat);
        mAdapter = new FeedbackAdapter(context);
        mListChat.setAdapter(mAdapter);
        mImgface1 = (ImageView)feekbackView.findViewById(R.id.img_feedback_img01);//笑脸
        mImgface2 = (ImageView)feekbackView.findViewById(R.id.img_feedback_img02);//哭脸
        mImgface1.setOnClickListener(this);
        mImgface2.setOnClickListener(this);
        mEditFeed = (MultiEditText)feekbackView.findViewById(R.id.edit_feedback);
        mEditFeed.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mEditFeed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //消息发送
                    sendmSG();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_feekback_close:
                getDialog().dismiss();
            break;
            case R.id.img_feedback_img01://笑脸
                FaceStatus = 1;
                mImgface1.setImageResource(R.drawable.ic_idea_word_look01);
                mImgface2.setImageResource(R.drawable.ic_idea_look_default02);
                break;
            case R.id.img_feedback_img02://哭脸
                FaceStatus = 2;
                mImgface1.setImageResource(R.drawable.ic_idea_look_default01);
                mImgface2.setImageResource(R.drawable.ic_idea_word_look02);
                break;
        }
    }

    //消息发送
    private void sendmSG(){
        //判断内容是否为空
        final String content = mEditFeed.getText().toString();
        //判断网络
        Boolean select = FaceStatus == 0?false:true;
        if(!content.equals("") && select){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    mSubparams = "user_id="+mUserId+"&uf_content="+content+"&uf_dagree="+FaceStatus;
                    try {
                        String getdata = new HttpUtils().PostString(context,mUrlSub,mSubparams);
                        JSONObject js = new JSONObject(getdata);
                        if(js.getString("event").equals("0")){
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("event","0");
                            bundle.putString("content",content);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else if(!select){
            Toast.makeText(context,context.getResources().getString(R.string.toast_evaluate_succeed),Toast.LENGTH_LONG).show();
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bu = msg.getData();
            if(bu.getString("event").equals("0")){
                ChatMsgEntity entity01 = new ChatMsgEntity();
                entity01.setDate(getDate());
                entity01.setText(bu.getString("content"));
                entity01.setMsgType(false);
                entity01.setFaceType(FaceStatus);
                mDataArrays.add(0,entity01);
                mAdapter.setList(mDataArrays);
                mAdapter.notifyDataSetChanged();
                mEditFeed.setText("");
            }
        }
    };
    /**
     * 将url对应的JSON格式数据
     * @param url
     * @return
     */
    private List<ChatMsgEntity> getJsonData(String url) {
        mParamsFeed = "user_id="+mUserId;
        String resultdata = null;
        //转化JSON数据
        JSONObject jsonobject;
        try {
            resultdata = new HttpUtils().PostString(context,mUrlFeed, mParamsFeed);
            try {
                jsonobject = new JSONObject(resultdata);
                String event = jsonobject.getString("event");
                if(event.equals("0")){
                    JSONArray jsonarray = jsonobject.getJSONArray("objlist");
                    if (jsonarray.length() > 0) {
                        for(int i=jsonarray.length()-1;i>=0;i--){//倒叙排列
                            JSONObject obj = jsonarray.getJSONObject(i);
                            ChatMsgEntity entity = new ChatMsgEntity();
                            entity.setText(obj.getString("uf_content"));//内容
                            Long date =Long.parseLong(obj.getString("uf_sj"));
                            String strdate = TimeUtils.getDateToStringAll(date * 1000L);
                            entity.setDate(strdate);//时间
                            entity.setFaceType( Integer.parseInt(obj.getString("uf_dagree")));//重新定义
                            String rel = obj.getString("uf_lx");
                            //判断信息类型
                            if(rel.equals("1")){//用户自己的数据
                                entity.setMsgType(false);
                            }else{//后台返回的数据
                                entity.setMsgType(true);
                            }
                            mDataArrays.add(entity);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return mDataArrays;
    }
    /**
     * 实现网络的异步访问
     * @author Zhutt
     */
    class NewsAsycnTask extends AsyncTask<String, Void, List<ChatMsgEntity>> {
        @Override
        protected List<ChatMsgEntity> doInBackground(String... params) {
            return getJsonData(params[0]);
        }
        @Override
        protected void onPostExecute(List<ChatMsgEntity> result) {
            super.onPostExecute(result);
            mAdapter.setList(result);
            mAdapter.notifyDataSetChanged();
        }
    }
    /**
     *  获取当前时间--发送时需要
     */

    private String getDate() {
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));
        String s = String.valueOf(c.get(Calendar.SECOND));

        if(month.length() == 1){
            month = "0"+month;
        }

        if(day.length() == 1){
            day = "0"+day;
        }

        if(mins.length() == 1){
            mins = "0"+mins;
        }

        if(hour.length() == 1){
            hour = "0"+hour;
        }
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
                + mins+":"+s);

        return sbBuffer.toString();
    }
}
