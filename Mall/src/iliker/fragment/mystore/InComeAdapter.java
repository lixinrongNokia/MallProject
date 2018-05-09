package iliker.fragment.mystore;

import android.content.Context;

import iliker.adapter.DefaultAdapter;
import iliker.entity.IncomeInfo;
import iliker.holder.BaseHolder;

import java.util.List;


class InComeAdapter extends DefaultAdapter<IncomeInfo> {
    private final Context context;

    InComeAdapter(List<IncomeInfo> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new InComeHolder(context);
    }
}
