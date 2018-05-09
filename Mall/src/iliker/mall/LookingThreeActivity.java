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

import com.iliker.application.CustomApplication;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import cn.iliker.mall.R;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;

import static com.fjl.widget.DialogFactory.initDialog;
import static com.fjl.widget.ToastFactory.getMyToast;
import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;

public class LookingThreeActivity extends Activity implements OnClickListener {
    private Button regbtn;
    private EditText pwd, regpwd;
    private Dialog progressDialog;
    private String regphone;
    private ImageView closebtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookingthree);
        regphone = getIntent().getStringExtra("regphone");
        findViews();
        setListener();
    }

    private void findViews() {
        regbtn = (Button) findViewById(R.id.regbtn);
        pwd = (EditText) findViewById(R.id.pwd);
        regpwd = (EditText) findViewById(R.id.regpwd);
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
                // 修改密码
                String fpwd = pwd.getText().toString().trim();
                String rpwd = regpwd.getText().toString().trim();
                if (TextUtils.isEmpty(fpwd) || TextUtils.isEmpty(rpwd)) {
                    getMyToast("请填写新密码")
                            .show();
                    return;
                }
                if (fpwd.length() < 6 || fpwd.length() > 16) {
                    getMyToast("密码长度介于6位至16位之间")
                            .show();
                    return;
                }
                if (!rpwd.equals(fpwd)) {
                    getMyToast("密码不一致")
                            .show();
                    return;
                }
                if (progressDialog == null) {
                    progressDialog = initDialog(this);
                }
                progressDialog.show();
                RequestParams params = new RequestParams(GeneralUtil.UPDATEPWD);
                params.addBodyParameter("password", MD5Util.getMD5Str(fpwd));
                params.addBodyParameter("phone", regphone);
                getHttpUtils().post(params,
                        new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                progressDialog.dismiss();
                                getMyToast(result).show();
                                if ("修改成功！马上登录".equals(result)) {
                                    getDB().remoreUser();
                                    customApplication.removeUserinfo();
                                    Intent intent = new Intent(
                                            LookingThreeActivity.this,
                                            LoginActivity.class);
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
                break;
            case R.id.closebtn:
                this.finish();
                break;
        }
    }
}
