package iliker.mall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.iliker.mall.R;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.mob.MobSDK;
import iliker.utils.CheckUtil;

import static com.fjl.widget.ToastFactory.getMyToast;

//找回密码第二步
public class LookingTwoActivity extends Activity implements OnClickListener {
    private Button regbtn, getregcode;
    private String backstr, regphone;
    private TextView nickname, phone;
    private EditText regcheck;
    private ProgressDialog progressDialog;
    private int recLen = 30;
    private boolean ready = true;
    private ImageView closebtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookingtwo);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        backstr = bundle.getString("backstr");
        regphone = bundle.getString("regphone");
        findViews();
        initdata();
        setListener();
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

    private void initdata() {
        if (CheckUtil.isMobileNO2(backstr)) {
            nickname.setVisibility(View.GONE);
            String repl = backstr.substring(3, 7);
            String newstr = backstr.substring(0, 3)
                    + repl.replaceAll(repl, "****")
                    + backstr.substring(7, backstr.length());
            phone.setVisibility(View.VISIBLE);
            phone.setText(String.valueOf("关联的手机号:" + newstr));
        } else {
            phone.setVisibility(View.GONE);
            nickname.setVisibility(View.VISIBLE);
            nickname.setText(String.valueOf(backstr + "，你好！"));
        }
    }

    private void findViews() {
        regbtn = (Button) findViewById(R.id.regbtn);
        getregcode = (Button) findViewById(R.id.getregcode);
        nickname = (TextView) findViewById(R.id.nickname);
        phone = (TextView) findViewById(R.id.phone);
        regcheck = (EditText) findViewById(R.id.regcheck);
        closebtn = (ImageView) findViewById(R.id.closebtn);
    }

    private void setListener() {
        regbtn.setOnClickListener(this);
        getregcode.setOnClickListener(this);
        closebtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regbtn:
                String inputcode = regcheck.getText().toString().trim();
                if (TextUtils.isEmpty(inputcode)) {
                    getMyToast("请输入验证码").show();
                    return;
                }
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(this);
                }
                progressDialog.setCancelable(false);
                progressDialog.setMessage("正在验证...");
                progressDialog.show();
                SMSSDK.submitVerificationCode("86", regphone, inputcode);
                break;
            case R.id.getregcode:
                if (ready)
                    ready = false;
                getregcode.setEnabled(false);
                if (regphone == null) {
                    regphone = backstr;
                }
                SMSSDK.getVerificationCode("86", regphone);
                Message message = handler.obtainMessage(1); // Message
                handler.sendMessageDelayed(message, 1000);
                break;
            case R.id.closebtn:
                if (!ready) {
                    AlertDialog.Builder builder = new Builder(this);
                    builder.setTitle("提醒:");
                    builder.setMessage("验证码在发送中,稍等下...");
                    builder.setPositiveButton("还是返回",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                    finish();
                                }
                            }).setNegativeButton("取消", null).show();
                } else {
                    finish();
                }
                break;
        }
    }

    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    Log.e("event", "event=" + event);
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        // 短信注册成功后，返回MainActivity,然后提示新好友
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                            ready = true;
                            regcheck.setText(null);
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            getMyToast("验证成功")
                                    .show();
                            Intent intent = new Intent(LookingTwoActivity.this,
                                    LookingThreeActivity.class);
                            intent.putExtra("regphone", regphone);
                            startActivity(intent);
                            finish();
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
                        getregcode.setEnabled(true);
                        getregcode.setText("获取验证码");
                        getMyToast(data.toString()).show();
                        ((Throwable) data).printStackTrace();
                    }
                    break;
                case 1:
                    recLen--;
                    getregcode.setText(String.valueOf(recLen + "/30秒"));
                    if (recLen > 0) {
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 1000); // send message
                    } else {
                        handler.removeMessages(1);
                        recLen = 30;
                        getregcode.setEnabled(true);
                        getregcode.setText("获取验证码");
                    }
                    break;
            }

        }

    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!ready) {
                AlertDialog.Builder builder = new Builder(this);
                builder.setTitle("提醒:");
                builder.setMessage("验证码稍有延迟,稍等下...");
                builder.setPositiveButton("还是返回",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                finish();
                            }
                        }).setNegativeButton("继续等待", null).show();
            } else
                finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        handler.removeMessages(1);
        if (!ready) {
            SMSSDK.unregisterAllEventHandler();
        }
        super.onDestroy();
    }
}
