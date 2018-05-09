package com.cardsui.example;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.fragment.type.CheckStandActivity;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.BitmapHelp.getImageOptions;
import static iliker.utils.HttpHelp.getHttpUtils;

public class PrepaidcardActivity extends BaseStoreActivity implements View.OnClickListener {
    private ADInfo info;
    private ImageView url;
    private TextView product_price;
    private TextView product_info;
    private Button buy;
    private Dialog dialog;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initMyViews() {
        title.setText("预付卡购买");
        View view = View.inflate(this, R.layout.prepaidcard_layout, null);
        findChlidViews(view);
        dialog = DialogFactory.initDialog(this);
        initData();
        setListener();
        storeContent.addView(view);
    }

    private void findChlidViews(View view) {
        this.url = (ImageView) view.findViewById(R.id.url);
        this.product_price = (TextView) view.findViewById(R.id.product_price);
        this.product_info = (TextView) view.findViewById(R.id.product_info);
        this.buy = (Button) view.findViewById(R.id.buy);
        if (userInfo == null) {
            buy.setText("没登陆");
            buy.setEnabled(false);
        } else {
            buy.setText("购买");
            buy.setEnabled(true);
        }
    }

    private void initData() {
        info = (ADInfo) getIntent().getSerializableExtra("info");
        getBitmapUtils().bind(url, info.getUrl(), getImageOptions());
        product_info.setText(info.getContent());
        product_price.setText(String.valueOf(info.getPrice()));
    }

    private void setListener() {
        buy.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dialog.show();
        buyAction();
    }

    private void buyAction() {
        buy.setEnabled(false);
        RequestParams re = new RequestParams(GeneralUtil.PREPAIDCARDPAY);
        re.addBodyParameter("denomination", info.getPrice() + "");
        re.addBodyParameter("voucher_value", info.getVoucher_value() + "");
        re.addBodyParameter("phone", userInfo.getPhone());
        String url = info.getUrl();
        re.addBodyParameter("cardUrl", url.substring(url.lastIndexOf("/") + 1, url.length()));
        getHttpUtils().post(re, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                if (!"0".equals(result)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("out_trade_no", result);// 订单号
                    bundle.putString("subject", "预付卡");// 商品名
                    bundle.putString("body", "iliker buy");// 商品详情
                    bundle.putString("goods_type", "0");
                    bundle.putDouble("total_fee", info.getPrice());// 总金额
                    if (result.length() > 0) {
                        Intent intent = new Intent(PrepaidcardActivity.this,
                                CheckStandActivity.class);
                        intent.putExtra("shopdata", bundle);
                        startActivity(intent);
                    }
                } else {
                    ToastFactory.getMyToast("出了点问题!").show();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dialog.dismiss();
                buy.setEnabled(true);
                ToastFactory.getMyToast("网络错误").show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                dialog.dismiss();
                buy.setEnabled(true);
            }
        });
    }
}
