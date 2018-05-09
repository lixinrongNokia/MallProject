package cn.iliker.mall.storemodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import iliker.entity.UnPackOrder;
import iliker.utils.DisplayUtils;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.utils.HttpHelp.getHttpUtils;

public class ViewUnPackOrderActivity extends AppCompatActivity {
    private TextView orderStatus, clearingType, amount, details;
    private ListView goodsLv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewunpackorder_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViews();
        UnPackOrder unPackOrder = getIntent().getParcelableExtra("unPackOrder");
        if (unPackOrder == null) {
            getUnPackOrderByID(getIntent().getStringExtra("unPackOrderID"));
        } else {
            initView(unPackOrder);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUnPackOrderByID(String unPackOrderID) {
        RequestParams params = new RequestParams(GeneralUtil.GETUNPACKORDERBYIDSVC + "?unPackOrderId=" + unPackOrderID);
        getHttpUtils().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                JSONObject jsonObject = JSON.parseObject(s);
                if (jsonObject.getBooleanValue("success")) {
                    UnPackOrder unPackOrder = JSON.parseObject(jsonObject.getJSONObject("unPackOrder").toJSONString(), UnPackOrder.class);
                    initView(unPackOrder);
                } else {
                    ToastFactory.getMyToast("没有东西").show();
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

    private void findViews() {
        orderStatus = (TextView) findViewById(R.id.orderStatus);
        amount = (TextView) findViewById(R.id.amount);
        clearingType = (TextView) findViewById(R.id.clearingType);
        details = (TextView) findViewById(R.id.details);
        goodsLv = (ListView) findViewById(R.id.goodsLv);
    }

    private void initView(UnPackOrder unPackOrder) {
        if (unPackOrder != null) {
            if (unPackOrder.getUser_confirm()) {
                orderStatus.setText("订单已确认");
            } else {
                orderStatus.setText("订单未经客户确认");
                details.setVisibility(View.INVISIBLE);
            }
            amount.setText(String.valueOf("￥" + unPackOrder.getTotalPrice()));
            goodsLv.setAdapter(new GoodsAdapter(unPackOrder.getOrderItems(), this));
            DisplayUtils.setListViewHeight(goodsLv);
            clearingType.setText(unPackOrder.getClearingType());
        }
    }
}
