package iliker.fragment.finding.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.jikexueyuan.tulingdemo.ListData;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

import static iliker.utils.BitmapHelp.getBitmapUtils;

/**
 * Created by WDHTC on 2016/4/7.
 */
public class SendHoder extends BaseHolder<ListData> {
    private TextView time;//时间
    private ImageView iv;//头像
    private TextView tv;//内容

    public SendHoder(Context context) {
        super(context);
    }

    @Override
    public View initViews() {
        View view = View.inflate(context, R.layout.rightitem, null);
        this.time = (TextView) view.findViewById(R.id.time);
        this.iv = (ImageView) view.findViewById(R.id.iv);
        this.tv = (TextView) view.findViewById(R.id.tv);
        return view;
    }

    @Override
    public void refreshView(ListData listData) {
        this.time.setText(listData.getTime());
        getBitmapUtils().bind(this.iv, GeneralUtil.HOST + listData.getHeadimg());
        this.tv.setText(listData.getContent());
    }
}
