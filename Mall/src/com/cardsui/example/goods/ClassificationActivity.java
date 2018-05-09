package com.cardsui.example.goods;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.fjl.widget.ToastFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.iliker.mall.R;
import iliker.fragment.home.LinkActivity;
import iliker.fragment.type.ListViewAdapter;
import iliker.fragment.type.SearchActivity;
import iliker.utils.GeneralUtil;
import iliker.utils.ProUtils;

import static com.fjl.widget.DialogFactory.initDialog;
import static com.iliker.application.CustomApplication.customApplication;
import static iliker.utils.HttpHelp.getHttpUtils;

public class ClassificationActivity extends Activity implements
        SwipeRefreshLayout.OnRefreshListener {
    private ListView mListView;
    private static final int REFRESH_COMPLETE = 0X110;
    private ListViewAdapter mListViewAdapter;
    private List<Map<String, Object>> mArrayList;
    private Dialog maydialog;
    private boolean isFirstLoad = true;
    private AlertDialog.Builder builder;
    private SwipeRefreshLayout mSwipeLayout;
    private View footerview;

    private String classifyUrl;

    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    init();
                    mSwipeLayout.setRefreshing(false);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.typeview);
        classifyUrl = GeneralUtil.GETCLOTYPE;
        ActionBar activityBar = getActionBar();
        if (activityBar != null) {
            activityBar.setDisplayHomeAsUpEnabled(true);
            activityBar.setDisplayShowHomeEnabled(false);
            activityBar.setTitle("返回");
            activityBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        findViews();
    }

    @Override
    public void onResume() {
        String classifyJson = ProUtils.getCecheJson(this, "classification", classifyUrl);
        if (classifyJson != null) {
            if (mArrayList != null) mArrayList.clear();
            mArrayList = parsJson(classifyJson);
            mListViewAdapter = new ListViewAdapter(mArrayList, this);
            mListView.setAdapter(mListViewAdapter);
        } else {
            init();
        }
        if (mListViewAdapter != null)
            mListViewAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, 1, 0, "");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 1:
                Intent intent = new Intent(this, SearchActivity.class);
                this.startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        mListView.removeFooterView(footerview);
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mArrayList != null) {
            mArrayList.clear();
        }
        super.onStop();
    }

    private void init() {
        if (!customApplication.networkIsAvailable()) {
            ToastFactory.getMyToast("你已经断开网络了！").show();
            return;
        }

        asyncGetData();
    }

    private void findViews() {
        mListView = (ListView) findViewById(R.id.listView);
        footerview = LayoutInflater.from(this).inflate(
                R.layout.type_footer, null);
        footerview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(ClassificationActivity.this, LinkActivity.class);
                intent.putExtra("openHref", "https://iliker888.taobao.com");
                startActivity(intent);
                return true;
            }

        });
        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        if (mListView.getFooterViewsCount() == 0) {
                            mListView.addFooterView(footerview);
                        }
                    }
                }
            }

        });
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
    }

    private void asyncGetData() {
        if (mArrayList != null) {
            mArrayList.clear();
        }
        if (maydialog == null) {
            maydialog = initDialog(this);
        }
        if (isFirstLoad) {
            maydialog.show();
            isFirstLoad = false;
        }
        RequestParams params = new RequestParams(classifyUrl);
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseString) {
                maydialog.dismiss();
                mArrayList = parsJson(responseString);
                ProUtils.cecheJson(ClassificationActivity.this, "classification", classifyUrl, responseString);
                if (mArrayList != null && mListView != null) {
                    mListViewAdapter = new ListViewAdapter(mArrayList, ClassificationActivity.this);
                    mListView.setAdapter(mListViewAdapter);
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

    public void loadNotework() {
        isFirstLoad = true;
        if (builder == null) {
            builder = new Builder(this);
        }
        builder.setMessage("连接服务器失败");
        builder.setPositiveButton("刷新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                init();
            }

        }).show();
    }

    private static List<Map<String, Object>> parsJson(String responseString) {
        List<Map<String, Object>> list;
        try {
            JSONArray jsonarray = new JSONArray(responseString);
            list = new ArrayList<>();
            int len = jsonarray.length();
            for (int i = 0; i < len; i++) {
                Map<String, Object> map = new LinkedHashMap<>();
                JSONObject object = jsonarray.getJSONObject(i);
                String crowdName = object.getString("crowdName");
                JSONArray clothestypes = object.getJSONArray("data");
                List<Map<String, String>> clolist = new ArrayList<>();
                int count = clothestypes.length();
                for (int j = 0; j < count; j++) {
                    JSONObject cloObject = clothestypes.getJSONObject(j);
                    Map<String, String> cloes = new LinkedHashMap<>();
                    cloes.put("id", cloObject.getString("id"));
                    cloes.put("name", cloObject.getString("name"));
                    cloes.put("typeimg", cloObject.getString("typeimg"));
                    clolist.add(cloes);
                }
                map.put("crowdName", crowdName);
                map.put("clothes", clolist);
                list.add(map);
            }
        } catch (JSONException e) {
            return null;
        }
        return list;
    }

    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

}
