package cn.iliker.mall.storemodule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.cardsui.example.BNGuideActivity;

public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {
    private BNRoutePlanNode mBNRoutePlanNode = null;
    private Context context;

    public DemoRoutePlanListener(Context context, BNRoutePlanNode node) {
        this.context = context;
        mBNRoutePlanNode = node;
    }

    @Override
    public void onJumpToNavigator() {
        Intent intent = new Intent(context, BNGuideActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("routePlanNode", mBNRoutePlanNode);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onRoutePlanFailed() {

    }
}
