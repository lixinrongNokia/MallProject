package iliker.fragment.person.activity;


import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.CheckUtil;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;
import iliker.utils.ParsJsonUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.utils.HttpHelp.getHttpUtils;

public class RefundActivity extends BaseStoreActivity implements View.OnClickListener {
    private EditText t_account, t_realname, t_amount, t_accountpwd;
    private String account, payment_code, realname;
    private double amount;
    private int orderid;
    private Button b_send;
    private Dialog dialog;

    @Override
    protected void initMyViews() {
        title.setText("申请退款");
        dialog = DialogFactory.initDialog(this);
        View view = View.inflate(this, R.layout.resund_layout, null);
        storeContent.addView(view);
        this.findViews(view);
    }

    private void findViews(View v) {
        Intent intent = getIntent();
        t_account = (EditText) v.findViewById(R.id.receive_account);
        t_realname = (EditText) v.findViewById(R.id.realname);
        t_amount = (EditText) v.findViewById(R.id.amount);
        t_accountpwd = (EditText) v.findViewById(R.id.accountpwd);
        b_send = (Button) v.findViewById(R.id.send);
        b_send.setOnClickListener(this);
        amount = intent.getDoubleExtra("toalprice", 0);
        orderid = intent.getIntExtra("orderid", 0);
        t_amount.setText(String.valueOf(amount));
    }

    @Override
    public void onClick(View v) {
        if (regAttr()) {
            dialog.show();
            b_send.setEnabled(false);
            RequestParams params = new RequestParams(GeneralUtil.TRANSFERACTION);
            params.addBodyParameter("transfer.phone", phone);
            params.addBodyParameter("transfer.account", account);
            params.addBodyParameter("transfer.realname", realname);
            params.addBodyParameter("device", "app");
            params.addBodyParameter("payment_code", MD5Util.getMD5Str(payment_code));
            params.addBodyParameter("transfer.amount", amount + "");
            params.addBodyParameter("transfer.note", "退款");
            params.addBodyParameter("transfer.tOrder.id", orderid + "");
            getHttpUtils().post(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String s) {
                    dialog.dismiss();
                    b_send.setEnabled(true);
                    JSONObject jsonObject = JSON.parseObject(s);
                    if (jsonObject.getBoolean("success")) {
                        ToastFactory.getMyToast("已提交,请耐心等待！").show();
                        finish();
                    } else {
                        ToastFactory.getMyToast(jsonObject.getString("msg")).show();
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    dialog.dismiss();
                    b_send.setEnabled(true);
                }

                @Override
                public void onCancelled(CancelledException e) {
                    dialog.dismiss();
                    b_send.setEnabled(true);
                }

                @Override
                public void onFinished() {
                    dialog.dismiss();
                    b_send.setEnabled(true);
                }
            });
        }
    }

    private boolean regAttr() {
        account = t_account.getText().toString().trim();
        realname = t_realname.getText().toString().trim();
        payment_code = t_accountpwd.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastFactory.getMyToast("请输入支付宝账户").show();
            return false;
        }
        if (CheckUtil.isMobileNO2(account) || ParsJsonUtil.isEmail(account)) {
            if (TextUtils.isEmpty(realname)) {
                ToastFactory.getMyToast("请输入用户名").show();
                return false;
            }
            if (!realname.matches("^([\u4e00-\u9fa5]+)$")) {
                ToastFactory.getMyToast("请输入简体中文名").show();
                return false;
            }
            if (TextUtils.isEmpty(payment_code)) {
                ToastFactory.getMyToast("请输入支付密码").show();
                return false;
            }
            if (amount == 0) {
                ToastFactory.getMyToast("金额有误").show();
                return false;
            }
            return true;
        }

        ToastFactory.getMyToast("请正确输入支付宝账户").show();
        return false;
    }
}
