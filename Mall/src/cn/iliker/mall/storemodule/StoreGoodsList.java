package cn.iliker.mall.storemodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

//展示门店对应品牌下的商品
public class StoreGoodsList extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int storeId = intent.getIntExtra("storeId", 0);
        int brandId = intent.getIntExtra("brandId", 0);
        TextView view = new TextView(this);
        view.setText("商店编号:" + storeId + "/品牌编号" + brandId);
        setContentView(view);
    }
}
