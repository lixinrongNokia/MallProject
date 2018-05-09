package iliker.fragment.type;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;

import static iliker.utils.HttpHelp.getHttpUtils;

//购物付款
public class CheckStandActivity extends Activity implements OnClickListener {
    private RelativeLayout prepaidcardPay, walletpay, alipay, wxpay;
    private String out_trade_no;
    private String subject;
    private String body;
    private double total_fee;
    private ImageView backbtn;
    private String goods_type;
    private Dialog dialog;
    private CustomApplication cap;
    private TextView balance;
    private TextView remainingSum;
    private final int ALI_PAY_FLAG = 1;
    private final int INAPP_PAY_FLAG = 2;
    private final int GETBALANCE_FLAG = 3;
    private final int WXPAY_PAY_FLAG = 4;
    private final int ALI_SIGN_FLAG = 5;
    private final MyHandler handler = new MyHandler(this);

    private class MyHandler extends Handler {
        private final WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            CheckStandActivity activity = (CheckStandActivity) reference.get();
            if (activity != null) {
                final String data = (String) msg.obj;
                switch (msg.what) {
                    case ALI_PAY_FLAG:
                        if (TextUtils.equals(data, "9000")) {
                            ToastFactory.getMyToast("支付成功").show();
                            activity.finish();
                        } else {
                            if (TextUtils.equals(data, "8000")) {
                                ToastFactory.getMyToast("支付结果确认中").show();
                            } else {
                                ToastFactory.getMyToast("支付失败").show();
                            }
                        }
                        break;
                    case INAPP_PAY_FLAG:
                        //优惠券余额支付成功返回
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckStandActivity.this);
                        builder.setView(new AlertView(CheckStandActivity.this).getView());
                        builder.setCancelable(false);
                        builder.show();
                        break;
                    case GETBALANCE_FLAG:
                        //查询优惠券余额返回
                        JSONObject jsonObject = JSON.parseObject(data);
                        BigDecimal data1 = new BigDecimal(jsonObject.getDouble("remainingSum"));
                        BigDecimal data3 = new BigDecimal(jsonObject.getDouble("balance"));
//                        ListjsonObject.getDouble("balance"));
                        BigDecimal data2 = new BigDecimal(0);
                        BigDecimal data4 = new BigDecimal(total_fee);
                        if (data1.compareTo(data2) > 0) {
                            walletpay.setVisibility(View.VISIBLE);
                            if (data1.compareTo(data4) < 0) {
                                remainingSum.setText("余额不足");
                                remainingSum.setTextColor(Color.RED);
                                walletpay.setEnabled(false);
                            } else
                                remainingSum.setText(String.valueOf(jsonObject.getDouble("remainingSum")));
                        }
                        if (data3.compareTo(data2) > 0) {
                            prepaidcardPay.setVisibility(View.VISIBLE);
                            if (data3.compareTo(data4) < 0) {
                                balance.setText("卡券余额不足");
                                balance.setTextColor(Color.RED);
                                prepaidcardPay.setEnabled(false);
                            } else
                                balance.setText(String.valueOf(jsonObject.getDouble("balance")));
                        }
                        break;
                    case WXPAY_PAY_FLAG://调起微信支付
                        JSONObject wxjson = JSONObject.parseObject(data);
                        IWXAPI api = WXAPIFactory.createWXAPI(CheckStandActivity.this, wxjson.getString("appid"));
                        if (api.registerApp(wxjson.getString("appid"))) {
                            PayReq req = new PayReq();
                            req.appId = wxjson.getString("appid");
                            req.partnerId = wxjson.getString("partnerid");
                            req.prepayId = wxjson.getString("prepayid");
                            req.packageValue = wxjson.getString("package");
                            req.nonceStr = wxjson.getString("noncestr");
                            req.timeStamp = wxjson.getString("timestamp");
                            req.sign = wxjson.getString("sign");
                            api.sendReq(req);
                            finish();
                        }
                        break;
                    case ALI_SIGN_FLAG://调起支付宝支付
                        Runnable payRunnable = new Runnable() {
                            @Override
                            public void run() {
                                // 构造PayTask 对象
                                // 调用支付接口，获取支付结果
                                PayTask payTask = new PayTask(CheckStandActivity.this);
                                String resultStatus = payTask.payV2(data, true).get("resultStatus");
                                Message msg = new Message();
                                msg.what = ALI_PAY_FLAG;
                                msg.obj = resultStatus;
                                handler.sendMessage(msg);
                            }
                        };
                        // 必须异步调用支付宝SDK支付
                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkstand);
        cap = (CustomApplication) this.getApplication();
        dialog = DialogFactory.initDialog(this);
        Bundle bundle = getIntent().getBundleExtra("shopdata");
        out_trade_no = bundle.getString("out_trade_no");
        subject = bundle.getString("subject");
        body = bundle.getString("body");
        total_fee = bundle.getDouble("total_fee");
        goods_type = bundle.getString("goods_type");
        findViews();
        setListener();
    }

