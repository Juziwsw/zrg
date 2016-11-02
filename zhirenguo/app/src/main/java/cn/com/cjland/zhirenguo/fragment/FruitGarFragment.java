package cn.com.cjland.zhirenguo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.adapter.FruitGardenAdapter;
import cn.com.cjland.zhirenguo.adapter.GridviewInsectAdapter;
import cn.com.cjland.zhirenguo.bean.FruitGarden;
import cn.com.cjland.zhirenguo.bean.ImageWithId;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.bean.SumConstants;
import cn.com.cjland.zhirenguo.utils.HttpUtils;

/**
 * 果园
 */
public class FruitGarFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private View mRootView;
    private ListView mGardenList;
    private TextView mEmptyView;
    private List<FruitGarden> mFruitGardenDatas = new ArrayList<FruitGarden>();
    private FruitGardenAdapter mFruitGardenAdapter;
    private FragmentManager mFragmentManager;
    private Button mBtnCreate,mBtnSearch,btnSubmit;
    private ImageView mImgCreate, mImgSearch;
    private GridView mGridInsect;
    private List<ImageWithId> insectList = new ArrayList<ImageWithId>();
    private GridviewInsectAdapter mInsectAdpter;
    private ImageView mImgThings;
    private ImageView mImgGolist;
    private ImageView mImgDNew;
    private ImageView mTmgrefereeOne;
    private ImageView mImgSelfTree;
    private ImageView mImgSelffruit;
    private ImageView mImgSysTree;
    private ImageView mImgSysfruit;
    private static ImageView mImgGet;
    private static RelativeLayout mRelEveryGd;
    private static RelativeLayout mRelGardenL;
    private SharedPreferences preferences;
    private String mEvent = "0";
    private Dialog mDialog01, mDialog02;
    private EditText mGardenName,mGardenNote;
    private TextView mTvFisrtIn;
    private static TextView mTvGardenName;
    private static int plantindex = 1;//种树与采摘 索引 1为种树，2为采摘
    private int current = 0;
    private int index = 1,mPosition;
    public static int mDefaultgId;
    private static int mRecommendedLevel = 0;
    private static RelativeLayout mRelGardenName;
    private ExecutorService cachedThreadPool;
    private String gardenName,mTreeId;
    private String gardenContext,mUrlplant,mUrlGrowth,mUrlGardenInfo,mUrlKillInsect
            ,mUrlDefalutGarden,mUrlInsectNum,mUrlpickFruit;
    private int mInsectNum;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        cachedThreadPool = Executors.newCachedThreadPool();
        preferences = mContext.getSharedPreferences(
                SumConstants.SHAREDPREFERENCES_NAME, mContext.MODE_PRIVATE);
        mUrlplant = getResources().getString(R.string.urlheader) + "/Garden/plantTree";
        mUrlGrowth = getResources().getString(R.string.urlheader)+"/Garden/autoValue";
        mUrlGardenInfo = getResources().getString(R.string.urlheader)+"/Garden/getGardenInfo";
        mUrlKillInsect = getResources().getString(R.string.urlheader)+"/Garden/killWorm";
        mUrlInsectNum = getResources().getString(R.string.urlheader)+"/Garden/wormValue";
        mUrlDefalutGarden = getResources().getString(R.string.urlheader)+"/Garden/getMyGardenInfo";
        mUrlpickFruit = getResources().getString(R.string.urlheader)+"/Garden/pickFruit";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentManager = getFragmentManager();
        mRootView = inflater.inflate(R.layout.fragment_fruit_garden, container, false);
        findview();//获取所需控件
        setinitData();//初始化果园列表
        initGridView();//初始化虫子布局 gridview
        getDefalutGarden();
        return mRootView;
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        insectList.clear();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!cachedThreadPool.isShutdown()){
            cachedThreadPool.shutdownNow();
        }
    }
    //获取所需控件
    private void findview() {
        mGridInsect = (GridView) mRootView.findViewById(R.id.gridview_group_insect); // 虫子
        mImgThings = (ImageView) mRootView.findViewById(R.id.img_garden_things);// 果园事件
        mImgGet = (ImageView) mRootView.findViewById(R.id.img_garden_get);//种果树 采摘
        mImgGolist = (ImageView) mRootView.findViewById(R.id.img_garden_golist);//切换列表
        mRelEveryGd = (RelativeLayout) mRootView.findViewById(R.id.layout_garden_everygd);// 单个果园信息
        mRelGardenL = (RelativeLayout) mRootView.findViewById(R.id.layout_garden_list);//果园列表
        mTmgrefereeOne = (ImageView) mRootView.findViewById(R.id.img_garden_referee_one);//推荐人
        mImgSelfTree = (ImageView) mRootView.findViewById(R.id.img_garden_fruittree);// 自我 果树
        mImgSelffruit = (ImageView) mRootView.findViewById(R.id.img_garden_tree_fruit);//自我 果子
        mImgSysTree = (ImageView) mRootView.findViewById(R.id.img_garden_systemtree);//系统 果树
        mImgSysfruit = (ImageView) mRootView.findViewById(R.id.img_garden_sys_fruit);// 系统 果子
        mTvGardenName = (TextView) mRootView.findViewById(R.id.txt_garden_name);// 果园名称
        mRelGardenName = (RelativeLayout) mRootView.findViewById(R.id.layout_gerden_name);//果园背景
        mGardenList = (ListView) mRootView.findViewById(R.id.list_fruit_garden);
        mEmptyView = (TextView) mRootView.findViewById(R.id.empty_view);
        mEmptyView.setVisibility(View.GONE);
        mImgThings.setOnClickListener(this);
        mImgGet.setOnClickListener(this);
        mImgGolist.setOnClickListener(this);
    }
    //第一次进入应用 第一步 新建果园弹出框
    private void showNewDialog() {
        //消息按钮监听事件
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.pop_garden_new, null);
        mTvFisrtIn = (TextView) layout.findViewById(R.id.txt_new_first_waring);
        mImgDNew = (ImageView) layout.findViewById(R.id.img_garden_new);
        if (index == 1) {
            mImgDNew.setImageResource(R.drawable.ic_garden_create);
            mTvFisrtIn.setText(R.string.txt_garden_Legend_01);
        } else {
            mImgDNew.setImageResource(R.drawable.ic_garden_plant_tree);
            mTvFisrtIn.setText(R.string.txt_garden_Legend_02);
        }
        //对话框
        mDialog01 = new AlertDialog.Builder(getActivity()).create();
        mDialog01.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        mDialog01.setOnKeyListener(keylistener);
        Window dialogWindow = mDialog01.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        mDialog01.show();
        mDialog01.getWindow().setContentView(layout);
        mImgDNew.setOnClickListener(this);
    }
    //第一次进入应用 第二步 新建果园弹出框
    private void showCreateDialog() {
        //消息按钮监听事件
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.create_fruit_garden, null);
        //对话框
        mDialog02 = new AlertDialog.Builder(getActivity()).create();
        mDialog02.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        mDialog02.setOnKeyListener(keylistener);
        Window dialogWindow = mDialog02.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        mDialog02.show();
        mDialog02.getWindow().setContentView(layout);
        mDialog02.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mDialog02.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mGardenName = (EditText) layout.findViewById(R.id.edt_garden_name);
        mGardenNote = (EditText) layout.findViewById(R.id.edt_garden_note);
        btnSubmit = (Button) layout.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }
    //初始化列表数据
    private void setinitData() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout headView = (LinearLayout) inflater.inflate(R.layout.fruit_garden_header, null);
        mGardenList.addHeaderView(headView);
        mBtnCreate = (Button) headView.findViewById(R.id.btn_create_garden);
        mBtnSearch = (Button) headView.findViewById(R.id.btn_search_garden);
        mImgCreate = (ImageView) headView.findViewById(R.id.iv_create_garden);
        mImgSearch = (ImageView) headView.findViewById(R.id.iv_search_garden);
        mBtnCreate.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);
        mImgCreate.setOnClickListener(this);
        mImgSearch.setOnClickListener(this);
        mFruitGardenDatas.clear();
    }
    //初始化gridview
    private void initGridView() {
        ImageWithId insect;
        for (int i = 0; i < SumConstants.INSECTCOUNT; i++) {
            insect = new ImageWithId();
            insect.ishas = false;
            insect.mInsect = 0;
            insectList.add(insect);
        }
        mInsectAdpter = new GridviewInsectAdapter(mContext, insectList, R.layout.fragment_fruit_garden_gridview_item);
        mGridInsect.setAdapter(mInsectAdpter);
        mGridInsect.setOnItemClickListener(new ImplOnItemClickListener());
    }
    //监听点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_garden://创建果园
            case R.id.iv_create_garden://创建果园
                CreateFruitGarDialog mCreateFruitGarDialog = new CreateFruitGarDialog();
                Bundle bundle = new Bundle();
                bundle.putString("sourceType","gardenList");
                mCreateFruitGarDialog.setArguments(bundle);
                mCreateFruitGarDialog.show(getFragmentManager(), "CreateFruitGarDialog");
                break;
            case R.id.btn_search_garden://搜索果园
            case R.id.iv_search_garden://搜索果园
                FruitGarSearchDialog fruitGarSearchDialog = new FruitGarSearchDialog();
                fruitGarSearchDialog.show(getFragmentManager(), "FruitGarSearchDialog");
                break;
            case R.id.img_garden_things://果园事件
                FruitGarMsgDialog fruitGarMsgDialog = new FruitGarMsgDialog();
                Bundle bud = new Bundle();
                bud.putString("gardenId", String.valueOf(mDefaultgId));
                fruitGarMsgDialog.setArguments(bud);
                fruitGarMsgDialog.show(getFragmentManager(), "FruitGarMsgDialog");
                break;
            case R.id.img_garden_golist://列表按钮点击 跳转到列表
                HiddenAllView();
                handlerGrowthValue(false,100,0);
                mRelGardenL.setVisibility(View.VISIBLE);
                mRelEveryGd.setVisibility(View.GONE);
                new GetDataTask().execute();
                break;
            case R.id.img_garden_new:// 第一步 或 第二步弹出框中 建果园/种果树 点击事件
                if (index == 1) {
                    mDialog01.dismiss();
                    showCreateDialog();
                } else {//点击种果树事件
                    plantFruitTree(mDefaultgId);
                }
                break;
            case R.id.btn_submit://第二步创建果园 弹出框 提交按钮
                String name = mGardenName.getText().toString();
                String context = mGardenNote.getText().toString();
                if(name.equals("")){
                    Toast.makeText(getActivity(), "请输入果园名称！", Toast.LENGTH_SHORT).show();
                    mGardenName.requestFocus();
                }else {
                    new CreateGarden(name, context).start();
                }
                break;
            case R.id.img_garden_get:
                if (plantindex == 1) {//种果树
                    //访问后台数据
                    plantindex = 2;
                    mImgGet.setVisibility(View.GONE);
                    //成功时 调用
                    plantFruitTree(mDefaultgId);
                } else {//采摘
                    mImgGet.setClickable(false);
                    pickFruitData();
//                    Toast.makeText(mContext, getResources().getString(R.string.toast_start_pick),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    //进入应用 获取默认果园
    private void getDefalutGarden(){
        final String params = "user_id="+SharePreService.getUserId(mContext);
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String defalutdata = new HttpUtils().PostString(mContext,mUrlDefalutGarden,params);
                    Log.e("wu", "defalutdata== " + defalutdata);
                    JSONObject defalutJs = new JSONObject(defalutdata);
                    String event = defalutJs.getString("event");
                    if (event.equals("0")) {//0没有果树 1有果树
                        plantindex = 2;
                        gardenName = defalutJs.getString("zg_name");
                        mDefaultgId = Integer.parseInt(defalutJs.getString("zg_id"));
                        JSONObject obj = defalutJs.getJSONObject("ObjectList");
                        //果园树的ID
                        mTreeId = obj.getString("t_id");
                        Message msg = new Message();
                        msg.what = 301;
                        Bundle bu = new Bundle();
                        mRecommendedLevel = obj.getInt("user_level");//推荐人
                        bu.putString("growthValue", obj.getString("t_value"));//成长值
                        bu.putInt("treeStatus", obj.getInt("t_status"));//果树状态
                        msg.setData(bu);
                        handler.sendMessage(msg);
                        insectNum();
                    }else if(event.equals("1")){
                        plantindex = 1;
                        gardenName = defalutJs.getString("zg_name");
                        mDefaultgId = Integer.parseInt(defalutJs.getString("zg_id"));
                        Message msg = new Message();
                        msg.what = 10;
                        Bundle bu = new Bundle();
                        bu.putInt("gardenId", mDefaultgId);
                        bu.putString("gardenName", gardenName);
                        msg.setData(bu);
                        JumpHandler.sendMessage(msg);
                    }else if(event.equals("103")){
                        mEvent = event;
                        Message msg = new Message();
                        msg.what = 101;
                        handler.sendMessage(msg);
                    }else{
                        Message msg = new Message();
                        msg.what = 110;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //虫子 gridview的点击事件
    class ImplOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mPosition = position;
            if(insectList.get(position).ishas){
                killInsect();
            }
        }
    }
    //种果树事件
    private void plantFruitTree(int gardenId) {
        //向服务器发送种树消息
        //1 传递的参数
        final String params = "zg_id="+mDefaultgId+"&user_id="+SharePreService.getShaedPrerence(mContext,SumConstants.USERID);
        Log.i("plantFruitTree", "params=" + params);
        //2 连接服务器
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String resultData = new HttpUtils().PostString(mContext, mUrlplant, params);
                    Log.i("plantFruitTree", "resultData=" + resultData);
                    Message msg = new Message();
                    msg.what = 302;
                    msg.obj = resultData;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //种果树获取服务器数据处理
    private void overPlantTree(String data){
        try {
            JSONObject js = new JSONObject(data);
            String event = js.getString("event");
            String msgdata = js.getString("msg");
            if(event.equals("0")){
                if(mEvent.equals("103")){
                    mDialog01.dismiss();
                }
                mImgSysTree.setVisibility(View.VISIBLE);
                mImgSelfTree.setImageResource(R.drawable.ic_darden_tree_01);
                mImgSelfTree.setVisibility(View.VISIBLE);
                plantTreeAnimation();
                // 种完果树 果园事件
                mImgThings.setVisibility(View.VISIBLE);
                // 推荐人
                showRefereeUI();
                //种完果树显示成长值
                handlerGrowthValue(true,0x123,0);
            }else{
                Toast.makeText(mContext,""+msgdata,Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //进入果园详情  果园列表进入果园详情,根据果园ID与用户ID获取果园详情
    private void GoInGardenDes(int gardenId,String gardenname) {
        final int mGardenid = gardenId;
        //1 设置果园名称
        mTvGardenName.setText(gardenName);
        mRelGardenName.setVisibility(View.VISIBLE);
        //2 获取果园信息
        final String params = "user_id="+SharePreService.getShaedPrerence(mContext, SumConstants.USERID)+"&zg_id="+mGardenid;
        Log.i("DefalutInGardenDes","params="+params);
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String gardenInfo = new HttpUtils().PostString(mContext,mUrlGardenInfo,params);
                    Log.i("DefalutInGardenDes", "gardenInfo=" + gardenInfo);
                    HandleGardenInfo(gardenInfo,mGardenid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //处理果园详情获取值
    private void HandleGardenInfo(String gardenInfo,int gardenId){
        try {
            JSONObject gardenJs = new JSONObject(gardenInfo);
            String event = gardenJs.getString("event");//0有果树 1没有果树
            if (event.equals("0")) {
                plantindex = 2;
                JSONObject obj = gardenJs.getJSONObject("ObjectList");
                //果园树的ID
                mTreeId = obj.getString("t_id");
                Message msg = new Message();
                msg.what = 301;
                Bundle bu = new Bundle();
                mRecommendedLevel = obj.getInt("user_level");//推荐人
                bu.putString("growthValue", obj.getString("t_value"));//成长值
                bu.putInt("treeStatus", obj.getInt("t_status"));//果树状态
                msg.setData(bu);
                handler.sendMessage(msg);
                insectNum();
            }else if(event.equals("1")){
                plantindex = 1;
                Message msg = new Message();
                msg.what = 10;
                Bundle bu = new Bundle();
                bu.putInt("gardenId", gardenId);
                bu.putString("gardenName", gardenName);
                msg.setData(bu);
                JumpHandler.sendMessage(msg);
            }else{
                Message msg = new Message();
                msg.what = 110;
                handler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //果园详情 根据数据处理页面效果
    private void GargenInfoUI(Bundle bundle){
        //1 显示成长值
        handlerGrowthValue(true, 0x123, Integer.parseInt(bundle.getString("growthValue")));
        //2 显示果园事件
        mImgThings.setVisibility(View.VISIBLE);
        //3 设置果树状态
        switch (bundle.getInt("treeStatus")){
            //0 种子，1 开小花，2 花盛开，3 结果，4 可摘
            case 0:
                break;
            case 1:
                mImgSelffruit.setImageResource(R.drawable.ic_garden_tree_flour_01);
                mImgSelffruit.setVisibility(View.VISIBLE);
                break;
            case 2:
                mImgSelffruit.setImageResource(R.drawable.ic_garden_tree_flour_02);
                mImgSelffruit.setVisibility(View.VISIBLE);
                break;
            case 3:
                mImgSelffruit.setImageResource(R.drawable.ic_garden_tree_fruit);
                mImgSelffruit.setVisibility(View.VISIBLE);
                break;
            case 4:
                mImgSelffruit.setImageResource(R.drawable.ic_garden_tree_fruit);
                mImgSelffruit.setVisibility(View.VISIBLE);
                mImgGet.setImageResource(R.drawable.ic_garden_get);
                mImgGet.setVisibility(View.VISIBLE);
                break;
        }
        mImgSelfTree.setImageResource(R.drawable.ic_darden_tree_05);
        mImgSelfTree.setVisibility(View.VISIBLE);
        //4 设置推荐人 0未推荐，1一级推荐，2二级推荐
        showRefereeUI();
        mTmgrefereeOne.setVisibility(View.VISIBLE);
        mRelEveryGd.setVisibility(View.VISIBLE);
        //5 系统树状态
        mImgSysTree.setVisibility(View.VISIBLE);
//        mImgSysfruit.setVisibility(View.VISIBLE);
//        mGridInsect.setVisibility(View.VISIBLE);
//        setinsectData(5);
    }
    //推荐人设置
    private void showRefereeUI(){
        switch (mRecommendedLevel){
            case 0:
                mTmgrefereeOne.setImageResource(R.drawable.ic_garden_referee_00);
                break;
            case 1:
                mTmgrefereeOne.setImageResource(R.drawable.ic_garden_referee_01);
                break;
            default:
                mTmgrefereeOne.setImageResource(R.drawable.ic_garden_referee_02);
                break;
        }
        mTmgrefereeOne.setVisibility(View.VISIBLE);
    }
    //获取自动成长值
    private void getGrowthvalue(){
        final String params = "user_id="+SharePreService.getShaedPrerence(mContext, SumConstants.USERID)+"&t_id=15";
        Log.i("getGrowthvalue", "params=" + params);
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String valueData = new HttpUtils().PostString(mContext, mUrlGrowth, params);
                    Log.i("getGrowthvalue", "valueData=" + valueData);
                    JSONObject valueJs = new JSONObject(valueData);
                    String event = valueJs.getString("event");
                    if (event.equals("0")) {
                        Message msg = new Message();
                        msg.what = 900;
                        msg.arg1 = valueJs.getInt("t_value");
                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //获取虫子数
    private void insectNum(){
        final String params = "t_id="+mTreeId;
        Log.i("insectNum", "params=" + params);
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String valueData = new HttpUtils().PostString(mContext, mUrlInsectNum, params);
                    Log.i("insectNum", "valueData=" + valueData);
                    if(!valueData.equals("")||valueData!=null){
                        JSONObject valueJs = new JSONObject(valueData);
                        String event = valueJs.getString("event");
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        if (event.equals("0")) {
                            JSONObject js = valueJs.getJSONObject("ObjectList");
                            mInsectNum = js.getInt("num");
                            msg.what = 901;
                        }else if(!event.equals("0")||!event.equals("1")){
                            msg.what = 902;
                            bundle.putString("msg",valueJs.getString("msg"));
                        }
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //杀虫
    private void killInsect(){
        final String params = "t_id="+mTreeId;
        Log.i("killInsect", "params=" + params);
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String killdata = new HttpUtils().PostString(mContext, mUrlKillInsect, params);
                    Log.i("killInsect", "killdata=" + killdata);
                    handinsetData(killdata);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //处理杀虫数据
    private void handinsetData(String data) throws JSONException {
        JSONObject killJs = new JSONObject(data);
        String event = killJs.getString("event");
        Message msg = new Message();
        if(event.equals("0")){
            msg.what = 0x7;
            msg.obj = killJs.getInt("t_value");
        }else{
            msg.what = 0x8;
            msg.obj = killJs.getString("msg");
        }
        handler.sendMessage(msg);
    }
    //采摘果子
    private void pickFruitData(){
        final String params = "t_id="+mTreeId;
        Log.i("pickFruitData", "params=" + params);
        //2 连接服务器
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String resultData = new HttpUtils().PostString(mContext, mUrlpickFruit, params);
                    Log.i("pickFruitData", "resultData=" + resultData);
                    JSONObject pickJs = new JSONObject(resultData);
                    Message msg = new Message();
                    String event = pickJs.getString("event");
                    msg.what = 0x9;
                    Bundle bundle = new Bundle();
                    bundle.putString("event",event);
                    bundle.putString("msg",pickJs.getString("msg"));
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //新建果园进入果园详情  出现种果树场景
    private static void NewCreateGarden(String gardenName,int gardenId) {
        //将除种果园控件显示 其余都隐藏
        mImgGet.setImageResource(R.drawable.ic_garden_plant_tree);
        mImgGet.setVisibility(View.VISIBLE);
        //设置种树与采摘 索引
        plantindex = 1;
        //设置果园名称
        mTvGardenName.setText(gardenName);
        mRelGardenName.setVisibility(View.VISIBLE);
        //获取ID值
        mDefaultgId = gardenId;
        //跳转到单果园页面
        mRelGardenL.setVisibility(View.GONE);
        mRelEveryGd.setVisibility(View.VISIBLE);
    }
    //新建果园
    private class CreateGarden extends Thread {

        public CreateGarden(String name, String context) {
            gardenName = name;
            gardenContext = context;
        }
        @Override
        public void run() {
            super.run();
            try {
                String parmas = "user_id=" + SharePreService.getUserId(mContext) +
                        "&zg_name=" + gardenName +
                        "&zg_content=" + gardenContext;
                Log.i("CreateGarden","parmas="+parmas);
                String loginResult = HttpUtils.PostString(mContext, getResources().getString(R.string.urlheader) + "/Garden/buildGarden", parmas);
                Log.i("CreateGarden","loginResult="+loginResult);
                if (loginResult == null) return;
                JSONObject jsonObject = new JSONObject(loginResult);
                String is_ok = jsonObject.getString("event");
                Message msg = new Message();
                if (is_ok.equals("0")) {
                    JSONObject obj = jsonObject.getJSONObject("obj");
                    msg.what = 200;
                    msg.arg1 = obj.getInt("zg_id");
                    mRecommendedLevel = Integer.parseInt(obj.getString("user_level"));
                    handler.sendMessage(msg);
                    Intent intent = new Intent(SumConstants.INTENTACTION);
                    intent.putExtra("index", 2);
                    mContext.sendBroadcast(intent);
                } else if (is_ok.equals("110")) {
                    msg.what = 110;
                    handler.sendMessage(msg);
                } else if (is_ok.equals("105")) {
                    msg.what = 105;
                    handler.sendMessage(msg);
                }
                mDialog02.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class GetDataTask extends AsyncTask<Void, Void, String> {
        String result;
        String url = mContext.getResources().getString(R.string.urlheader) + "/Garden/GardenList";
        String postParams = "user_id="+SharePreService.getUserId(mContext);
        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job.
            try {
                result = HttpUtils.PostString(mContext, url, postParams);
                Log.e("wu", "result =="+result );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (null == result||result.equals("")) {
                System.out.println("数据为空");
                mGardenList.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_garden_data));
                return;
            }
            mFruitGardenDatas.clear();
            FruitGarden fruitGarden = null;
            JSONObject data = null;
            try {
                data = new JSONObject(result);
                if (data.getString("event").equals("0")) {
                    JSONObject dataArray = new JSONObject(data.getString("objList"));
                    String defaultId = dataArray.getString("user_orchard");
                    mDefaultgId = Integer.valueOf(defaultId);
                    JSONArray arr = new JSONArray(dataArray.getString("mylist"));
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject temp = (JSONObject) arr.get(i);
                        fruitGarden = new FruitGarden();
                        fruitGarden.id = temp.getString("garden_id");
                        fruitGarden.title = temp.getString("garden_name");
                        fruitGarden.memberCount = temp.getString("count");
                        fruitGarden.masterId = temp.getString("zg_userid");
                        mFruitGardenDatas.add(fruitGarden);
                    }
                    JSONArray arrlist = new JSONArray(dataArray.getString("list"));
                    for (int i = 0; i < arrlist.length(); i++) {
                        JSONObject temp = (JSONObject) arrlist.get(i);
                        fruitGarden = new FruitGarden();
                        fruitGarden.id = temp.getString("garden_id");
                        fruitGarden.title = temp.getString("garden_name");
                        fruitGarden.memberCount = temp.getString("count");
                        fruitGarden.masterId = temp.getString("zg_userid");
                        mFruitGardenDatas.add(fruitGarden);
                    }
                } else {
                    Toast.makeText(mContext, data.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (0 == mFruitGardenDatas.size()) {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(mContext.getResources().getString(R.string.no_garden_data));
            } else {
                   mEmptyView.setVisibility(View.GONE);
            }
            mFruitGardenAdapter = new FruitGardenAdapter(mContext, mFragmentManager, handler, mFruitGardenDatas, R.layout.fruit_garden_item);
            mGardenList.setAdapter(mFruitGardenAdapter);
            super.onPostExecute(result);
        }
    }
    private class PostDataTask extends AsyncTask<String, Void, String> {
        String result;
        String url = mContext.getResources().getString(R.string.urlheader) + "/user/SetDefault";
        String postParams = "UserId=" + SharePreService.getUserId(mContext)+"&GardenId=";
        String tmpGardenId;
        @Override
        protected String doInBackground(String... params) {
            // Simulates a background job.
            try {
                tmpGardenId = params[0];
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
                    mDefaultgId = Integer.valueOf(tmpGardenId);
                    mFruitGardenAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
    //设置虫子数据
    private void setinsectData(int insectnum) {
        int[] insects = creatRandom(SumConstants.INSECTCOUNT, insectnum);
        for (int i = 0; i < insects.length; i++) {
            int status = insects[i];
            insectList.get(status).mInsect = status % 2;
            insectList.get(status).ishas = true;
        }
        mInsectAdpter.notifyDataSetChanged();
    }
    //树成长动画
    private void plantTreeAnimation(){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (current < 5) {
                    Message msg = new Message();
                    msg.what = current;
                    handler.sendMessage(msg);
                    current++;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    //隐藏果园详情中的所有控件
    private void HiddenAllView() {
        current = 0;
        insectList.clear();
        mTmgrefereeOne.setVisibility(View.GONE);
        mImgSelfTree.setVisibility(View.GONE);
        mImgSysTree.setVisibility(View.GONE);
        mImgThings.setVisibility(View.GONE);
        mGridInsect.setVisibility(View.GONE);
        mImgSelffruit.setVisibility(View.GONE);
        mImgSysfruit.setVisibility(View.GONE);
        mImgGet.setVisibility(View.GONE);
    }
    //通知MainFragment处理成长值
    private void handlerGrowthValue(boolean isshow,int what,int value){
        Message GrowthValuemsg = new Message();
        GrowthValuemsg.what = what;
        if(isshow){
            GrowthValuemsg.arg1 = value;//设置成长值
        }
        MainFragment.mainhandler.sendMessage(GrowthValuemsg);
    }
    //获取虫子不重复的随机数
    private static int[] creatRandom(int total, int number) {
        int randoms[] = new int[number];
        Random random = new Random();
        List arr = new ArrayList();
        for (int i = 0; i < total; i++)
            arr.add(i);// 为ArrayList添加元素
        for (int j = 0; j < number; j++) {
            int index = random.nextInt(total);// 产生一个随机数作为索引
            randoms[j] = (int) arr.get(index);
            arr.remove(index);// 移除已经取过的元素
            total--;// 将随机数范围缩小1
        }
        return randoms;
    }
    //设置dialog返回键 无效
    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle;
            switch (msg.what) {
                case 0:
                    mImgSelfTree.setImageResource(R.drawable.ic_darden_tree_01);
                    break;
                case 1:
                    mImgSelfTree.setImageResource(R.drawable.ic_darden_tree_02);
                    break;
                case 2:
                    mImgSelfTree.setImageResource(R.drawable.ic_darden_tree_03);
                    break;
                case 3:
                    mImgSelfTree.setImageResource(R.drawable.ic_darden_tree_04);
                    break;
                case 4:
                    mImgSelfTree.setImageResource(R.drawable.ic_darden_tree_05);
                    break;
                case 0x5:
                    insectList.clear();//清空虫子数
                    initGridView();
                    FruitGarden fg = (FruitGarden)msg.obj;
                    gardenName = fg.title;
                    Log.e("wu","fg.id=="+fg.id);
                    mDefaultgId = Integer.parseInt(fg.id);
                    mRelGardenName.setVisibility(View.VISIBLE);
                    GoInGardenDes(mDefaultgId, gardenName);
                    mRelGardenL.setVisibility(View.GONE);
                    mRelEveryGd.setVisibility(View.VISIBLE);
                    break;
                case 0x7:
                    insectList.get(mPosition).ishas = false;
                    mInsectAdpter.notifyDataSetChanged();
                    handlerGrowthValue(true, 0x123,(int) msg.obj);
                    break;
                case 0x8:
                    String msgd = (String) msg.obj;
                    Toast.makeText(mContext, ""+msgd, Toast.LENGTH_SHORT).show();
                    break;
                case 0x9:
                    bundle = msg.getData();
                    String event = bundle.getString("event");
                    if(event.equals("0")){//成长值为0 果子去掉 采摘排隐藏
                        handlerGrowthValue(true, 0x123,0);
                        mImgSelffruit.setVisibility(View.GONE);
                        mImgGet.setVisibility(View.GONE);
                    }else{
                        mImgGet.setClickable(false);
                        Toast.makeText(mContext, ""+bundle.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 200:
                    //防止软键盘弹出
                    getActivity().getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    mDialog02.dismiss();
                    index = 2;
                    mRelEveryGd.setVisibility(View.VISIBLE);
                    showNewDialog();
                    Log.i("wu", "果园名称：" + gardenName);
                    mTvGardenName.setText(gardenName);
                    mRelGardenName.setVisibility(View.VISIBLE);
                    mDefaultgId = msg.arg1;
                    break;
                case 110:
                    Toast.makeText(mContext, getResources().getString(R.string.toast_servicer_error), Toast.LENGTH_SHORT).show();
                    break;
                case 105:
                    Toast.makeText(mContext, getResources().getString(R.string.toast_submit_succeed), Toast.LENGTH_SHORT).show();
                case 0x6:
                    new PostDataTask().execute(msg.obj.toString());
                    break;
                case 302://种果树获取服务器数据处理
                    overPlantTree(msg.obj.toString());
                    break;
                case 900:
                    handlerGrowthValue(true,0x123,msg.arg1);
                    break;
                case 301:
                    //1 设置果园名称
                    mTvGardenName.setText(gardenName);
                    mRelGardenName.setVisibility(View.VISIBLE);
                    GargenInfoUI(msg.getData());
                    break;
                case 101:
                    showNewDialog();
                    break;
                case 901:
                    mGridInsect.setVisibility(View.VISIBLE);
                    setinsectData(mInsectNum);
                    break;
                case 902:
                    bundle = msg.getData();
//                    Toast.makeText(mContext,"" +bundle.getString("msg") , Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    static Handler JumpHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1.接受消息
            switch (msg.what) {
                case 10://2接受新建果园信息
                    Bundle bundle = msg.getData();
                    mRecommendedLevel = bundle.getInt("user_level");
                    NewCreateGarden(bundle.getString("gardenName"),bundle.getInt("gardenId"));
                    break;
            }
        }
    };

}
