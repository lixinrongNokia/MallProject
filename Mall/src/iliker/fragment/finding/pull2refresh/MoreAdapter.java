package iliker.fragment.finding.pull2refresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import iliker.fragment.finding.holder.MoreHolder;
import iliker.fragment.finding.holder.MorePersonHolder;

/**
 * Created by WDHTC on 2016/5/12.
 */
public class MoreAdapter extends BaseAdapter {
    final private Context context;
    final private String[] strings;

    public MoreAdapter(Context context, String[] strings) {
        this.context = context;
        this.strings = strings;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position > 0 ? 1 : 0;
    }

    @Override
    public int getCount() {
        return strings.length + 1;
    }

    @Override
    public String getItem(int position) {
        int id = position - 1;
        return strings[id < 0 ? 0 : id];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == 0) {
            MorePersonHolder morePersonHolder;
            if (convertView == null) {
                morePersonHolder = new MorePersonHolder(context);
            } else {
                morePersonHolder = (MorePersonHolder) convertView.getTag();
            }
            morePersonHolder.setData();
            return morePersonHolder.getConvertView();
        }
        if (getItemViewType(position) == 1) {
            MoreHolder moreHolder;
            if (convertView == null) {
                moreHolder = new MoreHolder(context);
            } else {
                moreHolder = (MoreHolder) convertView.getTag();
            }
            moreHolder.setData(getItem(position));
            return moreHolder.getConvertView();
        }

        return null;
    }
}
