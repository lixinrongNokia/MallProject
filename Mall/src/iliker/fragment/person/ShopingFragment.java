package iliker.fragment.person;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import cn.iliker.mall.R;
import iliker.fragment.BaseFragment;
import iliker.mall.LoginActivity;

import static com.iliker.application.CustomApplication.customApplication;

public class ShopingFragment extends BaseFragment implements OnClickListener {
    private TextView paymenting, takegoods, querylogistics, coupons;// 待付款，待收货，查询物流，优惠券

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.shoping, null);
            findViews();
            onsetListener();
        }
        return view;
    }

    private void findViews() {
        paymenting = (TextView) view.findViewById(R.id.paymenting);
        takegoods = (TextView) view.findViewById(R.id.takegoods);
        querylogistics = (TextView) view.findViewById(R.id.querylogistics);
        coupons = (TextView) view.findViewById(R.id.coupons);
    }

    private void onsetListener() {
        paymenting.setOnClickListener(this);
        takegoods.setOnClickListener(this);
        querylogistics.setOnClickListener(this);
        coupons.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (customApplication.getUserinfo() == null) {
            context.startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        switch (v.getId()) {
            case R.id.paymenting:
                Intent intent = new Intent(context, AllOrderActivity.class);
                intent.putExtra("checkid", 0);
                context.startActivity(intent);
                break;
            case R.id.takegoods:
                Intent intent1 = new Intent(context, AllOrderActivity.class);
                intent1.putExtra("checkid", 1);
                context.startActivity(intent1);
                break;
            case R.id.querylogistics:
                Intent intent2 = new Intent(context, AllOrderActivity.class);
                intent2.putExtra("checkid", 1);
                context.startActivity(intent2);
                break;
            case R.id.coupons:
                context.startActivity(new Intent(context, CouponsActivity.class));
                break;
        }
    }

}
