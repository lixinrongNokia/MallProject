package com.cardsui.example.goods;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.DialogFactory;
import com.iliker.application.CustomApplication;
import iliker.entity.Goods;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.fragment.type.ProductDetailActivity;
import iliker.utils.GeneralUtil;
import iliker.utils.ProUtils;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;

public class NewProductActivity extends BaseStoreActivity implements AdapterView.OnItemClickListener {
    private List<Goods> datas;// 数据
    private String url;
    private ListView view;
    private Dialog progressDialog;

    @Override
    protected void initMyViews() {
        //TODO 移除sql
        title.setText("最近新品");
        view = new ListView(this);
        view.setOnItemClickListener(this);
        progressDialog = DialogFactory.initDialog(this);
        storeContent.addView(view);
        if (CustomApplication.customApplication.networkIsAvailable()) {
            url = GeneralUtil.GETGOODS;
            progressDialog.show();
            asyncGetGoods("date_sub(curdate(), INTERVAL 120 DAY) <= date(`market_date`) and visible=1", "market_date desc");
        } else {
            String responseString = ProUtils.getCecheJson(NewProductActivity.this, "newproduct", url);
            if (!TextUtils.isEmpty(responseString)) {
                JSONObject jsonObject = JSONObject.parseObject(responseString);
                datas = JSON.parseArray(jsonObject.getJSONArray("dataSet").toJSONString(), Goods.class);
                ProUtils.cecheJson(NewProductActivity.this, "newproduct", url, responseString);
                view.setAdapter(new NewPDAdapter(datas, NewProductActivity.this));
            }
        }
    }

    private void asyncGetGoods(String where_clause, String order_clause) {
        RequestParams params = new RequestParams(url);
        if (where_clause != null) {
            params.addBodyParameter("where_clause", where_clause);
        }
        if (order_clause != null) {
            params.addBodyParameter("order_clause", order_clause);
        }
        params.addBodyParameter("offset", 1 + "");
        getHttpUtils().post(params,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String responseString) {
                        progressDialog.dismiss();
                        if (responseString != null) {
                            JSONObject jsonObject = JSON.parseObject(responseString);
                            datas = JSON.parseArray(jsonObject.getJSONArray("dataSet").toJSONString(), Goods.class);
                            ProUtils.cecheJson(NewProductActivity.this, "newproduct", url, responseString);
                            view.setAdapter(new NewPDAdapter(datas, NewProductActivity.this));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {
                        progressDialog.dismiss();
                    }
                });
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Goods good = datas.get(i);
        Intent intent = new Intent(this, ProductDetailActivity.class);// 跳到商品详情页面
        getDB().addHistory(good, sdf.format(new Date()));
        intent.putExtra("good", good);
        startActivity(intent);
    }
}
