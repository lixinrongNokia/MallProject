package iliker.fragment.person.holder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.entity.WebOrder;
import iliker.fragment.person.ShoplistLayout;
import iliker.fragment.person.activity.PayingOrderDetail;
import iliker.fragment.type.CheckStandActivity;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.fragment.person.holder.ActionOrderMethod.changeOrderIDBuy;
import static iliker.utils.HttpHelp.getHttpUtils;

public class ForThePaymentHolder extends BaseHolder<WebOrder> {
    public ForThePaymentHolder(Context context) {
        super(context);
    }

    private ShoplistLayout goodlistimg;
    private TextView orderprice;
    private Button payingbtn;
    private TextView paymentstatus;
    private TextView createTime;

    @Override
    public View initViews() {
        View convertView = View.inflate(context, R.layout.forthepayment_layout_item, null);
        this.paymentstatus = (TextView) convertView.findViewById(R.id.paymentstatus);
        this.goodlistimg = (ShoplistLayout) convertView
                .findViewById(R.id.goodlistimg);
        this.orderprice = (TextView) convertView
                .findViewById(R.id.orderprice);
        this.payingbtn = (Button) convertView
                .findViewById(R.id.payingbtn);
        this.createTime = (TextView) convertView
                .findViewById(R.id.createTime);
        return convertView;
    }

    @Override
    public void refreshView(final WebOrder datas) {
        this.goodlistimg.removeAllViews();
        this.orderprice.setText(String.valueOf("￥"
                + datas.getToalprice()));
        String[] imges = datas.getImgpath().split(",");
        this.goodlistimg.setData(context, imges, bitmapUtils);
        this.paymentstatus.setText("待付款");
        this.payingbtn.setText("去支付");
        this.createTime.setText(String.valueOf("下单时间:" + datas.getOrderdate()));
        this.payingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderIDBuy(context,datas,false);//重新生成支付订单
            }
        });
        this.goodlistimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        PayingOrderDetail.class);
                intent.putExtra("orderid", datas.getId());
                context.startActivity(intent);
            }

        });

    }
}
