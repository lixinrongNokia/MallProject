package iliker.fragment.finding;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.iliker.application.CustomApplication;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import cn.iliker.mall.R;
import iliker.entity.Share;
import iliker.fragment.faxian.ShareAdapter;
import iliker.fragment.faxian.ShareDetailsActivity;
import iliker.fragment.finding.pull2refresh.PullToRefreshLayout;
import iliker.utils.GeneralUtil;
import iliker.utils.ParsJsonUtil;
import iliker.utils.ProUtils;

import static com.fjl.widget.DialogFactory.initDialog;
import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 发现碎片
 */

public class FaXianPager extends BaseFindPager implements AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener {
    private GridView gridView;// 主体列表
    private List<Share> datas;// 数据
    private static int PAGEINDEX = 1;// 当前页数
    private boolean isFirstLoad = true;// 判断是否是第一次加载本页面，如果是就显示加载进度条
    private AlertDialog.Builder builder;
    private Dialog progressDialog;// 加载页面的进度条
    private ShareAdapter shareAdapter;
    private String url;

    public FaXianPager(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        view = View.inflate(context, R.layout.faxian_layout, null);
        findViews();
        setListeners();
        url = GeneralUtil.GETSHARE;//网络数据请求路径
        return view;
    }

    @Override
    public void initDate() {
        if (CustomApplication.customApplication.networkIsAvailable()) {
            asyncGetShares(null);//远程获取json数据
        } else {
            String cecheJson = ProUtils.getCecheJson(context, "sharelist", url);//先从本地获取第一页的json数据
            getMyToast("你已经断开网络了！").show();
            if (cecheJson != null) {
                datas = ParsJsonUtil.getShares(cecheJson);
                shareAdapter = new ShareAdapter(context, datas);
                gridView.setAdapter(shareAdapter);
            }
        }
    }

    private void findViews() {
        gridView = (GridView) view.findViewById(R.id.gridView);
        ((PullToRefreshLayout) view.findViewById(R.id.refresh_view)).setOnRefreshListener(this);
    }


    private void setListeners() {
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (CustomApplication.customApplication.getUserinfo() == null) {
            getMyToast("对不起你没登陆！").show();
            return;
        }
        Share share = datas.get(i);
        Intent intent = new Intent();
        intent.putExtra("share", share);
        intent.setClass(context, ShareDetailsActivity.class);
        context.startActivity(intent);
    }


    private void asyncGetShares(final PullToRefreshLayout pullToRefreshLayout) {
        if (isFirstLoad) {
            if (progressDialog == null) {
                progressDialog = initDialog(context);
            }
            isFirstLoad = false;
            progressDialog.show();// 如果是第一次加载页面才显示总进度条
        }
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("nickname", "");
        params.addBodyParameter("pageIndex", PAGEINDEX + "");
        params.addBodyParameter("pageItems", 16 + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseString) {
                progressDialog.dismiss();
                if ("0".equals(responseString)) {
                    if (pullToRefreshLayout != null)
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    Toast.makeText(context, "没有啦", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Share> webdata = ParsJsonUtil.getShares(responseString);
                if (pullToRefreshLayout != null) {
                    datas.addAll(webdata);
                    shareAdapter.notifyDataSetChanged();
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    datas = webdata;
                    shareAdapter = new ShareAdapter(context, datas);
                    gridView.setAdapter(shareAdapter);
                    if (isFirstLoad)
                        ProUtils.cecheJson(context, "sharelist", url, responseString);/*缓存在本地*/
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                progressDialog.dismiss();
                isFirstLoad = true;
                if (pullToRefreshLayout != null)
                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                loadNotework();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void loadNotework() {
        isFirstLoad = true;
        if (builder == null) {
            builder = new Builder(context);
        }
        builder.setMessage("连接服务器失败");
        builder.setPositiveButton("刷新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                asyncGetShares(null);
            }

        }).show();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
// 下拉刷新操作
        if (!CustomApplication.customApplication.networkIsAvailable()) {
            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
            getMyToast("你已经断开网络了！").show();
            return;
        }
        PAGEINDEX = 0;
        if (datas != null)
            datas.clear();
        asyncGetShares(pullToRefreshLayout);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
//加载更多
        if (!CustomApplication.customApplication.networkIsAvailable()) {
            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
            getMyToast("你已经断开网络了！").show();
            return;
        }
        PAGEINDEX++;
        asyncGetShares(pullToRefreshLayout);
    }
}
