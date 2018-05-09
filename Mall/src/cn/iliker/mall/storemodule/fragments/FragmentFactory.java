package cn.iliker.mall.storemodule.fragments;

import iliker.fragment.BaseFragment;

import java.util.HashMap;
import java.util.Map;

public final class FragmentFactory {

    private static Map<Integer, BaseFragment> mFragments = new HashMap<>();

    public static BaseFragment createFragment(int position) {
        BaseFragment fragment;
        fragment = mFragments.get(position);
        if (fragment == null) {
            if (position == 0) {
                fragment = new StoreFragment();
            } else if (position == 1) {
                fragment = new StockFragment();
            } else if (position == 2) {
                fragment = new MarketFragment();
            }
            if (fragment != null) {
                mFragments.put(position, fragment);
            }
        }
        return fragment;
    }
}
