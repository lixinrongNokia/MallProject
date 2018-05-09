package iliker.fragment.type;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.iliker.mall.R;
import iliker.utils.GeneralUtil;

import static iliker.utils.BitmapHelp.getBitmapUtils;

public class GoodsPDIMGActivity extends Activity {

    private static final String IMGPATHS = "imgpaths";
    public static final String CURRENT_INDEX = "currentIndex";
    private String[] imgpaths;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goodspdquery);
        this.imgpaths = this.getIntent().getStringArrayExtra(IMGPATHS);
        int index = this.getIntent().getIntExtra(CURRENT_INDEX, 0);
        findViews();
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(index);
    }

    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    private class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imgpaths.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            String url = GeneralUtil.GOODSPATH + imgpaths[position];
            View view = View.inflate(container.getContext(), R.layout.goodspdquery_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.good_img);
            getBitmapUtils().bind(imageView, url);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
