package com.cardsui.example;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.entity.StoreInfo;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

import java.util.List;

public class StoreListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private Callback callback;
    private List<StoreInfo> dadas;

    @Override
    public void onClick(View v) {
        if (callback != null) {
            callback.click(v);
        }
    }

    @Override
    public int getCount() {
        return dadas.size();
    }

    @Override
    public StoreInfo getItem(int position) {
        return dadas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StoreListHolder storeListHolder = null;
        if (convertView == null) {
            storeListHolder = new StoreListHolder(context);
            storeListHolder.t_showMap.setTag(position);
            storeListHolder.setDatas(getItem(position));
        } else {
            storeListHolder = (StoreListHolder) convertView.getTag();
        }
        return storeListHolder.getConvertView();
    }

    public interface Callback {
        void click(View v);
    }

    public StoreListAdapter(List<StoreInfo> datas, Context context, Callback callback) {
        this.dadas = datas;
        this.context = context;
        this.callback = callback;
    }


    class StoreListHolder extends BaseHolder<StoreInfo> {
        private ImageView faceIcon;
        private TextView storeName;
        private TextView storeAddress;
        private TextView t_showMap;
        private TextView t_distance;

        private StoreListHolder(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.store_item, null);
            faceIcon = (ImageView) view.findViewById(R.id.faceIcon);
            storeName = (TextView) view.findViewById(R.id.storeName);
            storeAddress = (TextView) view.findViewById(R.id.storeAddress);
            t_showMap = (TextView) view.findViewById(R.id.showMap);
            t_distance = (TextView) view.findViewById(R.id.distance);
            t_showMap.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            return view;
        }

        @Override
        public void refreshView(StoreInfo datas) {
            String addStr=context.getString(R.string.storeAdd);
            String distanceStr=context.getString(R.string.storeDistance);
            storeName.setText(datas.getStoreName());
            storeAddress.setText(String.format(addStr,datas.getAddress()));
            t_distance.setText(String.format(distanceStr,datas.getDistance()));
            bitmapUtils.bind(faceIcon, GeneralUtil.STOREICON + datas.getFaceIcon().split("#")[0]);
            t_showMap.setOnClickListener(StoreListAdapter.this);
        }
    }
}
