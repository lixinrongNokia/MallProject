package com.cardsui.example;

import android.content.Context;

import iliker.utils.GeneralUtil;

/**
 * 实现可循环，可轮播的viewpager
 */
public class CycleViewPager3 extends BaseCycleViewPager {
    @Override
    public void initData() {
        isWheel = false;
        isCycle = false;
        ADInfo adInfo = new ADInfo();
        adInfo.setUrl(GeneralUtil.GETTYPEIMG + "/preferential.jpg");
        adInfo.setContent("https://item.taobao.com/item.htm?spm=a1z10.1-c.w9889560-11887127033.1.42PDdt&id=521778412599&scm=1007.10115.10897.100200300000000&pvid=8d6c25d4-a43b-4b84-81b0-03c36005fe83");
        infos.add(adInfo);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initData();
    }
}