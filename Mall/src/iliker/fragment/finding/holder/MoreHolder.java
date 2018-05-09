package iliker.fragment.finding.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.iliker.mall.R;


public class MoreHolder {
    final private Context context;
    final private View view;
    private TextView operation;

    public MoreHolder(Context context) {
        this.context = context;
        this.view = loadViews();
        this.view.setTag(this);
    }

    private View loadViews() {
        View view = View.inflate(context, R.layout.more_item_layout, null);
        this.operation = (TextView) view.findViewById(R.id.operation);
        return view;
    }

    public View getConvertView() {
        return view;
    }

    public void setData(String s) {
        this.operation.setText(s);
    }
}
