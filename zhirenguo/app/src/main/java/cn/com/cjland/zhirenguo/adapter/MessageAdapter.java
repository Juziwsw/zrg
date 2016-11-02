package cn.com.cjland.zhirenguo.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.Messagebean;
import cn.com.cjland.zhirenguo.fragment.MsgDialogFragment;
import cn.com.cjland.zhirenguo.model.ImageLoader;
/**
 * Created by Administrator on 2015/11/30.
 */
public class MessageAdapter extends BaseAdapter {
    private List<Messagebean> mList;
    private LayoutInflater mInflaier;
    private ImageLoader imageloader;
    private Context mContext;
    private MsgDialogFragment.ListItemCallBack callBack;
    Typeface typeface;
    public MessageAdapter(Context context, List<Messagebean> data,MsgDialogFragment.ListItemCallBack callBack) {
        this.mContext = context;
        mList = data;
        mInflaier = LayoutInflater.from(context);
        imageloader = new ImageLoader();
        this.callBack = callBack;
        this.typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Font1.ttf");
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflaier.inflate(R.layout.fragment_msg_item, null);
            viewHolder.ivIcon = (ImageView)convertView.findViewById(R.id.iv_message_avatar);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_message_who);
            viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_message_content);
            viewHolder.mBtnLook = (Button)convertView.findViewById(R.id.btn_msg_look);
            viewHolder.tvName.setTypeface(typeface);
            viewHolder.tvContent.setTypeface(typeface);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Messagebean msg = (Messagebean)mList.get(position);
        viewHolder.ivIcon.setImageResource(R.drawable.ic_person_header);//设置默认图片
        String url = msg.imgUrl;
        if(!url.equals("")){
            //多线程加载图片
            viewHolder.ivIcon.setTag(url);//给imageview设置一个名为url的tag,就是相对应的进行绑定
            imageloader.showImageByAsycnTask(viewHolder.ivIcon, url);
        }else{
            viewHolder.ivIcon.setImageResource(R.drawable.ic_person_header);//设置默认图片
        }
//        viewHolder.tvName.setText(!msg.userName.equals("") ? msg.userName : msg.userPhone);
        viewHolder.tvContent.setText(msg.msgContent);
        switch (msg.msgTpye) {
            case 0://0被邀请成为果友
                viewHolder.tvName.setText(msg.userName);
                break;
            case 1://1被邀请加入果园
                viewHolder.tvName.setText(msg.userName);
                break;
            case 4://4邀请果友加入果园
                viewHolder.tvName.setText(msg.userName);
                break;
            case 2://2乐投获取多少果币
                viewHolder.tvName.setText("系统消息");
                break;
            case 3://3果袋容量不足
                viewHolder.tvName.setText("系统消息");
                break;
        }
//        if(msg.msgTpye == 0){
//            viewHolder.tvContent.setText(!msg.msgContent.equals("") ? "邀请加入果园:" + msg.groupName + "\n邀请内容:" + msg.msgContent : "邀请加入果园:" + msg.groupName);
//        }else if(msg.msgTpye == 1){
//            viewHolder.tvContent.setText("邀请你成为果友!一起来玩~");
//        }
        //稍后处理按钮事件
        viewHolder.mBtnLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.ListItemListener(msg);
            }
        });
        return convertView;
    }
    //文意思
    class ViewHolder{
        public TextView tvName;
        public TextView tvContent;
        public ImageView ivIcon;
        public Button mBtnLook;
    }
}
