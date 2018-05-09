package cn.iliker.mall.storemodule.fragments;

import android.view.View;
import cn.iliker.mall.R;
import iliker.fragment.BaseFragment;

public class MarketFragment extends BaseFragment {
    //todo
    @Override
    protected View initSubclassView() {
        view = View.inflate(context, R.layout.goddesslist_layout, null);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {

    }
}
