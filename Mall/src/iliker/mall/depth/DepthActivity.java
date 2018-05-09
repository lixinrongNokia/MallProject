package iliker.mall.depth;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import cn.iliker.mall.R;

import java.util.ArrayList;
import java.util.List;

public class DepthActivity extends Activity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private List<Basepager> pagers;//填充集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.depth_activity);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setListener();
        initData();
        initViews();
    }

    private void setListener() {
        viewPager.addOnPageChangeListener(this);
    }

    private void initData() {
        pagers = new ArrayList<>();
        pagers.add(new Choosepager1(this));
        pagers.add(new Choosepager2(this));
        pagers.add(new Choosepager3(this));
        pagers.add(new Choosepager4(this));
        pagers.add(new Choosepager5(this));
        pagers.add(new ConfirmPager1(this));
        pagers.add(new ConfirmPager2(this));
        pagers.add(new ConfirmPager3(this));
        pagers.add(new ConfirmPager4(this));
        pagers.add(new ConfirmPager5(this));
        pagers.add(new ConfirmPager6(this));
        pagers.add(new ConfirmPager7(this));
        pagers.add(new ConfirmPager8(this));
        pagers.add(new ConfirmPager9(this));
        pagers.add(new ConfirmPager10(this));
        pagers.add(new ConfirmPager11(this));
        pagers.add(new EndPager(this));
    }

    private void initViews() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pagers.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(pagers.get(position).view);
                return pagers.get(position).view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        pagers.get(0).initData();
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        pagers.get(i).initData();
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    protected void onDestroy() {
        pagers.clear();
        pagers=null;
        super.onDestroy();
    }
}
