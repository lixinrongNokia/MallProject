package iliker.mall.depth;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.iliker.mall.R;

abstract class Basepager {
    public View view;
    final Activity activity;
    TextView title;
    FrameLayout contentPanel;
    final SharedPreferences sf;

    Basepager(Activity activity) {
        this.activity = activity;
        sf = activity.getSharedPreferences("depth_result", Context.MODE_PRIVATE);
        initViews();
    }

    private void initViews() {
        view = View.inflate(activity, R.layout.single_layout, null);
        title = (TextView) view.findViewById(R.id.title);
        contentPanel = (FrameLayout) view.findViewById(R.id.contentPanel);
    }

    public abstract void initData();

}
