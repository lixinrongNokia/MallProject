package iliker.fragment.person;

import android.content.Context;

import iliker.fragment.person.shopadapter.ForTheGoodsAdapter;
import iliker.utils.GeneralUtil;

import org.xutils.http.RequestParams;

class ForTheGoodsPager extends BaseShopPager {

    ForTheGoodsPager(Context context) {
        super(context);
    }

    @Override
    public void initData(boolean isRefresh) {
        RequestParams requestParams = new RequestParams(GeneralUtil.FINDORDERSVC);
        requestParams.addBodyParameter("propertyName", "phone");
        requestParams.addBodyParameter("queryVal", customApplication.getUserinfo().getPhone());
        requestParams.addBodyParameter("propertyName2", "paymentstate");
        requestParams.addBodyParameter("queryVal2", "1");
        requestParams.addBodyParameter("offset", index + "");
        getDataFromServer(isRefresh, requestParams, ForTheGoodsAdapter.class);
    }
}
