package cn.iliker.mall.storemodule;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.entity.MyOrderInfo;
import iliker.entity.StoreInfo;
import iliker.utils.DisplayUtils;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.utils.HttpHelp.getHttpUtils;

public class UpInStoresActivity extends AppCompatActivity implements View.OnClickListener {
    private int orderID;
    private ProgressBar progressBar;
    private ListView orderLv;
    private TextView orderId, tPhone, tNickName, point, totalPrice, realPayment, orderState, service_fee;
    private Button bCash, bPhysical;
    private Dialog dialog;
    private double amount;
    private MyOrderInfo orderInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upinstore_layout);
        dialog = DialogFactory.initDialog(this);
        orderID = getIntent().getIntExtra("orderID", 0);
        findViews();
        setListener();
        initData();
    }

    private void findViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        orderLv = (ListView) findViewById(R.id.orderLv);
        orderId = (TextView) findViewById(R.id.orderId);
        orderState = (TextView) findViewById(R.id.orderState);
        point = (TextView) findViewById(R.id.point);
        totalPrice = (TextView) findViewById(R.id.totalPrice);
        realPayment = (TextView) findViewById(R.id.realPayment);
        service_fee = (TextView) findViewById(R.id.service_fee);
        bCash = (Button) findViewById(R.id.cash_clearing);
        bPhysical = (Button) findViewById(R.id.physical_clearing);
        tPhone = (TextView) findViewById(R.id.phone);
        tNickName = (TextView) findViewById(R.id.nickName);
    }

    private void initData() {
        RequestParams params = new RequestParams(GeneralUtil.PARSORDERBYIDUNPACKVIEW);
        params.addBodyParameter("id", orderID + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressBar.setVisibility(View.GONE);
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBooleanValue("success")) {
                    orderInfo = JSON.parseObject(jsonObject.getJSONObject("orderInfo").toJSONString(), MyOrderInfo.class);
                    setData();
                } else {
                    ToastFactory.getMyToast("找不到订单").show();
                    finish();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void setData() {
        orderId.setText(orderInfo.getOrderid());
        tPhone.setText(orderInfo.getPhone());
        tNickName.setText(orderInfo.getUser_nickName());
        point.setText(orderInfo.getPoint());
        totalPrice.setText(String.valueOf("￥" + orderInfo.getTotalPrice()));
        amount = orderInfo.getTotalPrice();
        realPayment.setText(String.valueOf("￥" + amount));
        orderState.setText(orderInfo.getOrderstate());
        orderLv.setAdapter(new GoodsAdapter(orderInfo.getOrderItems(), this));
        DisplayUtils.setListViewHeight(orderLv);
        service_fee.setText(String.valueOf("￥" + orderInfo.getService_fee()));
    }

    private void setListener() {
        bCash.setOnClickListener(this);
        bPhysical.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dialog.show();
        StoreInfo storeInfo = CustomApplication.customApplication.getStoreInfo();
        RequestParams params = new RequestParams(GeneralUtil.ADDUNPACKORDER);
        params.addBodyParameter("unpackOrder.unpackOrderId.orderInfo.id",orderInfo.getId()+"" );
        params.addBodyParameter("unpackOrder.unpackOrderId.storeInfo.id",storeInfo.getId() + "");
        if (v.getId() == R.id.cash_clearing) {
            params.addBodyParameter("unpackOrder.clearingType", "CASH");
        } else params.addBodyParameter("unpackOrder.clearingType", "SPOT");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                dialog.dismiss();
                if ("success".equals(s)) {
                    ToastFactory.getMyToast("转单成功").show();
                    finish();
                } else ToastFactory.getMyToast("系统异常").show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dialog.dismiss();
                ToastFactory.getMyToast("操作未完成").show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
