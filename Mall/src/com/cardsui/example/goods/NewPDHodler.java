package com.cardsui.example.goods;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.iliker.mall.R;
import iliker.entity.Goods;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

/**
 * Created by WDHTC on 2016/7/4.
 */
public class NewPDHodler extends BaseHolder<Goods> {
    private ImageView product_img;
    private TextView product_name;
    private TextView product_price;

    public NewPDHodler(Context context) {
        super(context);
    }

    @Override
    public View initViews() {
        View view = View.inflate(context, R.layout.newproduct_layout, null);
        this.product_img = (ImageView) view.findViewById(R.id.product_img);
        this.product_name = (TextView) view.findViewById(R.id.product_name);
        this.product_price = (TextView) view.findViewById(R.id.product_price);
        return view;
    }

    @Override
    public void refreshView(Goods datas) {
        bitmapUtils.bind(this.product_img, GeneralUtil.GOODSPATH + datas.getImgpath());
        this.product_price.setText(String.valueOf("￥" + datas.getPrice()));
        this.product_name.setText(Html.fromHtml(datas.getGoodName()));
    }
}
