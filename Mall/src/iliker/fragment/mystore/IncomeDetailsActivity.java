package iliker.fragment.mystore;

import android.view.View;
import android.widget.ListView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import iliker.entity.IncomeInfo;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * Created by WDHTC on 2016/6/22.
 */
public class IncomeDetailsActivity extends BaseStoreActivity {
    private ListView incomelv;
    private InComeAdapter inComeAdapter;
    private int offset = 1;

    @Override
    protected void initMyViews() {
        title.setText("收入明细");
        View view = View.inflate(this, R.layout.income_layout, null);
        incomelv = (ListView) view.findViewById(R.id.incomelv);
        storeContent.addView(view);
        initData();
    }

    private void initData() {
        RequestParams params = new RequestParams(GeneralUtil.GETINCOMESVC);
        params.addBodyParameter("phone", userInfo.getPhone());
        params.addBodyParameter("offset", offset + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBoolean("success")) {
                    JSONObject root = jsonObject.getJSONObject("data");
                    JSONObject totalSize = jsonObject.getJSONObject("totalSize");//总条目
                    JSONObject pageCount = jsonObject.getJSONObject("pageCount");//总页数
                    List<IncomeInfo> list = JSON.parseArray(root.getJSONArray("dataSet").toJSONString(), IncomeInfo.class);
                    inComeAdapter = new InComeAdapter(list, IncomeDetailsActivity.this);
                    incomelv.setAdapter(inComeAdapter);
                } else {
                    ToastFactory.getMyToast("没有任何收入").show();
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
