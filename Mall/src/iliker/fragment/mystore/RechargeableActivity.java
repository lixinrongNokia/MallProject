package iliker.fragment.mystore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import iliker.fragment.type.CheckStandActivity;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.utils.HttpHelp.getHttpUtils;

public class RechargeableActivity extends BaseStoreActivity implements View.OnClickListener {
    private View view;
    private TextView phonenum;
    private EditText numerical;
    private Button submitbtn;

    @Override
    protected void initMyViews() {
        title.setText("账户充值");
        view = View.inflate(this, R.layout.rechargeable_layout, null);
        findChildViews();
        initDatas();
        storeContent.addView(view);
    }

    private void findChildViews() {
        phonenum = (TextView) view.findViewById(R.id.phonenum);
        numerical = (EditText) view.findViewById(R.id.numerical);
        submitbtn = (Button) view.findViewById(R.id.submitbtn);
        submitbtn.setOnClickListener(this);
    }

    private void initDatas() {
        phonenum.setText(phone);
    }

    @Override
    public void onClick(View v) {
        submitbtn.setEnabled(false);
        final String numeval = numerical.getText().toString().trim();
        if (TextUtils.isEmpty(numeval)) return;
        if (Integer.parseInt(numeval) == 0) return;
        RequestParams re = new RequestParams(GeneralUtil.RECHARGEABLESVC);
        re.addBodyParameter("rechargeableorder.paytype", "other");
        re.addBodyParameter("rechargeableorder.amount", numeval);
        re.addBodyParameter("rechargeableorder.fromPhone", userInfo.getPhone());
        re.addBodyParameter("rechargeableorder.taggetPhone", userInfo.getPhone());
        re.addBodyParameter("rechargeableorder.remarks", "账户充值");
        getHttpUtils().post(re, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                submitbtn.setEnabled(true);
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBoolean("success")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("out_trade_no", jsonObject.getString("out_trade_no"));// 订单号
                    bundle.putString("subject", "账户充值");// 商品名
                    bundle.putString("body", "iliker buy");// 商品详情
                    bundle.putString("goods_type", "0");//商品类型1为实物0为虚拟
                    bundle.putDouble("total_fee", jsonObject.getDoubleValue("total_amount"));// 总金额
                    Intent intent = new Intent(RechargeableActivity.this,
                            CheckStandActivity.class);
                    intent.putExtra("shopdata", bundle);
                    startActivity(intent);
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                submitbtn.setEnabled(true);
                ToastFactory.getMyToast("网络错误").show();
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
