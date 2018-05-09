package iliker.fragment.mystore;

import android.content.Context;

import java.util.List;

import iliker.adapter.DefaultAdapter;
import iliker.entity.BookKeep;
import iliker.holder.BaseHolder;
 class BookKeepAdapter extends DefaultAdapter<BookKeep>{
    private final Context context;
     BookKeepAdapter(List<BookKeep> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new BookKeepHodler(context);
    }
}
