package com.cardsui.example.goods;

import android.content.Context;
import iliker.utils.GeneralUtil;
import org.xutils.http.RequestParams;

class FalshSale_Item1 extends BasePager {
    FalshSale_Item1(Context context) {
        super(context);
    }

    @Override
    public void initData(boolean isRefresh) {
        RequestParams params = new RequestParams(GeneralUtil.GETTIMEGOODS);
        params.addBodyParameter("starttime", 8 + "");
        params.addBodyParameter("offset", pageIndex + "");
        params.addBodyParameter("count", 20 + "");
        asyncGetGoods(isRefresh, params);
    }
}
