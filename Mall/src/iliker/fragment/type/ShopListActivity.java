package iliker.fragment.type;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.*;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.db.DatabaseService;
import iliker.entity.Cartitem;
import iliker.entity.Order;
import iliker.entity.SerializableList;
import iliker.entity.ShipAddress;
import iliker.utils.DBHelper;
import iliker.utils.GeneralUtil;
import org.xutils.ImageManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.List;

import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.HttpHelp.getHttpUtils;

//订单确认
@SuppressWarnings("deprecation")
public class ShopListActivity extends Activity implements OnClickListener {
    private SerializableList serializablelist;// 从上一个activity传入的商品项目集合
    private TextView newaddress, goodprice, freight, bbar_card_sumprice,
            consigneeNameV, phoneV, addressV, paytype, distribution, goodinfo,
            price, count, totalcount, pointinfo;
    private ImageView goodimg;
    private EditText buymessage;
    private LinearLayout userinfo, goodlistimg;
    private CustomApplication cap;
    private RelativeLayout payanddistribution, showgoodsinfo, goodslist,
            goodslayout;
    private ImageManager bitmapUtils;// 加载图片用
    private String paytypestr = "在线支付", sendtype = "普通快递", recevername = "",
            recevertel = "", receveraddr = "", point = "";// 支付方式，配送方式，收货人姓名，联系电话，收货地址，自提点
    private Button submitOrder;
    private double totalprice = 0.00;// 订单总价钱
    private int topitemcount = 0;// 订单总数量
    private double goodsTotalPrice;//商品总价格
    private Double deliverFee = 0.00;
    private DatabaseService ds;// 数据库操作对象
    private Dialog progressDialog;
    private int windowWidth;
    private int id = 0;
    private final MHandler mHandler = new MHandler(this);
    private boolean isfromCart;
    private int storeId;
    private final int ADDRESS_FLAG = 1;
    private final int GETORDERID_FLAG = 2;
    private final int REGETADDRESS_FLAG = 4;
    final BigDecimal zeno = new BigDecimal(0.00);
    private com.alibaba.fastjson.JSONArray orderDetails = new com.alibaba.fastjson.JSONArray();
    private List<ShipAddress> shipAddresses;

    final class MHandler extends Handler {
        private final WeakReference<Context> weakReference;

        MHandler(Context context) {
            this.weakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            ShopListActivity shopListActivity = (ShopListActivity) weakReference.get();
            if (shopListActivity != null) {
                String data = (String) msg.obj;
                switch (msg.what) {
                    case ADDRESS_FLAG:
                        if (!TextUtils.isEmpty(data)) {
                            shipAddresses = JSON.parseArray(data, ShipAddress.class);
                            ShipAddress address = shipAddresses.get(id);
                            newaddress.setVisibility(View.GONE);
                            userinfo.setVisibility(View.VISIBLE);
                            recevername = address.getConsigneeName();
                            consigneeNameV.setText(recevername);
                            recevertel = address.getPhone();
                            phoneV.setText(recevertel);
                            receveraddr = address.getAddress();
                            addressV.setText(receveraddr);

                        } else {
                            userinfo.setVisibility(View.GONE);
                            newaddress.setVisibility(View.VISIBLE);
                        }
                        break;
                    case GETORDERID_FLAG:
                        if (isfromCart) {
                            for (Cartitem cartitem : serializablelist.getList()) {
                                Order order = cartitem.getOrder();
                                ds.delOrder(order.getCid());// 删除本地数据库里的购物商品
                            }
                        }
                        if (!"货到付款".equals(paytypestr)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("out_trade_no", data);// 订单号
                            bundle.putString("subject", "艾拉奇购物订单");// 商品描述(支付宝字段)
                            bundle.putString("body", "爱内秀应用内购买");// 商品详情
                            bundle.putString("goods_type", "1");
                            bundle.putDouble("total_fee", ("门店自提").equals(sendtype) ? goodsTotalPrice : totalprice);// 总金额
                            Intent intent = new Intent(shopListActivity,
                                    CheckStandActivity.class);
                            intent.putExtra("shopdata", bundle);
                            shopListActivity.startActivity(intent);
                        }
                        shopListActivity.finish();
                        break;
                    case REGETADDRESS_FLAG:
                        shopListActivity.getAddress(cap.getUserinfo().getuID());
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoplist);
        // 获取Android屏幕的服务
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowWidth = wm.getDefaultDisplay().getWidth();
        ds = DBHelper.getDB();
        progressDialog = DialogFactory.initDialog(this);
        bitmapUtils = getBitmapUtils();
        cap = (CustomApplication) getApplication();
        serializablelist = (SerializableList) getIntent().getSerializableExtra("serializablelist");
        isfromCart = getIntent().getBooleanExtra("isfromCart", false);
        findViews();
        setListener();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(point)) {
            if (cap.networkIsAvailable()) {
                getAddress(cap.getUserinfo().getuID());
            }
        }
    }

