package com.cardsui.example.goods;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.cardsui.example.goods.clock.AlarmHelper;
import com.cardsui.example.goods.clock.ObjectPool;

import cn.iliker.mall.R;

public class FlashSaleActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {
    private BaseFlashSaleFragment baseFlashSale;
    private RemindFragment remindFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashsale_activity);
        ObjectPool.mAlarmHelper = new AlarmHelper(this);
        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        radiogroup.setOnCheckedChangeListener(this);
        fragmentManager = getSupportFragmentManager();
        radiogroup.check(R.id.checkflashsale);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (checkedId) {
            case R.id.checkflashsale:
                if (baseFlashSale == null) {
                    baseFlashSale = new BaseFlashSaleFragment();
                }
                fragmentTransaction.replace(R.id.flashcontent, baseFlashSale);
                break;
            case R.id.checkremind:
                if (remindFragment == null) {
                    remindFragment = new RemindFragment();
                }
                fragmentTransaction.replace(R.id.flashcontent, remindFragment);
                break;
        }
        fragmentTransaction.commit();
    }
}
