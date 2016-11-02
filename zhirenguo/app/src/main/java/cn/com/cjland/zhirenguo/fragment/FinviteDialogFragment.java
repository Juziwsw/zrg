package cn.com.cjland.zhirenguo.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.SharePreService;
import cn.com.cjland.zhirenguo.bean.SumConstants;

/**
 * Created by Administrator on 2015/12/28.
 */

public class FinviteDialogFragment extends DialogFragment implements View.OnClickListener{
    private Context context;
    private View mView;
    private ImageView mInviteClose,mImgInviteWX,mImgInviteQQ,mImgInviteSina;
    private TextView mTvPhone;
    private String mPnoneNum;
    /*---------------------邀请功能----------------- */
    // 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(SumConstants.DESCRIPTOR);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        /*---------------------邀请功能----------------- */
        mController.getConfig().closeToast();//关闭自我提示
        // 配置需要分享的相关平台
        configPlatforms();
        setShareContent();
        mPnoneNum = SharePreService.getShaedPrerence(context,SumConstants.PHONENUM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View view = inflater.inflate(R.layout.fragment_invite_dialog, container);
        mView = view;
        findview();
        return view;
    }
    private void findview(){
        mInviteClose = (ImageView)mView.findViewById(R.id.img_invite_close);
        mImgInviteWX = (ImageView)mView.findViewById(R.id.img_invite_wx);
        mImgInviteQQ = (ImageView)mView.findViewById(R.id.img_invite_qq);
        mImgInviteSina = (ImageView)mView.findViewById(R.id.img_invite_sina);
        mTvPhone = (TextView)mView.findViewById(R.id.tv_invist_phone);
        if(!mPnoneNum.equals("")){
            mTvPhone.setText(""+mPnoneNum);
        }
        mImgInviteWX.setOnClickListener(this);
        mImgInviteQQ.setOnClickListener(this);
        mImgInviteSina.setOnClickListener(this);
        mInviteClose.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_invite_close:
                getDialog().dismiss();
                break;
            case R.id.img_invite_wx:
                directShare(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.img_invite_qq:
                directShare(SHARE_MEDIA.QQ);
                break;
            case R.id.img_invite_sina:
                directShare(SHARE_MEDIA.SINA);
                break;

        }
    }
    //------------------------- QQ、微信邀请测试 初始化 ----------------------
    /**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() {
        // 添加QQ、QZone平台
        addQQQZonePlatform();
        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }
    /**
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     * @return
     */
    private void addQQQZonePlatform() {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
                SumConstants.QQappId, SumConstants.QQappKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
        qqSsoHandler.addToSocialSDK();
    }
    /**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context, SumConstants.WXappId, SumConstants.WXappSecret);
        wxHandler.addToSocialSDK();
    }
    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent() {
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent
                .setShareContent("老朋友我在至人果发财了，你还在等什么！--邀请码："+mPnoneNum);
        weixinContent.setTitle("邀请加入至人果");
        weixinContent.setTargetUrl("http://www.baidu.com");
        weixinContent.setShareImage(new UMImage(context, "http://pic46.nipic.com/20140813/10153265_103918805357_2.jpg"));
        mController.setShareMedia(weixinContent);


        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent("老朋友我在至人果发财了，你还在等什么！--邀请码："+mPnoneNum);
        qqShareContent.setTitle("邀请加入至人果");
        //设置分享图片
        qqShareContent.setShareImage(new UMImage(context, "http://pic46.nipic.com/20140813/10153265_103918805357_2.jpg"));
        qqShareContent.setTargetUrl("http://www.baidu.com");
        mController.setShareMedia(qqShareContent);

        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setTitle("邀请加入至人果");
        sinaContent.setShareContent("老朋友我在至人果发财了，你还在等什么！--邀请码："+mPnoneNum);
        sinaContent.setShareImage(new UMImage(context, "http://pic46.nipic.com/20140813/10153265_103918805357_2.jpg"));
        sinaContent.setTargetUrl("http://www.baidu.com");
        mController.setShareMedia(sinaContent);
    }
    /**
     * 直接分享，底层分享接口。如果分享的平台是新浪、腾讯微博、豆瓣、人人，则直接分享，无任何界面弹出； 其它平台分别启动客户端分享</br>
     */
    private void directShare(final SHARE_MEDIA platform) {
        mController.directShare(context, platform, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                String showText = "邀请成功";
                if (eCode != StatusCode.ST_CODE_SUCCESSED) {
                    if (eCode == 40000) {
                        showText = "取消邀请";
                    } else {
                        showText = "邀请失败 [" + eCode + "]";
                    }

                }
//                Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}

