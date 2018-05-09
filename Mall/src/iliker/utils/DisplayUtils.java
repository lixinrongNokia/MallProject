package iliker.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

public class DisplayUtils {

    public static int px2dip(float pxValue, Context context) {
        return (int) (pxValue
                / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int dip2px(float dipValue, Context context) {
        return (int) (dipValue
                * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 动态获取ListView的高度
     */
    public static void setListViewHeight(ListView lv) {
        Adapter la = lv.getAdapter();
        if (null == la) {
            return;
        }
        // calculate height of all items.
        int h = 0;
        final int cnt = la.getCount();
        for (int i = 0; i < cnt; i++) {
            View item = la.getView(i, null, lv);
            item.measure(0, 0);
            h += item.getMeasuredHeight();
        }
        // reset ListView height
        ViewGroup.LayoutParams lp = lv.getLayoutParams();
        lp.height = h + (lv.getDividerHeight() * (cnt - 1));
        lv.setLayoutParams(lp);
    }
}
