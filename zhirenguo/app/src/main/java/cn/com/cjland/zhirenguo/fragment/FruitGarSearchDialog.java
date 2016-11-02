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
import cn.com.cjland.zhirenguo.adapter.FruitGarDataAdapter;
import cn.com.cjland.zhirenguo.bean.FruitGarden;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;


public class FruitGarSearchDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private ImageView mImgClose;
    private EditText mSearchText;
    private Button mBtnSearch;
    private ListView mSearchList;
    private TextView mEmptyView;
    public FruitGarDataAdapter mFruitGarDataAdapter;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x68) {
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
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.fruit_garden_search, container);
        mSearchText = (EditText) view.findViewById(R.id.edt_garden_name);
        mBtnSearch = (Button) view.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(this);
        mImgClose = (ImageView) view.findViewById(R.id.iv_search_colse);
        mImgClose.setOnClickListener(this);
        mSearchList = (ListView) view.findViewById(R.id.list_fruit_garden_search);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                if (checkInput()) {
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
    public boolean checkInput() {
        if (null == mSearchText || mSearchText.getText().toString().trim().equals("")) {
            Toast.makeText(getActivity(), getResources().getString(R.string.toast_cannotempty_input), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private class SearchDataTask extends AsyncTask<Void, Void, String> {
        String result;
        String url = mContext.getResources().getString(R.string.urlheader) + "/Garden/SearchGarden";
        String postParams = "keyw=" + mSearchText.getText().toString().trim();
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
                mSearchList.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_search_garden_data));
                return;
            }
            List<FruitGarden> gardenData = new ArrayList<FruitGarden>();
            FruitGarden fruitGarden = null;
            JSONObject data = null;
            try {
                data = new JSONObject(result);
                JSONArray arr = new JSONArray(data.getString("objList"));
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject temp = (JSONObject) arr.get(i);
                    fruitGarden = new FruitGarden();
                      fruitGarden.id = temp.getString("zg_id");
                    fruitGarden.title = temp.getString("zg_name");
                    fruitGarden.summary = temp.getString("zg_content");
                    gardenData.add(fruitGarden);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (0 == gardenData.size()) {
                mSearchList.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_search_garden_data));
                return;
            } else {
                mSearchList.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
            mFruitGarDataAdapter = new FruitGarDataAdapter(mContext, handler, gardenData, R.layout.fruit_garden_data_item);
            mSearchList.setAdapter(mFruitGarDataAdapter);
            mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
            super.onPostExecute(result);
        }
    }
    private class PostDataTask extends AsyncTask<String, Void, String> {
        String result;
        String url = mContext.getResources().getString(R.string.urlheader) + "/Garden/JoinGarden";
        String postParams = "user_id=" + SharePreService.getUserId(mContext)+"&zg_id=";
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
