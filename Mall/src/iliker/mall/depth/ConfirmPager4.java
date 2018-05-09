package iliker.mall.depth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.RadioGroup;

import cn.iliker.mall.R;


class ConfirmPager4 extends ConfirmPager implements RadioGroup.OnCheckedChangeListener {

    ConfirmPager4(Activity activity) {
        super(activity);
    }

    private Bitmap bm;

    @Override
    public void initData() {
        prompt.setText("肩膀窄小或下斜是很多女人存在的现象");
        ask.setText("你是窄肩或美人肩吗？");
        if (bm == null)
            bm = ImageUtil.getCompressedImgPath(activity.getResources(), R.drawable.zj);
        confirmimg.setImageBitmap(bm);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
