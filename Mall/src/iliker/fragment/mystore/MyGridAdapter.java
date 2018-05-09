package iliker.fragment.mystore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.iliker.mall.R;

/**
 * gridview的Adapter
 */
public class MyGridAdapter extends BaseAdapter {
    private final Context mContext;

    private final String[] img_text = {"成为会员", "成为推客", "金牌推客", "账户管理", "邀请合伙人", "分销管理", "收益分析", "答疑中心"};
    private final int[] imgs = {R.drawable.join, R.drawable.tuike, R.drawable.gold, R.drawable.m3, R.drawable.m4, R.drawable.m7, R.drawable.analysis, R.drawable.bright};

    public MyGridAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.mystore_item_layout, parent, false);
        }
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
        ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
        iv.setBackgroundResource(imgs[position]);

        tv.setText(img_text[position]);
        return convertView;
    }

}