    private void getBalance(String phone) {
        RequestParams params = new RequestParams(GeneralUtil.GETBALANCESVC);
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("requestCode", GETBALANCE_FLAG + "");
        sharingRequest(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ("1".equals(goods_type)) {
            if (cap.networkIsAvailable())
                getBalance(cap.getUserinfo().getPhone());
        }
    }

    private void findViews() {
        alipay = (RelativeLayout) findViewById(R.id.alipay);
        wxpay = (RelativeLayout) findViewById(R.id.wxpay);
        walletpay = (RelativeLayout) findViewById(R.id.walletpay);
        prepaidcardPay = (RelativeLayout) findViewById(R.id.prepaidcardPay);
        ((TextView) findViewById(R.id.orderprice)).setText(String.valueOf("支付金额:￥" + total_fee));
        remainingSum = (TextView) findViewById(R.id.remainingSum);
        balance = (TextView) findViewById(R.id.balance);
        backbtn = (ImageView) findViewById(R.id.backbtn);
    }

    private void setListener() {
        alipay.setOnClickListener(this);
        wxpay.setOnClickListener(this);
        prepaidcardPay.setOnClickListener(this);
        walletpay.setOnClickListener(this);
        backbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prepaidcardPay:
                inAppPay("你将使用卡券支付", "giveaccount", out_trade_no, cap.getUserinfo().getPhone(), total_fee);
                break;
            case R.id.walletpay:
                inAppPay("你将使用账户余额支付", "wallet", out_trade_no, cap.getUserinfo().getPhone(), total_fee);
                break;
            case R.id.alipay:
                //支付宝支付--请求服务后台获取订单签名
                RequestParams aliPayarams = new RequestParams(GeneralUtil.ALIPAYSIGN);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("out_trade_no", out_trade_no);
                jsonObject.put("subject", subject);
                jsonObject.put("body", body);
                jsonObject.put("total_amount", total_fee + "");
                jsonObject.put("goods_type", goods_type);
                jsonObject.put("timeout_express", "30m");
                jsonObject.put("product_code", "QUICK_MSECURITY_PAY");
                aliPayarams.addBodyParameter("requestCode", ALI_SIGN_FLAG + "");
                aliPayarams.addBodyParameter("biz_content", jsonObject.toString());
                sharingRequest(aliPayarams);
                break;
            case R.id.wxpay:
                //微信支付--统一下单
                try {
                    body=new String(body.getBytes("UTF-8"),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (CustomApplication.customApplication.networkIsAvailable()) {
                    String builder = "<xml>" +
                            "<appid>" +
                            "<![CDATA[" +
                            "wx82a6291f4ce0547e" +
                            "]]>" +
                            "</appid>" +
                            "<body>" +
                            "<![CDATA[" +
                            body +
                            "]]>" +
                            "</body>" +
                            "<out_trade_no>" +
                            "<![CDATA[" +
                            out_trade_no +
                            "]]>" +
                            "</out_trade_no>" +
                            "<total_fee>" +
                            "<![CDATA[" +
                            (int) (total_fee * 100) +
                            "]]>" +
                            "</total_fee>" +
                            "<trade_type>" +
                            "<![CDATA[" +
                            "APP" +
                            "]]>" +
                            "</trade_type>" +
                            "</xml>";
                    RequestParams params = new RequestParams(GeneralUtil.WXPREPAY);
                    params.addBodyParameter("requestCode", WXPAY_PAY_FLAG + "");
                    params.addBodyParameter("payProperty", builder);
                    sharingRequest(params);
                } else ToastFactory.getMyToast("没网络").show();
                break;
            case R.id.backbtn:
                this.finish();
                break;
        }
    }

    private void inAppPay(final String title, final String table, final String out_trade_no, final String phone, final double money) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RequestParams params = new RequestParams(GeneralUtil.INAPPPAYSVC);
                params.addBodyParameter("tableName", table);
                params.addBodyParameter("out_trade_no", out_trade_no);
                params.addBodyParameter("phone", phone);
                params.addBodyParameter("money", money + "");
                params.addBodyParameter("requestCode", INAPP_PAY_FLAG + "");
                sharingRequest(params);
            }
        }).setNegativeButton("取消", null).show();
    }


    private void sharingRequest(RequestParams params) {
        dialog.show();
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                JSONObject jsonObject = JSON.parseObject(result);
                if ("SUCCESS".equals(jsonObject.getString("result_code"))) {
                    Message message = new Message();
                    int requestCode = jsonObject.getIntValue("requestCode");
                    if (requestCode == ALI_SIGN_FLAG) {
                        message.obj = jsonObject.getString("data");
                    } else {
                        message.obj = jsonObject.getJSONObject("data").toString();
                    }
                    message.what = requestCode;
                    handler.sendMessage(message);
                } else {
                    ToastFactory.getMyToast(jsonObject.getString("data")).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dialog.dismiss();
                ToastFactory.getMyToast("请求未完成").show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    class AlertView {
        private final View view;
        private TextView closebtn;

        AlertView(Context context) {
            this.view = createView(context);
        }

        View createView(Context context) {
            View view = View.inflate(context, R.layout.paysuccess_layout, null);
            this.closebtn = (TextView) view.findViewById(R.id.closebtn);
            this.closebtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            return view;
        }

        public View getView() {
            return this.view;
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}
