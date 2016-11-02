package cn.com.cjland.zhirenguo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.FruitFriendAdapter;
import cn.com.cjland.zhirenguo.bean.DataServer;
import cn.com.cjland.zhirenguo.bean.FruitFriend;
import cn.com.cjland.zhirenguo.bean.Messagebean;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.views.SwipeMenu;
import cn.com.cjland.zhirenguo.views.SwipeMenuCreator;
import cn.com.cjland.zhirenguo.views.SwipeMenuItem;
import cn.com.cjland.zhirenguo.views.SwipeMenuListView;

public class FruitFriFragment extends Fragment implements View.OnClickListener{
    private Context mContext;
    private RelativeLayout mRelFriendPart;
    private RelativeLayout mRelSearchPart;
    private RelativeLayout mRelBottomPart;
    private TextView mEmptyView;
    private ImageView mFriAdd;
    private ImageView mSearchBack;
    private EditText mSearchText;
    private Button mBtnSearch;
    private SwipeMenuListView mFriendList;
    private int mPosition;
    private ListView mFriendSearchList;
    private List<FruitFriend> mFruitFriendDatas = new ArrayList<FruitFriend>();
    FruitFriendAdapter mFruitFriendAdapter;
    FruitFriendAdapter mSearchFriendAdapter;
    private ImageView mImgGx,mImgInvist;
    private View mRootView;
    private Dialog mDialog;
    private Bundle mSavedInstanceState;
    private Message fruitfirMsg;
    public static int flag = 1;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x58) {
                new PostDataTask().execute(msg.obj.toString());
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fruitfirMsg = new Message();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        flag = 1;
        fruitfirMsg.what = 100;
        MainFragment.mainhandler.sendMessage(fruitfirMsg);
        mSavedInstanceState = savedInstanceState;
        mContext = getActivity();
        final View view = inflater.inflate(R.layout.fruit_friend_layout, container, false);
        mRootView = view;
        firfindview();
        setListMenu();
        new GetDataTask().execute();
        return view;
    }
    private void firfindview(){
        mRelFriendPart = (RelativeLayout)mRootView.findViewById(R.id.rel_friend_part);
        mRelSearchPart = (RelativeLayout)mRootView.findViewById(R.id.rel_search_part);
        mRelBottomPart = (RelativeLayout)mRootView.findViewById(R.id.rel_bottom_part);
        mEmptyView = (TextView)mRootView.findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);
        mFriAdd = (ImageView)mRootView.findViewById(R.id.iv_gf_add);
        mFriAdd.setOnClickListener(this);
        mSearchBack = (ImageView)mRootView.findViewById(R.id.iv_gf_back);
        mSearchBack.setOnClickListener(this);
        mSearchText  = (EditText)mRootView.findViewById(R.id.edt_friend_num);
        mSearchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (checkInput()) {
                        new SearchDataTask().execute();
                    }
                    return true;
                }
                return false;
            }
        });
        mBtnSearch = (Button)mRootView.findViewById(R.id.btn_friend_search);
        mBtnSearch.setOnClickListener(this);
        mFriendList = (SwipeMenuListView) mRootView.findViewById(R.id.list_fruit_friend);
        mFriendList.setVisibility(View.VISIBLE);
        mFriendSearchList = (ListView) mRootView.findViewById(R.id.list_search_fruit_friend);
        mFriendSearchList.setVisibility(View.GONE);
        mImgInvist = (ImageView)mRootView.findViewById(R.id.iv_firend_invite);
        mImgInvist.setOnClickListener(this);
//        mImgGx = (ImageView)mRootView.findViewById(R.id.iv_firend_contribution);
//        mImgGx.setOnClickListener(this);
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
        mFriendList.setMenuCreator(creator);

        // step 2. listener item click event
        mFriendList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                FruitFriend friend = mFruitFriendDatas.get(position);
                switch (index) {
                    case 0:
                        // 左滑 删除列表
                        mPosition = position;
                        new DeteleDataTask().execute(friend.id);
                        break;
                }
                return false;
            }
        });
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_firend_invite:
                showInvistDialog();
            break;
//            case R.id.iv_firend_contribution:
//                showDialog();
//                break;
            case R.id.iv_gf_add:
                flag = 2;
                mFriendList.setVisibility(View.GONE);
                mFriendSearchList.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_search_friend_data));
                mRelFriendPart.setVisibility(View.GONE);
                mRelSearchPart.setVisibility(View.VISIBLE);
