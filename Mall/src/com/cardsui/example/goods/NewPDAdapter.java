package com.cardsui.example.goods;

import android.content.Context;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Goods;
import iliker.holder.BaseHolder;

import java.util.List;

/**
 * Created by WDHTC on 2016/7/4.
 */
public class NewPDAdapter extends DefaultAdapter<Goods> {
    private final Context context;

    public NewPDAdapter(List<Goods> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new NewPDHodler(context);
    }
}
