package com.cardsui.example;

import android.content.Context;

import iliker.utils.GeneralUtil;

/**
 * 实现可循环，可轮播的viewpager
 */
public class CycleViewPager2 extends BaseCycleViewPager {
    @Override
    public void initData() {
        isWheel = false;
        isCycle = false;
        ADInfo adInfo = new ADInfo();
        adInfo.setUrl(GeneralUtil.GETTYPEIMG + "/manstore.jpg");
        adInfo.setContent("https://iliker-man.taobao.com/?spm=a1z10.1-c.w10145688-12739579300.1.jgYXzL");
        infos.add(adInfo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initData();
    }
}