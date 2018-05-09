package iliker.mall.depth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.RadioGroup;

import cn.iliker.mall.R;

/**
 * Created by WDHTC on 2016/3/16.
 */
class ConfirmPager1 extends ConfirmPager implements RadioGroup.OnCheckedChangeListener {

    ConfirmPager1(Activity activity) {
        super(activity);
    }

    private Bitmap bm;

    @Override
    public void initData() {
        title.setText("你有超大的胸围吗");
        prompt.setText("当你的乳房的上半部分的乳腺组织明显大过乳房下围组织时，你就可以说你拥有一对豪乳。你的乳头的位置通常会低于你的乳房下围线");
        ask.setText("你有超大的胸围吗？");
        if (bm == null)
            bm = ImageUtil.getCompressedImgPath(activity.getResources(), R.drawable.cdxw);
        confirmimg.setImageBitmap(bm);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
