package iliker.mall.depth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.RadioGroup;

import cn.iliker.mall.R;

class ConfirmPager9 extends ConfirmPager implements RadioGroup.OnCheckedChangeListener {

    ConfirmPager9(Activity activity) {
        super(activity);
    }

    private Bitmap bm;

    @Override
    public void initData() {
        prompt.setText("胳膊之下有松弛的皮肤皱纹形成的赘肉。\n老年妇女和有过巨大减肥后的女性常见，\n这种症状在年轻及体重均匀的女性中不多见");
        ask.setText("你有腋下赘肉吗？");
        if (bm == null)
            bm = ImageUtil.getCompressedImgPath(activity.getResources(), R.drawable.zr);
        confirmimg.setImageBitmap(bm);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
