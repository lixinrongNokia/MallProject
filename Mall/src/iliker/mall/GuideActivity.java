package iliker.mall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.iliker.mall.R;
import iliker.utils.GetBitMapUtil;

/**
 * 引导页面
 */
public class GuideActivity extends Activity {

    /* 1、viewpage控件 */
    private ViewPager viewpage;
    private TextView showtime;
    private List<View> list;
    private static int recLen = 3;
    private final MyHandler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            GuideActivity activity = (GuideActivity) reference.get();
            if (activity != null) {

                switch (msg.what) {
                    case 1:
                        recLen--;
                        activity.showtime.setText(String.valueOf("倒计时:" + recLen));
                        if (recLen > 0) {
                            Message message = activity.handler.obtainMessage(1);
                            activity.handler.sendMessageDelayed(message, 1000); // send
                            // message
                        } else {
                            activity.showtime.setVisibility(View.GONE);
                            activity.handler.removeMessages(1);
                            activity.startActivity(new Intent(activity,
                                    SelectSexActivity.class));
                            activity.finish();
                            activity.overridePendingTransition(R.anim.enteralpha,
                                    R.anim.exitalpha);
                        }
                }

            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        findViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initdata();
    }

    private void initdata() {
        /* 3、适配 */
        viewpage.setAdapter(new GuideAdapter(getData()));
    }

    private void findViews() {
        viewpage = (ViewPager) findViewById(R.id.viewpage);
        showtime = (TextView) findViewById(R.id.showtime);
    }

    private void setListeners() {
        // 页面改变事件
        viewpage.addOnPageChangeListener(new OnPageChangeListener() {

            public void onPageScrollStateChanged(int arg0) {

            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            /* 选中当前页面 */
            public void onPageSelected(int arg0) {
                /* 最后一个页面 */
                if (arg0 == list.size() - 1) {
                    showtime.setVisibility(View.VISIBLE);
                    Message message = handler.obtainMessage(1); // Message
                    handler.sendMessageDelayed(message, 1000);
                } else {
                    handler.removeMessages(1);
                    recLen = 3;
                    showtime.setText(null);
                    showtime.setVisibility(View.GONE);
                }
            }

        });
    }

    /* 2、数据 */
    @SuppressLint("InflateParams")
    private List<View> getData() {
        list = new ArrayList<>();
        Bitmap bitmap1 = GetBitMapUtil.compression(this, R.drawable.img1);
        Bitmap bitmap2 = GetBitMapUtil.compression(this, R.drawable.img2);
        Bitmap bitmap3 = GetBitMapUtil.compression(this, R.drawable.img3);
        ImageView image1 = new ImageView(this);
        image1.setImageBitmap(bitmap1);
        image1.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView image2 = new ImageView(this);
        image2.setImageBitmap(bitmap2);
        image2.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView image3 = new ImageView(this);
        image3.setImageBitmap(bitmap3);
        image3.setScaleType(ImageView.ScaleType.FIT_XY);
        list.add(image1);
        list.add(image2);
        list.add(image3);
        return list;
    }

    @Override
    protected void onDestroy() {
        list.clear();
        list = null;
        handler.removeCallbacksAndMessages(null);
        showtime.setText(null);
        super.onDestroy();
    }
}
