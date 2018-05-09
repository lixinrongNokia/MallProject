package iliker.fragment.person.shopadapter;

import android.content.Context;
import iliker.adapter.DefaultAdapter;
import iliker.entity.WebOrder;
import iliker.fragment.person.holder.ConfirmHolder;
import iliker.holder.BaseHolder;

import java.util.List;

public class ConfirmAdapter extends DefaultAdapter<WebOrder> {

    private Context context;

    public ConfirmAdapter() {
    }

    public ConfirmAdapter(Context context, List<WebOrder> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new ConfirmHolder(context);
    }

}
