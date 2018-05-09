package iliker.fragment.finding.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jikexueyuan.tulingdemo.ListData;

import cn.iliker.mall.R;
import iliker.holder.BaseHolder;

/**
 * 附近的人对话hodler封装
 * Created by WDHTC on 2016/4/7.
 */
public class ReceiverHoder extends BaseHolder<ListData> {
    private TextView time;//时间
    private TextView tv;//内容

    public ReceiverHoder(Context context) {
        super(context);
    }

    @Override
    public View initViews() {
        View view = View.inflate(context, R.layout.leftitem, null);
        this.time = (TextView) view.findViewById(R.id.time);
        this.tv = (TextView) view.findViewById(R.id.tv);
        return view;
    }

    @Override
    public void refreshView(ListData listData) {
        this.time.setText(listData.getTime());
        this.tv.setText(listData.getContent());
    }
}
