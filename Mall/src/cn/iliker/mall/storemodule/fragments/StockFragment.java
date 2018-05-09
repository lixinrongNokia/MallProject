package cn.iliker.mall.storemodule.fragments;

import android.view.View;
import cn.iliker.mall.R;
import iliker.fragment.BaseFragment;

/*库存*/
public class StockFragment extends BaseFragment {
    @Override
    protected View initSubclassView() {
        view = View.inflate(context, R.layout.stock_fragmeg_layout, null);
        return view;
    }
}
