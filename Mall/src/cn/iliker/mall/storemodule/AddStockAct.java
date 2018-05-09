package cn.iliker.mall.storemodule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.entity.Goods;
import iliker.entity.StockInfo;
import iliker.entity.StockItem;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static iliker.utils.HttpHelp.getHttpUtils;
import static iliker.utils.ViewUtils.removeParent;

/*添加库存*/
public class AddStockAct extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener, DialogInterface.OnClickListener {
    private Spinner spinner_brand, spinner_colorS, spinner_sizeS, spinner_typeS;
    private String[] brandArray;
    private JSONArray brands;
    private ImageButton save_btn;
    private String[] actionType = {"入库","出库"};
    private String[] colorS;
    private String[] sizeS;
    private EditText text_nameS;
    private EditText inputServer;
    private EditText e_countEt;
    private AlertDialog.Builder builder;
    private Button btn_returnB;
    private Map<String, StockInfo> stockItemMap = new LinkedHashMap<>();
    private Map<String, Integer> brandS = new LinkedHashMap<>();
    private String brandName;
    private Goods goods;
    private String colorName;
    private String selectSize;
    private boolean stockStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_logadd);
        findViews();
        getbrand();
        setListener();
        setData();
    }

    private void setData() {
        inputServer = new EditText(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddStockAct.this,
                android.R.layout.simple_spinner_item, actionType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_typeS.setAdapter(adapter);
    }

    private void setListener() {
        spinner_brand.setOnItemSelectedListener(this);
        spinner_colorS.setOnItemSelectedListener(this);
        spinner_sizeS.setOnItemSelectedListener(this);
        spinner_typeS.setOnItemSelectedListener(this);
        save_btn.setOnClickListener(this);
        text_nameS.setOnClickListener(this);
        btn_returnB.setOnClickListener(this);
    }

    private void findViews() {
        spinner_brand = (Spinner) findViewById(R.id.brandS);
        spinner_colorS = (Spinner) findViewById(R.id.colorS);
        spinner_sizeS = (Spinner) findViewById(R.id.sizeS);
        spinner_typeS = (Spinner) findViewById(R.id.typeS);
        save_btn = (ImageButton) findViewById(R.id.stockSave);
        text_nameS = (EditText) findViewById(R.id.nameS);
        btn_returnB = (Button) findViewById(R.id.returnB);
        e_countEt = (EditText) findViewById(R.id.countEt);
    }

    private void getbrand() {
        if (!CustomApplication.customApplication.networkIsAvailable()) {
            ToastFactory.getMyToast("请联网后再试!").show();
            return;
        }
        RequestParams params = new RequestParams(GeneralUtil.LOADBRANDS);
        getHttpUtils().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.getBoolean("success")) {
                    brands = jsonObject.getJSONArray("brands");
                    brandArray = new String[brands.size()];
                    for (int i = 0; i < brands.size(); i++) {
                        String brandName = brands.getJSONObject(i).getString("brandName");
                        brandArray[i] = brandName;
                        brandS.put(brandName, brands.getJSONObject(i).getIntValue("brandId"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddStockAct.this,
                            android.R.layout.simple_spinner_item, brandArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_brand.setAdapter(adapter);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.brandS:
                brandName = (String) parent.getItemAtPosition(position);
                break;
            case R.id.colorS:
                colorName = (String) parent.getItemAtPosition(position);
                StockInfo stockInfo = stockItemMap.get(colorName);
                List<StockItem> stockItems = stockInfo.getStockItems();
                sizeS = new String[stockItems.size()];
                for (int i = 0; i < stockItems.size(); i++) {
                    sizeS[i] = stockItems.get(i).getSize();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddStockAct.this,
                        android.R.layout.simple_spinner_item, sizeS);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_sizeS.setAdapter(adapter);
                break;
            case R.id.sizeS:
                selectSize = (String) parent.getItemAtPosition(position);
                break;
            case R.id.typeS:
                String type = (String) parent.getItemAtPosition(position);
                stockStatus = "入库".equals(type);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnB:
                finish();
                break;
            case R.id.stockSave:
                String inputCount = e_countEt.getText().toString();
                if (TextUtils.isEmpty(colorName) || TextUtils.isEmpty(selectSize) || TextUtils.isEmpty(inputCount)) {
                    return;
                }
                JSONObject storeStockInfo = new JSONObject();
                JSONObject goodsJSON = new JSONObject();
                JSONObject storeInfoJSON = new JSONObject();

                storeInfoJSON.put("id", CustomApplication.customApplication.getStoreInfo().getId());
                JSONObject storestockitem = new JSONObject();
                storestockitem.put("size", selectSize);
                storestockitem.put("stockCount", inputCount);
                goodsJSON.put("id", goods.getId());

                storeStockInfo.put("goods", goodsJSON);
                storeStockInfo.put("color", colorName);
                storeStockInfo.put("storeInfo", storeInfoJSON);

                JSONArray storeStockItems = new JSONArray();
                storeStockItems.add(storestockitem);
                storeStockInfo.put("stockItems", storeStockItems);
                RequestParams params = new RequestParams(GeneralUtil.UPDATESTORESTOCK);
                params.addBodyParameter("storeStockInfoStr", storeStockInfo.toString());
                params.addBodyParameter("stockStatus", stockStatus + "");
                params.addBodyParameter("brandName", brandName);
                getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        JSONObject jsonObject = JSON.parseObject(result);
                        if (jsonObject.getBooleanValue("success")) {
                            ToastFactory.getMyToast("保存成功").show();
                        }else {
                            ToastFactory.getMyToast(jsonObject.getString("msg")).show();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.d("", "");
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Log.d("", "");
                    }

                    @Override
                    public void onFinished() {
                        Log.d("", "");
                    }
                });
                break;
            case R.id.nameS:
                removeParent(inputServer);
                if (builder == null) {
                    builder = new AlertDialog.Builder(this);
                    builder.setTitle("输入货号:").setView(inputServer)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", this);
                }
                builder.show();
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String goodsCode = inputServer.getText().toString();
        if (!TextUtils.isEmpty(goodsCode)) {
            text_nameS.setText(goodsCode);
            RequestParams params = new RequestParams(GeneralUtil.GETATTRBYGOODSCODE);
            params.addBodyParameter("goods.goodCode", goodsCode);
            params.addBodyParameter("goods.brand.brandId", brandS.get(brandName) + "");
            getHttpUtils().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        goods = JSON.parseObject(result, Goods.class);
                        List<StockInfo> stockInfos = goods.getStockInfoSet();
                        colorS = new String[stockInfos.size()];
                        for (int i = 0; i < stockInfos.size(); i++) {
                            StockInfo stockInfo = stockInfos.get(i);
                            colorS[i] = stockInfo.getColor();
                            stockItemMap.put(stockInfo.getColor(), stockInfo);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddStockAct.this,
                                android.R.layout.simple_spinner_item, colorS);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_colorS.setAdapter(adapter);
                    } else {
                        ToastFactory.getMyToast("该品牌没有对应的货号").show();
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
    }

}
