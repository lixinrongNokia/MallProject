package iliker.fragment.mystore;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.cardsui.example.goods.ClassificationActivity;

import cn.iliker.mall.R;

public class PromoteActivity extends BaseStoreActivity {
    private View view;
    private TextView conditions, earnings;

    @Override
    protected void initMyViews() {
        view = View.inflate(this, R.layout.promote_layout, null);
        storeContent.addView(view);
        findChildViews();
        boolean goldtwitter = getIntent().getBooleanExtra("goldtwitter", false);
        if (goldtwitter) {
            title.setText("金牌推客说明");
            conditions.setText(getResources().getText(R.string.supertwitter));
            earnings.setText(getResources().getText(R.string.superearnings));
        } else {
            title.setText("推客说明");
            conditions.setText(getResources().getText(R.string.twitterconditions));
            earnings.setText(getResources().getText(R.string.twitterearnings));
        }
    }

    private void findChildViews() {
        conditions = (TextView) view.findViewById(R.id.conditions);
        earnings = (TextView) view.findViewById(R.id.earnings);
        view.findViewById(R.id.tbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean forwardProduct = getIntent().getBooleanExtra("forwardProduct", false);
                if (forwardProduct) {
                    finish();
                } else {
                    Intent classintent = new Intent(PromoteActivity.this, ClassificationActivity.class);
                    startActivity(classintent);
                }
            }
        });
    }

}
