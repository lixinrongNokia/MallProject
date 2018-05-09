package iliker.fragment.person;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import cn.iliker.mall.R;
import com.viewpagerindicator.TabPageIndicator;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.mall.MainActivity;
import iliker.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class AllOrderActivity extends BaseStoreActivity {
    private TabPageIndicator tabPageIndicator;
    private ViewPager viewPager;
    private List<BaseShopPager> shoppagers;
    private String[] titles;
    private int checkid;
    private View view;

    @Override
    protected void initMyViews() {
        title.setText("所有订单");
        view = View.inflate(this, R.layout.shop_layout, null);
        storeContent.addView(view);
        checkid = getIntent().getIntExtra("checkid", 0);
        findChildViews();
        initDatas();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shoppagers.get(checkid).initData(false);//远程获取数据
    }

    private void setListener() {
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                checkid = position;
                shoppagers.get(checkid).initData(false);
            }
        });
    }

    private void initDatas() {
        titles = new String[]{"待付款", "待收货", "确认收货", "已取消", "全部"};
        shoppagers = new ArrayList<>();
        shoppagers.add(new ForThePaymentPager(this));
        shoppagers.add(new ForTheGoodsPager(this));
        shoppagers.add(new ConfirmOrderPager(this));
        shoppagers.add(new CancelledPager(this));
        shoppagers.add(new AllOrderPager(this));
        viewPager.setAdapter(new MyPagerAdapter());
        tabPageIndicator.setViewPager(viewPager);
        viewPager.setCurrentItem(checkid);
    }

    private void findChildViews() {
        tabPageIndicator = (TabPageIndicator) view.findViewById(R.id.indicator);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        findViewById(R.id.backbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.sendActivity(AllOrderActivity.this, MainActivity.class);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ViewUtils.sendActivity(AllOrderActivity.this, MainActivity.class);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            if (shoppagers != null) {
                return shoppagers.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = shoppagers.get(position).rootView;
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
