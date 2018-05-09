package iliker.mall;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.iliker.mall.R;
import iliker.utils.CheckUtil;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static com.fjl.widget.DialogFactory.initDialog;
import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.HttpHelp.getHttpUtils;

//找回密码第一步
public class LookingOneActivity extends Activity implements OnClickListener {
    private Button regbtn;
    private EditText regcheck;
    private Dialog progressDialog;
    private String regphone = null;
    private ImageView closebtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookingone);
        findViews();
        setListener();
    }

    private void findViews() {
        regbtn = (Button) findViewById(R.id.regbtn);
        regcheck = (EditText) findViewById(R.id.regcheck);
        closebtn = (ImageView) findViewById(R.id.closebtn);
    }

    private void setListener() {
        regbtn.setOnClickListener(this);
        closebtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regbtn:
                String regstr = regcheck.getText().toString().trim();
                if (TextUtils.isEmpty(regstr)) {
                    getMyToast("输入不能为空").show();
                    return;
                }
                if (!CheckUtil.isMobileNO2(regstr)) {
                    getMyToast("手机号格式不正确！").show();
                    return;
                }
                regphone = regstr;
                AasyncCheck(regstr);
                break;
            case R.id.closebtn:
                this.finish();
                break;
        }
    }

    private void AasyncCheck(String regstr) {
        if (progressDialog == null) {
            progressDialog = initDialog(this);
        }
        progressDialog.show();
        RequestParams params = new RequestParams(GeneralUtil.LOOKPWD);
        params.addBodyParameter("regstr", regstr);
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                if ("对不起用户不存在".equals(result)) {
                    getMyToast(result)
                            .show();
                } else {
                    Bundle bundle = new Bundle();
                    if (regphone != null) {
                        bundle.putString("regphone", regphone);
                    }
                    bundle.putString("backstr", result);
                    Intent intent = new Intent(LookingOneActivity.this,
                            LookingTwoActivity.class);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressDialog.dismiss();
                getMyToast("发生错误")
                        .show();
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
