package cn.iliker.mall.storemodule;

import android.content.Context;
import iliker.adapter.DefaultAdapter;
import iliker.entity.IncomeInfo;
import iliker.fragment.mystore.InComeHolder;
import iliker.holder.BaseHolder;

import java.util.List;

public class AccountLogAdapter extends DefaultAdapter<IncomeInfo> {
    private Context context;

    public AccountLogAdapter(List<IncomeInfo> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new InComeHolder(context);
    }
}
