package iliker.mall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iliker.application.CustomApplication;
import iliker.entity.UserInfo;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.CheckUtil;
import iliker.utils.GeneralUtil;
import iliker.utils.MD5Util;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.fjl.widget.ToastFactory.getMyToast;
import static com.iliker.application.CustomApplication.openIMLogin;
import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 昵称密码设置页面
 *
 * @author Administrator
 */
public class CompleteActivity extends BaseStoreActivity implements OnClickListener {
    private EditText nicknameEdit, regpwdEdit, confirmpwdEdit;
    private Button sub;
    private String phone;
    private String recode;
    private ProgressDialog progressDialog;

    private int uID = 0;

    @Override
    protected void initMyViews() {
        CustomApplication.actlist.add(this);
        title.setText("昵称与密码");
        View view = View.inflate(this, R.layout.complete, null);
        storeContent.addView(view);
        phone = getIntent().getStringExtra("phone");
        recode = getIntent().getStringExtra("recode");
        findViews(view);
        onListener();
    }

    private void findViews(View view) {
        nicknameEdit = (EditText) view.findViewById(R.id.regname);
        regpwdEdit = (EditText) view.findViewById(R.id.regpwd);
        confirmpwdEdit = (EditText) view.findViewById(R.id.confirmpwd);
        sub = (Button) view.findViewById(R.id.sub);
    }

    private void onListener() {
        sub.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String nickname = nicknameEdit.getText().toString().trim();
        String pwd = regpwdEdit.getText().toString().trim();
        String regpwd = confirmpwdEdit.getText().toString().trim();
        if (TextUtils.isEmpty(nickname) || !CheckUtil.StringFilter(nickname) || TextUtils.isEmpty(pwd) || !CheckUtil.pwdFilter(pwd) || !pwd.equals(regpwd)) {
            getMyToast("请填写必要项目").show();
            return;
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage("请稍后...");
        progressDialog.show();
        registerUser(nickname, pwd);

    }

    /**
     * 把注册数据插入数据库，本地同时保存一份，同时跳转页面
     */
    private void registerUser(final String nickname, String pwd) {
        RequestParams params = new RequestParams(GeneralUtil.REGISTER);
        final String parspwd = MD5Util.getMD5Str(pwd);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nickname", nickname);
        jsonObject.put("password", parspwd);
        jsonObject.put("phone", phone);
        jsonObject.put("superiornum", recode);
        params.addBodyParameter("userinfo", jsonObject.toJSONString());

        getHttpUtils().post(params,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        progressDialog.dismiss();
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getBoolean("success")) {
                            uID = jsonObject.getIntValue("uid");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                            UserInfo userInfo = new UserInfo(uID, nickname, parspwd, phone, recode, sdf.format(new Date()));
                            int i = getDB().register(userInfo);
                            if (i > 0) {
                                openIMLogin(userInfo.getPhone(), userInfo.getPassword());
                                CustomApplication.customApplication.bindAccount(userInfo.getNickName());
                                getMyToast(jsonObject.getString("msg")).show();
                                getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 1).apply();
                                Intent intent = new Intent(CompleteActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                            } else
                                getMyToast("稍后再试!").show();
                        } else {
                            getMyToast(jsonObject.getString("msg")).show();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        progressDialog.dismiss();
                        getMyToast("网络中断").show();
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
