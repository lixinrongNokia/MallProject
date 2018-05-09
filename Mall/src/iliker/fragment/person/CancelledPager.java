package iliker.fragment.person;

import android.content.Context;

import iliker.fragment.person.shopadapter.CancelledAdapter;
import iliker.utils.GeneralUtil;

import org.xutils.http.RequestParams;

class CancelledPager extends BaseShopPager {

    CancelledPager(Context context) {
        super(context);
    }

    @Override
    public void initData(boolean isRefresh) {
        RequestParams requestParams = new RequestParams(GeneralUtil.FINDORDERSVC);
        requestParams.addBodyParameter("propertyName", "phone");
        requestParams.addBodyParameter("queryVal", customApplication.getUserinfo().getPhone());
        requestParams.addBodyParameter("propertyName2", "orderstate");
        requestParams.addBodyParameter("queryVal2", "已取消");
        requestParams.addBodyParameter("offset", index + "");
        getDataFromServer(isRefresh, requestParams, CancelledAdapter.class);
    }
}
