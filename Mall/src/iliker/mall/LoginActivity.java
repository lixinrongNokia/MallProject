package iliker.mall;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import iliker.entity.UserInfo;
import iliker.entity.WXUser;
import iliker.utils.DBHelper;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.ref.WeakReference;

import static com.fjl.widget.ToastFactory.getMyToast;
import static com.iliker.application.CustomApplication.openIMLogin;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 用户登录activity
 *
 * @author Administrator
 */
@ContentView(R.layout.login)
public class LoginActivity extends BaseActivity implements OnClickListener {
    private TextView text, looking;// 注册文本
    private EditText textEdit, pwdEdit;// 昵称密码文本框
    private Button logbtn, fecelogin;// 登录按钮
    private UserInfo userinfo;
    private ImageView closebtn;
    private String logtext, pwd;
    private final MyHandler handler = new MyHandler(this);
    @ViewInject(R.id.store_login_link)
    private TextView store_login_link;
    private ImageButton btn_wx_login;
    private Dialog dialog;
    private final String GETWXCODESUCCESSFUL = "com.tencent.mm.sdk.openapi.authorization_code";

    class MyHandler extends Handler {
        private final WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = (LoginActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        int i = (int) DBHelper.getDB().saveUserInfo(userinfo);
                        if (i > 0) {
                            getMyToast("登录成功").show();
                            getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 1).apply();
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void subClassInit() {
        dialog = DialogFactory.initDialog(this);
        findViews();
        text.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        looking.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        store_login_link.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        setListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GETWXCODESUCCESSFUL);
        registerReceiver(mBatteryReceiver, filter);
    }

    private void findViews() {
        text = (TextView) findViewById(R.id.prompt);
        looking = (TextView) findViewById(R.id.looking);
        textEdit = (EditText) findViewById(R.id.username);
        pwdEdit = (EditText) findViewById(R.id.logpwd);
        logbtn = (Button) findViewById(R.id.submit);
        fecelogin = (Button) findViewById(R.id.facelogin);
        closebtn = (ImageView) findViewById(R.id.closebtn);
        btn_wx_login = (ImageButton) findViewById(R.id.wx_account_login);
    }

    private void setListener() {
        btn_wx_login.setOnClickListener(this);
        store_login_link.setOnClickListener(this);
        logbtn.setOnClickListener(this);
        text.setOnClickListener(this);
        looking.setOnClickListener(this);
        closebtn.setOnClickListener(this);
        fecelogin.setOnClickListener(this);
        textEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                logtext = s.toString();
                if (!TextUtils.isEmpty(pwd)) {
                    pwdEdit.setText("");
                    logbtn.setEnabled(false);
                    logbtn.setBackgroundColor(LoginActivity.this.getResources()
                            .getColor(R.color.stroke));
                }

            }

        });
        pwdEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pwd = s.toString();
                if (!TextUtils.isEmpty(logtext)) {
                    if (pwd.length() > 0) {
                        logbtn.setEnabled(true);
                        logbtn.setBackgroundColor(LoginActivity.this
                                .getResources().getColor(R.color.logcolor));
                    } else {
                        logbtn.setEnabled(false);
                        logbtn.setBackgroundColor(LoginActivity.this
                                .getResources().getColor(R.color.stroke));

                    }
                }
            }

        });
    }

    /**
     * 处理登录事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                RequestParams params = new RequestParams(GeneralUtil.LOGINON);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);// 隐藏虚拟键盘
                }
                params.addBodyParameter("logtext", logtext);
                params.addBodyParameter("password", MD5Util.getMD5Str(pwd));
                params.addBodyParameter("device", "app");
                getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getBoolean("success")) {
                            userinfo = JSON.parseObject(jsonObject.getJSONObject("userInfo").toJSONString(), UserInfo.class);
                            openIMLogin(userinfo.getPhone(), userinfo.getPassword());
                            CustomApplication.customApplication.bindAccount(userinfo.getNickName());
                            handler.sendEmptyMessageDelayed(0, 800);
                        } else {
                            getMyToast("登录失败").show();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        getMyToast("登录失败").show();
                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
                break;
            case R.id.prompt:
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.looking:
                Intent look = new Intent(this, LookingOneActivity.class);
                startActivity(look);
                break;
            case R.id.facelogin:
                Intent faceintent = new Intent(this, FaceLoginActivity.class);
                startActivity(faceintent);
                break;
            case R.id.closebtn:
                finish();
                break;
            case R.id.store_login_link:
                if (CustomApplication.customApplication.getStoreInfo() == null) {
                    startActivity(new Intent(this, StoreLoginActivity.class));
                } else ToastFactory.getMyToast("一个门店已登陆").show();
                break;
            case R.id.wx_account_login:
                IWXAPI api = WXAPIFactory.createWXAPI(this, "wx82a6291f4ce0547e");
                if (api.isWXAppInstalled()) {//检测微信是否安装
                    boolean b = api.registerApp("wx82a6291f4ce0547e");
                    if (b) {
                        dialog.show();
                        SendAuth.Req req = new SendAuth.Req();
                        req.scope = "snsapi_userinfo";
                        req.state = "wechat_sdk_app_login";
                        api.sendReq(req);
                    }
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://weixin.qq.com")));
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBatteryReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (GETWXCODESUCCESSFUL.equals(intent.getAction())) {
                if (intent.getBooleanExtra("success", false)) {
                    getServerProperty(intent.getStringExtra("code"));
                } else {
                    dialog.dismiss();
                    ToastFactory.getMyToast(intent.getStringExtra("msg")).show();
                }
            }
        }
    };

    private void getServerProperty(String code) {
        RequestParams params = new RequestParams(GeneralUtil.GETSERVERPROPERTY);
        params.addBodyParameter("code", code);
        params.addBodyParameter("device", "app");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBooleanValue("success")) {
                    getWXUserData(jsonObject.getString("openid"));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getWXUserData(String openid) {
        RequestParams params = new RequestParams(GeneralUtil.GETWXUSERDATA);
        params.addBodyParameter("openid", openid);
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                dialog.dismiss();
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBooleanValue("success")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("wxuser");
                    if (jsonObject1.getBooleanValue("onbind")) {
                        userinfo = JSON.parseObject(jsonObject.getJSONObject("wxuser").toJSONString(), UserInfo.class);
                        openIMLogin(userinfo.getPhone(), userinfo.getPassword());
                        CustomApplication.customApplication.bindAccount(userinfo.getNickName());
                        handler.sendEmptyMessageDelayed(0, 800);
                    } else {
                        WXUser wxUser = JSON.parseObject(jsonObject1.toJSONString(), WXUser.class);
                        Intent intent = new Intent(LoginActivity.this, BindWXUserActivity.class);
                        intent.putExtra("wxUser", wxUser);
                        startActivity(intent);
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

    // 处理后退键的情况
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getSharedPreferences("applogin", Context.MODE_PRIVATE).edit()
                    .clear().apply();
            this.finish(); // finish当前activity
            overridePendingTransition(R.anim.back_left_in,
                    R.anim.back_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
