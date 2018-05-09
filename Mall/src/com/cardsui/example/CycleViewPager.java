package com.cardsui.example;

import android.content.Context;

import iliker.utils.GeneralUtil;

/**
 * 实现可循环，可轮播的viewpager
 */
public class CycleViewPager extends BaseCycleViewPager {
    @Override
    public void initData() {
        time = 8000;
        isCycle = true;
        isWheel = true;
        /*imageUrls = new String[]{
                GeneralUtil.GETTYPEIMG + "/womenstore.jpg", GeneralUtil.GETTYPEIMG + "/preferential.jpg"};*/
        ADInfo info = new ADInfo();
        info.setUrl(GeneralUtil.GETTYPEIMG + "/womenstore.jpg");
        info.setContent("https://iliker888.taobao.com/?spm=a1z10.1-c.w4067-12039971308.2.JAvLGE&scene=taobao_shop");
        ADInfo info2 = new ADInfo();
        info2.setUrl(GeneralUtil.GETTYPEIMG + "/preferential.jpg");
        info2.setContent("https://item.taobao.com/item.htm?spm=a1z10.1-c.w9889560-11887127033.1.XJqp8d&id=521778412599&scm=1007.10115.10897.100200300000000&pvid=8d6c25d4-a43b-4b84-81b0-03c36005fe83");
        infos.add(info);
        infos.add(info2);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initData();
    }
}