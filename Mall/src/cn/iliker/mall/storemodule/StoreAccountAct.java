package cn.iliker.mall.storemodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import iliker.entity.StoreAccountInfo;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.utils.HttpHelp.getHttpUtils;

public class StoreAccountAct extends AppCompatActivity {
    private TextView tAccount, tBalance;
    private int storeId;
    private String account;
    private ListView bookKeeplv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = getIntent().getStringExtra("account");
        storeId = getIntent().getIntExtra("storeId", 0);
        setContentView(R.layout.store_account);
        tAccount = (TextView) findViewById(R.id.account);
        bookKeeplv = (ListView) findViewById(R.id.bookKeeplv);
        tAccount.setText(account);
        tBalance = (TextView) findViewById(R.id.balance);
        getStoreBaclance();
    }

    public void getStoreBaclance() {
        RequestParams params = new RequestParams(GeneralUtil.STOREACCOUNTINFO);
        params.addBodyParameter("storeId", storeId + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                JSONObject jsonObject=JSON.parseObject(s);
                if (jsonObject.getBooleanValue("success")) {
//                    StoreAccountInfo storeAccountInfo = JSON.parseObject(s, StoreAccountInfo.class);
                    tBalance.setText(String.valueOf(jsonObject.getDouble("balance")));
//                    bookKeeplv.setAdapter(new AccountLogAdapter(storeAccountInfo.getIncomeInfos(), StoreAccountAct.this));
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
    }
}
