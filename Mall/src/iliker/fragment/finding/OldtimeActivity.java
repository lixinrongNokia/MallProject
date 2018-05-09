package iliker.fragment.finding;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.iliker.application.CustomApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iliker.mall.R;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.GeneralUtil;

import static com.fjl.widget.DialogFactory.initDialog;
import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.HttpHelp.getHttpUtils;

public class OldtimeActivity extends BaseStoreActivity implements OnItemClickListener {

    private ListView listView;
    private Dialog dialog;
    private List<Map<String, String>> webdata;
    private TextView empty;
    private CustomApplication cap;


    private void initDate() {
        if (!cap.networkIsAvailable()) {
            getMyToast("网络慢").show();
            return;
        }
        if (userInfo == null) {
            empty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        AsyncGetData(cap.getUname());
    }

    private void AsyncGetData(String nickname) {
        dialog = initDialog(this);
        dialog.show();
        RequestParams params = new RequestParams(GeneralUtil.GETSHAREBYTIMESVC);
        params.addBodyParameter("nickname", nickname);
        params.addBodyParameter("pageIndex", nickname);
        params.addBodyParameter("pageSize", nickname);
        getHttpUtils().post(params,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.dismiss();
                        if (!"0".equals(result)) {
                            webdata = parsBackVal(result);
                            if (webdata != null) {
                                empty.setVisibility(View.GONE);
                                listView.setVisibility(View.VISIBLE);
                                listView.setAdapter(new TimelineAdapter(OldtimeActivity.this, webdata));
                            }
                        } else {
                            empty.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
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
                        dialog.dismiss();
                    }

                });
    }

    private static List<Map<String, String>> parsBackVal(String responseString) {
        List<Map<String, String>> list;
        try {
            JSONArray array = new JSONArray(responseString);
            list = new ArrayList<>();
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject object = array.getJSONObject(i);
                Map<String, String> map = new HashMap<>();
                map.put("date", object.getString("date"));
                map.put("shareCount", object.getString("shareCount"));
                map.put("picCount", object.getString("picCount"));
                list.add(map);
            }
        } catch (JSONException e) {
            return null;
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Map<String, String> map = webdata.get(arg2 % webdata.size());
        Bundle bundle = new Bundle();
        bundle.putString("nickname", cap.getUname());
        bundle.putString("date", map.get("date"));
        Intent intent = new Intent(this, OldPhotoActivity.class);
        intent.putExtra("property", bundle);
        startActivity(intent);
    }


    @Override
    protected void initMyViews() {
        title.setText("图库");
        View view = View.inflate(this, R.layout.activity_listview, null);
        cap = (CustomApplication) this.getApplicationContext();
        listView = (ListView) view.findViewById(R.id.listview);
        empty = (TextView) view.findViewById(R.id.empty);
        listView.setDividerHeight(0);
        listView.setOnItemClickListener(this);
        storeContent.addView(view);
        initDate();
    }
}
