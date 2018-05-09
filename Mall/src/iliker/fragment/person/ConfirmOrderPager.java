package iliker.fragment.person;

import android.content.Context;
import iliker.fragment.person.shopadapter.ConfirmAdapter;
import iliker.utils.GeneralUtil;
import org.xutils.http.RequestParams;

class ConfirmOrderPager extends BaseShopPager {

    ConfirmOrderPager(Context context) {
        super(context);
    }

    @Override
    public void initData(boolean isRefresh) {
        RequestParams requestParams = new RequestParams(GeneralUtil.FINDORDERSVC);
        requestParams.addBodyParameter("propertyName", "phone");
        requestParams.addBodyParameter("queryVal", customApplication.getUserinfo().getPhone());
        requestParams.addBodyParameter("propertyName2", "orderstate");
        requestParams.addBodyParameter("queryVal2", "已收货");
        requestParams.addBodyParameter("offset", index + "");
        getDataFromServer(isRefresh, requestParams, ConfirmAdapter.class);
    }
}
