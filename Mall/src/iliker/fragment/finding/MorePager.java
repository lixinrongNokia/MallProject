package iliker.fragment.finding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.iliker.application.CustomApplication;

import java.util.Date;

import cn.iliker.mall.R;
import iliker.fragment.finding.pull2refresh.MoreAdapter;
import me.maxwin.view.XListView;

public class MorePager extends BaseFindPager implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private XListView more_lv;
    final private String[] strings = new String[]{"我分享的图片", "打赏我的", "我的关注"};
    private TextView empty;

    public MorePager(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        view = View.inflate(context, R.layout.more_layout, null);
        findChlidViews();
        setListeners();
        return view;
    }

    private void setListeners() {
        more_lv.setOnItemClickListener(this);
    }

    private void findChlidViews() {
        more_lv = (XListView) view.findViewById(R.id.more_lv);
        empty = (TextView) view.findViewById(R.id.empty);
    }

    @Override
    public void initDate() {
        if (CustomApplication.customApplication.getUserinfo() == null) {
            more_lv.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            return;
        }
        more_lv.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        more_lv.setAdapter(new MoreAdapter(context, strings));
        more_lv.setXListViewListener(this);
        more_lv.setPullLoadEnable(false);
    }

    private void onLoad() {
        more_lv.stopRefresh();
        String currentTime = sdf.format(new Date(System.currentTimeMillis()));
        more_lv.setRefreshTime(currentTime);// 更新时间
    }

    @Override
    public void onRefresh() {
        onLoad();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch ((int) id) {
            case 0:
                Intent intent = new Intent(context, InShowPersonAlity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userinfo", CustomApplication.customApplication.getUserinfo());
                intent.putExtra("bundle", bundle);
                context.startActivity(intent);
                break;
            case 1:
                Intent intent1 = new Intent(context, OldtimeActivity.class);
                context.startActivity(intent1);
                break;
            case 2:
                Intent intentReward = new Intent(context, RewardListActivity.class);
                context.startActivity(intentReward);
                break;
            case 3:
                Intent intent2 = new Intent(context, FollowersActivity.class);
                context.startActivity(intent2);
                break;
        }
    }
}
