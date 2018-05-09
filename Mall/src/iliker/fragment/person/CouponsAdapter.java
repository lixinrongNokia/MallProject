package iliker.fragment.person;

import android.content.Context;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Prepaidcard;
import iliker.fragment.person.holder.CouponsHodler;
import iliker.holder.BaseHolder;

import java.util.List;

/**
 * Created by LIXINRONG on 2016/7/28.
 */
public class CouponsAdapter extends DefaultAdapter<Prepaidcard>{
    private final Context context;

    public CouponsAdapter(List<Prepaidcard> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new CouponsHodler(context);
    }
}
