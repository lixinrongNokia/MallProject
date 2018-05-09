package cn.iliker.mall.storemodule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.stone.pile.libs.PileLayout;
import iliker.entity.StoreInfo;
import iliker.mall.XCFlowLayout;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import static iliker.utils.HttpHelp.getHttpUtils;

public class StoreDetail extends AppCompatActivity {
    private PileLayout pileLayout;
    private StoreInfo store;
    private String[] storePIC;
    private TextView t_storeAdd;
    private TextView t_storePhone;
    private XCFlowLayout brand_layout;
    private String[] brands;
    private ViewGroup.MarginLayoutParams lp;
    private String myLat;
    private String myLong;
    private MyBrandTextListener myBrandTextListener = new MyBrandTextListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("locationinfo", Context.MODE_PRIVATE);
        myLat = sharedPreferences.getString("mylat", null);
        myLong = sharedPreferences.getString("mylong", null);
        setContentView(R.layout.store_face_forperson);
        ActionBar bar = getSupportActionBar();
        store = getIntent().getParcelableExtra("store");
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            String storeName = store.getStoreName();
            if (storeName.length() > 6) {
                storeName = storeName.substring(0, 7);
                storeName += "...";
            }
            bar.setTitle(storeName);
        }
        findViews();
        storePIC = store.getFaceIcon().split("#");
        initData();
        getStoreRunBrands();
    }

    private void getStoreRunBrands() {
        RequestParams params = new RequestParams(GeneralUtil.GETBRANDBYSTOREID);
        params.addBodyParameter("storeId", store.getId() + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = JSON.parseObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("brands");
                    int len = jsonArray.size();
                    brands = new String[len];
                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        TextView view = new TextView(StoreDetail.this);
//                        view.setOnClickListener(myBrandTextListener);
                        view.setId(jsonObject1.getIntValue("brandId"));
                        view.setText(jsonObject1.getString("brandName"));
                        view.setTextColor(getResources().getColor(R.color.myblack));
                        view.setBackgroundResource(R.drawable.sel_textview_bg);
                        brand_layout.addView(view, lp);
                    }
                } else {
                    TextView view = new TextView(StoreDetail.this);
                    view.setText("还没添加品牌");
                    view.setTextColor(getResources().getColor(R.color.myblack));
                    view.setBackgroundResource(R.drawable.sel_textview_bg);
                    brand_layout.addView(view, lp);
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

    class MyBrandTextListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(StoreDetail.this, StoreGoodsList.class);
            intent.putExtra("storeId", store.getId());
            intent.putExtra("brandId", v.getId());
            startActivity(intent);
        }
    }

    private void findViews() {
        pileLayout = (PileLayout) findViewById(R.id.pileLayout);
        t_storeAdd = (TextView) findViewById(R.id.storeAdd);
        brand_layout = (XCFlowLayout) findViewById(R.id.brands);
        t_storeAdd.setText("地址:" + store.getAddress());
        t_storeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BNRoutePlanNode sNode = new BNRoutePlanNode(Double.valueOf(myLong), Double.valueOf(myLat), "我", null, BNRoutePlanNode.CoordinateType.BD09LL);
                BNRoutePlanNode eNode = new BNRoutePlanNode(store.getLongitude(), store.getLatitude(), store.getStoreName(), null, BNRoutePlanNode.CoordinateType.BD09LL);
                if (sNode != null && eNode != null) {
                    List<BNRoutePlanNode> list = new ArrayList<>();
                    list.add(sNode);
                    list.add(eNode);
                    BaiduNaviManager.getInstance().launchNavigator(StoreDetail.this, list, 1, true, new DemoRoutePlanListener(StoreDetail.this, sNode));
                }
            }
        });
        t_storeAdd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        t_storePhone = (TextView) findViewById(R.id.storePhone);
        t_storePhone.setText("电话:" + store.getTell());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        lp = new ViewGroup.MarginLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        lp.topMargin = 10;
        lp.bottomMargin = 10;
        pileLayout.setAdapter(new PileLayout.Adapter() {
            @Override
            public int getLayoutId() {
                return R.layout.pile_item_layout;
            }

            @Override
            public int getItemCount() {
                return storePIC.length;
            }

            @Override
            public void bindView(View view, int index) {
                RoundedImageView imageView = (RoundedImageView) view.getTag();
                if (imageView == null) {
                    imageView = (RoundedImageView) view.findViewById(R.id.imageView);
                    view.setTag(imageView);
                }
                try {
                    Glide.with(StoreDetail.this).load(GeneralUtil.STOREICON + storePIC[index]).into(imageView);
                } catch (Exception e) {
                    Log.d("", e.getMessage());
                }
            }

            @Override
            public void displaying(int position) {
                super.displaying(position);
            }

            @Override
            public void onItemClick(View view, int position) {
            }
        });
    }
}
