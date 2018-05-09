package com.cardsui.example.goods;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class BaseFlashSaleFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private HorizontalScrollView scrollView;
    private LinearLayout titleLayout;
    private ArrayList<Integer> moveToList;
    private int mTitleMargin;

    private ArrayList<BasePager> pagerArrayList;
    private ArrayList<TextView> textViewList;
    private ArrayList<String> titleList;
    private ContentAdapter mAdapter;

    private int currentPos = 0;
    private String[] strList;
    private final int[] idList = new int[]{0, 1, 2, 3};

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.flashsale, null);
            mTitleMargin = dip2px(context, 10);
            initView();
            loadFragments();
        }
        return view;
    }

    @Override
    public void onResume() {
        initData();
        if (mAdapter == null) {
            mAdapter = new ContentAdapter();
            viewPager.setAdapter(mAdapter);
            viewPager.setOffscreenPageLimit(9);
        }
        textViewList.get(currentPos).setTextColor(Color.rgb(255, 0, 0));
        super.onResume();
    }

    private void initView() {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(this);
        scrollView = (HorizontalScrollView) view.findViewById(R.id.scrollView);
        titleLayout = (LinearLayout) view.findViewById(R.id.titleLayout);
    }

    private void loadFragments() {
        pagerArrayList = new ArrayList<>();
        FalshSale_Item1 fixme = new FalshSale_Item1(context);
        FalshSale_Item2 fixme1 = new FalshSale_Item2(context);
        FalshSale_Item3 fixme2 = new FalshSale_Item3(context);
        FalshSale_Item4 fixme3 = new FalshSale_Item4(context);
        pagerArrayList.add(0, fixme);
        pagerArrayList.add(1, fixme1);
        pagerArrayList.add(2, fixme2);
        pagerArrayList.add(3, fixme3);
    }

    private void initData() {
        Calendar calendar = Calendar.getInstance();// 日期对象
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        if (strList != null) {
            strList = null;
        }
        if (time >= 0 && time < 12) {
            strList = new String[]{"08:00", "12:00", "16:00", "20:00"};
        } else if (time >= 12 && time < 16) {
            strList = new String[]{"12:00", "16:00", "20:00", "08:00"};

        } else if (time >= 16 && time < 20) {
            strList = new String[]{"16:00", "20:00", "08:00", "12:00"};

        } else if (time >= 20 && time < 24) {
            strList = new String[]{"20:00", "08:00", "12:00", "16:00"};
        }
        titleList = new ArrayList<>();
        textViewList = new ArrayList<>();
        moveToList = new ArrayList<>();
        for (int i = 0; i < strList.length; i++) {
            titleList.add(strList[i]);
            addTitleLayout(titleList.get(i), idList[i]);
        }
    }

    private void addTitleLayout(String title, int position) {
        final TextView textView = (TextView) View.inflate(context, R.layout.find_title, null);
        textView.setText(title);
        textView.setTag(position);
        textView.setOnClickListener(new posOnClickListener());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dip2px(context, mTitleMargin);
        params.rightMargin = dip2px(context, mTitleMargin);
        titleLayout.addView(textView, params);
        textViewList.add(textView);
        int width;
        if (position == 0) {
            width = 0;
            moveToList.add(width);
        } else {
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            textViewList.get(position - 1).measure(w, h);
            width = textViewList.get(position - 1).getMeasuredWidth() + mTitleMargin * 4;
            moveToList.add(width + moveToList.get(moveToList.size() - 1));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        pagerArrayList.get(0).initData(false);
    }

    @Override
    public void onPageSelected(int position) {
        textViewList.get(currentPos).setTextColor(Color.rgb(0, 0, 0));
        textViewList.get(position).setTextColor(Color.rgb(255, 0, 0));
        currentPos = position;
        scrollView.scrollTo(moveToList.get(position), 0);
        pagerArrayList.get(position).initData(false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private class posOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if ((int) view.getTag() == currentPos) {
                return;
            }
            textViewList.get(currentPos).setTextColor(Color.rgb(0, 0, 0));
            currentPos = (int) view.getTag();
            textViewList.get(currentPos).setTextColor(Color.rgb(255, 0, 0));
            viewPager.setCurrentItem(currentPos);
        }
    }

    @Override
    public void onStop() {
        if (titleList != null) {
            titleList.clear();
        }
        if (textViewList != null) {
            textViewList.clear();
        }
        titleLayout.removeAllViews();
        super.onStop();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private class ContentAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pagerArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pagerArrayList.get(position).view);
            return pagerArrayList.get(position).view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


}
