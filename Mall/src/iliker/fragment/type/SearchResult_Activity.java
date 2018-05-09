package iliker.fragment.type;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cardsui.example.goods.NewPDAdapter;
import com.fjl.widget.ToastFactory;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import iliker.entity.Goods;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.GeneralUtil;

import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;
import static iliker.utils.ParsJsonUtil.parseJSON;

public class SearchResult_Activity extends BaseStoreActivity implements AdapterView.OnItemClickListener {
    private ListView view;
    private NewPDAdapter newPDAdapter;
    private String searchText;
    private List<Goods> list;

    @Override
    protected void initMyViews() {
        searchText = getIntent().getStringExtra("searchText");
        title.setText("搜索结果");
        view = new ListView(this);
        view.setOnItemClickListener(this);
        storeContent.addView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryGoods();
    }

    private void queryGoods() {
        RequestParams params = new RequestParams(GeneralUtil.SEARCHSVC);
        params.addBodyParameter("keyword", searchText);
        params.addBodyParameter("firstResult", 0 + "");
        params.addBodyParameter("maxResult", 20 + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (!"".equals(s)) {
                    list = parseJSON(s);
                    newPDAdapter = new NewPDAdapter(list, SearchResult_Activity.this);
                    view.setAdapter(newPDAdapter);
                } else {
                    ToastFactory.getMyToast("没有匹配的信息").show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastFactory.getMyToast("没有记录").show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Goods good = list.get(position);
        Intent intent = new Intent(this, ProductDetailActivity.class);// 跳到商品详情页面
        getDB().addHistory(good, sdf.format(new Date()));
        intent.putExtra("good", good);
        startActivity(intent);
    }
}
