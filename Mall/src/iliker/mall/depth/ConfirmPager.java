package iliker.mall.depth;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.iliker.mall.R;

abstract class ConfirmPager extends Basepager {
    TextView prompt;
    TextView ask;
    ImageView confirmimg;

    ConfirmPager(Activity activity) {
        super(activity);
        loadData();
    }


    private void loadData() {
        title.setText("更多乳房细节");
        View itemview = View.inflate(activity, R.layout.confirm_layout, null);
        prompt = (TextView) itemview.findViewById(R.id.prompt);
        ask = (TextView) itemview.findViewById(R.id.ask);
        confirmimg = (ImageView) itemview.findViewById(R.id.confirmimg);
        contentPanel.addView(itemview);
    }
}
