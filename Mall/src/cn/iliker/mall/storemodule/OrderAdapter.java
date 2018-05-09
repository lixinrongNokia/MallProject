package cn.iliker.mall.storemodule;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Goods;
import iliker.entity.OrderItem;
import iliker.entity.UnPackOrder;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

import java.util.List;

public class OrderAdapter extends DefaultAdapter<UnPackOrder> {
    private Context context;

    public OrderAdapter(List<UnPackOrder> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new StoreOrderHodler(context);
    }

    class StoreOrderHodler extends BaseHolder<UnPackOrder> {
        TextView status;
        TextView info;
        TextView amount;
        ImageView good_img;

        public StoreOrderHodler(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.unpackorder_layout, null);
            status = (TextView) view.findViewById(R.id.status);
            info = (TextView) view.findViewById(R.id.info);
            amount = (TextView) view.findViewById(R.id.amount);
            good_img = (ImageView) view.findViewById(R.id.good_img);
            return view;
        }

        @Override
        public void refreshView(UnPackOrder order) {
            if (order.getUser_confirm()) {
                status.setText("订单已确认");
            } else {
                status.setText("客户没确认");
            }
            int size = 0;
            Goods goods = order.getOrderItems().get(0).getGoods();

            for (OrderItem orderItem : order.getOrderItems()) {
                size += orderItem.getOrderamount();
            }
            if (size > 1) {
                info.setText(String.valueOf(goods.getGoodName() + "等" + size + "件商品"));
            } else {
                info.setText(goods.getGoodName());
            }
            amount.setText(String.valueOf(size));
            bitmapUtils.bind(good_img, GeneralUtil.GOODSPATH + goods.getImgpath());
        }
    }
}
