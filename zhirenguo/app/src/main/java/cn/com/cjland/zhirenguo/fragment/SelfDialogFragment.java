package cn.com.cjland.zhirenguo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.acticity.LoginActivity;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.utils.HttpUtils;
import cn.com.cjland.zhirenguo.utils.JsonHelper;


public class SelfDialogFragment extends BaseFragment implements View.OnClickListener{
    private final static String TAG = "SelfDialogFragment";
    private Context context;
    private View mSelfView;
    private ImageView mImgSetting,mImgClose01,mImgBack,mImgClose02,mImgHeader,mImgHeaderChange;
    private TextView txtNikeName,txtPhoneNumber,txtSignature,txtName,txtCard,txtBank,txtBankCode;
    private EditText edtNikeName,edtSignature,edtName,edtCard,edtBank,edtBankCode;
    private LinearLayout mLayoutInfo,mLayoutChange;
    private RadioGroup mSex = null;
    private RadioButton mMale = null;
    private RadioButton mFemale = null;
    private Button btnSumbit,mBtnOutLoad;
    private List<Map<String, String>> listUserInfo;
    private String temp = "男";
    //选择头像
    private Dialog mDialog;
    private Button mBtnphonegraph,mBtnphonealbum;
    /* 用来标识请求gallery的activity */
    private static final int UPLOAD_IMAGE = 1;
    private static final int PHOTO_REQUEST_CUT = 2;

