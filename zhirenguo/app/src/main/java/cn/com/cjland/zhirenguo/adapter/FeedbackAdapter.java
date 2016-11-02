package cn.com.cjland.zhirenguo.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.cjland.zhirenguo.R;
import cn.com.cjland.zhirenguo.bean.ChatMsgEntity;

/**
 * @author zhu
 * 吐槽适配器的自定义
 */
public class FeedbackAdapter extends BaseAdapter {

    public static interface IMsgViewType
    {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }

    private List<ChatMsgEntity> MsgList;

    private Context ctx;

    private LayoutInflater mInflater;

    public FeedbackAdapter(Context context) {
        ctx = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setList(List<ChatMsgEntity> coll){
        this.MsgList = coll;
    }

    public int getCount() {
        if(MsgList == null){
            return 0;
        }
        return MsgList.size();
    }

    public Object getItem(int position) {
        if(MsgList == null){
            return null;
        }
        return MsgList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        ChatMsgEntity entity = MsgList.get(position);
        if (entity.getMsgType())
        {
            return IMsgViewType.IMVT_COM_MSG;
        }else{
            return IMsgViewType.IMVT_TO_MSG;
        }

    }
    public int getViewTypeCount() {
        return 2;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        if(this.MsgList == null || this.MsgList.isEmpty()){
            return convertView;
        }
        ChatMsgEntity entity = MsgList.get(position);
        boolean isComMsg = entity.getMsgType();
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            if (isComMsg)
            {
                convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
            }else{
                convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
            }
            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            viewHolder.isComMsg = isComMsg;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvSendTime.setText(entity.getDate());
        viewHolder.tvContent.setText(entity.getText());
        int type = entity.getFaceType();
        if(type == 1 ){
            viewHolder.tvContent.append(Html.fromHtml("<img src='"+R.drawable.ic_idea_word_look01+"'/>", imageGetter, null));
        }else if(type ==2 ){
            viewHolder.tvContent.append(Html.fromHtml("<img src='"+R.drawable.ic_idea_word_look02+"'/>", imageGetter, null));
        }


        return convertView;
    }

    static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvContent;
        public boolean isComMsg = true;
    }

    Html.ImageGetter imageGetter = new Html.ImageGetter() {

        @Override
        public Drawable getDrawable(String source) {
            int id = Integer.parseInt(source);
            Drawable drawable =ctx.getResources().getDrawable(id);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            return drawable;
        }
    };
}
