package com.cardsui.example.goods;

import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.math.BigDecimal;
import java.util.List;

import cn.iliker.mall.R;
import iliker.entity.FlashSale;
import iliker.entity.Goods;
import iliker.fragment.BaseFragment;
import iliker.utils.GeneralUtil;
import iliker.utils.ParsJsonUtil;

import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.HttpHelp.getHttpUtils;

public class TimelimitFragment extends BaseFragment {
    private LinearLayout flashsalell;

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.timelimit, null);
            findViews();
            initviews();
        }
        return view;
    }

    private void findViews() {
        flashsalell = (LinearLayout) view.findViewById(R.id.flashsalell);
    }

    private void initviews() {
       /* Calendar calendar = Calendar.getInstance();// 日期对象查找当前时间
        int time = calendar.get(Calendar.HOUR_OF_DAY);*/
        //TODO 移除sql
        //String sql = "SELECT f.id, g.id,g.goodCode,g.goodName,g.goodsDesc,g.price,g.imgpath,g.illustrations,g.sales,f.number,f.`STATUS`,f.discount from flashsale f join goods g on (f.goodid=g.id) where starttime=8 limit 0,10";
        asyncGetCloudData();
    }

    /**
     * 依据给定条件获取商品
     * propertyMap封装了查询条件与值 conditions为条件，columnValue为值
     * 只有条件就是根据销量降序查询，或价格升序查询
     */
    private void asyncGetCloudData() {
        RequestParams params = new RequestParams(GeneralUtil.GETTIMEGOODS);
        params.addBodyParameter("starttime", 8 + "");
        params.addBodyParameter("offset", 1 + "");
        params.addBodyParameter("count", 6 + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject=JSON.parseObject(result);
                    List<FlashSale> flash = JSON.parseArray(JSON.toJSONString(jsonObject.getJSONArray("saleses")),FlashSale.class);
                    for (int i = 0; i < flash.size(); i++) {
                        FlashSale fixme = flash.get(i);
                        Goods goods = fixme.getGoods();
                        View view = View.inflate(context, R.layout.flashsale_item, null);
                        ImageView lv = (ImageView) view.findViewById(R.id.goodsimg);
                        TextView tv = (TextView) view.findViewById(R.id.flashsaleprice);
                        TextView tv2 = (TextView) view.findViewById(R.id.normalprice);
                        tv2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
                        getBitmapUtils().bind(lv, GeneralUtil.GOODSPATH + goods.getImgpath().split("#")[0]);
                        BigDecimal bigbmi = new BigDecimal(goods.getPrice() * fixme.getDiscount());
                        Double dd = bigbmi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                        tv.setText(String.valueOf("￥" + dd));
                        tv2.setText(String.valueOf("￥" + goods.getPrice()));
                        view.setLeft(10);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, FlashSaleActivity.class);
                                startActivity(intent);
                            }
                        });
                        flashsalell.addView(view);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
