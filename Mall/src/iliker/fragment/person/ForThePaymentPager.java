package iliker.fragment.person;

import android.content.Context;

import org.xutils.http.RequestParams;

import iliker.fragment.person.shopadapter.ForThePaymentAdapter;
import iliker.utils.GeneralUtil;


class ForThePaymentPager extends BaseShopPager {

    ForThePaymentPager(Context context) {
        super(context);
    }

    @Override
    public void initData(boolean isRefresh) {
        RequestParams requestParams = new RequestParams(GeneralUtil.FINDORDERSVC);
        requestParams.addBodyParameter("propertyName", "phone");
        requestParams.addBodyParameter("queryVal", customApplication.getUserinfo().getPhone());
        requestParams.addBodyParameter("propertyName2", "orderstate");
        requestParams.addBodyParameter("queryVal2", "等待付款");
        requestParams.addBodyParameter("offset", index + "");
        getDataFromServer(isRefresh, requestParams, ForThePaymentAdapter.class);
    }
}
