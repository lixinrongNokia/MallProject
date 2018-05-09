package cn.iliker.mall.storemodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.entity.UnPackOrder;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import static iliker.utils.HttpHelp.getHttpUtils;

public class UnPackOrderActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView orderLv;
    private List<UnPackOrder> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_order_layout);
        orderLv = (ListView) findViewById(R.id.orderLv);
        orderLv.setOnItemClickListener(this);
        getOrders();
    }

    public void getOrders() {
        RequestParams params = new RequestParams(GeneralUtil.GETUNPACKORDERSVC);
        params.addBodyParameter("storeId", CustomApplication.customApplication.getStoreInfo().getId() + "");
        params.addBodyParameter("indexPage", 1 + "");
        params.addBodyParameter("pageCount", 10 + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                JSONObject jsonObject = JSON.parseObject(s);
                if (jsonObject.getBooleanValue("success")) {
                    list = JSON.parseArray(jsonObject.getJSONArray("orders").toJSONString(), UnPackOrder.class);
                    orderLv.setAdapter(new OrderAdapter(list, UnPackOrderActivity.this));
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ViewUnPackOrderActivity.class);
        intent.putExtra("unPackOrder", list.get(position));
        startActivity(intent);
    }
}
