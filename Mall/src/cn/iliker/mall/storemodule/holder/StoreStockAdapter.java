package cn.iliker.mall.storemodule.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.StoreStockInfo;
import iliker.holder.BaseHolder;
import iliker.holder.DefaultHolder;

import java.util.List;

public class StoreStockAdapter extends DefaultAdapter<StoreStockInfo> {
    private List<StoreStockInfo> storeStockInfoList;
    private Context context;

    public StoreStockAdapter(List<StoreStockInfo> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new StockHolder(context);
    }

    class StockHolder extends BaseHolder<StoreStockInfo> {
        private TextView goodsCode;
        private TextView color;
        private TextView stockCount;

        public StockHolder(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.store_stock_layout, null);
            goodsCode = (TextView) view.findViewById(R.id.goodsCode);
            color = (TextView) view.findViewById(R.id.color);
            stockCount = (TextView) view.findViewById(R.id.stockCount);
            return view;
        }

        @Override
        public void refreshView(StoreStockInfo datas) {
            goodsCode.setText(datas.getGoods().getGoodCode());
            color.setText(datas.getColor());
            stockCount.setText(datas.getStockCount()+"");
        }
    }
}
