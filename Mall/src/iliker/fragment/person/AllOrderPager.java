package iliker.fragment.person;

import android.content.Context;

import org.xutils.http.RequestParams;

import iliker.fragment.person.shopadapter.AllOrderAdapter;
import iliker.utils.GeneralUtil;

class AllOrderPager extends BaseShopPager {

    AllOrderPager(Context context) {
        super(context);
    }

    @Override
    public void initData(boolean isRefresh) {
        RequestParams requestParams = new RequestParams(GeneralUtil.FINDORDERSVC);
        requestParams.addBodyParameter("propertyName", "phone");
        requestParams.addBodyParameter("queryVal", customApplication.getUserinfo().getPhone());
        requestParams.addBodyParameter("offset", index + "");
        getDataFromServer(isRefresh, requestParams, AllOrderAdapter.class);
    }

}
