package com.cardsui.example;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import cn.iliker.mall.R;
import cn.iliker.mall.storemodule.DemoRoutePlanListener;
import cn.iliker.mall.storemodule.StoreDetail;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import iliker.entity.StoreInfo;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.XmlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BargainingActivity extends BaseStoreActivity implements AdapterView.OnItemClickListener, StoreListAdapter.Callback {
    private ListView listView;
    private StoreListAdapter storeListAdapter;
    private List<StoreInfo> stores;
    private TextView empty_list;
    private String myLat;
    private String myLong;
//    public static List<Activity> activityList = new LinkedList<>();

    @Override
    protected void initMyViews() {
        title.setText("附近门店");
//        activityList.add(this);
        SharedPreferences sharedPreferences = getSharedPreferences("locationinfo", Context.MODE_PRIVATE);
        myLat = sharedPreferences.getString("mylat", null);
        myLong = sharedPreferences.getString("mylong", null);
        View view = View.inflate(this, R.layout.bargain_goods_layout, null);
        storeContent.addView(view);
        listView = (ListView) view.findViewById(R.id.storeList);
        empty_list = (TextView) view.findViewById(R.id.empty_list);
        stores = XmlUtil.xml2Object();
        if (stores != null) {
            Collections.sort(stores);
            storeListAdapter = new StoreListAdapter(stores, this, this);
            listView.setAdapter(storeListAdapter);
            listView.setOnItemClickListener(this);
        } else {
            empty_list.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, StoreDetail.class);
        intent.putExtra("store", stores.get(position));
        startActivity(intent);
    }

    @Override
    public void click(View v) {
        routeplanToNavi(stores.get((Integer) v.getTag()));
    }

    private void routeplanToNavi(StoreInfo datas) {
        if (myLat != null) {
            BNRoutePlanNode sNode = new BNRoutePlanNode(Double.valueOf(myLong), Double.valueOf(myLat), "我", null, BNRoutePlanNode.CoordinateType.BD09LL);
            BNRoutePlanNode eNode = new BNRoutePlanNode(datas.getLongitude(), datas.getLatitude(), datas.getStoreName(), null, BNRoutePlanNode.CoordinateType.BD09LL);
            if (sNode != null && eNode != null) {
                List<BNRoutePlanNode> list = new ArrayList<>();
                list.add(sNode);
                list.add(eNode);
                BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(BargainingActivity.this, sNode));
            }
        }
    }

    /*public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
        *//*
         * 设置途径点以及resetEndNode会回调该接口
         *//*
           *//* for (Activity ac : activityList) {
                if (ac.getClass().getName().endsWith("BNGuideActivity")) {
                    return;
                }
            }*//*
            Intent intent = new Intent(BargainingActivity.this, BNGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("routePlanNode", mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
//            Toast.makeText(BNDemoMainActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }*/
}