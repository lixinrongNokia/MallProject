package iliker.fragment.person.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.entity.WebOrder;
import iliker.fragment.person.ShoplistLayout;
import iliker.fragment.person.activity.CancellOrderDetail;
import iliker.holder.BaseHolder;

public class DoneHolder extends BaseHolder<WebOrder> {

    public DoneHolder(Context context) {
        super(context);
    }

    private ShoplistLayout goodlistimg;
    private TextView orderprice;
    private Button evaluation;
    private TextView paymentstatus;

    @Override
    public View initViews() {
        View convertView = View.inflate(context, R.layout.done_layout_item, null);
        this.paymentstatus = (TextView) convertView.findViewById(R.id.paymentstatus);
        this.goodlistimg = (ShoplistLayout) convertView
                .findViewById(R.id.goodlistimg);
        this.orderprice = (TextView) convertView
                .findViewById(R.id.orderprice);
        this.evaluation = (Button) convertView
                .findViewById(R.id.evaluation);
        return convertView;
    }

    @Override
    public void refreshView(final WebOrder datas) {
        this.goodlistimg.removeAllViews();
        this.orderprice.setText(String.valueOf("￥" + datas.getToalprice()));
        String imgstr = datas.getImgpath();
        String[] imges = imgstr.split(",");
        this.goodlistimg.setData(context, imges, bitmapUtils);
        this.paymentstatus.setText("已完成");
        this.evaluation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //评价
            }
        });
        this.goodlistimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看订单详情
                Intent intent = new Intent(context,
                        CancellOrderDetail.class);
                intent.putExtra("orderid", datas.getId());
                context.startActivity(intent);
            }

        });
    }
}
