package iliker.fragment.finding;

import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.TabHost;
import cn.iliker.mall.R;
import iliker.fragment.mystore.BaseStoreActivity;

public class FollowersActivity extends BaseStoreActivity implements TabHost.OnTabChangeListener{
    private FragmentTabHost tabhost;// 选项卡主控件
    private View view;

    @Override
    protected void initMyViews() {
        title.setText("我的关注");
        view = View.inflate(this, R.layout.follwers_layout, null);
        storeContent.addView(view);
        findChildViews();
        initdata();
    }

    private void findChildViews() {
        // 获取TabHost对象
        tabhost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        tabhost.setOnTabChangedListener(this);
    }

    private void initdata() {
        tabhost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("关注"), FromFollFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("粉丝"), ToFollFragment.class, null);
    }

    @Override
    public void onTabChanged(String tabId) {
        /*if ("tab1".equals(tabId)){

        }else {

        }*/
    }
}
