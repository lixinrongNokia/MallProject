package iliker.mall.depth;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.RadioGroup;

import cn.iliker.mall.R;


class ConfirmPager6 extends ConfirmPager implements RadioGroup.OnCheckedChangeListener {

    ConfirmPager6(Activity activity) {
        super(activity);
    }

    private Bitmap bm;

    @Override
    public void initData() {
        prompt.setText("大肚腩是指肚皮在两乳之间高高的突起\n这在超重女性中是很常见的,特别是矮小的的超重女性");
        ask.setText("你有突起很高的肚腩吗?");
        if (bm == null) {
            bm = ImageUtil.getCompressedImgPath(activity.getResources(), R.drawable.ddl);
        }
        confirmimg.setImageBitmap(bm);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }
}