    private void findViews() {
        goodimg = (ImageView) findViewById(R.id.goodimg);
        goodinfo = (TextView) findViewById(R.id.goodinfo);
        price = (TextView) findViewById(R.id.price);
        count = (TextView) findViewById(R.id.count);
        pointinfo = (TextView) findViewById(R.id.pointinfo);
        goodprice = (TextView) findViewById(R.id.goodprice);
        freight = (TextView) findViewById(R.id.freight);
        consigneeNameV = (TextView) findViewById(R.id.consigneeName);
        phoneV = (TextView) findViewById(R.id.phone);
        addressV = (TextView) findViewById(R.id.address);
        paytype = (TextView) findViewById(R.id.paytype);
        distribution = (TextView) findViewById(R.id.distribution);
        totalcount = (TextView) findViewById(R.id.totalcount);
        newaddress = (TextView) findViewById(R.id.newaddress);
        userinfo = (LinearLayout) findViewById(R.id.userinfo);
        goodlistimg = (LinearLayout) findViewById(R.id.goodlistimg);
        bbar_card_sumprice = (TextView) findViewById(R.id.bbar_card_sumprice);
        payanddistribution = (RelativeLayout) findViewById(R.id.payanddistribution);
        showgoodsinfo = (RelativeLayout) findViewById(R.id.showgoodsinfo);
        goodslist = (RelativeLayout) findViewById(R.id.goodslist);
        goodslayout = (RelativeLayout) findViewById(R.id.goodslayout);
        submitOrder = (Button) findViewById(R.id.submitOrder);
        buymessage = (EditText) findViewById(R.id.buymessage);
    }

    private void getAddress(int uID) {
        RequestParams params = new RequestParams(GeneralUtil.GETADDRESSSVC);
        params.addBodyParameter("uid", uID + "");
        params.addBodyParameter("requestCode", ADDRESS_FLAG + "");
        sharingRequest(params);
    }

