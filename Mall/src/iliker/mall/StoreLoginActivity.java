package iliker.mall;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import cn.iliker.mall.storemodule.Store_MainActivity;
import com.alibaba.fastjson.JSON;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.entity.StoreInfo;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;
import static iliker.utils.ParsJsonUtil.isEmail;

@ContentView(R.layout.store_login_layout)
public class StoreLoginActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.login_account)
    private EditText eAccount;//登录账号输入框

    @ViewInject(R.id.login_pwd)
    private EditText ePwd;//登录密码输入框

    @ViewInject(R.id.login_unRemember)
    private TextView unRemember;//忘记密码文本

    @ViewInject(R.id.login_btn)
    private Button bLogin;//登录按钮

    @ViewInject(R.id.login_eye)
    private ImageView iEye;//密码是否可见点击切换图片

    @ViewInject(R.id.swithGUI)
    private TextView swithGUI;

    private String loginAccount;
    private String loginPwd;

    private boolean sign;
    private Dialog dialog;

    @Override
    protected void subClassInit() {
        dialog = DialogFactory.initDialog(this);
        bLogin.setOnClickListener(this);
        iEye.setOnClickListener(this);
        swithGUI.setOnClickListener(this);
    }

    private boolean regDate() {
        loginAccount = eAccount.getText().toString().trim();
        loginPwd = ePwd.getText().toString().trim();
        return !TextUtils.isEmpty(loginAccount) && isEmail(loginAccount) && !TextUtils.isEmpty(loginPwd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                if (regDate()) {
                    dialog.show();
                    RequestParams params = new RequestParams(GeneralUtil.STORELONINSVC);
                    params.addBodyParameter("loginEmail", loginAccount);
                    params.addBodyParameter("loginPwd", MD5Util.getMD5Str(loginPwd));
                    getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            dialog.dismiss();
                            if ("error".equals(result)) {
                                ToastFactory.getMyToast(getResources().getString(R.string.loginError)).show();
                            } else {
                                StoreInfo storeInfo = JSON.parseObject(result, StoreInfo.class);
                                long i = getDB().saveStoreInfo(storeInfo);
                                if (i > 0) {
                                    getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 0).apply();
                                    startActivity(new Intent(StoreLoginActivity.this, Store_MainActivity.class));
                                    CustomApplication.customApplication.bindAccount(storeInfo.getLoginEmail());
                                    /*for (Activity activity : CustomApplication.actlist) {
                                        activity.finish();
                                    }
                                    CustomApplication.actlist.clear();
                                    finish();*/
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {
                            dialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.login_eye:
                if (sign) {
                    sign = false;
                    ePwd.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Editable editable = ePwd.getText();
                    Selection.setSelection(editable, editable.length());
                } else {
                    sign = true;
                    ePwd.setInputType(0);
                    Editable editable = ePwd.getText();
                    Selection.setSelection(editable, editable.length());
                }
                break;
            case R.id.swithGUI:
                getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 1).apply();
                Intent switchIntent = new Intent(this, StartActivity.class);
                startActivity(switchIntent);
                finish();
                break;
        }
    }
}
