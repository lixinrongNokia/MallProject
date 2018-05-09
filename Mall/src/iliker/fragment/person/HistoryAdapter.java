package iliker.fragment.person;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Goods;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

public class HistoryAdapter extends DefaultAdapter {
    private final Context context;

    public HistoryAdapter(Context context, List datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new HistoryHolder(context);
    }

    class HistoryHolder extends BaseHolder<Goods> {
        public ImageView good_img;
        public TextView goodsdesc;
        public TextView price;

        public HistoryHolder(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.history_item_layout, null);
            this.good_img = (ImageView) view.findViewById(R.id.good_img);
            this.goodsdesc = (TextView) view.findViewById(R.id.goodsdesc);
            this.price = (TextView) view.findViewById(R.id.price);
            return view;
        }

        @Override
        public void refreshView(Goods datas) {
            this.goodsdesc.setText(Html.fromHtml(datas.getGoodsDesc()));
            this.price.setText(String.valueOf("￥" + datas.getPrice()));
            bitmapUtils.bind(this.good_img, GeneralUtil.GOODSPATH + datas.getImgpath());
        }
    }
}
