package iliker.fragment.person;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;

import cn.iliker.mall.R;
import iliker.db.DatabaseService;
import iliker.fragment.BaseFragment;
import iliker.mall.CartActivity;
import iliker.mall.LoginActivity;

import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.DBHelper.getDB;

public class ShopedFragment extends BaseFragment implements OnClickListener {
    private TextView myorder, mycart, footprint;// 我的订单，我的购物车，我的足迹
    private DatabaseService ds;
    private BadgeView badge;
    private RelativeLayout cart;

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.shoped, null);
        }
        ds = getDB();
        findViews();
        badge = new BadgeView(context, mycart);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initDate();
    }

    private void findViews() {
        myorder = (TextView) view.findViewById(R.id.myorder);
        mycart = (TextView) view.findViewById(R.id.mycart);
        footprint = (TextView) view.findViewById(R.id.footprint);
        cart = (RelativeLayout) view.findViewById(R.id.cart);
    }

    private void initDate() {
        int counts = ds.findTotalCount();
        if (counts > 0) {
            badge.setText(String.valueOf(counts));// 赋值
            badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);// 设置徽章位置为右上角
            badge.show();
        } else {
            badge.setText("");
            badge.hide();
        }

    }

    private void setListeners() {
        myorder.setOnClickListener(this);
        cart.setOnClickListener(this);
        footprint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (customApplication.getUserinfo() == null) {
            context.startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        switch (v.getId()) {
            case R.id.myorder:
                Intent findorder = new Intent(context, AllOrderActivity.class);
                findorder.putExtra("checkid", 4);
                context.startActivity(findorder);
                break;
            case R.id.cart:
                Intent intent = new Intent(context, CartActivity.class);
                startActivity(intent);
                break;
            case R.id.footprint:
                Intent history = new Intent(context, HistoryActivity.class);
                startActivity(history);
                break;
        }
    }

}
