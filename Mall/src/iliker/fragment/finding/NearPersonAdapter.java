package iliker.fragment.finding;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.UserInfo;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

public class NearPersonAdapter extends DefaultAdapter<UserInfo> {

    private final Context context;
    private List<Map<String, Object>> list;

    public NearPersonAdapter(Context context, List<UserInfo> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new ViewHold(context);
    }

    final class ViewHold extends BaseHolder<UserInfo> {
        public ViewHold(Context context) {
            super(context);
        }

        public ImageView personhead;
        public TextView nickname;
        public TextView signature;
        public TextView distance;

        @Override
        public View initViews() {
            View convertView = View.inflate(context, R.layout.nearpsrson, null);
            this.personhead = (ImageView) convertView.findViewById(R.id.personhead);
            this.nickname = (TextView) convertView.findViewById(R.id.nickname);
            this.signature = (TextView) convertView.findViewById(R.id.signature);
            this.distance = (TextView) convertView.findViewById(R.id.distance);
            return convertView;
        }

        @Override
        public void refreshView(UserInfo userInfo) {
            bitmapUtils.bind(this.personhead, GeneralUtil.SHAREPATH + userInfo.getHeadImg());
            this.nickname.setText(userInfo.getNickName());
            this.signature.setText(userInfo.getSignature());
            this.distance.setText(String.valueOf(userInfo.getDistance() + "km"));
        }
    }

}
