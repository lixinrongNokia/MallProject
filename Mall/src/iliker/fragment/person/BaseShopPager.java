package iliker.fragment.person;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iliker.application.CustomApplication;
import iliker.adapter.DefaultAdapter;
import iliker.entity.WebOrder;
import iliker.fragment.person.shopadapter.*;
import me.maxwin.view.XListView;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static iliker.utils.HttpHelp.getHttpUtils;

abstract class BaseShopPager implements XListView.IXListViewListener {
    private final Context context;
    View rootView;
    private XListView orderidlv;
    final CustomApplication customApplication;
    private List<WebOrder> list;
    private DefaultAdapter defaultadapter;
    private RelativeLayout emptyorder;
    protected int index = 1;
    private int pageCount = 1;

    BaseShopPager(Context context) {
        this.context = context;
        customApplication = (CustomApplication) context.getApplicationContext();
        initViews();
    }

    private void initViews() {
        rootView = View.inflate(context, R.layout.order_layout, null);
        orderidlv = (XListView) rootView.findViewById(R.id.orderidlv);
        emptyorder = (RelativeLayout) rootView.findViewById(R.id.emptyorder);
        orderidlv.setXListViewListener(this);
    }

    public abstract void initData(boolean isRefresh);

    void getDataFromServer(final boolean isRefresh, RequestParams requestParams, final Class clazz) {

        getHttpUtils().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                List<WebOrder> webOrders = null;
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = JSON.parseObject(result);
                    pageCount = jsonObject.getInteger("pageCount");
                    webOrders = JSON.parseArray(jsonObject.getJSONArray("dataSet").toJSONString(), WebOrder.class);
                }
                if (!isRefresh) {
                    if (webOrders == null) {
                        orderidlv.setVisibility(View.GONE);
                        emptyorder.setVisibility(View.VISIBLE);
                    } else {
                        emptyorder.setVisibility(View.GONE);
                        orderidlv.setVisibility(View.VISIBLE);
                        list = webOrders;
                        try {
                            Object object = clazz.newInstance();
                            if (object instanceof ForThePaymentAdapter) {
                                defaultadapter = new ForThePaymentAdapter(context, list);
                            } else if (object instanceof ForTheGoodsAdapter) {
                                defaultadapter = new ForTheGoodsAdapter(context, list);
                            } else if (object instanceof ConfirmAdapter) {
                                defaultadapter = new ConfirmAdapter(context, list);
                            } else if (object instanceof CancelledAdapter) {
                                defaultadapter = new CancelledAdapter(context, list);
                            } else if (object instanceof AllOrderAdapter) {
                                defaultadapter = new AllOrderAdapter(context, list);
                            }

                        } catch (Exception e) {
                        }
                        if (pageCount > index) {
                            orderidlv.setPullLoadEnable(true);
                        } else {
                            orderidlv.setPullLoadEnable(false);
                        }
                        orderidlv.setAdapter(defaultadapter);
                    }

                } else {
                    if (webOrders == null) {
                        orderidlv.setPullLoadEnable(false);
                    } else {
                        list.addAll(webOrders);
                        defaultadapter.notifyDataSetChanged();
                        if (pageCount > index) {
                            orderidlv.setPullLoadEnable(true);
                        } else {
                            orderidlv.setPullLoadEnable(false);
                        }
                    }
                    onLoad();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onRefresh() {
        this.index = 1;
        if (list == null)
            list = new ArrayList<>();
        list.clear();
        initData(true);
    }

    @Override
    public void onLoadMore() {
        index++;
        initData(true);
    }

    /**
     * 停止顶部更新、停止底部加载,更新时间
     */
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

    private void onLoad() {
        orderidlv.stopRefresh();
        orderidlv.stopLoadMore();
        Date date = new Date(System.currentTimeMillis());
        String currentTime = sdf.format(date);
        orderidlv.setRefreshTime(currentTime);// 更新时间
    }
}
