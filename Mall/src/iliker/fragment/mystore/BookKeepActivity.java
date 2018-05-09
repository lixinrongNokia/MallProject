package iliker.fragment.mystore;

import android.view.View;
import android.widget.ListView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import iliker.entity.BookKeep;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import static iliker.utils.HttpHelp.getHttpUtils;

public class BookKeepActivity extends BaseStoreActivity {
    private ListView bookKeeplv;
    private BookKeepAdapter adapter;
    private List<BookKeep> data;
    private int index = 1;

    @Override
    protected void initMyViews() {
        title.setText("消费记录");
        View view = View.inflate(this, R.layout.bookkeep_list_layout, null);
        bookKeeplv = (ListView) view.findViewById(R.id.bookKeeplv);
        storeContent.addView(view);
        setData();
    }

    private void setData() {
        RequestParams params = new RequestParams(GeneralUtil.GETBOOKKEEPLIST);
        params.addBodyParameter("phone", userInfo.getPhone());
        params.addBodyParameter("offset", index + "");//分页查询
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBoolean("success")) {
                    JSONObject root = jsonObject.getJSONObject("data");
                    JSONObject totalSize = jsonObject.getJSONObject("totalSize");//总条目
                    JSONObject pageCount = jsonObject.getJSONObject("pageCount");//总页数
                    data = JSON.parseArray(root.getJSONArray("dataSet").toJSONString(), BookKeep.class);
                    adapter = new BookKeepAdapter(data, BookKeepActivity.this);
                    bookKeeplv.setAdapter(adapter);
                } else {
                    ToastFactory.getMyToast("没有消费记录").show();
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
