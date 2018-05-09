package iliker.fragment.home;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.iliker.application.CustomApplication;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import cn.iliker.mall.R;
import iliker.utils.GeneralUtil;

import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.HttpHelp.getHttpUtils;

public class ReportActivity extends Activity implements OnClickListener,
        OnCheckedChangeListener {
    private TextView shengao, xiongwei, xiongwei2, yaowei, tunwei, tizhong,
            tixing, bmi, tags, cuptype, dikusize;
    private SharedPreferences sf;
    private ImageView backbtn;
    private CustomApplication cap;
    private SharedPreferences sncsf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        sncsf = getSharedPreferences("sncremind", Context.MODE_PRIVATE);
        cap = (CustomApplication) getApplication();
        findViews();
        sf = getSharedPreferences("result", MODE_PRIVATE);
        setListener();
    }

    private void setListener() {
        backbtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initdata();
    }

    private void initdata() {
        shengao.setText(sf.getString("h", ""));
        xiongwei.setText(sf.getString("b", ""));
        xiongwei2.setText(sf.getString("bd", ""));
        yaowei.setText(sf.getString("w", ""));
        tunwei.setText(sf.getString("t", ""));
        tizhong.setText(String.valueOf(sf.getString("bw", "") + "KG"));
        tixing.setText(sf.getString("typetext", ""));
        bmi.setText(sf.getString("bmi", ""));
        cuptype.setText(sf.getString("cuptype", ""));
        tags.setText(sf.getString("tags", ""));
        dikusize.setText(sf.getString("underpants", ""));
    }

    private void findViews() {
        backbtn = (ImageView) findViewById(R.id.backbtn);
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
        ((CheckBox) findViewById(R.id.synchronous)).setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (sncsf.getInt("snc", 0) != 1) {
            getMyToast("你没有新的数据需要同步")
                    .show();
            return;
        }
        if (cap.getUserinfo() == null) {
            getMyToast("加入爱内秀同步你的数据").show();
            return;
        }
        if (!cap.networkIsAvailable()) {
            getMyToast("等你连接网络后为你同步数据").show();
            return;
        }
        nm.cancel(1);
        if (isChecked) {
            RequestParams params = new RequestParams(GeneralUtil.SYNCHRONOUS);
            params.addBodyParameter("nickname", cap.getUname());
            params.addBodyParameter("h", sf.getString("h", ""));
            params.addBodyParameter("b", sf.getString("b", ""));
            params.addBodyParameter("bd", sf.getString("bd", ""));
            params.addBodyParameter("w", sf.getString("w", ""));
            params.addBodyParameter("t", sf.getString("t", ""));
            params.addBodyParameter("bw", sf.getString("bw", "") + "KG");
            params.addBodyParameter("typetext", sf.getString("typetext", ""));
            params.addBodyParameter("bmi", sf.getString("bmi", ""));
            params.addBodyParameter("cuptype", sf.getString("cuptype", ""));
            params.addBodyParameter("tags", sf.getString("tags", ""));
            params.addBodyParameter("uw", sf.getString("uw", ""));
            getHttpUtils().post(params,
                    new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            getMyToast(result).show();
                            if ("已同步".equals(result)) {
                                getSharedPreferences("sncremind", Context.MODE_PRIVATE).edit().clear().apply();
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            getMyToast("同步出错系统会再次尝试同步").show();
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
}
