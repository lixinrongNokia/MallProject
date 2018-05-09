package iliker.mall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.iliker.mall.R;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import com.mob.MobSDK;
import iliker.entity.UserInfo;
import iliker.entity.WXUser;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.CheckUtil;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static com.fjl.widget.ToastFactory.getMyToast;
import static com.iliker.application.CustomApplication.openIMLogin;
import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;

public class BindWXUserActivity extends BaseStoreActivity implements View.OnClickListener {
    private EditText edt_bind_phone;
    private EditText edt_sms_code;
    private EditText edt_nickName;
    private EditText edt_password;
    private EditText edt_recommendPhone;
    private Button btn_sendSMS;
    private Button btn_submit;
    private ProgressDialog progressDialog;
    private int recLen = 120;
    private boolean ready = true;
    private JSONObject inputJson = new JSONObject();

    @Override
    protected void initMyViews() {
        title.setText("绑定《爱内秀》");
        View view = View.inflate(this, R.layout.onbind_wx_user, null);
        storeContent.addView(view);
        initViews(view);
        WXUser user = getIntent().getParcelableExtra("wxUser");
        inputJson.put("unionID", user.getUnionid());
        inputJson.put("openid", user.getOpenid());
        setListener();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        MobSDK.init(this, "1e77534358d00", "ecbfb7e6740bff4234a1116faa9a1e1b");//1e77534358d00
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);
    }

    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    progressDialog.dismiss();
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    Log.e("event", "event=" + event);
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        // 短信注册成功后，返回MainActivity,然后提示新好友
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                            ready = true;
                            bindAction();
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            getMyToast("验证码已经发送")
                                    .show();
                        }
                    } else {
                        progressDialog.dismiss();
                        recLen = 120;
                        handler.removeMessages(1);
                        btn_sendSMS.setEnabled(true);
                        btn_sendSMS.setText("获取验证码");
                        getMyToast(data.toString()).show();
                        ((Throwable) data).printStackTrace();
                    }
                    break;
                case 1:
                    recLen--;
                    btn_sendSMS.setText(String.valueOf(recLen + "/120秒"));
                    if (recLen > 0) {
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 1000); // send message
                    } else {
                        handler.removeMessages(1);
                        recLen = 120;
                        btn_sendSMS.setEnabled(true);
                        btn_sendSMS.setText("获取验证码");
                    }
                    break;
            }
        }
    };

    private void setListener() {
        btn_sendSMS.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    private void bindAction() {
        RequestParams params = new RequestParams(GeneralUtil.REGUSERBIND);
        params.addBodyParameter("registerBean", inputJson.toJSONString());
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBooleanValue("success")) {
                    JSONObject user = jsonObject.getJSONObject("wxuser");
                    UserInfo userInfo = new UserInfo(user.getIntValue("uID"), user.getString("nickName"), user.getString("password"), user.getString("phone"), user.getString("superiornum"), user.getString("registered"),user.getBooleanValue("onbind"));
                    int i = getDB().register(userInfo);
                    if (i > 0) {
                        openIMLogin(userInfo.getPhone(), userInfo.getPassword());
                        CustomApplication.customApplication.bindAccount(userInfo.getNickName());
                        getMyToast(jsonObject.getString("msg")).show();
                        getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 1).apply();
                        Intent intent = new Intent(BindWXUserActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    }
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
                progressDialog.dismiss();
            }
        });
    }

    private void initViews(View view) {
        edt_bind_phone = (EditText) view.findViewById(R.id.bind_phone);
        edt_sms_code = (EditText) view.findViewById(R.id.sms_code);
        edt_nickName = (EditText) view.findViewById(R.id.nickName);
        edt_password = (EditText) view.findViewById(R.id.password);
        edt_recommendPhone = (EditText) view.findViewById(R.id.recommendPhone);
        btn_sendSMS = (Button) view.findViewById(R.id.sendSMS);
        btn_submit = (Button) view.findViewById(R.id.submit);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendSMS) {
            if (regPhoneInput()) {
                progressDialog.show();
                checkPhone();
            } else {
                ToastFactory.getMyToast("请输入待绑定的手机号").show();
            }
        } else {
            if (regInput()) {
                progressDialog.setMessage("正在验证...");
                progressDialog.show();
                SMSSDK.submitVerificationCode("86", inputJson.getString("phoneNum"), inputJson.getString("regcode"));
            }
        }
    }

    private void checkPhone(){
        RequestParams params = new RequestParams(GeneralUtil.BINDWXCHECK);
        params.addBodyParameter("phone", inputJson.getString("phoneNum"));
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBooleanValue("success")) {
                    progressDialog.dismiss();
                    ToastFactory.getMyToast(jsonObject.getString("msg")).show();
                }else {
                    if (ready) ready = false;
                    btn_sendSMS.setEnabled(false);
                    SMSSDK.getVerificationCode("86", inputJson.getString("phoneNum"));
                    Message message = handler.obtainMessage(1); // Message
                    handler.sendMessageDelayed(message, 1000);
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
                progressDialog.dismiss();
            }
        });
    }

    private boolean regPhoneInput() {
        String regphone = edt_bind_phone.getText().toString().trim();
        if (TextUtils.isEmpty(regphone) || !CheckUtil.isMobileNO2(regphone)) {
            ToastFactory.getMyToast("手机号码为空或格式有误").show();
            return false;
        }
        inputJson.put("phoneNum", regphone);
        return true;
    }

    private boolean regInput() {
        String regphone = edt_bind_phone.getText().toString().trim();
        if (TextUtils.isEmpty(regphone) || !CheckUtil.isMobileNO2(regphone)) {
            ToastFactory.getMyToast("手机号码为空或格式有误").show();
            return false;
        }
        inputJson.put("phoneNum", regphone);

        String regcode = edt_sms_code.getText().toString().trim();
        if (TextUtils.isEmpty(regcode)) {
            ToastFactory.getMyToast("验证码不能为空").show();
            return false;
        }
        inputJson.put("regcode", regcode);

        String regNickName = edt_nickName.getText().toString().trim();
        if (TextUtils.isEmpty(regNickName) || !CheckUtil.StringFilter(regNickName)) {
            ToastFactory.getMyToast("昵称不合要求").show();
            return false;
        }
        inputJson.put("nickname", regNickName);

        String regPwd = edt_password.getText().toString().trim();
        if (TextUtils.isEmpty(regPwd) || !CheckUtil.pwdFilter(regPwd)) {
            ToastFactory.getMyToast("密码不合要求").show();
            return false;
        }
        inputJson.put("password", MD5Util.getMD5Str(regPwd));

        String superiornum = edt_recommendPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(superiornum)) {
            inputJson.put("superiornum", superiornum);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(1);
        if (!ready) {
            SMSSDK.unregisterAllEventHandler();
        }
        super.onDestroy();
    }
}
