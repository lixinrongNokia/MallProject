package iliker.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import iliker.holder.BaseHolder;

public abstract class DefaultAdapter<T> extends BaseAdapter {
    private List<T> datas;

    public DefaultAdapter() {
    }

    public DefaultAdapter(List<T> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder;
        if (convertView == null) {
            holder = getHolder();
        } else {
            holder = (BaseHolder) convertView.getTag();
        }
        holder.setDatas(getItem(position));
        return holder.getConvertView();
    }

    public abstract BaseHolder getHolder();
}
