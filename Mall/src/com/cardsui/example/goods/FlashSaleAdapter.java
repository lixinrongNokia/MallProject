package com.cardsui.example.goods;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardsui.example.goods.clock.ObjectPool;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.FlashSale;
import iliker.entity.Goods;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

class FlashSaleAdapter extends DefaultAdapter<FlashSale> {
    private final Context context;

    FlashSaleAdapter(Context context, List<FlashSale> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new ViewHolder(context);
    }

    /* 面向对象，做的视图容器类 */
    final class ViewHolder extends BaseHolder<FlashSale> {
        public ViewHolder(Context context) {
            super(context);
        }

        public ImageView img;//商品图片
        public TextView info;//商品名称
        TextView flashsaleprice;//
        TextView normalprice;//原价
        Button operation;//数量
        public TextView status;//活动状态

        @Override
        public View initViews() {
            View convertView = View.inflate(context, R.layout.flashsale_time, null);
            this.info = (TextView) convertView
                    .findViewById(R.id.category_item_info);
            this.img = (ImageView) convertView
                    .findViewById(R.id.category_item_img);
            this.flashsaleprice = (TextView) convertView.findViewById(R.id.flashsaleprice);
            this.normalprice = (TextView) convertView.findViewById(R.id.normalprice);
            this.normalprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
            this.operation = (Button) convertView.findViewById(R.id.operation);
            this.status = (TextView) convertView.findViewById(R.id.status);
            return convertView;
        }

        @Override
        public void refreshView(FlashSale datas) {
            Goods good = datas.getGoods();
            this.info.setText(good.getGoodsDesc());
            BigDecimal bigbmi = new BigDecimal(good.getPrice() * datas.getDiscount());
            Double dd = bigbmi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            this.flashsaleprice.setText(String.valueOf("￥" + dd));
            this.normalprice.setText(String.valueOf("￥" + good.getPrice()));
            this.status.setText(String.valueOf(datas.getDiscount()));
            this.operation.setOnClickListener(new MClickListener());
            String imgUrl = GeneralUtil.GOODSPATH + good.getImgpath();
            bitmapUtils.bind(this.img, imgUrl);
        }
    }

    private class MClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            if (button.getText().equals("提醒我")) {
                Calendar mCalendar = Calendar.getInstance();
                ObjectPool.mAlarmHelper.openAlarm(32, "到时提醒",
                        "限时抢购时间到了", mCalendar.getTimeInMillis() + 15000);
                button.setText("取消提醒");
            } else {
                ObjectPool.mAlarmHelper.closeAlarm(32, "", "");
                button.setText("提醒我");
            }
        }
    }
}
