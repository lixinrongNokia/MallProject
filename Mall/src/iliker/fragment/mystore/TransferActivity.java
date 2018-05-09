package iliker.fragment.mystore;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import iliker.utils.CheckUtil;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;
import iliker.utils.ParsJsonUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.math.BigDecimal;

import static iliker.utils.HttpHelp.getHttpUtils;

public class TransferActivity extends BaseStoreActivity implements View.OnClickListener {
    private EditText receive_account;//收款账号
    private EditText amount;//转账金额
    private EditText pwdEdit;
    private EditText realName;
    private Button send;
    private double balance = 0;
    private String amountVal;
    private String _account;
    private String _realName;
    private String payment_code;

    @Override
    protected void initMyViews() {
        balance = getIntent().getDoubleExtra("balance", 0);
        title.setText("转账到支付宝");
        View view = View.inflate(this, R.layout.transfer_layout, null);
        findChlidView(view);
        setListener();
        storeContent.addView(view);
    }

    private void findChlidView(View view) {
        receive_account = (EditText) view.findViewById(R.id.receive_account);
        amount = (EditText) view.findViewById(R.id.amount);
        pwdEdit = (EditText) view.findViewById(R.id.accountpwd);
        realName = (EditText) view.findViewById(R.id.realname);
        send = (Button) view.findViewById(R.id.send);
        ((TextView) view.findViewById(R.id.remainingsum)).setText(String.valueOf(balance));
    }

    private void setListener() {
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (regProperties()) {
                    send.setEnabled(false);
                    RequestParams params = new RequestParams(GeneralUtil.TRANSFERACTION);
                    params.addBodyParameter("transfer.phone", userInfo.getPhone());
                    params.addBodyParameter("transfer.account", _account);
                    params.addBodyParameter("transfer.realname", _realName);
                    params.addBodyParameter("device", "app");
                    params.addBodyParameter("payment_code", MD5Util.getMD5Str(payment_code));
                    params.addBodyParameter("transfer.amount", amountVal);
                    params.addBodyParameter("transfer.note", "账户提现");
                    getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String responseInfo) {
                            send.setEnabled(true);
                            JSONObject jsonObject = JSON.parseObject(responseInfo);
                            if (jsonObject.getBoolean("success")) {
                                ToastFactory.getMyToast("已提交,请耐心等待！").show();
                                finish();
                            } else {
                                ToastFactory.getMyToast(jsonObject.getString("msg")).show();
                            }
                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {
                            send.setEnabled(true);
                        }

                        @Override
                        public void onCancelled(CancelledException e) {

                        }

                        @Override
                        public void onFinished() {
                            send.setEnabled(true);
                        }
                    });
                }
                break;
        }
    }

    private boolean regProperties() {
        return regAccount() && regRealName() && regBalance() && regBalance() && regPayment_code();
    }

    private boolean regAccount() {
        _account = receive_account.getText().toString().trim();
        return !TextUtils.isEmpty(_account) && (CheckUtil.isMobileNO2(_account) || ParsJsonUtil.isEmail(_account));
    }

    private boolean regRealName() {
        _realName = realName.getText().toString().trim();
        return !TextUtils.isEmpty(_realName) && _realName.matches("^([\u4e00-\u9fa5]+)$");
    }

    private boolean regBalance() {
        amountVal = amount.getText().toString();
        if (TextUtils.isEmpty(amountVal)) {
            return false;
        }
        BigDecimal balanceBc = new BigDecimal(balance);
        BigDecimal amountBc = new BigDecimal(Integer.parseInt(amountVal));
        return balanceBc.compareTo(amountBc) > 0 && Integer.parseInt(amountVal) >= 100;
    }

    private boolean regPayment_code() {
        payment_code = pwdEdit.getText().toString().trim();
        return !TextUtils.isEmpty(payment_code);
    }
}
