package com.cardsui.example.goods;


import android.support.v4.app.Fragment;
import android.view.View;
import cn.iliker.mall.R;
import iliker.fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemindFragment extends BaseFragment {


    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.fragment_remind, null);
        }
        return view;
    }

}
