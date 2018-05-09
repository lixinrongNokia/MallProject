package iliker.mall.depth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.RadioGroup;

import cn.iliker.mall.R;

class ConfirmPager5 extends ConfirmPager implements RadioGroup.OnCheckedChangeListener {

    ConfirmPager5(Activity activity) {
        super(activity);
    }

    private Bitmap bm;

    @Override
    public void initData() {
        prompt.setText("大小乳是指两个乳房还均匀，大小不一致；这是普遍现象");
        ask.setText("你是大小乳？");
        if (bm == null)
            bm = ImageUtil.getCompressedImgPath(activity.getResources(), R.drawable.dx);
        confirmimg.setImageBitmap(bm);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
