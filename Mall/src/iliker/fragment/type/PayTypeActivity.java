package iliker.fragment.type;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.iliker.mall.R;
import iliker.entity.StoreInfo;
import iliker.utils.XmlUtil;

import java.util.Collections;
import java.util.List;

import static com.fjl.widget.ToastFactory.getMyToast;

public class PayTypeActivity extends Activity implements OnClickListener,
        OnCheckedChangeListener {
    private RadioGroup couriertype, shiptype;
    private RadioButton onpay, storeon, ordinary, door;
    private LinearLayout stores;
    private List<StoreInfo> storesdata;
    private TextView storeName, storeAddress;
    private Button save_btn;
    private String paytype = "在线支付", sendtype = "普通快递", point = "";// 支付方式，配送方式，自提点
    private boolean nodoor = true;
    private int storeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paytype);
        storesdata = XmlUtil.xml2Object();
        if (storesdata != null)
            Collections.sort(storesdata);
        findViews();
        setListener();
    }

    private void findViews() {
        shiptype = (RadioGroup) findViewById(R.id.shiptype);
        couriertype = (RadioGroup) findViewById(R.id.couriertype);
        onpay = (RadioButton) findViewById(R.id.onpay);
        storeon = (RadioButton) findViewById(R.id.storeon);
        ordinary = (RadioButton) findViewById(R.id.ordinary);
        door = (RadioButton) findViewById(R.id.door);
        stores = (LinearLayout) findViewById(R.id.stores);
        storeName = (TextView) findViewById(R.id.storeName);
        storeAddress = (TextView) findViewById(R.id.storeAddress);
        save_btn = (Button) findViewById(R.id.save_btn);
        findViewById(R.id.backbtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PayTypeActivity.this.finish();
            }
        });

    }

    private void setListener() {
        shiptype.setOnCheckedChangeListener(this);
        couriertype.setOnCheckedChangeListener(this);
        stores.setOnClickListener(this);
        save_btn.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {
        switch (arg0.getId()) {
        /*
         * 支付方式与配送方式选择
		 */
            case R.id.shiptype:
                if (arg1 == onpay.getId()) {
                    door.setEnabled(true);
                    paytype = onpay.getText().toString();
                }
                if (arg1 == storeon.getId()) {
                    door.setEnabled(false);
                    paytype = storeon.getText().toString();
                }
                break;
            case R.id.couriertype:
                if (arg1 == ordinary.getId()) {
                    storeon.setEnabled(true);
                    stores.setVisibility(View.GONE);
                    sendtype = ordinary.getText().toString();
                    nodoor = true;
                    point = "";
                }
                if (arg1 == door.getId()) {
                    getMyToast("本方式需本人亲自来店办理!").show();
                    storeon.setEnabled(false);
                    if (storesdata != null && !storesdata.isEmpty()) {
                        sendtype = door.getText().toString();
                        stores.setVisibility(View.VISIBLE);
                        StoreInfo store = storesdata.get(0);
                        storeName.setText(store.getStoreName());
                        point = store.getAddress();
                        storeId = store.getId();
                        storeAddress.setText(point);
                    } else {
                        nodoor = false;
                        getMyToast("你附近没有艾拉奇门店!").show();
                    }
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stores:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String[] stores = new String[storesdata.size()];
                for (int i = 0; i < storesdata.size(); i++) {
                    StoreInfo store = storesdata.get(i);
                    stores[i] = store.getStoreName() + "\r\r距离:" + store.getDistance() + "千米";
                }
                builder.setTitle("离你最近的" + storesdata.size() + "家门店:");
                builder.setItems(stores, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StoreInfo selectStore = storesdata.get(which);
                        storeName.setText(selectStore.getStoreName());
                        point = selectStore.getAddress();
                        storeAddress.setText(selectStore.getAddress());
                    }

                }).show();
                break;
            case R.id.save_btn:
                if (!nodoor) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("paytype", paytype);
                bundle.putString("sendtype", sendtype);
                bundle.putString("point", point);
                bundle.putInt("storeId", storeId);

                Intent intent = new Intent();
                intent.putExtras(bundle);
                getMyToast("更改成功！").show();
                this.setResult(2, intent);
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        point = null;
        super.onDestroy();
    }
}
