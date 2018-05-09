package iliker.fragment.mystore;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.utils.HttpHelp.getHttpUtils;

public class AccountActivity extends BaseStoreActivity implements View.OnClickListener {
    private TextView account;//账户名
    private TextView balance;//账户余额
    private TextView integral;//剩余积分
    private TextView rechargeable;//充值
    private TextView withdrawal;//提现
    private TextView bookKeeping;//消费记录
    private TextView laundry_list;//收入明细
    private EditText settingPayCode;//收入明细
    private View view;
    private View inputView;

    private TextView t_loginPwd;
    private TextView t_payCode;
    private Button b_setting;
    private AlertDialog builder;
    private String passWrod, payCode;

    @Override
    protected void initMyViews() {
        title.setText("账户管理");
        view = View.inflate(this, R.layout.account_layout, null);
        storeContent.addView(view);
        inputView = View.inflate(this, R.layout.inputpaycode_layout, null);
        findChildViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void findChildViews() {
        account = (TextView) view.findViewById(R.id.accountnum);
        balance = (TextView) view.findViewById(R.id.balance);
        integral = (TextView) view.findViewById(R.id.integral);
        rechargeable = (TextView) view.findViewById(R.id.rechargeable);
        withdrawal = (TextView) view.findViewById(R.id.withdrawal);
        bookKeeping = (TextView) view.findViewById(R.id.bookKeeping);
        laundry_list = (TextView) view.findViewById(R.id.laundry_list);
        settingPayCode = (EditText) view.findViewById(R.id.settingPayCode);
        t_loginPwd = (TextView) inputView.findViewById(R.id.login_pwd);
        t_payCode = (TextView) inputView.findViewById(R.id.pay_code);
        b_setting = (Button) inputView.findViewById(R.id.setting);
    }

    private void initData() {
        account.setText(phone);
        if (userInfo != null) {
            RequestParams params = new RequestParams(GeneralUtil.GETMYASSETS);
            params.addBodyParameter("phone", userInfo.getPhone());
            getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String responseInfo) {
                    JSONObject jsonObject = JSON.parseObject(responseInfo);
                    if (jsonObject.getBoolean("success")) {
                        balance.setText(String.valueOf(jsonObject.getDouble("balance")));
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {

                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private void setListeners() {
        rechargeable.setOnClickListener(this);
        withdrawal.setOnClickListener(this);
        bookKeeping.setOnClickListener(this);
        laundry_list.setOnClickListener(this);
        settingPayCode.setOnClickListener(this);
        b_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rechargeable:
                Intent intent = new Intent(this, RechargeableActivity.class);
                startActivity(intent);
                break;
            case R.id.withdrawal:
                Intent transferIntent = new Intent(this, TransferActivity.class);
                transferIntent.putExtra("balance", Double.parseDouble(balance.getText().toString()));
                startActivity(transferIntent);
                break;
            case R.id.bookKeeping:
                Intent intent1 = new Intent(this, BookKeepActivity.class);
                startActivity(intent1);
                break;
            case R.id.laundry_list:
                Intent intent2 = new Intent(this, IncomeDetailsActivity.class);
                startActivity(intent2);
                break;
            case R.id.settingPayCode:
                if (builder == null) {
                    builder = new AlertDialog.Builder(this).create();
                    builder.setTitle("设置支付密码");
                    builder.setView(inputView);
                }
                builder.show();
                break;
            case R.id.setting:
                if (regAttr()) {
                    RequestParams params = new RequestParams(GeneralUtil.SETTINGPAYCODE);
                    params.addBodyParameter("phone", phone);
                    params.addBodyParameter("password", MD5Util.getMD5Str(passWrod));
                    params.addBodyParameter("payment_code", MD5Util.getMD5Str(payCode));
                    params.addBodyParameter("device", "app");
                    getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String responseInfo) {
                            JSONObject jsonObject = JSON.parseObject(responseInfo);
                            if (jsonObject.getBoolean("success")) {
                                ToastFactory.getMyToast("设置成功").show();
                                builder.dismiss();
                            } else {
                                ToastFactory.getMyToast("密码不正确").show();
                            }
                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {

                        }

                        @Override
                        public void onCancelled(CancelledException e) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                }
                break;
        }
    }

    private boolean regAttr() {
        passWrod = t_loginPwd.getText().toString().trim();
        payCode = t_payCode.getText().toString().trim();
        if (TextUtils.isEmpty(passWrod)) {
            ToastFactory.getMyToast("密码不能为空").show();
            return false;
        }
        if (TextUtils.isEmpty(payCode)) {
            ToastFactory.getMyToast("请输入支付密码").show();
            return false;
        }
        if (payCode.length() > 16) {
            ToastFactory.getMyToast("支付密码太长了").show();
            return false;
        }
        return true;
    }
}
