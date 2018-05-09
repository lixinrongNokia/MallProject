package iliker.fragment.type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.iliker.mall.R;
import iliker.utils.BitmapHelp;
import iliker.utils.GeneralUtil;

class GridViewAdapter extends BaseAdapter {
    private final Context mContext;
    private final Map<String, Object> mList;
    private List<Map<String, String>> clolist = null;

    GridViewAdapter(Context mContext, Map<String, Object> mList) {
        this.mContext = mContext;
        this.mList = mList;
        clolist = (List<Map<String, String>>) this.mList.get("clothes");
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return clolist.size();
        }
    }

    @Override
    public Map<String, String> getItem(int position) {
        if (mList == null) {
            return null;
        } else {
            return this.clolist.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(
                    R.layout.activity_label_item, null, false);
            holder.typeimg = (ImageView) convertView.findViewById(R.id.typeimg);
            holder.activity_name = (TextView) convertView
                    .findViewById(R.id.activity_name);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (this.mList != null) {
            Map<String, String> hashMap = getItem(position);
            String url = GeneralUtil.GETTYPEIMG + hashMap.get("typeimg");
            if (holder.activity_name != null) {
                holder.activity_name.setText(hashMap.get("name"));
            }
            if (holder.typeimg != null) {
                BitmapHelp.getBitmapUtils().bind(holder.typeimg, url);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView typeimg;
        TextView activity_name;
    }
}
