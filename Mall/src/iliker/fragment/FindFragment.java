package iliker.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import iliker.fragment.finding.SelectImgActivity;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

import cn.iliker.mall.R;
import iliker.fragment.finding.BaseFindPager;
import iliker.fragment.finding.FaXianPager;
import iliker.fragment.finding.MorePager;
import iliker.fragment.finding.NearPager;
import iliker.fragment.finding.ThemeActivityPager;
import iliker.fragment.finding.ViewPagerAdapter;

import static com.fjl.widget.ToastFactory.getMyToast;
import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.ViewUtils.removeParent;

public class FindFragment extends BaseFragment implements OnClickListener {
    private ViewPager viewPager;
    private ArrayList<BaseFindPager> fragmentsList;
    private TabPageIndicator indicator;

    private RelativeLayout launch_share;

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.finding, null);
            findViews();
            setListeners();
            loadFragments();
            initData();
        }
        removeParent(view);
        return view;
    }

    private void setListeners() {
        launch_share.setOnClickListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                fragmentsList.get(position).initDate();
            }
        });
    }

    private void findViews() {
        launch_share = (RelativeLayout) view.findViewById(R.id.launch_share);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        indicator = (TabPageIndicator) view.findViewById(R.id.indicator);
    }

    private void loadFragments() {
        fragmentsList = new ArrayList<>();
        fragmentsList.add(new ThemeActivityPager(context));
        fragmentsList.add(new FaXianPager(context));
        fragmentsList.add(new NearPager(context));
        fragmentsList.add(new MorePager(context));
    }

    private void initData() {
        viewPager.setAdapter(new ViewPagerAdapter(fragmentsList, new String[]{"活动", "趣事", "秀场", "圈子"}));
        indicator.setViewPager(viewPager);
        fragmentsList.get(0).initDate();
    }

    @Override
    public void onResume() {
        SharedPreferences preferences = context.getSharedPreferences("urlfaxia", Context.MODE_PRIVATE);
        boolean isFaxia = preferences.getBoolean("isFaxia", false);
        if (isFaxia) {
            preferences.edit().clear().apply();
            viewPager.setCurrentItem(1);
        }
        super.onResume();
    }

    @Override
    public void onClick(View arg0) {
        if (!customApplication.networkIsAvailable()) {
            getMyToast("你已经断开网络！").show();
            return;
        }
        if (customApplication.getUserinfo() == null) {
            getMyToast("你没有登陆，请登陆后操作！").show();
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, SelectImgActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        if (fragmentsList != null) {
            fragmentsList.clear();
            fragmentsList = null;
        }
        super.onDestroy();
    }
}
