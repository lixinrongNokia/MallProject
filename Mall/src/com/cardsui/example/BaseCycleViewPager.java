package com.cardsui.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

import cn.iliker.mall.R;
import iliker.fragment.home.LinkActivity;

import static iliker.utils.ViewUtils.removeParent;

/**
 * 实现可循环，可轮播的viewpager
 */
public abstract class BaseCycleViewPager extends Fragment implements OnPageChangeListener, ImageCycleViewListener {
    private View view;
    private int showPosition = 0;
    private final List<ImageView> imageViews = new ArrayList<>();
    private ImageView[] indicators;
    private FrameLayout viewPagerFragmentLayout;
    private LinearLayout indicatorLayout; // 指示器
    private BaseViewPager viewPager;
    int time = 5000; // 默认轮播时间
    private int currentPosition = 0; // 轮播当前位置
    private boolean isScrolling = false; // 滚动框是否滚动着
    boolean isCycle = true; // 是否循环
    boolean isWheel = true; // 是否轮播
    private long releaseTime = 0; // 手指松开、页面不滚动时间，防止手机松开后短时间进行切换
    private final int WHEEL = 100; // 转动
    private final int WHEEL_WAIT = 101; // 等待
    final List<ADInfo> infos = new ArrayList<>();
    private Activity activity;

    private final CycleViewPagerHandler handler = new CycleViewPagerHandler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHEEL && imageViews.size() != 0) {
                if (!isScrolling) {
                    int max = imageViews.size() + 1;
                    int position = (currentPosition + 1) % imageViews.size();
                    viewPager.setCurrentItem(position, true);
                    if (position == max) { // 最后一页时回到第一页
                        viewPager.setCurrentItem(1, false);
                    }
                }

                releaseTime = System.currentTimeMillis();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, time);
                return;
            }
            if (msg.what == WHEEL_WAIT && imageViews.size() != 0) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, time);
            }
        }
    };


    public abstract void initData();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.view_cycle_viewpager_contet, null);
            viewPager = (BaseViewPager) view.findViewById(R.id.viewPager);
            indicatorLayout = (LinearLayout) view
                    .findViewById(R.id.layout_viewpager_indicator);
            viewPagerFragmentLayout = (FrameLayout) view
                    .findViewById(R.id.layout_viewager_content);
        }
        removeParent(view);
        viewPager.setScrollable(true);
        configImageLoader();
        initialize();
        setData();
        handler.postDelayed(runnable, time);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    private void initialize() {
        // 将最后一个ImageView添加进来
        if (isWheel) {
            imageViews.add(ViewFactory.getImageView(activity,
                    infos.get(infos.size() - 1).getUrl()));
            for (ADInfo adInfo : infos) {
                imageViews.add(ViewFactory.getImageView(activity, adInfo.getUrl()));
            }
        }
        // 将第一个ImageView添加进来
        imageViews.add(ViewFactory
                .getImageView(activity, infos.get(0).getUrl()));
    }

    /**
     * 配置ImageLoder
     */
    private void configImageLoader() {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                activity).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 初始化viewpager
     */
    private void setData() {
        if (imageViews.size() == 0) {
            viewPagerFragmentLayout.setVisibility(View.GONE);
            return;
        }

        int ivSize = imageViews.size();

        // 设置指示器
        indicators = new ImageView[ivSize];
        if (isCycle)
            indicators = new ImageView[ivSize - 2];
        indicatorLayout.removeAllViews();
        for (int i = 0; i < indicators.length; i++) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.view_cycle_viewpager_indicator, null);
            indicators[i] = (ImageView) view.findViewById(R.id.image_indicator);
            indicatorLayout.addView(view);
        }
        // 默认指向第一项，下方viewPager.setCurrentItem将触发重新计算指示器指向
        setIndicator(0);

        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(new ViewPagerAdapter());
        if (showPosition < 0 || showPosition >= imageViews.size())
            showPosition = 0;
        if (isCycle) {
            showPosition = showPosition + 1;
        }
        viewPager.setCurrentItem(showPosition);

    }

    /**
     * 设置指示器居中，默认指示器在右方
     */
    /*public void setIndicatorCenter() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        indicatorLayout.setLayoutParams(params);
    }*/

    private final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (getActivity() != null && !getActivity().isFinishing()
                    && isWheel) {
                long now = System.currentTimeMillis();
                // 检测上一次滑动时间与本次之间是否有触击(手滑动)操作，有的话等待下次轮播
                if (now - releaseTime > time - 500) {
                    handler.sendEmptyMessage(WHEEL);
                } else {
                    handler.sendEmptyMessage(WHEEL_WAIT);
                }
            }
        }
    };

    /**
     * 释放指示器高度，可能由于之前指示器被限制了高度，此处释放
     */
   /* public void releaseHeight() {
        View view = getView();
        if (view != null) {
            view.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
            refreshData();
        }
    }*/

    /**
     * 刷新数据，当外部视图更新后，通知刷新数据
     */
   /* private void refreshData() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }*/

    /**
     * 隐藏CycleViewPager
     */
   /* public void hide() {
        viewPagerFragmentLayout.setVisibility(View.GONE);
    }*/

    /**
     * 返回内置的viewpager
     *
     * @return viewPager
     */
    public BaseViewPager getViewPager() {
        return viewPager;
    }

    /**
     * 页面适配器 返回对应的view
     *
     * @author Yuedong Li
     */
    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            ImageView v = imageViews.get(position);
            v.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    BaseCycleViewPager.this.onImageClick(
                            infos.get(isWheel ? currentPosition - 1 : currentPosition));
                }
            });
            container.addView(v);
            return v;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (arg0 == 1) { // viewPager在滚动
            isScrolling = true;
            return;
        } else if (arg0 == 0) { // viewPager滚动结束
            if (viewPager != null)
                viewPager.setScrollable(true);

            releaseTime = System.currentTimeMillis();

            viewPager.setCurrentItem(currentPosition, false);

        }
        isScrolling = false;
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        int max = imageViews.size() - 1;
        int position = arg0;
        currentPosition = arg0;
        if (isCycle) {
            if (arg0 == 0) {
                currentPosition = max - 1;
            } else if (arg0 == max) {
                currentPosition = 1;
            }
            position = currentPosition - 1;
        }
        setIndicator(position);
    }

    /**
     * 设置指示器
     *
     * @param selectedPosition 默认指示器位置
     */
    private void setIndicator(int selectedPosition) {
        for (ImageView imageView : indicators) {
            imageView.setBackgroundResource(R.drawable.icon_point);
        }
        if (indicators.length > selectedPosition)
            indicators[selectedPosition]
                    .setBackgroundResource(R.drawable.icon_point_pre);
    }

    @Override
    public void onImageClick(ADInfo info) {
        if ("Prepaidcard".equals(info.getType())) {
            Intent prepaidcardintent = new Intent(activity, PrepaidcardActivity.class);
            prepaidcardintent.putExtra("info", info);
            activity.startActivity(prepaidcardintent);
        } else {
            Intent intent = new Intent(activity,
                    LinkActivity.class);
            intent.putExtra("openHref", info.getContent());
            activity.startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}