//                mRelBottomPart.setVisibility(View.VISIBLE);
//                RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(DataServer.dip2px(mContext,220), DataServer.dip2px(mContext,180));
//                rl.setMargins(DataServer.dip2px(mContext, 80), 0, 0, DataServer.dip2px(mContext, 180));
//                rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                mFriendList.setLayoutParams(rl);
//                mFriendList.setAdapter(null);
                mFriendSearchList.setAdapter(null);
                break;
            case R.id.iv_gf_back:
                flag = 1;
                mFriendList.setVisibility(View.VISIBLE);
                mFriendSearchList.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_friend_data));
                mRelFriendPart.setVisibility(View.VISIBLE);
                mRelSearchPart.setVisibility(View.GONE);
//                mRelBottomPart.setVisibility(View.VISIBLE);
//                RelativeLayout.LayoutParams rlt = new RelativeLayout.LayoutParams(DataServer.dip2px(mContext,220), DataServer.dip2px(mContext,210));
//                rlt.setMargins(DataServer.dip2px(mContext, 80), 0, 0, DataServer.dip2px(mContext, 180));
//                rlt.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//                mFriendList.setLayoutParams(rlt);
                clickItem();
                break;
            case R.id.btn_friend_search:
                if (checkInput()) {
                    new SearchDataTask().execute();
                }
                break;
        }
    }
    private void clickItem() {
        if (null != mFruitFriendAdapter) {
            mFriendList.setAdapter(mFruitFriendAdapter);
            mFruitFriendAdapter.notifyDataSetChanged();
            mFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FruitFriendCenterDialog fruitFriendCenterDialog = new FruitFriendCenterDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("friendId", mFruitFriendDatas.get(position).id);
                    fruitFriendCenterDialog.setArguments(bundle);
                    fruitFriendCenterDialog.show(getFragmentManager(), "FruitFriendCenterDialog");
                }
            });
        }
    }

    //邀请
    private void showInvistDialog(){
        if(mSavedInstanceState == null){
            FinviteDialogFragment inviteDialog = new FinviteDialogFragment();
            inviteDialog.show(getFragmentManager(), "FinviteDialogFragment");
        }
    }

    //点击贡献弹出框
    private void showDialog(){
        //消息按钮监听事件
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.pop_firend_gx, null );
        //对话框
        mDialog = new AlertDialog.Builder(getActivity()).create();
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        lp.x = 0; // 新位置X坐标
        lp.y = 230; // 新位置Y坐标
        dialogWindow.setAttributes(lp);
        mDialog.show();
        mDialog.getWindow().setContentView(layout);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                mFruitFriendDatas = DataServer.getFruitFriends(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (null == mFruitFriendDatas) {
                System.out.println("数据为空");
                mFriendList.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_friend_data));
                return;
            }
            MainFragment.mainhandler.sendMessage(MainFragment.mainhandler.obtainMessage(0x205, String.valueOf(mFruitFriendDatas.size())));
            mFruitFriendAdapter = new FruitFriendAdapter(mContext, mFruitFriendDatas, R.layout.fruit_friend_item);
            mFriendList.setAdapter(mFruitFriendAdapter);
            clickItem();
            super.onPostExecute(result);
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
                mFriendSearchList.setVisibility(View.GONE);
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
                mFriendSearchList.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_search_friend_data));
                return;
            } else {
                mFriendSearchList.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
            mSearchFriendAdapter = new FruitFriendAdapter(mContext, handler, friendData, R.layout.fruit_friend_item);
            mFriendSearchList.setAdapter(mSearchFriendAdapter);
            mFriendSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
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
        String url = mContext.getResources().getString(R.string.urlheader) + "/friend/setPush";
        String postParams = "UserId=" + SharePreService.getUserId(mContext)+"&pl_type=0"+"&FriendId=";
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
    private class DeteleDataTask extends AsyncTask<String, Void, String> {
        String result;
        String url = mContext.getResources().getString(R.string.urlheader) + "/user/deleteUserFriend";
        String postParams = "UserId=" + SharePreService.getUserId(mContext)+"&FriendId=";
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
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                if (event.equals("0")) {
                    mFruitFriendDatas.remove(mPosition);
                    mFruitFriendAdapter.notifyDataSetChanged();
                    MainFragment.mainhandler.sendMessage(MainFragment.mainhandler.obtainMessage(0x205, String.valueOf(mFruitFriendDatas.size())));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
}
