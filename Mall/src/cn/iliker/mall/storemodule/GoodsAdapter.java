package cn.iliker.mall.storemodule;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Goods;
import iliker.entity.OrderItem;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

import java.util.List;

public class GoodsAdapter extends DefaultAdapter<OrderItem> {
    private Context context;

    public GoodsAdapter(List<OrderItem> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new GoodsHolder(context);
    }

    class GoodsHolder extends BaseHolder<OrderItem> {
        ImageView good_img;
        TextView goodName, goodCode, price, count;

        public GoodsHolder(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.upinstoregoods_layout, null);
            good_img = (ImageView) view.findViewById(R.id.good_img);
            goodName = (TextView) view.findViewById(R.id.goodName);
            goodCode = (TextView) view.findViewById(R.id.goodCode);
            price = (TextView) view.findViewById(R.id.price);
            count = (TextView) view.findViewById(R.id.count);
            return view;
        }

        @Override
        public void refreshView(OrderItem datas) {
            Goods goods = datas.getGoods();
            goodName.setText(goods.getGoodName());
            goodCode.setText(goods.getGoodCode());
            price.setText(String.valueOf("￥" + datas.getProductPrice()));
            bitmapUtils.bind(good_img, GeneralUtil.GOODSPATH + goods.getImgpath().split("#")[0]);
            count.setText(String.valueOf("x" + datas.getOrderamount()));
        }
    }
}
