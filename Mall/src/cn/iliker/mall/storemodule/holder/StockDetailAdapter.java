package cn.iliker.mall.storemodule.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.StoreStockInfo;
import iliker.entity.StoreStockItem;
import iliker.holder.BaseHolder;

import java.util.List;

public class StockDetailAdapter extends DefaultAdapter<StoreStockItem> {
    private Context context;
    private StoreStockInfo storeStockInfo;

    public StockDetailAdapter(List<StoreStockItem> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new StockDetailHolder(context);
    }

    class StockDetailHolder extends BaseHolder<StoreStockItem> {
        private TextView stockSize;
        private TextView stockCount;


        public StockDetailHolder(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.stock_detail_layout, null);
            stockSize = (TextView) view.findViewById(R.id.stockSize);
            stockCount = (TextView) view.findViewById(R.id.stockCount);
            return view;
        }

        @Override
        public void refreshView(StoreStockItem datas) {
            stockSize.setText(datas.getSize());
            stockCount.setText(datas.getStockCount() + "");
        }
    }
}
