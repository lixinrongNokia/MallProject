package iliker.fragment.finding;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.fjl.widget.ToastFactory;
import iliker.entity.UserInfo;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.math.BigDecimal;

import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 打赏页面
 * Created by WDHTC on 2016/5/24.
 */
public class RewardActivity extends BaseStoreActivity implements OnClickListener {
    private EditText amount;//金额
    private EditText paypwd;//支付密码
    private Button submitBtn;//提交
    private double remainingsum;
    private String numeval;
    private UserInfo webuser;

    @Override
    protected void initMyViews() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        webuser = (UserInfo) bundle.getSerializable("userinfo");
        remainingsum = bundle.getDouble("remainingsum");
        View view = View.inflate(this, R.layout.reward_layout, null);
        findChildViews(view);
        storeContent.addView(view);
        title.setText("对" + webuser.getNickName() + "打赏");
    }

    private void findChildViews(View v) {
        amount = (EditText) v.findViewById(R.id.amount);
        paypwd = (EditText) v.findViewById(R.id.paypwd);
        submitBtn = (Button) v.findViewById(R.id.submitbtn);
        ((TextView) v.findViewById(R.id.remainingsum)).setText(String.valueOf(remainingsum));
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (regProperties()) {
            submitBtn.setEnabled(false);
            RequestParams params = new RequestParams(GeneralUtil.PAYTIP);
            params.addBodyParameter("amount", numeval);//金额
            params.addBodyParameter("fromPhone", phone);//支出账户
            params.addBodyParameter("taggetPhone", webuser.getPhone());//收入账户
            params.addBodyParameter("desc", userInfo.getNickName() + "打赏:" + webuser.getNickName());//说明
            getHttpUtils().post(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String responseInfo) {
                    if (("1").equals(responseInfo)) {
                        ToastFactory.getMyToast("打赏成功!").show();
                        finish();
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    submitBtn.setEnabled(true);
                    ToastFactory.getMyToast("网络错误").show();
                }

                @Override
                public void onCancelled(CancelledException e) {

                }

                @Override
                public void onFinished() {
//                    ToastFactory.getMyToast("没完成").show();
                }
            });
        }
    }

    private boolean regProperties() {
        numeval = amount.getText().toString().trim();
        if (TextUtils.isEmpty(numeval)) return false;
        BigDecimal balanceBc = new BigDecimal(remainingsum);
        BigDecimal amountBc = new BigDecimal(Integer.parseInt(numeval));
        if (Integer.parseInt(numeval) == 0) return false;
        if (balanceBc.compareTo(amountBc) < 0) {
            ToastFactory.getMyToast("余额不够").show();
            return false;
        }
        String pwd = paypwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            return false;
        }
        if (!MD5Util.getMD5Str(pwd).equals(userInfo.getPassword())) {
            ToastFactory.getMyToast("密码不正确").show();
            return false;
        }
        return true;
    }
}
