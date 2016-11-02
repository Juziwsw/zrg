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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.mime.Header;

import java.io.File;

import cn.com.cjland.zhirenguo.R;


public class FruitFetchDialog extends DialogFragment implements View.OnClickListener{
    private Context mContext;
    private ImageView mImgClose;
    private EditText mCoinCount;
    private Button mBtnFetch;
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
        View view = inflater.inflate(R.layout.fruit_fetch_layout, container);
        mCoinCount = (EditText) view.findViewById(R.id.edt_coin_count);
        mBtnFetch = (Button) view.findViewById(R.id.btn_fetch);
        mBtnFetch.setOnClickListener(this);
        mImgClose = (ImageView) view.findViewById(R.id.iv_fetch_close);
        mImgClose.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_fetch:
                if(mCoinCount.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_earnest_write), Toast.LENGTH_SHORT).show();
                }else{
                    FruitFetchSuccessDialog fruitFetchSuccessDialog = new FruitFetchSuccessDialog();
                    fruitFetchSuccessDialog.show(getFragmentManager(), "FruitFetchSuccessDialog");
                    getDialog().dismiss();
                }
                break;
            case R.id.iv_fetch_close:
                getDialog().dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * @param path 要上传的文件路径
     * @param url  服务端接收URL
     * @throws Exception
     */
    public void uploadFile(String path, String url) throws Exception {
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("uploadfile", file);
            // 上传文件
            final RequestHandle post = client.post(url, params, new AsyncHttpResponseHandler() {


                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_LONG).show();

                }


                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(mContext, "上传成功", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onRetry(int retryNo) {
                    // TODO Auto-generated method stub
                    super.onRetry(retryNo);
                    // 返回重试次数
                }

            });
        } else {
            Toast.makeText(mContext, "文件不存在", Toast.LENGTH_LONG).show();
        }

    }
}
