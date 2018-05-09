package cn.iliker.mall.storemodule.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import cn.iliker.mall.R;
import iliker.entity.StoreInfo;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

public class StorePicHolder extends BaseHolder<StoreInfo> {
    private ImageView[] ivs;

    public StorePicHolder(Context context) {
        super(context);
    }

    @Override
    public View initViews() {
        View view = View.inflate(context, R.layout.store_screen, null);
        ivs = new ImageView[5];
        ivs[0] = (ImageView) view.findViewById(R.id.screen_1);
        ivs[1] = (ImageView) view.findViewById(R.id.screen_2);
        ivs[2] = (ImageView) view.findViewById(R.id.screen_3);
        ivs[3] = (ImageView) view.findViewById(R.id.screen_4);
        ivs[4] = (ImageView) view.findViewById(R.id.screen_5);
        return view;
    }

    @Override
    public void refreshView(StoreInfo datas) {
        String[] storeIcons = datas.getFaceIcon().split("#");
        for (int i = 0; i < 5; i++) {
            if (i < storeIcons.length) {
                ivs[i].setVisibility(View.VISIBLE);
                bitmapUtils.bind(ivs[i], GeneralUtil.STOREICON + storeIcons[i]);
            } else {
                ivs[i].setVisibility(View.GONE);
            }
        }
    }
}
