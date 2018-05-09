package com.cardsui.example;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iliker.application.CustomApplication;
import iliker.entity.Goods;
import iliker.fragment.finding.pull2refresh.PullToRefreshLayout;
import iliker.fragment.type.MyAdapter;
import iliker.fragment.type.ProductDetailActivity;
import iliker.utils.GeneralUtil;
import iliker.utils.ProUtils;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.fjl.widget.DialogFactory.initDialog;
import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 首页商品详情
 */
public class Mall_Activity extends FragmentActivity implements
        OnTabChangeListener, DrawerListener, OnClickListener, GridView.OnItemClickListener {
    private boolean isFirstLoad = true;// 判断是否是第一次加载本页面，如果是就显示加载进度条
    private DrawerLayout drawer_goods_filter;// 侧滑容器控件
    private TabHost tabhost;// 选项卡主控件
    private FrameLayout right;// 筛选侧滑菜单
    private TabWidget tabs;// 选项卡标题容器
    private int selectid = 1;
    private PopupWindow popupWindow;// 弹出菜单
    private boolean isclick = true;
    private final MyListener mylistener = new MyListener();
    private GridView gridView;
    private List<Goods> datas;// 数据
    private MyAdapter myDadpter;
    private final int PAGE_COUNT = 10;// 每页显示的条目数量
    private int pageIndex = 1;// 当前页码
    private Dialog progressDialog;// 加载页面的进度条
    private Map<Integer, Integer> selectbrandids = null;
    private int start, end;
    private int clothestypeid;//
    private CustomApplication cap;
    private String url;
    private RelativeLayout load_more;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Goods good = datas.get(position);
        Intent intent = new Intent(this, ProductDetailActivity.class);// 跳到商品详情页面
        getDB().addHistory(good, GeneralUtil.SDF.format(new Date()));
        intent.putExtra("good", good);
        startActivity(intent);// 执行
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mall);
        cap = (CustomApplication) this.getApplication();
        url = GeneralUtil.GETGOODS;
        clothestypeid = Integer.valueOf(getIntent().getStringExtra("clothestypeid"));
        findViews();
        initdata();
        createPopView();
        setListener();
        asyncGetCloudData("clothestypeid=" + clothestypeid + " and visible=1", "market_date desc", null);
    }

    /**
     * 依据给定条件获取商品
     * propertyMap封装了查询条件与值 conditions为条件，columnValue为值
     * 只有条件就是根据销量降序查询，或价格升序查询
     */
    private void asyncGetCloudData(String where_clause, String order_clause, final PullToRefreshLayout pullToRefreshLayout) {

        RequestParams requestParams = new RequestParams(url);
        if (where_clause != null) {
            requestParams.addBodyParameter("where_clause", where_clause);
        }
        if (order_clause != null) {
            requestParams.addBodyParameter("order_clause", order_clause);
        }
        requestParams.addBodyParameter("offset", pageIndex + "");
        if (progressDialog == null) {
            progressDialog = initDialog(this);
        }
        progressDialog.show();
        getHttpUtils().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseString) {
                progressDialog.dismiss();
                if (TextUtils.isEmpty(responseString)) {
                    if (pullToRefreshLayout != null)
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    Toast.makeText(Mall_Activity.this, "没有啦", Toast.LENGTH_SHORT).show();
                    load_more.setVisibility(View.GONE);
                    return;
                }
                JSONObject jsonObject = JSON.parseObject(responseString);
                List<Goods> webdata = JSON.parseArray(jsonObject.getJSONArray("dataSet").toJSONString(), Goods.class);
                if (pullToRefreshLayout != null) {
                    datas.addAll(webdata);
                    myDadpter.notifyDataSetChanged();
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    datas = webdata;
                    myDadpter = new MyAdapter(Mall_Activity.this, datas);
                    gridView.setAdapter(myDadpter);
                    if (isFirstLoad) {
                        ProUtils.cecheJson(Mall_Activity.this, "sharelist", url, responseString);
                        isFirstLoad = false;
                    }
                }
                int pageCount = jsonObject.getInteger("pageCount");
                if (pageIndex >= pageCount) load_more.setVisibility(View.GONE);
                else load_more.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }

    private void findViews() {
        // 获取TabHost对象
        tabhost = (TabHost) findViewById(R.id.tabhost);
        drawer_goods_filter = (DrawerLayout) findViewById(R.id.drawer_goods_filter);
        right = (FrameLayout) findViewById(R.id.right);
        tabs = (TabWidget) findViewById(android.R.id.tabs);
        gridView = (GridView) findViewById(R.id.gridView);
        ((PullToRefreshLayout) findViewById(R.id.refresh_view)).setOnRefreshListener(new MyRefreshListener());
        load_more = (RelativeLayout) findViewById(R.id.loadmore_view);
    }

    private void initdata() {
        // 如果没有继承TabActivity时，通过该种方法加载启动tabHost
        tabhost.setup();
        tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("综合")
                .setContent(R.id.refresh_view));
        tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("销量")
                .setContent(R.id.refresh_view));
        tabhost.addTab(tabhost.newTabSpec("tab3").setIndicator("价格")
                .setContent(R.id.refresh_view));
        tabhost.addTab(tabhost.newTabSpec("tab4").setIndicator("筛选")
                .setContent(R.id.view4));
        tabhost.setCurrentTab(selectid);
        FilterHodler filterHodler = new FilterHodler(this);
        right.addView(filterHodler.getConvertView());
    }

    private void setListener() {
        tabhost.setOnTabChangedListener(this);
        View tabitem0 = tabhost.getTabWidget().getChildTabViewAt(0);
        tabitem0.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isclick) {
                    popupWindow.showAsDropDown(tabs);
                }
                return true;
            }

        });
        drawer_goods_filter.addDrawerListener(this);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onTabChanged(String tabId) {
        switch (tabId) {
            case "tab1":
                break;
            case "tab2":
                if (selectid == 1) {
                    return;
                }
                selectid = 1;
                break;
            case "tab3":
                if (selectid == 2) {
                    return;
                }
                selectid = 2;
                asyncGetCloudData("clothestypeid=" + clothestypeid + " and visible=1", "price desc", null);
                break;
            case "tab4":
                if (selectid == 0) {
                    isclick = false;
                }
                tabhost.setCurrentTab(selectid);
                drawer_goods_filter.openDrawer(right);
                break;
        }
    }

    private class MyListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            popupWindow.dismiss();
        }

    }

    private void createPopView() {
        View poView = getLayoutInflater().inflate(R.layout.filter_select, null);
        ListView lv = (ListView) poView.findViewById(R.id.filterLv);
        lv.setOnItemClickListener(mylistener);

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // 单选
        lv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_checked, getData()));
        lv.setItemChecked(0, true);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(this);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(true);
            popupWindow.setContentView(poView);
        }
    }

    /**
     * 显示选项
     */
    private List<String> getData() {
        List<String> list = new ArrayList<>();
        list.add("综合");
        list.add("新品");
        list.add("零元购");
        return list;
    }

    /**
     * 价格区间
     */
    private List<String> getPriceData() {
        List<String> list = new ArrayList<>();
        list.add("全部");
        list.add("20-100");
        list.add("100-300");
        list.add("300-500");
        list.add("500-1000");
        list.add("1000-10000");
        return list;
    }

    @Override
    public void onDrawerClosed(View arg0) {
        switch (arg0.getId()) {
            case R.id.right:
                isclick = true;
                break;
        }
    }

    @Override
    public void onDrawerOpened(View arg0) {

    }

    @Override
    public void onDrawerSlide(View arg0, float arg1) {

    }

    @Override
    public void onDrawerStateChanged(int arg0) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer_goods_filter.isDrawerOpen(right)) {
                drawer_goods_filter.closeDrawer(right);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                break;
            case R.id.reset:
                break;
        }

    }

    // 点击返回按钮关闭事件依次关闭侧滑页
    public void onbackclose(View v) {
        drawer_goods_filter.closeDrawer(right);
    }

    class FilterHodler {
        private final View convertView;
        final Context context;

        FilterHodler(Context context) {
            this.context = context;
            this.convertView = initViews();
            this.convertView.setTag(this);
        }

        public View getConvertView() {
            return convertView;
        }

        public View initViews() {
            return View.inflate(context, R.layout.goods_filter, null);
        }
    }

    class MyRefreshListener implements PullToRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            // 下拉刷新操作
            if (!cap.networkIsAvailable()) {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                getMyToast("你已经断开网络了！").show();
                return;
            }
            pageIndex = 1;
            if (datas != null)
                datas.clear();
            asyncGetCloudData("clothestypeid=" + clothestypeid + " and visible=1", "market_date desc", pullToRefreshLayout);
        }

        @Override
        public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
            //加载更多
            if (!cap.networkIsAvailable()) {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                getMyToast("你已经断开网络了！").show();
                return;
            }
            pageIndex++;
            asyncGetCloudData("clothestypeid=" + clothestypeid + " and visible=1", "market_date desc", pullToRefreshLayout);
        }

    }
}
