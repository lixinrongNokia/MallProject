package iliker.fragment.person.shopadapter;

import android.content.Context;
import iliker.adapter.DefaultAdapter;
import iliker.entity.WebOrder;
import iliker.fragment.person.holder.ForGoodsHolder;
import iliker.holder.BaseHolder;

import java.util.List;

public class ForTheGoodsAdapter extends DefaultAdapter<WebOrder> {

    private Context context;

    public ForTheGoodsAdapter() {
    }

    public ForTheGoodsAdapter(Context context, List<WebOrder> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new ForGoodsHolder(context);
    }

}
