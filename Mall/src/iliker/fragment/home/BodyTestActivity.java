package iliker.fragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.mall.SelectSexActivity;
import iliker.mall.depth.DepthActivity;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 身形自测修改碎片
 *
 * @author Administrator
 */
@SuppressLint("InflateParams")
public class BodyTestActivity extends Activity implements OnClickListener {
    private TextView shengao, xiongwei, xiongwei2, yaowei, tunwei, tizhong,
            tixing, bmi, tags, cuptype, dikusize;
    private SharedPreferences sf;
    private CheckBox synchronous;
    private CustomApplication cap;
    private SharedPreferences sncsf;
    private Button depthbtn, updatebtn;
    private AlertDialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        CustomApplication.actlist.add(this);
        sncsf = getSharedPreferences("sncremind", Context.MODE_PRIVATE);
        cap = (CustomApplication) getApplication();
        findViews();
        sf = getSharedPreferences("result", Context.MODE_PRIVATE);
        setListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        initdata();
    }

    private void initdata() {
        shengao.setText(sf.getString("height", ""));
        xiongwei.setText(sf.getString("onderbust", ""));
        xiongwei2.setText(sf.getString("underbust", ""));
        yaowei.setText(sf.getString("waist", ""));
        tunwei.setText(sf.getString("hip", ""));
        tizhong.setText(String.valueOf(sf.getString("weight", "") + "KG"));
        tixing.setText(sf.getString("typetext", ""));
        bmi.setText(sf.getString("bmi", ""));
        cuptype.setText(sf.getString("cuptype", ""));
        tags.setText(sf.getString("tags", ""));
        dikusize.setText(sf.getString("underpants", ""));
        if (sncsf.getInt("snc", 0) != 1) {
            synchronous.setChecked(true);
            synchronous.setEnabled(false);
            synchronous.setText("已同步");
        } else {
            synchronous.setChecked(false);
            synchronous.setText("未同步");
        }
    }

    private void findViews() {
        shengao = (TextView) findViewById(R.id.shengao);
        xiongwei = (TextView) findViewById(R.id.xiongwei);
        xiongwei2 = (TextView) findViewById(R.id.xiongwei2);
        yaowei = (TextView) findViewById(R.id.yaowei);
        tunwei = (TextView) findViewById(R.id.tunwei);
        tizhong = (TextView) findViewById(R.id.tizhong);
        tixing = (TextView) findViewById(R.id.tixing);
        bmi = (TextView) findViewById(R.id.bmi);
        cuptype = (TextView) findViewById(R.id.cuptype);
        tags = (TextView) findViewById(R.id.tags);
        dikusize = (TextView) findViewById(R.id.dikusize);
        synchronous = (CheckBox) findViewById(R.id.synchronous);
        depthbtn = (Button) findViewById(R.id.depthbtn);
        updatebtn = (Button) findViewById(R.id.updatebtn);
    }

    private void onCheckedChanged() {
        if (cap.getUserinfo() == null) {
            ToastFactory.getMyToast("登陆爱内秀同步数据").show();
            synchronous.setChecked(false);
            return;
        }
        if (!cap.networkIsAvailable()) {
            ToastFactory.getMyToast("连接网络后同步数据").show();
            synchronous.setChecked(false);
            return;
        }
        if (synchronous.isChecked()) {
            RequestParams params = new RequestParams(GeneralUtil.SYNCHRONOUS);
            params.addBodyParameter("userdata.userinfo.uid", cap.getUserinfo().getuID() + "");
            params.addBodyParameter("userdata.height", sf.getString("height", ""));
            params.addBodyParameter("userdata.onchest", sf.getString("onderbust", ""));
            params.addBodyParameter("userdata.underchest", sf.getString("underbust", ""));
            params.addBodyParameter("userdata.waist", sf.getString("waist", ""));
            params.addBodyParameter("userdata.hip", sf.getString("hip", ""));
            params.addBodyParameter("userdata.weight", sf.getString("weight", ""));
            params.addBodyParameter("userdata.bodytype", sf.getString("typetext", ""));
            params.addBodyParameter("userdata.bmi", sf.getString("bmi", ""));
            params.addBodyParameter("userdata.cuptype", sf.getString("cuptype", ""));
            params.addBodyParameter("userdata.tags", sf.getString("tags", ""));
            params.addBodyParameter("userdata.pants", sf.getString("underpants", ""));
            getHttpUtils().post(params,
                    new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String responseString) {
                            JSONObject jsonObject = JSON.parseObject(responseString);
                            if (jsonObject.getBooleanValue("success")) {
                                ToastFactory.getMyToast("已同步").show();
                                synchronous.setChecked(true);
                                synchronous.setEnabled(false);
                                synchronous.setText("已同步");
                                getSharedPreferences(
                                        "sncremind", Context.MODE_PRIVATE)
                                        .edit().clear().apply();
                            }
                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {
                            ToastFactory.getMyToast("同步出错系统会再次尝试同步")
                                    .show();
                            synchronous.setChecked(false);
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

    private void setListener() {
        depthbtn.setOnClickListener(this);
        updatebtn.setOnClickListener(this);
        synchronous.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.depthbtn:
                createAlertView();
                break;
            case R.id.updatebtn:
                Intent intent2 = new Intent(this, SelectSexActivity.class);
                intent2.putExtra("istwos", true);
                startActivity(intent2);
                break;
            case R.id.synchronous:
                onCheckedChanged();
                break;
        }
    }

    private void createAlertView() {
        if (dlg == null)
            dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        if (window != null) {
            window.setContentView(R.layout.alertpop_layout);
            Button button = (Button) window.findViewById(R.id.forward);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(BodyTestActivity.this, DepthActivity.class);
                    startActivity(intent);
                }
            });
            Button button1 = (Button) window.findViewById(R.id.cancel);
            button1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });
        }
    }

    @Override
    public void onPause() {
        if (dlg != null) {
            dlg.dismiss();
        }
        super.onPause();
    }
}
