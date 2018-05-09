package iliker.fragment.person.holder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.entity.WebOrder;
import iliker.fragment.home.LinkActivity;
import iliker.fragment.person.ShoplistLayout;
import iliker.fragment.person.activity.ForGoodActivity;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

public class ForGoodsHolder extends BaseHolder<WebOrder> {
    public ForGoodsHolder(Context context) {
        super(context);
    }

    private ShoplistLayout goodlistimg;
    private TextView orderprice,createTime;
    private Button query_logistics;
    private TextView paymentstatus;

    @Override
    public View initViews() {
        View convertView = View.inflate(context, R.layout.forthegoods_layout_item, null);
        this.paymentstatus = (TextView) convertView.findViewById(R.id.paymentstatus);
        this.createTime = (TextView) convertView.findViewById(R.id.createTime);
        this.goodlistimg = (ShoplistLayout) convertView
                .findViewById(R.id.goodlistimg);
        this.orderprice = (TextView) convertView
                .findViewById(R.id.orderprice);
        this.query_logistics = (Button) convertView
                .findViewById(R.id.query_logistics);
        return convertView;
    }

    @Override
    public void refreshView(final WebOrder datas) {
        this.goodlistimg.removeAllViews();
        this.orderprice.setText(String.valueOf("￥"
                + datas.getToalprice()));
        this.createTime.setText(datas.getOrderdate());
        this.goodlistimg.setData(context, datas.getImgpath().split(","), bitmapUtils);
        if (!datas.getOrderstate().equals("已取消") && (datas.getPaymethod().equals("货到付款") || !TextUtils.isEmpty(datas.getTrade_no()))) {
            //effective=? and (paymethod=? or trade_no<>null)
            if ("已发货".equals(datas.getOrderstate())) {
                this.paymentstatus.setText("已发货");
            } else if ("门店自提".equals(datas.getPostmethod())) {
                this.paymentstatus.setText(datas.getPostmethod());
                query_logistics.setVisibility(View.GONE);
            } else this.paymentstatus.setText("正在出库");
        }
        this.query_logistics.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //查看物流
                Intent intent = new Intent(context,
                        LinkActivity.class);
                intent.putExtra("openHref",
                        GeneralUtil.HOST
                                + "/MallService/querylogistics.do?id="
                                + datas.getId());
                context.startActivity(intent);
            }


        });
        this.goodlistimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        ForGoodActivity.class);
                intent.putExtra("orderid", datas.getId());
                context.startActivity(intent);
            }

        });
    }
}
