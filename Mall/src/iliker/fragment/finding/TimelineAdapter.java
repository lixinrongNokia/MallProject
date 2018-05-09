package iliker.fragment.finding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.iliker.mall.R;

class TimelineAdapter extends BaseAdapter {

    private final List<Map<String, String>> list;
    private final LayoutInflater inflater;

    TimelineAdapter(Context context, List<Map<String, String>> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.showtime = (TextView) convertView
                    .findViewById(R.id.show_time);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Map<String, String> map = list.get(position);
        viewHolder.title.setText("当天有" + map.get("shareCount") + "条分享！"
                + "分享图片:" + map.get("picCount") + "张。");
        viewHolder.showtime.setText(map.get("date"));
        return convertView;
    }

    static class ViewHolder {
        TextView showtime;
        TextView title;
    }
}
