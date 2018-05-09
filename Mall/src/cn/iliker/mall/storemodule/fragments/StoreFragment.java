package cn.iliker.mall.storemodule.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import cn.iliker.mall.R;
import cn.iliker.mall.storemodule.StockManagerAct;
import cn.iliker.mall.storemodule.StoreAccountAct;
import cn.iliker.mall.storemodule.UnPackOrderActivity;
import cn.iliker.mall.storemodule.holder.DetailHolder;
import cn.iliker.mall.storemodule.holder.StorePicHolder;
import com.iliker.application.CustomApplication;
import iliker.entity.StoreInfo;
import iliker.fragment.BaseFragment;
import iliker.utils.ViewUtils;

public class StoreFragment extends BaseFragment implements View.OnClickListener {
    private FrameLayout bottom_layout;
    private HorizontalScrollView horizontalScrollView;
    private StoreInfo info;

    @Override
    protected View initSubclassView() {
        view = View.inflate(context, R.layout.store_face, null);
        initViews();
        return view;
    }

    private void initViews() {
        info = CustomApplication.customApplication.getStoreInfo();
        bottom_layout = (FrameLayout) view.findViewById(R.id.bottom_layout);
        horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.detail_screen);
        DetailHolder holder = new DetailHolder(context);
        holder.setDatas(info);
        bottom_layout.addView(holder.getConvertView());
        StorePicHolder picHolder = new StorePicHolder(context);
        picHolder.setDatas(info);
        horizontalScrollView.addView(picHolder.getConvertView());
        view.findViewById(R.id.myAccount).setOnClickListener(this);
        view.findViewById(R.id.myOrder).setOnClickListener(this);
        view.findViewById(R.id.myStock).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myAccount:
                Intent intent = new Intent(context, StoreAccountAct.class);
                intent.putExtra("storeId", info.getId());
                intent.putExtra("account", info.getLoginEmail());
                startActivity(intent);
                break;
            case R.id.myOrder:
                ViewUtils.sendActivity(context, UnPackOrderActivity.class);
                break;
            case R.id.myStock:
                ViewUtils.sendActivity(context, StockManagerAct.class);
                break;
        }

    }
}
