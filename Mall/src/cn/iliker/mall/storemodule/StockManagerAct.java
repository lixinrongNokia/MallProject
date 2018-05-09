package cn.iliker.mall.storemodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import cn.iliker.mall.R;
import cn.iliker.mall.storemodule.holder.StoreStockAdapter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iliker.application.CustomApplication;
import iliker.entity.StoreStockInfo;
import iliker.utils.GeneralUtil;
import iliker.utils.ViewUtils;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import static iliker.utils.HttpHelp.getHttpUtils;

/*库存管理界面*/
public class StockManagerAct extends Activity implements View.OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    private ListView lv;
    private View loadMoreView;
    private Button loadBtn;
    private int indexPage = 1;
    private int pageCount = 10;
    private StoreStockAdapter storeStockAdapter;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private List<StoreStockInfo> storeStockInfoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_look);
        findViewById(R.id.addB).setOnClickListener(this);
        lv = (ListView) findViewById(R.id.listView);
        loadMoreView = View.inflate(this, R.layout.load_more_footer, null);
        lv.setOnScrollListener(this);
        loadBtn = (Button) loadMoreView.findViewById(R.id.stockLoadMore);
//        lv.addFooterView(loadMoreView);
        lv.setOnItemClickListener(this);
        getStoreStock();
    }

    private void getStoreStock() {
        RequestParams params = new RequestParams(GeneralUtil.GETSTORESTOCK);
        params.addBodyParameter("storeId", CustomApplication.customApplication.getStoreInfo().getId() + "");
        params.addBodyParameter("indexPage", indexPage + "");
        params.addBodyParameter("pageCount", pageCount + "");
        getHttpUtils().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = JSON.parseObject(result);
                    int totalSize = jsonObject.getIntValue("totalSize");
                    int pageCount = jsonObject.getIntValue("totalPage");
                    List<StoreStockInfo> webData = JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), StoreStockInfo.class);
                    if (storeStockAdapter == null) {
                        storeStockInfoList = webData;
                        storeStockAdapter = new StoreStockAdapter(storeStockInfoList, StockManagerAct.this);
                        lv.setAdapter(storeStockAdapter);
                    } else {
                        storeStockInfoList.addAll(webData);
                        storeStockAdapter.notifyDataSetChanged();
                    }
                    if (pageCount <= indexPage) {
                        lv.removeFooterView(loadMoreView);
                    } else {
                        lv.addFooterView(loadMoreView);
                        loadBtn.setText("加载更多");
                    }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addB:
                iliker.utils.ViewUtils.sendActivity(this, AddStockAct.class);
                break;
        }
    }

    /**
     * 滑动状态改变时被调用
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = storeStockAdapter.getCount() - 1;    //数据集最后一项的索引
        int lastIndex = itemsLastIndex + 1;             //加上底部的loadMoreView项
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
            //如果是自动加载,可以在这里放置异步加载数据的代码
            indexPage++;
            getStoreStock();
        }
    }

    /**
     * 滑动时被调用
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
        Log.d("==", totalItemCount + "");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StoreStockInfo storeStockInfo = storeStockInfoList.get(position);
        Intent intent = new Intent(this, StockDetailsActivity.class);
        intent.putExtra("store_stock", storeStockInfo);
        startActivity(intent);
    }
}
