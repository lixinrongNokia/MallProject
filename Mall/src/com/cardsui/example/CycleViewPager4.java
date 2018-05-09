package com.cardsui.example;

import android.content.Context;

import iliker.utils.GeneralUtil;

/**
 * 实现可循环，可轮播的viewpager
 */
public class CycleViewPager4 extends BaseCycleViewPager {
    @Override
    public void initData() {
        time = 8000;
        isCycle = true;
        isWheel = true;
        ADInfo info = new ADInfo();
        info.setPrice(3000);
        info.setVoucher_value(4998);
        info.setId("level3");
        info.setUrl(GeneralUtil.GETTYPEIMG + "plan3.png");
        info.setContent("充值3000元，赠送1998元，实际到帐4998元\n7月活动：额外赠送价值438元亲子套装(母女文胸各一套)\n12月活动：额外赠送价值588元家庭睡衣3套");
        info.setType("Prepaidcard");
        ADInfo info2 = new ADInfo();
        info2.setPrice(2000);
        info2.setVoucher_value(3078);
        info2.setId("level2");
        info2.setUrl(GeneralUtil.GETTYPEIMG + "plan2.png");
        info2.setContent("充值2000元，赠送1078元，实际到帐3078元\n7月活动：额外赠送价值388元亲子套装(大人小孩各一)\n12月活动：额外赠送价值198元保暖衣一套");
        info2.setType("Prepaidcard");
        ADInfo info3 = new ADInfo();
        info3.setPrice(1000);
        info3.setVoucher_value(1428);
        info3.setId("level1");
        info3.setUrl(GeneralUtil.GETTYPEIMG + "plan1.png");
        info3.setContent("充值1000元，赠送428元，实际到帐1428元\n7月活动：额外赠送价值208文胸一套\n12月活动：额外赠送价值198元保暖衣一套");
        info3.setType("Prepaidcard");
        infos.add(info);
        infos.add(info2);
        infos.add(info3);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initData();
    }
}