package iliker.mall.depth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.RadioGroup;

import cn.iliker.mall.R;

class ConfirmPager8 extends ConfirmPager implements RadioGroup.OnCheckedChangeListener {

    ConfirmPager8(Activity activity) {
        super(activity);
    }

    private Bitmap bm;

    @Override
    public void initData() {
        prompt.setText("鸡胸是指你的乳房之间的胸骨向外突出，您的前胸看起来高高突起。\n这个突起的有的很轻微，有的很明显");
        ask.setText("你有鸡胸吗？");
        if (bm == null)
            bm = ImageUtil.getCompressedImgPath(activity.getResources(), R.drawable.jx);
        confirmimg.setImageBitmap(bm);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
