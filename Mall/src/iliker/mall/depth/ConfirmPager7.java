package iliker.mall.depth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.RadioGroup;

import cn.iliker.mall.R;

class ConfirmPager7 extends ConfirmPager implements RadioGroup.OnCheckedChangeListener {

    ConfirmPager7(Activity activity) {
        super(activity);
    }

    private Bitmap bm;

    @Override
    public void initData() {
        prompt.setText("女人的下胸围折痕形状类似胸罩钢圈。然而，有些女性也可具有非圆形的下胸围折痕");
        ask.setText("你有不规则的下胸围吗?");
        if (bm == null) {
            bm = ImageUtil.getCompressedImgPath(activity.getResources(), R.drawable.zh);
        }
        confirmimg.setImageBitmap(bm);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
