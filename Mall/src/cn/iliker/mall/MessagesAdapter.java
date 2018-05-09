package cn.iliker.mall;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import cn.iliker.mall.alipush.MessageEntity;
import iliker.adapter.DefaultAdapter;
import iliker.holder.BaseHolder;

import java.util.List;

public class MessagesAdapter extends DefaultAdapter<MessageEntity> {
    private Context context;

    public MessagesAdapter(List<MessageEntity> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new MsgHolder(context);
    }

    class MsgHolder extends BaseHolder<MessageEntity> {

        private TextView title;
        private TextView content;
        private TextView starttime;

        public MsgHolder(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.sys_msg_item, null);
            this.title = (TextView) view.findViewById(R.id.title);
            this.content = (TextView) view.findViewById(R.id.content);
            this.starttime = (TextView) view.findViewById(R.id.starttime);
            return view;
        }

        @Override
        public void refreshView(MessageEntity messageEntity) {
            this.title.setText(messageEntity.getMessageTitle());
            this.content.setText(messageEntity.getMessageContent());
            this.starttime.setText(messageEntity.getCreateTime());
        }
    }
}
