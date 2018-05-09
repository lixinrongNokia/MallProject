package com.cardsui.example.goods;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import iliker.entity.Goods;
import iliker.fragment.type.MyAdapter;
import iliker.fragment.type.ProductDetailActivity;
import iliker.holder.DefaultHolder;
import iliker.utils.GeneralUtil;
import iliker.utils.ProUtils;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * A simple {@link Fragment} subclass.NewProductHolder
 */
public class NewProductHolder extends DefaultHolder implements GridView.OnItemClickListener {
    private GridView staggeredGridView1;
    private MyAdapter myAdapter;
    private List<Goods> datas;// 数据
    private String url;
    private View view;

    public NewProductHolder(Context context) {
        super(context);
    }

    private void setListener() {
        staggeredGridView1.setOnItemClickListener(this);
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
                        if (responseString != null) {
                            JSONObject jsonObject = JSON.parseObject(responseString);
                            datas = JSON.parseArray(jsonObject.getJSONArray("dataSet").toJSONString(), Goods.class);
                            ProUtils.cecheJson(context, "newproductfragment", url, responseString);
                            staggeredGridView1.setAdapter(new MyAdapter(context, datas));
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!customApplication.networkIsAvailable()) {
            ToastFactory.getMyToast("没网络").show();
            return;
        }
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra("good", datas.get(position));
        context.startActivity(intent);
    }

    @Override
    public View initViews() {
        if (view == null) {
            view = View.inflate(context, R.layout.newproduct, null);
            url = GeneralUtil.GETGOODS;
            staggeredGridView1 = (GridView) view
                    .findViewById(R.id.staggeredGridView1);
        }

        if (customApplication.networkIsAvailable()) {
            asyncGetGoods("date_sub(curdate(), INTERVAL 120 DAY) <= date(`market_date`)", "market_date desc");
        } else {
            String json = ProUtils.getCecheJson(context, "newproductfragment", url);
            if (json != null) {
                JSONObject jsonObject = JSON.parseObject(json);
                datas = JSON.parseArray(jsonObject.getJSONArray("dataSet").toJSONString(), Goods.class);
                staggeredGridView1.setAdapter(new MyAdapter(context, datas));
            }
        }
        setListener();
        return view;
    }
}
