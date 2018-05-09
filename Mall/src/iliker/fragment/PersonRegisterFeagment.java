package iliker.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.iliker.mall.R;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import com.mob.MobSDK;
import iliker.mall.CompleteActivity;
import iliker.mall.LoginActivity;
import iliker.utils.CheckUtil;
import iliker.utils.GeneralUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.xing.qr_code.scan.MipcaActivityCapture;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.lang.ref.WeakReference;

import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.HttpHelp.getHttpUtils;


public class PersonRegisterFeagment extends BaseFragment implements View.OnClickListener {
    private EditText phoneEdit, smscodeEdit, reccodeEdit;
    private Button showtime, regbtn, qrcode;
    private String inputphone;
    private String recode;
    private int recLen = 30;
    private TextView login;
    private ProgressDialog progressDialog;
    private View view;
    private boolean ready;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private MyHandler handler;
    private final int REQUEST_CODE_ASK_READ_PHONE = 113;

    @Override
    protected View initSubclassView() {
        handler = new MyHandler(context);
        view = View.inflate(context, R.layout.register, null);
        findViews();
        onListener();
        MobSDK.init(context, "1750af71ac368", "4d12074cde95a19ce5abd9bfeb07f6fe");
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
        ready = true;
        askPermission();
        return view;
    }

    private class MyHandler extends Handler {
        private final WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            Context context = reference.get();
            if (context != null) {
                switch (msg.what) {
                    case 0:
                        int event = msg.arg1;
                        int result = msg.arg2;
                        Object data = msg.obj;
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // 短信验证成功后跳转下一步
                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                                progressDialog.dismiss();
                                Intent intent = new Intent(context, CompleteActivity.class);
                                intent.putExtra("phone", inputphone);
                                intent.putExtra("recode", recode);
                                context.startActivity(intent);
                            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                getMyToast("验证码已经发送")
                                        .show();
                            }
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            recLen = 30;
                            handler.removeMessages(1);
                            showtime.setEnabled(true);
                            showtime.setText("获取验证码");
                            getMyToast(data.toString()).show();
                            ((Throwable) data).printStackTrace();
                        }
                        break;
                    case 1:
                        recLen--;
                        showtime.setText(String.valueOf(recLen + "/30秒"));
                        if (recLen > 0) {
                            handler.sendEmptyMessageDelayed(1, 1000);
                            // message
                        } else {
                            handler.removeMessages(1);
                            recLen = 30;
                            showtime.setEnabled(true);
                            showtime.setText("获取验证码");
                        }
                        break;
                }
            }
        }
    }

    private void findViews() {
        phoneEdit = (EditText) view.findViewById(R.id.regphone);
        reccodeEdit = (EditText) view.findViewById(R.id.recommendcode);
        smscodeEdit = (EditText) view.findViewById(R.id.smscode);
        regbtn = (Button) view.findViewById(R.id.regbtn);
        showtime = (Button) view.findViewById(R.id.showtime);
        qrcode = (Button) view.findViewById(R.id.qrcode);
        login = (TextView) view.findViewById(R.id.login);
    }

    private void askPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            //检查权限是否已授权
            int hasPermission = context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            //如果没有授权
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                //请求权限，此方法异步执行，会弹出权限请求对话框，让用户授权，并回调 onRequestPermissionsResult 来告知授权结果
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_ASK_READ_PHONE);
            } else {//已经授权过
                //做一些你想做的事情，即原来不需要动态授权时做的操作
                readPhoneNum();
            }
        } else
            readPhoneNum();

    }

    @SuppressLint("HardwareIds")
    private void readPhoneNum() {
        TelephonyManager tm = (TelephonyManager) context.getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            String tmSerial = tm.getLine1Number();
            if (tmSerial.contains("+"))
                phoneEdit.setText(tmSerial.subSequence(3, tmSerial.length()));
            else
                phoneEdit.setText(tmSerial);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_READ_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readPhoneNum();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void onListener() {
        regbtn.setOnClickListener(this);
        showtime.setOnClickListener(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }
        });
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int asPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
                if (asPermission == PackageManager.PERMISSION_GRANTED) {
                    reccodeEdit.setText(null);
                    Intent intent1 = new Intent();
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.setClass(context, MipcaActivityCapture.class);
                    startActivityForResult(intent1, SCANNIN_GREQUEST_CODE);
                } else ToastFactory.getMyToast("没有获得相机权限").show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!CustomApplication.customApplication.networkIsAvailable()) {
            getMyToast("无网络，开启网络再操作！").show();
            return;
        }
        inputphone = phoneEdit.getText().toString().trim();
        String inputsmscode = smscodeEdit.getText().toString().trim();
        recode = reccodeEdit.getText().toString().trim();
        if (TextUtils.isEmpty(inputphone)) {
            getMyToast("请输入手机号！").show();
            return;
        }
        if (!CheckUtil.isMobileNO2(inputphone)) {
            getMyToast("手机号格式不正确！").show();
            return;
        }
        /*if (TextUtils.isEmpty(recode)) {
            getMyToast("请输入邀请码！").show();
            return;
        }*/

        switch (v.getId()) {
            case R.id.regbtn:
                if (TextUtils.isEmpty(inputsmscode)) {
                    getMyToast("请输入短信验证码！").show();
                    return;
                }
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(context);
                }
                progressDialog.setCancelable(false);
                progressDialog.setMessage("请稍后...");
                progressDialog.show();
                SMSSDK.submitVerificationCode("86", inputphone, inputsmscode);//校验手机号
                break;
            case R.id.showtime:
                RequestParams params = new RequestParams(GeneralUtil.EXISTSREG);
                params.addBodyParameter("phone", inputphone);
                params.addBodyParameter("recode", TextUtils.isEmpty(recode) ? "18680602795" : recode);
                getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String responseInfo) {
                        int errorCode = Integer.valueOf(responseInfo);
                        switch (errorCode) {
                            case -1:
                                getMyToast("推荐人不存在").show();
                                break;
                            case 0:
                                SMSSDK.getVerificationCode("86", inputphone);// 发送验证码
                                showtime.setEnabled(false);
                                handler.sendEmptyMessage(1);
                                break;
                            case 1:
                                getMyToast("手机号已使用").show();
                                break;
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
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case SCANNIN_GREQUEST_CODE:
                    String result = data.getStringExtra("result");
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (!jsonObject.isNull("phone")) {
                            String phone = jsonObject.getString("phone");
                            if (CheckUtil.isMobileNO2(phone)) {
                                reccodeEdit.setText(phone);
                            } else getMyToast("不正确数据").show();
                        }
                    } catch (JSONException e) {
                        getMyToast("不正确数据").show();
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (ready) {
            SMSSDK.unregisterAllEventHandler();
        }
        super.onDestroy();
    }
}
