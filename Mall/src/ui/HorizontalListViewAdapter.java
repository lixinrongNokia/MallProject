package ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import cn.iliker.mall.R;
import iliker.utils.GeneralUtil;

import static iliker.utils.BitmapHelp.getBitmapUtils;

public class HorizontalListViewAdapter extends BaseAdapter {
    private final Context context;
    private final String[] strings;

    public HorizontalListViewAdapter(Context context, String[] strings) {
        this.context = context;
        this.strings = strings;
    }

    @Override
    public int getCount() {
        return strings.length - 1;
    }

    @Override
    public String getItem(int position) {
        return strings[position + 1];
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView im;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.horizontal_list_item, null);
            im = (ImageView) convertView.findViewById(R.id.img_list_item);
            convertView.setTag(im);
        } else {
            im = (ImageView) convertView.getTag();
        }
        getBitmapUtils().bind(im, GeneralUtil.HEADURL + getItem(position));
        return convertView;
    }
}