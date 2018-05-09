package iliker.fragment.finding.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.iliker.mall.R;

public class Person_Item_Holder {
    private TextView opkey;
    private TextView opvalues;
    private final Context context;
    private final View view;

    public Person_Item_Holder(Context context) {
        this.context = context;
        this.view = loadView();
        this.view.setTag(this);
    }

    private View loadView() {
        View view = View.inflate(context, R.layout.person_item_layout, null);
        this.opkey = (TextView) view.findViewById(R.id.opkey);
        this.opvalues = (TextView) view.findViewById(R.id.opvalues);
        return view;
    }

    public View getConvertView() {
        return view;
    }

    public void setDate(String s) {
        String[] strings = s.split("#");
        this.opkey.setText(strings[0]);
        this.opvalues.setText(strings[1]);
    }
}
