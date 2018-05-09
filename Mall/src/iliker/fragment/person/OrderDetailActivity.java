package iliker.fragment.person;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import iliker.adapter.DefaultAdapter;
import iliker.entity.OrderItem;
import iliker.entity.StoreInfo;
import iliker.entity.WebOrder;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.holder.BaseHolder;
import iliker.utils.DisplayUtils;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import static com.fjl.widget.DialogFactory.initDialog;
import static iliker.utils.HttpHelp.getHttpUtils;

public abstract class OrderDetailActivity extends BaseStoreActivity implements
        OnItemClickListener {
    private Dialog progressDialog;// 加载页面的进度条
    private TextView consigneeName, t_phone, address, paytype, distribution,
            t_orderid, toalPrice, point, orderStatus,goodprice,freight;
    private PayingAdapter payingadapter;
    private ListView orderLv;
    private View rootView;
    protected FrameLayout bbr_cartitem;
    private LinearLayout contactInfo;
    protected WebOrder webOrder;
    private int orderid;


    @Override
    protected void initMyViews() {
        title.setText("订单详情");
        rootView = View.inflate(this, R.layout.orderdetail, null);
        storeContent.addView(rootView);
        orderid = getIntent().getIntExtra("orderid", 0);
        findChildViews();
        setListener();
        progressDialog = initDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWebOrder(orderid);
    }

    protected abstract void loadBbr_Buttons();

    private void setListener() {
        orderLv.setOnItemClickListener(this);
    }

    private void findChildViews() {
        address = (TextView) rootView.findViewById(R.id.address);
        paytype = (TextView) rootView.findViewById(R.id.paytype);
        distribution = (TextView) rootView.findViewById(R.id.distribution);
        toalPrice = (TextView) rootView.findViewById(R.id.toalPrice);
        t_orderid = (TextView) rootView.findViewById(R.id.orderid);
        orderStatus = (TextView) rootView.findViewById(R.id.orderStatus);
        point = (TextView) rootView.findViewById(R.id.point);
        goodprice = (TextView) rootView.findViewById(R.id.goodprice);
        freight = (TextView) rootView.findViewById(R.id.freight);
        orderLv = (ListView) rootView.findViewById(R.id.orderLv);
        consigneeName = (TextView) rootView.findViewById(R.id.consigneeName);
        t_phone = (TextView) rootView.findViewById(R.id.phone);
        contactInfo = (LinearLayout) rootView.findViewById(R.id.contactInfo);
        bbr_cartitem = (FrameLayout) rootView.findViewById(R.id.bbr_cartitem);
    }

    private void initData() {
        consigneeName.setText(webOrder.getRecevername());
        t_orderid.setText(webOrder.getOrderid());
        toalPrice.setText(String.valueOf("￥" + webOrder.getToalprice()));
        orderStatus.setText(webOrder.getOrderstate());
        paytype.setText(webOrder.getPaymethod());
        goodprice.setText(String.valueOf("￥" + webOrder.getGoodsTotalPrice()));
        freight.setText(String.valueOf("￥" + webOrder.getDeliverFee()));
        distribution.setText(webOrder.getPostmethod());
        if (webOrder.getPostmethod().equals("门店自提")) {
            point.setText(String.valueOf("自提点:" + webOrder.getStoreInfo().getAddress()));
            contactInfo.setVisibility(View.GONE);
            point.setVisibility(View.VISIBLE);
        } else {
            contactInfo.setVisibility(View.VISIBLE);
            point.setVisibility(View.GONE);
            t_phone.setText(webOrder.getRecevertel());
            address.setText(webOrder.getReceveraddr());
        }
    }

    private void getWebOrder(int orderid) {
        progressDialog.show();
        RequestParams params = new RequestParams(GeneralUtil.FINDBYORDERIDSVC);
        params.addBodyParameter("id", orderid + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBoolean("success")) {
                    try {
                        webOrder = JSON.parseObject(jsonObject.getJSONObject("orderInfo").toJSONString(), WebOrder.class);
                        payingadapter = new PayingAdapter(OrderDetailActivity.this, webOrder.getOrderItem());
                        orderLv.setAdapter(payingadapter);
                        DisplayUtils.setListViewHeight(orderLv);
                        loadBbr_Buttons();
                        initData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        /*Order order = data.get(position).getOrder();
        Goods good = new Goods(order.getGoodid(), order.getGoodCode(), order.getGoodName(), order.getGoodsDesc(), order.getPrice(), order.getImgpath(), order.getIllustrations());
        Intent intent = new Intent(this, ProductDetailActivity.class);// 跳到商品详情页面
        intent.putExtra("good", good);
        startActivity(intent);// 执行*/
    }

    /**
     * 适配器
     */

    public class PayingAdapter extends DefaultAdapter<OrderItem> {

        private final Context context;

        PayingAdapter(Context context, List<OrderItem> datas) {
            super(datas);
            this.context = context;
        }

        @Override
        public BaseHolder getHolder() {
            return new Holder(context);
        }

        class Holder extends BaseHolder<OrderItem> {
            public ImageView img;
            public TextView info;
            public TextView color;
            public TextView size;
            public TextView price;
            public TextView count;

            Holder(Context context) {
                super(context);
            }

            @Override
            public View initViews() {
                View convertView = View.inflate(context, R.layout.showgoods_item, null);
                this.info = (TextView) convertView
                        .findViewById(R.id.good_info);
                this.img = (ImageView) convertView
                        .findViewById(R.id.good_img);
                this.color = (TextView) convertView.findViewById(R.id.color);
                this.size = (TextView) convertView.findViewById(R.id.size);
                this.price = (TextView) convertView.findViewById(R.id.price);
                this.count = (TextView) convertView
                        .findViewById(R.id.itmecount);
                return convertView;
            }

            @Override
            public void refreshView(OrderItem orderItem) {
                this.price.setText(String.valueOf("销售价:￥" + orderItem.getProductPrice()));
                this.info.setText(orderItem.getGoodname());
                this.count.setText(String.valueOf("x" + orderItem.getOrderamount()));
                this.color.setText(String.valueOf("颜色:" + orderItem.getColor()));
                this.size.setText(String.valueOf("尺寸:" + orderItem.getSize()));
                String imgUrl = GeneralUtil.GOODSPATH + orderItem.getImgpath();// 图片地址
                bitmapUtils.bind(this.img, imgUrl);
            }
        }

    }

}