    /* 拍照的照片存储位置 */
    public static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    private static File sdcardTempFile;
    private Bitmap bitmap;
    private String path,headurl;
    private String picPath = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdcardTempFile = new File(Environment.getExternalStorageDirectory() + "/"
                + "zhirenguo","tmp_pic_" + SystemClock.currentThreadTimeMillis() + ".jpg");
        context = getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View signView = inflater.inflate(R.layout.fragment_self_dialog, container);
        new GetUserInfo().start();
        mSelfView = signView;
        //获取用户基本信息
        infifindview();
        return signView;
    }
    private void infifindview(){
        mLayoutInfo = (LinearLayout)mSelfView.findViewById(R.id.layout_self_info);
        mLayoutChange = (LinearLayout)mSelfView.findViewById(R.id.layout_self_change);
        mImgSetting = (ImageView)mSelfView.findViewById(R.id.img_self_setting);
        mImgClose01 = (ImageView)mSelfView.findViewById(R.id.img_self_colse_01);
        mImgClose02 = (ImageView)mSelfView.findViewById(R.id.img_self_colse_02);
        mImgBack = (ImageView)mSelfView.findViewById(R.id.img_self_back);
        mSex = (RadioGroup)mSelfView.findViewById(R.id.self_sex);
        mMale = (RadioButton)mSelfView.findViewById(R.id.self_male);
        mFemale = (RadioButton)mSelfView.findViewById(R.id.self_female);
        btnSumbit = (Button) mSelfView.findViewById(R.id.button_submit);
        mImgHeader = (ImageView)mSelfView.findViewById(R.id.img_self_header);
        mImgHeaderChange = (ImageView)mSelfView.findViewById(R.id.img_self_header_change);
        txtNikeName = (TextView)mSelfView.findViewById(R.id.user_nike);
        txtPhoneNumber = (TextView)mSelfView.findViewById(R.id.user_phone);
        txtSignature = (TextView)mSelfView.findViewById(R.id.user_signature);
        txtName = (TextView)mSelfView.findViewById(R.id.user_name);
        txtCard = (TextView)mSelfView.findViewById(R.id.user_card);
        txtBank = (TextView)mSelfView.findViewById(R.id.open_bank);
        txtBankCode = (TextView)mSelfView.findViewById(R.id.user_bankcode);
        mBtnOutLoad = (Button)mSelfView.findViewById(R.id.btn_userinfo_outload);
        mImgSetting.setOnClickListener(this);
        mImgClose01.setOnClickListener(this);
        mImgClose02.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        btnSumbit.setOnClickListener(this);
        mImgHeaderChange.setOnClickListener(this);
        mBtnOutLoad.setOnClickListener(this);
        mSex.setOnCheckedChangeListener(new OnCheckedChangeListenerImp());
    }
    private void setData(){
        //个人信息界面
        Map<String,String> userInfo = listUserInfo.get(0);
        txtNikeName.setText(userInfo.get("user_nickname"));
        txtPhoneNumber.setText(userInfo.get("user_phone"));
        txtSignature.setText(getNullStr(userInfo.get("user_signature")));
        txtName.setText(getNullStr(getSex(userInfo.get("user_sex")))+"/"+ getNullStr(userInfo.get("user_name")));
        txtCard.setText(getFuzzyStr(userInfo.get("user_card")));
        txtBank.setText(getNullStr(userInfo.get("user_bank")));
        txtBankCode.setText(getFuzzyStr(userInfo.get("user_cnum")));
        headurl = userInfo.get("user_favicon");
        if(!headurl.equals("")){
            mImgHeader.setTag(headurl);
            mImgHeaderChange.setTag(headurl);
            getGroupHeader(mImgHeader, headurl);
            getGroupHeader(mImgHeaderChange,headurl);
        }else {
            mImgHeader.setImageResource(R.drawable.ic_userinfo_header_default);
            mImgHeaderChange.setImageResource(R.drawable.ic_userinfo_header_default);
        }

        //个人信息提交界面
        edtNikeName = (EditText)mSelfView.findViewById(R.id.submit_nikeName);
        edtSignature = (EditText)mSelfView.findViewById(R.id.submit_signature);
        edtName = (EditText)mSelfView.findViewById(R.id.submit_name);
        edtCard = (EditText)mSelfView.findViewById(R.id.submit_card);
        edtBank = (EditText)mSelfView.findViewById(R.id.submit_openBank);
        edtBankCode = (EditText)mSelfView.findViewById(R.id.submit_bankCard);
        edtNikeName.setText(userInfo.get("user_nickname"));
        edtSignature.setText(userInfo.get("user_signature"));
        edtName.setText(userInfo.get("user_name"));
        edtCard.setText(userInfo.get("user_card"));
        edtBank.setText(userInfo.get("user_bank"));
        edtBankCode.setText(userInfo.get("user_cnum"));
        if (userInfo.get("user_sex").equals("0"))mMale.setChecked(true);
        else mFemale.setChecked(true);
    }
    //获取头像
    private void getGroupHeader(ImageView iamgeview,String url){
        ImageLoader imageLoader = ImageLoader.getInstance();
        initImageLoader(context);
        imageLoader.displayImage(url, iamgeview);
    }
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove
                .build();
        ImageLoader.getInstance().init(config);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_self_colse_01:
                getDialog().dismiss();
                break;
            case R.id.img_self_colse_02:
                getDialog().dismiss();
                break;
            case R.id.img_self_setting:
                mLayoutInfo.setVisibility(View.GONE);
                mLayoutChange.setVisibility(View.VISIBLE);
                break;
            case R.id.img_self_back:
                mLayoutInfo.setVisibility(View.VISIBLE);
                mLayoutChange.setVisibility(View.GONE);
                break;
            case R.id.button_submit:
                new UpdataUserInfo().start();
                break;
            case R.id.img_self_header_change:
                Selectphotos();
                break;
            case R.id.btn_phone_ghotograph://拍照
                mImgHeaderChange.setClickable(false);
                doTakePhoto();
                break;
            case R.id.btn_photo_album://选择照片
                mImgHeaderChange.setClickable(false);
                doPickPhotoFromGallery();
                break;
            case R.id.btn_userinfo_outload://退出登录
                SharePreService.clearSomeData(context);
                startActivity(new Intent(context,LoginActivity.class));
                getActivity().finish();
                break;
        }
    }
    private class OnCheckedChangeListenerImp implements RadioGroup.OnCheckedChangeListener {

        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(mMale.getId()==checkedId){
                temp="男";
            }
            else if(mFemale.getId()==checkedId){
                temp="女";
            }
        }
    }
    private String getSex(String sexId){
        String strSex = "";
        switch (sexId){
            case "0":
                strSex = "男";
                break;
            case "男":
                strSex = "0";
                break;
            case "1":
                strSex = "女";
                break;
            case "女":
                strSex = "1";
                break;
        }
        return  strSex;
    }
    /**
     * 获取用户个人信息
     */
    private class GetUserInfo extends  Thread{
        @Override
        public void run() {
            super.run();
            if (!HttpUtils.isNetworkAvailable(context) && !HttpUtils.isWifiConnected(context)){
                handler.sendEmptyMessage(111);
                return ;
            }
            try {
                String parmas= "user_id="+ SharePreService.getUserId(context);
                String loginResult= HttpUtils.PostString(context, getResources().getString(R.string.urlheader)+"/user/userInfo", parmas);
                Log.e(TAG,"loginResultINFO111=="+loginResult);
                JSONObject jsonObject = new JSONObject(loginResult);
                String is_ok = jsonObject.getString("event");
                Message message = new Message();
                if (is_ok.equals("0")){
                    listUserInfo = JsonHelper.getQueryList(jsonObject.getJSONObject("ObjList"));
                    message.what = 0;
                    baseHander.sendMessage(message);
                }else{
                    message.what = Integer.valueOf(is_ok);
                    message.obj = jsonObject.getString("msg");
                    baseHander.sendMessage(message);
                }
            }catch ( Exception e){
                Log.e(TAG, "e: "+e );
            }
        }
    }
    /**
     * 成功处理
     */
    protected void successResponse(Message msg) {
        setData();
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Bundle bundle = msg.getData();
            switch (msg.what) {
                case 0://提交成功
                    Toast.makeText(context, getResources().getString(R.string.toast_submit_succeed), Toast.LENGTH_SHORT).show();
                    break;
                case 100://服务器繁忙
                    Toast.makeText(context, getResources().getString(R.string.toast_servicer_error), Toast.LENGTH_SHORT).show();
                    break;
                case 105:
                    Message filemsg = new Message();
                    filemsg.what = 200;
                    filemsg.obj = picPath;
                    MainFragment.mainhandler.sendMessage(filemsg);
                    Toast.makeText(context, getResources().getString(R.string.toast_submit_succeed), Toast.LENGTH_SHORT).show();
                    break;
                case 106:
                    getDialog().dismiss();
                    Toast.makeText(context, getResources().getString(R.string.toast_again_choicephoto), Toast.LENGTH_SHORT).show();
                    break;
                case 111:
                    Toast.makeText(context, getResources().getString(R.string.toast_open_network), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private class UpdataUserInfo extends  Thread{
        @Override
        public void run() {
            super.run();
            String nikeName = edtNikeName.getText().toString().trim();
            String signature = edtSignature.getText().toString().trim();
            String sex = getSex(temp) ;
            String name = edtName.getText().toString().trim();
            String card = edtCard.getText().toString().trim();
            String bank = edtBank.getText().toString().trim();
            String bankCode = edtBankCode.getText().toString().trim();
            if (nikeName.equals("")){
                baseHander.sendEmptyMessage(CANNOT_EMPTY_NIKENAME);
                return;
            }
            try {
                String parmasurl="user_id="+ SharePreService.getUserId(context) +
                        "&user_nickname="+nikeName+
                        "&user_signature="+signature+
                        "&user_sex="+sex+
                        "&user_name="+name+
                        "&user_card="+card+
                        "&user_bank="+bank+
                        "&user_cnum="+bankCode+
                        "&filename="+"";
                Log.e(TAG, "picPath=="+"787878787");
                String loginResult = HttpUtils.PostString(context, getResources().getString(R.string.urlheader) + "/user/updateInfo", parmasurl);

                Log.e(TAG, "loginResult=="+loginResult);
                if (loginResult == null)return;
                JSONObject jsonObject = new JSONObject(loginResult);
                String is_ok = jsonObject.getString("event");
                if (is_ok.equals("0")){
                    if (picPath.equals("") && picPath.length() == 0){
                        handler.sendEmptyMessage(0);
                    }else{
                        new UpdataUserPic().start();
                    }
                }else{
                    handler.sendEmptyMessage(100);
                }
            }catch ( Exception e){
                Log.e(TAG, "e=="+e);
            }
        }
    }
    private class UpdataUserPic extends  Thread{
        @Override
        public void run() {
            super.run();
            try {
                String parmasurl= getResources().getString(R.string.urlheader)+"/User/uploadA?user_id="+ SharePreService.getUserId(context);
                String loginResult = new HttpUtils().uploadFile(context, parmasurl, picPath);
                Log.e(TAG, "loginResult=="+loginResult);
                if (loginResult == null)return;
                JSONObject jsonObject = new JSONObject(loginResult);
                String is_ok = jsonObject.getString("event");
                if (is_ok.equals("0"))handler.sendEmptyMessage(105);
                else handler.sendEmptyMessage(100);
            }catch ( Exception e){
                Log.e(TAG, "e=="+e);
            }
        }
    }
    private void Selectphotos(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.select_phone_dialog, null );
        layout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.umeng_socialize_shareboard_animation_in));
        mDialog = new AlertDialog.Builder(getActivity()).create();
        Window dialogWindow =mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        mDialog.show();
        mDialog.getWindow().setContentView(layout);
        mBtnphonegraph = (Button)layout.findViewById(R.id.btn_phone_ghotograph);
        mBtnphonegraph.setOnClickListener(this);
        mBtnphonealbum = (Button)layout.findViewById(R.id.btn_photo_album);
        mBtnphonealbum.setOnClickListener(this);
    }
    /**
     * 拍照获取图片
     *
     */
    private void doTakePhoto() {
        try {
            path = Environment.getExternalStorageDirectory() + "/"
                    + "zhirenguo/userheader.jpg";
            final Intent intent = getTakePickIntent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(path)));
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, getResources().getString(R.string.toast_getphoto_error), Toast.LENGTH_LONG).show();
        }
    }

    private static Intent getTakePickIntent(String action) {
        Intent intent = new Intent();
        intent.putExtra("return-data", true);
        intent.setAction(action);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    private void doCropPhoto(Intent data) {
        Uri currImageURI = null;
        if (data != null) {
            if (data.getExtras() != null) {
                File file = getFile(getBitmap(data));
                // 给新照的照片文件命名
                currImageURI = Uri.fromFile(file);
            } else {
                currImageURI = data.getData();
            }
        } else {
            currImageURI = Uri.fromFile(new File(path));
        }

        try {
            // 启动gallery去剪辑这个照片
            final Intent intent = getCropImageIntent(currImageURI);

            startActivityForResult(intent, UPLOAD_IMAGE);
        } catch (Exception e) {
            Toast.makeText(context, getResources().getString(R.string.toast_getphoto_error), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Constructs an intent for image cropping. 调用图片剪辑程序
     */
    private static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        return intent;
    }

    private Bitmap getBitmap(Intent data) {

        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

        return bitmap;
    }

    private File getFile(Bitmap bitmap) {

        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用

            return null;
        }
        String name = new DateFormat().format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".jpg";

        FileOutputStream b = null;

        if (!PHOTO_DIR.isDirectory()) {
            PHOTO_DIR.mkdirs();// 创建文件夹
        }
        File fileName = new File(PHOTO_DIR, name);
        try {
            b = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileName;
    }

    // 请求Gallery程序
    private void doPickPhotoFromGallery() {
        try {
            final Intent intent = getPhotoPickIntent();
            startActivityForResult(intent, PHOTO_REQUEST_CUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, getResources().getString(R.string.toast_getphoto_error), Toast.LENGTH_LONG).show();
        }
    }
    // 封装请求Gallery的intent
    private static Intent getPhotoPickIntent() {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("output", Uri.fromFile(sdcardTempFile));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);// 输出图片大小
        intent.putExtra("outputY", 300);
        return intent;
    }

    /**
     * Save Bitmap to a file.保存图片到SD卡。
     *
     * @param bitmap
     * @return error message if the saving is failed. null if the saving is
     *         successful.
     * @throws IOException
     */
    public static String saveBitmapToFile(Bitmap bitmap) throws IOException {
        BufferedOutputStream os = null;
        String path_file = getFilePath().trim() + "/aaa.png";
        try {
            File file = new File(path_file);
            int end = path_file.lastIndexOf(File.separator);
            String _filePath = path_file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.fillInStackTrace();
                }
            }
        }
        return path_file;
    }

    public static String getFilePath() {
        File path = new File(Environment.getExternalStorageDirectory() + "/"
                + "zhirenguo");
//        File path = Environment.getExternalStorageDirectory();
        return path.toString();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1)
            return;
        switch (requestCode) {
            case UPLOAD_IMAGE:
                if (data != null) {
                    bitmap = data.getParcelableExtra("data");
                    mImgHeaderChange.setImageBitmap(bitmap);
                    mImgHeader.setImageBitmap(bitmap);
                    mDialog.dismiss();
                    Uri url;
                    try {
                        url = Uri.parse(saveBitmapToFile(bitmap));
                        picPath = url.toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CAMERA_WITH_DATA: // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                doCropPhoto(data);
                break;
            case PHOTO_REQUEST_CUT:
                bitmap = BitmapFactory.decodeFile(sdcardTempFile.getAbsolutePath());
                mImgHeaderChange.setImageBitmap(bitmap);
                mImgHeader.setImageBitmap(bitmap);
                mDialog.dismiss();
                Uri url;
                try {
                    url = Uri.parse(saveBitmapToFile(bitmap));
                    picPath = url.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 得到模糊字符串(身份证号和银行卡号)
     * @param oldStr
     * @return
     */
    private String getFuzzyStr(String oldStr){
        if (oldStr.equals("") && oldStr.length() == 0){
            return  "无";
        }else{
            String newStr = oldStr.substring(0,3) + "*** ***" +oldStr.substring(oldStr.length() - 3 ,oldStr.length());
            return newStr;
        }
    }

    /**
     * 对于返回为空的，显示为无
     * @param oldStr
     * @return
     */
    private String getNullStr(String oldStr){
        if (oldStr.equals("") && oldStr.length() == 0) {
            String newStr = "无";
            return  "无";
        }
        return  oldStr;
    }
}
