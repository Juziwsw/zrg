package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import cn.com.cjland.zhirenguo.adapter.FruitFriendAdapter;
import cn.com.cjland.zhirenguo.bean.FruitFriend;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;


public class FruitFriendInviteDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private String mGardenId;
    private ImageView mImgClose;
    private EditText mSearchText;
    private Button mBtnSearch;
    FruitFriendAdapter mSearchFriendAdapter;
    private ListView mFriendList;
    private TextView mEmptyView;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x58) {
                new PostDataTask().execute(msg.obj.toString());
            }
        }
    };
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
        if (null != getArguments()) {
            mGardenId = getArguments().getString("gardenId");
        }
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.fruit_friend_invite, container);
        mSearchText = (EditText) view.findViewById(R.id.edt_friend_name);
        mBtnSearch = (Button) view.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(this);
        mImgClose = (ImageView) view.findViewById(R.id.iv_search_colse);
        mImgClose.setOnClickListener(this);
        mFriendList = (ListView) view.findViewById(R.id.list_fruit_friend_search);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                if(checkInput()){
                    new SearchDataTask().execute();
                }
                break;
            case R.id.iv_search_colse:
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }
    private class SearchDataTask extends AsyncTask<Void, Void, String> {
        String result;
        String url = mContext.getResources().getString(R.string.urlheader) + "/friend/findFriend";
        String postParams = "Content=" + mSearchText.getText().toString().trim();
        @Override
        protected String doInBackground(Void... params) {
            try {
                result = HttpUtils.PostString(mContext, url, postParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (null == result || result.equals("")) {
                System.out.println("数据为空");
                mFriendList.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_search_friend_data));
                return;
            }
            List<FruitFriend> friendData = new ArrayList<FruitFriend>();
            FruitFriend fruitFriend = null;
            JSONObject data = null;
            try {
                data = new JSONObject(result);
                JSONArray arr = new JSONArray(data.getString("objList"));
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject temp = (JSONObject) arr.get(i);
                    fruitFriend = new FruitFriend();
                    fruitFriend.imgurl = temp.getString("user_favicon");
                    fruitFriend.id = temp.getString("user_id");
                    fruitFriend.title = temp.getString("user_nickname");
                    fruitFriend.summary = temp.getString("user_signature");
                    friendData.add(fruitFriend);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (0 == friendData.size()) {
                mFriendList.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_search_friend_data));
                return;
            } else {
                mFriendList.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
            mSearchFriendAdapter = new FruitFriendAdapter(mContext, handler, friendData, R.layout.fruit_friend_data_item);
            mFriendList.setAdapter(mSearchFriendAdapter);
            super.onPostExecute(result);
        }
    }
    public boolean checkInput() {
        if (null == mSearchText || mSearchText.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_cannotempty_input), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private class PostDataTask extends AsyncTask<String, Void, String> {
        String result;
        String url = mContext.getResources().getString(R.string.urlheader) + "/Friend/addGardenuser";
        String postParams = "user_id=" + SharePreService.getUserId(mContext)+"&garden_id=" + mGardenId + "&friend_id=";
        @Override
        protected String doInBackground(String... params) {
            // Simulates a background job.
            try {
                postParams = postParams + params[0];
                result = HttpUtils.PostString(mContext, url, postParams);
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
                Toast.makeText(mContext, "已发送成功！", Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
}
