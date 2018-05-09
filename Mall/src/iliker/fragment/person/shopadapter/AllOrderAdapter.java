package iliker.fragment.person.shopadapter;

import android.content.Context;
import android.text.TextUtils;
import iliker.adapter.DefaultAdapter;
import iliker.entity.WebOrder;
import iliker.fragment.person.holder.DoneHolder;
import iliker.fragment.person.holder.ForGoodsHolder;
import iliker.fragment.person.holder.ForThePaymentHolder;
import iliker.holder.BaseHolder;

import java.util.List;

public class AllOrderAdapter extends DefaultAdapter<WebOrder> {

    private List<WebOrder> list;
    private Context context;
    private static final int FORPAYMENT_ITEM = 0;
    private static final int FORGOODS_ITEM = 1;
    private static final int DONE_ITEM = 2;
    private int itemid;

    public AllOrderAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        itemid = position;
        WebOrder weborder = list.get(position);
        if (weborder.getOrderstate().equals("已收货")) {
            return DONE_ITEM;
        }
        if (weborder.getOrderdate().equals("等待付款")) {
            return FORPAYMENT_ITEM;
        }
        return FORGOODS_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 2;
    }

    public AllOrderAdapter(Context context, List<WebOrder> datas) {
        super(datas);
        this.context = context;
        this.list = datas;
    }

    @Override
    public BaseHolder getHolder() {
        BaseHolder holder = null;
        switch (getItemViewType(itemid)) {
            case FORPAYMENT_ITEM:
                holder = new ForThePaymentHolder(context);
                break;
            case DONE_ITEM:
                holder = new DoneHolder(context);
                break;
            case FORGOODS_ITEM:
                holder = new ForGoodsHolder(context);
                break;
        }
        return holder;
    }
}



