package iliker.fragment.finding;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.iliker.mall.R;
import iliker.entity.UserInfo;
import iliker.fragment.finding.holder.GoddessHolder;
import iliker.utils.GeneralUtil;
import me.maxwin.view.XListView;

import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.HttpHelp.getHttpUtils;

public class NearPager extends BaseFindPager implements AdapterView.OnItemLongClickListener, XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private XListView xlv;// 主体列表
    private List<UserInfo> list;
    private final int PAGE_COUNT = 20;// 每页显示的条目数量
    private int pageIndex = 0;// 当前页数
    private int count;
    private TextView empty;
    private GoddessHolder goddessHolder;
    private boolean isfirst = true;

    public NearPager(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        view = View.inflate(context, R.layout.near, null);
        findViews();
        setListeners();
        return view;
    }

    @Override
    public void initDate() {
        if (!CustomApplication.customApplication.networkIsAvailable()) {
            getMyToast("你已经断开网络了！").show();
            return;
        }
        if (CustomApplication.customApplication.getUserinfo() != null) {
            empty.setVisibility(View.GONE);
            xlv.setVisibility(View.VISIBLE);
            if (isfirst) {
                goddessHolder.setDatas();
                isfirst = false;
            }
            getNearPerson();
        } else {
            xlv.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }


    private void setListeners() {
        //xlv.setOnItemLongClickListener(this);
        xlv.setXListViewListener(this);// 这个监听是用于头部和底部刷新的
        xlv.setOnItemClickListener(this);
    }

    private void findViews() {
        xlv = (XListView) view.findViewById(R.id.xlv);
        goddessHolder = new GoddessHolder(context);
        xlv.addHeaderView(goddessHolder.getConvertView());
        count = xlv.getHeaderViewsCount();
        empty = (TextView) view.findViewById(R.id.empty);
    }

    private void getNearPerson() {
        RequestParams params = new RequestParams(GeneralUtil.GETNEARPERSON);
        SharedPreferences sf = context.getSharedPreferences("locationinfo", Context.MODE_PRIVATE);
        String latitude = sf.getString("mylat", "");
        String longitude = sf.getString("mylong", "");
        if (!TextUtils.isEmpty(latitude)) {
            params.addBodyParameter("latitude", latitude);
            params.addBodyParameter("longitude", longitude);
            getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    if ("0".equals(result)) {
                        xlv.setPullLoadEnable(false);// 停用加载更多
                    } else {
                        if (list != null) {
                            list.clear();
                        }
                        list = parsJson2Object(result);
                        Collections.sort(list);
                        xlv.setAdapter(new NearPersonAdapter(context, list));
                        if (list.size() < PAGE_COUNT) {
                            xlv.setPullLoadEnable(false);// 停用加载更多
                        } else {
                            xlv.setPullLoadEnable(true);// 启用加载更多
                        }
                        onLoad();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    xlv.stopRefresh();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        } else ToastFactory.getMyToast("没有位置信息未被授权访问").show();
    }

    private List<UserInfo> parsJson2Object(String responseString) {
        return JSON.parseArray(responseString, UserInfo.class);
    }

    /**
     * 停止顶部更新、停止底部加载,更新时间
     */
    private void onLoad() {
        xlv.stopRefresh();
        xlv.stopLoadMore();
        Date date = new Date(System.currentTimeMillis());
        String currentTime = sdf.format(date);
        xlv.setRefreshTime(currentTime);// 更新时间
    }

    @Override
    public void onRefresh() {
        pageIndex = 0;
        initDate();
    }

    // 底部加载更多
    public void onLoadMore() {
        if (list != null && list.size() == PAGE_COUNT) {
            pageIndex = pageIndex + 1;
            initDate();
        } else {
            xlv.setPullLoadEnable(false);// 最后一页了，禁用加载更多
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        int temp = (position - count);
        if (temp < 0) {
            return false;
        }
        final UserInfo webuserInfo = list.get(temp);
        if (webuserInfo.getNickName().equals(CustomApplication.customApplication.getUserinfo().getNickName())) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("操作");
        final String[] items = new String[]{"发消息", "加关注"};
        builder.setItems(items,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                       /* if ("发消息".equals(items[arg1])) {

                        } else {

                        }*/
                    }

                }).show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int temp = (position - count);
        if (temp >= 0) {

            Intent intent;
            UserInfo webuserInfo = list.get(temp);
            boolean personCenter = false;
            if (webuserInfo.getNickName().equals(CustomApplication.customApplication.getUserinfo().getNickName())) {
                intent = new Intent(context, InShowPersonAlity.class);
                personCenter = true;
            } else {
                intent = new Intent(context, OutShowPersonAlity.class);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("userinfo", webuserInfo);
            bundle.putBoolean("personCenter", personCenter);
            intent.putExtra("bundle", bundle);
            context.startActivity(intent);
        }
    }
}
