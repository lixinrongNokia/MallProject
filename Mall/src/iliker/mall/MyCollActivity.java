package iliker.mall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.iliker.mall.R;
import com.alibaba.fastjson.JSON;
import com.iliker.application.CustomApplication;
import iliker.db.DatabaseService;
import iliker.entity.Collection;
import iliker.entity.Goods;
import iliker.entity.Order;
import iliker.entity.UserInfo;
import iliker.fragment.type.ProductDetailActivity;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;

//查看收藏商品
public class MyCollActivity extends Activity implements OnItemClickListener,
        OnItemLongClickListener {
    private ListView colllv;
    private MycollFollAdapter maAdapter;
    private DatabaseService ds;
    private List<Collection> items = null;
    private int userId;
    private final MyHandler handler = new MyHandler(this);
    private int offset = 1;
    private int totalPage;
    private int totalSize;

    private static class MyHandler extends Handler {
        private final WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            MyCollActivity activity = (MyCollActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        activity.setContentView(R.layout.layout_nocoll);
                        break;
                    case 1:
                        getMyToast("取消成功！").show();
                        activity.maAdapter.notifyDataSetChanged();
                        if (activity.items.size() == 0) {
                            activity.setContentView(R.layout.layout_nocoll);
                        }
                        break;
                    case 2:
                        getMyToast("").show();
                        break;
                    case 3:
                        activity.initData();
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ds = getDB();
        CustomApplication cap = (CustomApplication) getApplication();
        UserInfo userInfo = cap.getUserinfo();
        if (userInfo == null) {
            setContentView(R.layout.layout_nocoll);
        } else {
            userId = userInfo.getuID();
        }
        if (!cap.networkIsAvailable()) {
            setContentView(R.layout.layout_nocoll);
            return;
        }
        setContentView(R.layout.mycoll_activity);
        findViews();
        setListener();
        getColl();
    }

    private void findViews() {
        colllv = (ListView) findViewById(R.id.colllv);
    }

    private void getColl() {
        RequestParams params = new RequestParams(GeneralUtil.GETCOLL);
        params.addBodyParameter("userId", userId + "");
        params.addBodyParameter("offset", offset + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (TextUtils.isEmpty(result)) {
                    handler.sendEmptyMessage(0);
                } else {
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);
                    totalPage = jsonObject.getInteger("totalPage");
                    totalSize = jsonObject.getInteger("totalSize");
                    items = JSON.parseArray(jsonObject.getJSONArray("collections").toJSONString(), Collection.class);
                    handler.sendEmptyMessage(3);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void initData() {
        maAdapter = new MycollFollAdapter(this, items);
        colllv.setAdapter(maAdapter);
    }

    private void setListener() {
        colllv.setOnItemClickListener(this);
        colllv.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        Goods good = items.get(position).getGoods();// 获得选中的商品信息map,xlistView的条目下标是从1开始的
        Intent intent = new Intent(this, ProductDetailActivity.class);// 跳到商品详情页面
        intent.putExtra("good", good);
        startActivity(intent);// 执行
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                   final int position, long arg3) {
        Collection collection = items.get(position);
        final Goods good = collection.getGoods();
        final String color = collection.getColor();
        final String size = collection.getSize();
        final int collid = collection.getId();
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("操作");
        builder.setItems(new String[]{"取消收藏", "加入购物车"},
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        collAction(arg1, good, position, color, size, collid);
                    }
                }).show();
        return true;
    }

    private void collAction(int actionid, Goods good, final int position,
                            String color, String collsize, int collid) {
        switch (actionid) {
            case 0:
                RequestParams params = new RequestParams(GeneralUtil.DELCOLL);
                params.addBodyParameter("collid", collid + "");
                getHttpUtils().post(params,
                        new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                if ("success".equals(result)) {
                                    items.remove(position);
                                    Message msg = new Message();
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                getMyToast(ex.getMessage()).show();
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                break;
            case 1:
                long size;
                Order order = ds.findOrder(good.getId(), color, collsize);
                if (order == null)
                    size = ds.addOrder(good, color, collsize);
                else
                    size = ds.updateOrder(order.getCid(), order.getCount() + 1);
                if (size > 0) {
                    getMyToast("已加入购物车").show();
                } else {
                    getMyToast("发生错误").show();
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}
