package iliker.mall.depth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.RadioGroup;

import cn.iliker.mall.R;

class ConfirmPager10 extends ConfirmPager implements RadioGroup.OnCheckedChangeListener {

    ConfirmPager10(Activity activity) {
        super(activity);
    }

    private Bitmap bm;

    @Override
    public void initData() {
        prompt.setText("乳房之间你的胸骨凹陷形成空洞。\n这种中空的凹槽有的轻微，有的很显著；明显的含胸是相当罕见的。\n有含胸现象多为骨架瘦小的女性。");
        ask.setText("你有含胸吗？");
        if (bm == null)
            bm = ImageUtil.getCompressedImgPath(activity.getResources(), R.drawable.hx);
        confirmimg.setImageBitmap(bm);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
