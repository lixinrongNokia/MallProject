package iliker.fragment.finding;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private final ArrayList<BaseFindPager> pagers;
    private final String[] strList;

    public ViewPagerAdapter(ArrayList<BaseFindPager> pagers,String[] strList) {
        this.pagers = pagers;
        this.strList=strList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return strList[position];
    }

    @Override
    public int getCount() {
        return pagers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view==o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pagers.get(position).view);
        return pagers.get(position).view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
