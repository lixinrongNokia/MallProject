package com.cardsui.example.goods;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.iliker.mall.R;
import iliker.entity.FlashSale;
import iliker.fragment.type.ProductDetailActivity;
import iliker.utils.ParsJsonUtil;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import static com.fjl.widget.DialogFactory.initDialog;
import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 抢购碎片基类
 */
abstract class BasePager implements
        OnItemClickListener, IXListViewListener {
    private XListView three_details;// 主体列表
    private List<FlashSale> datas;// 数据
    final int PAGE_COUNT = 10;// 每页显示的条目数量
    int pageIndex = 1;// 当前页数
    private boolean isFirstLoad = true;// 判断是否是第一次加载本页面，如果是就显示加载进度条
    private Dialog maydialog;
    private final Context context;
    public View view;
    public int time;
    private FlashSaleAdapter flashSaleAdapter;

    BasePager(Context context) {
        this.context = context;
        initView();
    }

    private void initView() {
        view = View.inflate(context, R.layout.category_three, null);
        findViews();
        setListeners();
    }

    /**
     * 初始化控件
     */
    private void findViews() {
        three_details = (XListView) view.findViewById(R.id.three_details);
    }

    /**
     * 初始化数据和视图
     */
    private void initViews(boolean isRefresh) {
        if (!CustomApplication.customApplication.networkIsAvailable()) {
            Toast.makeText(context, "没网络", Toast.LENGTH_SHORT).show();
            return;
        }
        initData(isRefresh);
    }

    /**
     * 初始化监听
     */
    private void setListeners() {
        three_details.setOnItemClickListener(this);// 这个监听是列表条目点击的
        three_details.setXListViewListener(this);// 这个监听是用于头部和底部刷新的
    }

    public abstract void initData(boolean isRefresh);

    void asyncGetGoods(final boolean isRefresh, RequestParams params) {
        if (maydialog == null) {
            maydialog = initDialog(context);
        }
        if (isFirstLoad) {
            maydialog.show();
            isFirstLoad = false;
        }

        getHttpUtils().post(params,
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (TextUtils.isEmpty(result)) {
                            three_details.setVisibility(View.GONE);
                            ToastFactory.getMyToast("没有商品").show();
                        } else {
                            three_details.setVisibility(View.VISIBLE);
                            JSONObject jsonObject=JSON.parseObject(result);
                            List<FlashSale> tmplist = JSON.parseArray(JSON.toJSONString(jsonObject.getJSONArray("saleses")),FlashSale.class);
                            if (tmplist != null) {
                                if (!isRefresh) {
                                    datas = tmplist;
                                    flashSaleAdapter = new FlashSaleAdapter(context, datas);
                                    three_details.setAdapter(flashSaleAdapter);
                                } else {
                                    datas.addAll(tmplist);
                                    flashSaleAdapter.notifyDataSetChanged();
                                    onLoad();
                                }
                                if (datas.size() < PAGE_COUNT) {
                                    three_details.setPullLoadEnable(false);// 禁用加载更多
                                } else
                                    three_details.setPullLoadEnable(true);// 启用加载更多
                            }
                        }
                        maydialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        maydialog.dismiss();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {
                        maydialog.dismiss();
                    }
                });
    }

    /*public void loadNotework() {
        isFirstLoad = true;
        if (builder == null) {
            builder = new Builder(context);
        }
        builder.setMessage("连接服务器失败");
        builder.setPositiveButton("刷新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                initViews(false);
            }

        }).show();
    }*/

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FlashSale flashSale = datas.get(position - 1 < 0 ? 0 : position - 1);// 获得选中的商品信息map,xlistView的条目下标是从1开始的
        Intent intent = new Intent(context, ProductDetailActivity.class);// 跳到商品详情页面
        getDB().addHistory(flashSale.getGoods(), sdf.format(new Date()));
        intent.putExtra("good", flashSale.getGoods());
        context.startActivity(intent);// 执行
    }

    /**
     * 停止顶部更新、停止底部加载,更新时间
     */
    @SuppressLint("SimpleDateFormat")
    private void onLoad() {
        three_details.stopRefresh();
        three_details.stopLoadMore();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentTime = sdf.format(date);
        three_details.setRefreshTime(currentTime);// 更新时间
    }

    /* 顶部下拉刷新 */
    public void onRefresh() {
        if (datas == null)
            datas = new ArrayList<>();
        datas.clear();
        pageIndex = 1;
        isFirstLoad = true;
        initViews(true);
    }

    /* 底部加载更多 */
    public void onLoadMore() {
        pageIndex++;
        initViews(true);
    }

}
