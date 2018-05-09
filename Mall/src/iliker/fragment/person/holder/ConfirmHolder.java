package iliker.fragment.person.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.entity.WebOrder;
import iliker.fragment.person.activity.ConfirmOrderAct;
import iliker.fragment.person.activity.ForGoodActivity;
import iliker.holder.BaseHolder;

public class ConfirmHolder extends BaseHolder<WebOrder> {
    private TextView storeName, orderStatus, orderAmount, good_info,confirmBtn;

    public ConfirmHolder(Context context) {
        super(context);
    }

    @Override
    public View initViews() {
        View view = View.inflate(context, R.layout.confirm_order_layout, null);
        storeName = (TextView) view.findViewById(R.id.storeName);
        orderStatus = (TextView) view.findViewById(R.id.orderStatus);
        orderAmount = (TextView) view.findViewById(R.id.orderAmount);
        good_info = (TextView) view.findViewById(R.id.good_info);
        confirmBtn = (TextView) view.findViewById(R.id.confirmBtn);
        return view;
    }

    @Override
    public void refreshView(final WebOrder datas) {
        orderStatus.setText(datas.getOrderstate());
        orderAmount.setText(String.valueOf("￥" + datas.getToalprice()));
       /* if (datas.getPostmethod().equals("门店自提")) {
            storeName.setText(datas.getStoreInfo().getAddress());
        } else {
            storeName.setText("应用内购买");
        }*/
        storeName.setText(datas.getPostmethod());
        good_info.setText(String.valueOf(datas.getOrderamount()+"件宝贝"));
        this.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        ConfirmOrderAct.class);
                intent.putExtra("orderid", datas.getId());
                context.startActivity(intent);
            }

        });
    }
}
