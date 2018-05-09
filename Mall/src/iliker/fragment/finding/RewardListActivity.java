package iliker.fragment.finding;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;

import com.fjl.widget.ToastFactory;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import cn.iliker.mall.R;
import iliker.entity.Reward_Item;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.utils.GeneralUtil;

import static iliker.utils.HttpHelp.getHttpUtils;

public class RewardListActivity extends BaseStoreActivity {
    private RecyclerView recycler_view;
    private RewardListAdapter rewardListAdapter;

    @Override
    protected void initMyViews() {
        title.setText("打赏我的人");
        View view = View.inflate(this, R.layout.rewardlist_layout, null);
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(manager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        storeContent.addView(view);
        initChlidView();
    }

    private void initChlidView() {
        RequestParams params = new RequestParams(GeneralUtil.GETREWARDLISTSVC + "?phone=" + userInfo.getPhone());
        getHttpUtils().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    List<Reward_Item> list = JSON.parseArray(result, Reward_Item.class);
                    rewardListAdapter = new RewardListAdapter(RewardListActivity.this, list);
                    recycler_view.setAdapter(rewardListAdapter);
                }else ToastFactory.getMyToast("还没有人打赏你喔!").show();
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
}
