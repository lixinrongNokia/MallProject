package iliker.fragment.type;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.entity.ShipAddress;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.lang.ref.WeakReference;
import java.util.List;

import static iliker.utils.HttpHelp.getHttpUtils;

public class AddressListActivity extends BaseStoreActivity implements OnClickListener,
        OnItemLongClickListener, OnItemClickListener {
    private ListView addressLv;
    private AddressAdapter addressadapter;
    private Button button;
    private int id = 0;
    private final MyHandler handler = new MyHandler(this);
    private View view;
    private CustomApplication cap;
    private int requsetCode;
    private List<ShipAddress> shipAddresses;

    private static class MyHandler extends Handler {
        private final WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            AddressListActivity activity = (AddressListActivity) reference
                    .get();
            if (activity != null) {
                if (msg.what == 1) {
                    Bundle bundle = msg.getData();
                    int position = bundle.getInt("position");
                    activity.shipAddresses.remove(position);
                    activity.addressadapter.notifyDataSetChanged();
                }
            }
        }

    }

    @Override
    protected void initMyViews() {
        title.setText("收货地址");
        view = View.inflate(this, R.layout.editaddress, null);
        storeContent.addView(view);
        cap = (CustomApplication) getApplication();
        /*addressList = (AddressList) getIntent().getSerializableExtra(
                "addressList");*/
        findChildViews();
        requsetCode = getIntent().getIntExtra("requsetCode", 0);
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddress(userInfo.getuID());
    }

    private void findChildViews() {
        addressLv = (ListView) view.findViewById(R.id.addressLv);
        button = (Button) view.findViewById(R.id.newaddress_btn);
    }

    private void getAddress(int uID) {
        RequestParams params = new RequestParams(GeneralUtil.GETADDRESSSVC);
        params.addBodyParameter("uid", uID + "");
        params.addBodyParameter("requestCode", "1");
        getHttpUtils().post(params,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            JSONObject jsonObject=JSON.parseObject(result);
                            if ("SUCCESS".equals(jsonObject.getString("result_code"))){
                                shipAddresses = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), ShipAddress.class);
                                addressadapter = new AddressAdapter(
                                        AddressListActivity.this, shipAddresses);
                                addressLv.setAdapter(addressadapter);
                            }
                        } else {
                            ToastFactory.getMyToast("无收货地址").show();
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

    private void setListener() {
        button.setOnClickListener(this);
        addressLv.setOnItemLongClickListener(this);
        addressLv.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, NewAddressActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   final int position, long id) {
        final ShipAddress shipaddress = shipAddresses.get(
                position);
        if (id != shipaddress.getId()) {
            AlertDialog.Builder builder = new Builder(this);
            builder.setTitle("选择操作:");
            builder.setPositiveButton("删除",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestParams params = new RequestParams(GeneralUtil.DELADDRESSSVC);
                            params.addBodyParameter("addressId", shipaddress.getId() + "");
                            getHttpUtils().post(params,
                                    new Callback.CommonCallback<String>() {

                                        @Override
                                        public void onSuccess(String result) {
                                            if ("1".equals(result)) {
                                                Message msg = new Message();
                                                msg.what = 1;
                                                Bundle bundle = new Bundle();
                                                bundle.putInt("position",
                                                        position);
                                                msg.setData(bundle);
                                                handler.sendMessage(msg);
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

                    }).setNegativeButton("取消", null).show();
        }
        return true;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (requsetCode == 1) {
            for (int i = 0; i < addressLv.getChildCount(); i++) {
                LinearLayout linearlayout = (LinearLayout) addressLv.getChildAt(i);
                ImageView selectedimageview = (ImageView) linearlayout
                        .findViewById(R.id.selected);
                if (linearlayout == view) {
                    selectedimageview.setVisibility(View.VISIBLE);
                    Intent intent = new Intent();
                    intent.putExtra("selectid", position);
                    this.setResult(requsetCode, intent);
                    finish();
                } else {
                    selectedimageview.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 适配器
     *
     * @author Administrator
     */

    public class AddressAdapter extends BaseAdapter {
        private final List<ShipAddress> address;
        private final Context context;

        AddressAdapter(Context context, List<ShipAddress> address) {
            this.context = context;
            this.address = address;
            getSelectAddress();
        }

        void getSelectAddress() {
            SharedPreferences addressSF = context.getSharedPreferences(
                    "localAddress", Context.MODE_PRIVATE);
            String selected = addressSF.getString("id", "");
            if (!TextUtils.isEmpty(selected)) {
                id = Integer.valueOf(selected);
            }
        }

        @Override
        public int getCount() {
            return address.size();
        }

        @Override
        public Object getItem(int position) {
            return address.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.addressitem,
                        null);
                holder.selected = (ImageView) convertView
                        .findViewById(R.id.selected);
                holder.recevername = (TextView) convertView
                        .findViewById(R.id.recevername);
                holder.recevertel = (TextView) convertView
                        .findViewById(R.id.recevertel);
                holder.receveraddr = (TextView) convertView
                        .findViewById(R.id.receveraddr);
                holder.editAddress = (ImageView) convertView
                        .findViewById(R.id.editAddress);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            final ShipAddress shipaddress = (iliker.entity.ShipAddress) getItem(position);
            holder.recevername.setText(shipaddress.getConsigneeName());
            holder.recevertel.setText(shipaddress.getPhone());
            holder.receveraddr.setText(shipaddress.getAddress());
            if (id == shipaddress.getId()) {
                holder.selected.setVisibility(View.VISIBLE);
            }
            holder.editAddress.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,
                            NewAddressActivity.class);
                    intent.putExtra("shipaddress", shipaddress);
                    startActivity(intent);
                }

            });
            return convertView;
        }

    }

    /* 面向对象，做的视图容器类 */
    final class ViewHolder {
        ImageView selected;
        TextView recevername;
        TextView recevertel;
        TextView receveraddr;
        ImageView editAddress;
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}
