package cn.iliker.mall.storemodule;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import cn.iliker.mall.R;
import cn.iliker.mall.storemodule.holder.StockDetailAdapter;
import iliker.entity.StoreStockInfo;

public class StockDetailsActivity extends Activity {
    private ListView listView;
    private TextView goodsCode;
    private TextView goodsColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail);
        listView = (ListView) findViewById(R.id.listView);
        StoreStockInfo tager = (StoreStockInfo) getIntent().getParcelableExtra("store_stock");
        StockDetailAdapter stockDetailAdapter = new StockDetailAdapter(tager.getStockItems(), this);
        listView.setAdapter(stockDetailAdapter);
        goodsCode = (TextView) findViewById(R.id.goodsCode);
        goodsColor = (TextView) findViewById(R.id.goodsColor);
        goodsCode.setText("编号:" + tager.getGoods().getGoodCode());
        goodsColor.setText("颜色:" + tager.getColor());
    }
}
