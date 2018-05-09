package iliker.fragment.person;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.lib.presenter.conversation.P2PConversation;

import java.util.Date;
import java.util.List;

import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.holder.BaseHolder;

import static iliker.utils.FormatDateTo.format;

class IMAdapter extends DefaultAdapter<YWConversation> {
    private final Context context;

    @Override
    public BaseHolder getHolder() {
        return new IMHolder(context);
    }

    IMAdapter(List<YWConversation> datas, Context context) {
        super(datas);
        this.context = context;
    }


    private class IMHolder extends BaseHolder<YWConversation> {
        private TextView nickName;
        private TextView content;
        private TextView time;
        private TextView unread;

        IMHolder(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.conversation_item, null);
            this.content = (TextView) view.findViewById(R.id.content);
            this.time = (TextView) view.findViewById(R.id.time);
            this.nickName = (TextView) view.findViewById(R.id.nickName);
            this.unread = (TextView) view.findViewById(R.id.unread);
            return view;
        }

        @Override
        public void refreshView(YWConversation datas) {
            if (datas instanceof P2PConversation) {
                P2PConversation p2PConversation = (P2PConversation) datas;
                this.content.setText(p2PConversation.getLatestContent());
                this.nickName.setText(p2PConversation.getConversationName());
                this.time.setText(format(new Date(p2PConversation.getLatestTimeInMillisecond())));
                int unreadnm = p2PConversation.getUnreadCount();
                if (unreadnm > 0) {
                    this.unread.setVisibility(View.VISIBLE);
                    if (unreadnm > 100) {
                        this.unread.setText(String.valueOf("99+"));
                    } else
                        this.unread.setText(String.valueOf(unreadnm));
                } else this.unread.setVisibility(View.INVISIBLE);
            }
        }
    }
}