    private void initData() {
        paytype.setText(paytypestr);
        distribution.setText(sendtype);
        List<Cartitem> list = serializablelist.getList();
        for (Cartitem item : list) {
            com.alibaba.fastjson.JSONObject cartItemJSON = new com.alibaba.fastjson.JSONObject();
            cartItemJSON.put("productPrice", item.getProductPrice());
            cartItemJSON.put("orderamount", item.getItemCount());
            cartItemJSON.put("color", item.getOrder().getColor());
            cartItemJSON.put("size", item.getOrder().getSize());
            com.alibaba.fastjson.JSONObject goodsJSON = new com.alibaba.fastjson.JSONObject();
            goodsJSON.put("id", item.getOrder().getGoodid());
            cartItemJSON.put("goods", goodsJSON);
            orderDetails.add(cartItemJSON);
            goodsTotalPrice += item.getItemprice();
            topitemcount += item.getItemCount();
        }
        BigDecimal totalPriceBD = new BigDecimal(goodsTotalPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalPriceB2D = new BigDecimal(150.00);

        if (totalPriceBD.compareTo(totalPriceB2D) < 0) {
            deliverFee = 10.00;
        }
        BigDecimal deliverFeeB2D = new BigDecimal(deliverFee);
        totalprice = (totalPriceBD.add(deliverFeeB2D)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        goodprice.setText(String.valueOf("￥" + goodsTotalPrice));
        bbar_card_sumprice.setText(String.valueOf("￥" + totalprice));
        freight.setText(String.valueOf("￥" + deliverFee));
        if (list.size() == 1) {
            goodslist.setVisibility(View.GONE);
            showgoodsinfo.setVisibility(View.VISIBLE);
            Cartitem cartitem = list.get(0);
            Order order = cartitem.getOrder();
            String imgUrl = GeneralUtil.GOODSPATH + order.getImgpath();// 图片地址
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(windowWidth / 4, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 0, 8, 0);
            goodimg.setLayoutParams(lp);
            bitmapUtils.bind(goodimg, imgUrl);
            goodinfo.setText(Html.fromHtml(order.getGoodsDesc()));
            price.setText(String.valueOf(order.getPrice()));
            count.setText(String.valueOf("x" + cartitem.getItemCount()));
        } else if (serializablelist.getList().size() >= 2) {
            int item = 2;
            showgoodsinfo.setVisibility(View.GONE);
            goodslist.setVisibility(View.VISIBLE);
            goodlistimg.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(windowWidth / 4, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 0, 0, 0);
            for (int i = 0; i < item; i++) {
                Order order = serializablelist.getList().get(i).getOrder();
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(lp);
                String imgUrl = GeneralUtil.GOODSPATH + order.getImgpath();// 图片地址
                bitmapUtils.bind(imageView, imgUrl);
                goodlistimg.addView(imageView, i);
            }
            totalcount.setText("共" + String.valueOf(topitemcount) + "件");
        }
    }

    private void setListener() {
        newaddress.setOnClickListener(this);
        payanddistribution.setOnClickListener(this);
        goodslayout.setOnClickListener(this);
        submitOrder.setOnClickListener(this);
        userinfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newaddress:// 跳转到创建收货地址页面
                Intent intent = new Intent(this, NewAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.payanddistribution:// 跳转到支付方式与物流选项
                Intent intent2 = new Intent(this, PayTypeActivity.class);
                startActivityForResult(intent2, 2);
                break;
            case R.id.goodslayout:// 跳转到商品清单
                Intent intent3 = new Intent(this, ShowGoodsActivity.class);
                intent3.putExtra("serializablelist", serializablelist);
                startActivity(intent3);
                break;
            case R.id.submitOrder:// 提交订单到数据库
                if (!sendtype.equals("门店自提")) {
                    if (TextUtils.isEmpty(recevername) || TextUtils.isEmpty(recevertel) || TextUtils.isEmpty(receveraddr)) {
                        getMyToast("缺少配送信息！").show();
                        return;
                    }
                }
                try {
                    String message = buymessage.getText().toString();
                    final List<Cartitem> list = serializablelist.getList();
                    com.alibaba.fastjson.JSONObject params = new com.alibaba.fastjson.JSONObject();
                    params.put("phone", cap.getUserinfo().getPhone());// 用户昵称
                    params.put("orderamount", topitemcount);// 订单总数量
                    params.put("message", TextUtils.isEmpty(message) ? "" : message);// 留言
                    params.put("postmethod", sendtype); // 配送方式
                    params.put("paymethod", paytypestr); // 支付方式
                    if (("门店自提").equals(sendtype)) {
                        com.alibaba.fastjson.JSONObject storeJSON = new com.alibaba.fastjson.JSONObject();
                        storeJSON.put("id", storeId);
                        params.put("storeInfo", storeJSON);// 自提点
                    } else {
                        params.put("recevername", recevername); // 收货人姓名
                        params.put("receveraddr", receveraddr);// 收货地址
                        params.put("recevertel", recevertel);// 收货人联系电话
                    }
                    params.put("goodsTotalPrice", goodsTotalPrice);
                    params.put("deliverFee", ("门店自提").equals(sendtype) ? 0.00 : deliverFee);
                    params.put("toalprice", ("门店自提").equals(sendtype) ? goodsTotalPrice : totalprice);
                    params.put("orderdetails", orderDetails);
                    RequestParams requestParams = new RequestParams(GeneralUtil.ADDORDERSVC);
                    requestParams.addBodyParameter("orderInfo", params.toJSONString());
                    requestParams.addBodyParameter("requestCode", GETORDERID_FLAG + "");
                    sharingRequest(requestParams);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case R.id.userinfo:
                Intent intent4 = new Intent(this, AddressListActivity.class);
                intent4.putExtra("requsetCode", 1);
                startActivityForResult(intent4, 1);
                break;
        }

    }

    private synchronized void sharingRequest(RequestParams params) {
        progressDialog.show();
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                if(!TextUtils.isEmpty(result)){
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);
                    if ("SUCCESS".equals(jsonObject.getString("result_code"))) {
                        Message message = new Message();
                        int requestCode = jsonObject.getIntValue("requestCode");
                        if (requestCode == ADDRESS_FLAG) {
                            message.obj = jsonObject.getJSONArray("data").toString();
                        } else {
                            message.obj = jsonObject.getString("data");
                        }
                        message.what = requestCode;
                        mHandler.sendMessage(message);
                    }
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                progressDialog.dismiss();
                ToastFactory.getMyToast(throwable.getMessage()).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case 1:
                id = data.getIntExtra("selectid", 0);
                mHandler.sendEmptyMessage(REGETADDRESS_FLAG);
                break;
            case 2:
                Bundle bundle = data.getExtras();
                paytypestr = bundle.getString("paytype", "在线支付");
                sendtype = bundle.getString("sendtype", "普通快递");
                paytype.setText(paytypestr);
                distribution.setText(sendtype);
                point = bundle.getString("point", "");
                storeId = bundle.getInt("storeId", 0);
                if (storeId != 0) {
                    if (pointinfo.getVisibility() == View.GONE) {
                        userinfo.setVisibility(View.GONE);
                        newaddress.setVisibility(View.GONE);
                        pointinfo.setVisibility(View.VISIBLE);
                    }
                    pointinfo.setText(String.valueOf("已选自提点:" + point));
                    bbar_card_sumprice.setText(String.valueOf("￥" + goodsTotalPrice));
                    freight.setText("￥0.00");
                } else {
                    if (shipAddresses != null
                            && !shipAddresses.isEmpty()) {
                        if (userinfo.getVisibility() == View.GONE) {
                            pointinfo.setVisibility(View.GONE);
                            newaddress.setVisibility(View.GONE);
                            userinfo.setVisibility(View.VISIBLE);
                        }
                        bbar_card_sumprice.setText(String.valueOf("￥" + totalprice));
                        freight.setText(String.valueOf("￥" + deliverFee));
                    } else {
                        if (newaddress.getVisibility() == View.GONE) {
                            pointinfo.setVisibility(View.GONE);
                            userinfo.setVisibility(View.GONE);
                            newaddress.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
        }

    }

    public void close(View vivew) {
        this.finish();
    }

    @Override
    public void onDestroy() {
        if (serializablelist != null) {
            serializablelist.getList().clear();
            serializablelist = null;
        }
        if (shipAddresses != null) {
            shipAddresses.clear();
        }
        mHandler.removeCallbacksAndMessages(null);
        getSharedPreferences("payAndSend", Context.MODE_PRIVATE).edit().clear()
                .apply();
        super.onDestroy();
    }
}
