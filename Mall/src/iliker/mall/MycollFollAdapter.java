package iliker.mall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Collection;
import iliker.entity.Goods;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

import java.util.List;

@SuppressLint("InflateParams")
public class MycollFollAdapter extends DefaultAdapter<Collection> {
    private final Context context;

    public MycollFollAdapter(Context context, List<Collection> collections) {
        super(collections);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new ViewHolder(context);
    }

    final class ViewHolder extends BaseHolder<Collection> {
        public ViewHolder(Context context) {
            super(context);
        }

        public ImageView imageView;
        public TextView title;
        public TextView price;

        @Override
        public View initViews() {
            View convertView = View.inflate(context, R.layout.mycollfoll_item, null);
            this.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            this.title = (TextView) convertView.findViewById(R.id.title);
            this.price = (TextView) convertView.findViewById(R.id.price);
            return convertView;
        }

        @Override
        public void refreshView(Collection collection) {
            Goods good = collection.getGoods();
            this.title.setText(good.getGoodName());
            this.price.setText(String.valueOf(good.getPrice()));
            String url = GeneralUtil.GOODSPATH + good.getImgpath();
            bitmapUtils.bind(this.imageView, url);
        }
    }

}
