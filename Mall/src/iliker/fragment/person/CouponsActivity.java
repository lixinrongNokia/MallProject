package iliker.fragment.person;

import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import iliker.entity.Prepaidcard;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import static iliker.utils.HttpHelp.getHttpUtils;

public class CouponsActivity extends BaseStoreActivity {
    private ListView couponslv;
    private int offset = 1;

    @Override
    protected void initMyViews() {
        title.setText("优惠券");
        View view = View.inflate(this, R.layout.coupons_layout, null);
        couponslv = (ListView) view.findViewById(R.id.couponslv);
        storeContent.addView(view);
        initData();
    }

    private void initData() {
        RequestParams params = new RequestParams(GeneralUtil.GETCOUPONS);
        params.addBodyParameter("phone", userInfo.getPhone());
        params.addBodyParameter("offset", offset + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject=JSON.parseObject(result);
                    /*rootObject.put("totalSize", cs.getInt(8));
                rootObject.put("pageCount", cs.getInt(9));*/
                    List<Prepaidcard> list = JSON.parseArray(jsonObject.getJSONArray("dataSet").toJSONString(), Prepaidcard.class);
                    couponslv.setAdapter(new CouponsAdapter(list, CouponsActivity.this));
                } else {
                    ToastFactory.getMyToast("木有东东").show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastFactory.getMyToast("网络有点问题").show();
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
