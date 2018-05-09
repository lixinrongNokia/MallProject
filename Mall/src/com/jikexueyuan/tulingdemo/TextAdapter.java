package com.jikexueyuan.tulingdemo;


import android.content.Context;
import iliker.adapter.DefaultAdapter;
import iliker.fragment.finding.holder.ReceiverHoder;
import iliker.fragment.finding.holder.SendHoder;
import iliker.holder.BaseHolder;

import java.util.List;

public class TextAdapter extends DefaultAdapter<ListData> {
    private List<ListData> list;
    private Context context;
    private static final int SEND = 0;
    private static final int RECEIVER = 1;
    private int itemid;

    public TextAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        itemid = position;
        ListData listData = list.get(position);
        if (listData.getFlag() == 1) {
            return SEND;
        }
        return RECEIVER;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public TextAdapter(Context context, List<ListData> datas) {
        super(datas);
        this.context = context;
        this.list = datas;
    }

    @Override
    public BaseHolder getHolder() {
        BaseHolder holder = null;
        switch (getItemViewType(itemid)) {
            case SEND:
                holder = new SendHoder(context);
                break;
            case RECEIVER:
                holder = new ReceiverHoder(context);
                break;
        }
        return holder;
    }

}
