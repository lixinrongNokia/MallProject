package iliker.fragment.type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Goods;
import iliker.holder.BaseHolder;
import iliker.utils.BitmapHelp;
import iliker.utils.GeneralUtil;

import java.util.List;

@SuppressLint("InflateParams")
public class MyAdapter extends DefaultAdapter<Goods> {
    // 加载图片用
    private final Context context;

    public MyAdapter(Context context, List<Goods> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new ViewHolder(context);
    }

    /* 面向对象，做的视图容器类 */
    class ViewHolder extends BaseHolder<Goods> {
        public ViewHolder(Context context) {
            super(context);
        }

        public ImageView img;
        public TextView info;
        public TextView price;

        @Override
        public View initViews() {
            View convertView = View.inflate(context, R.layout.category_itemd, null);
            this.info = (TextView) convertView
                    .findViewById(R.id.category_item_info);
            this.img = (ImageView) convertView
                    .findViewById(R.id.category_item_img);
            this.price = (TextView) convertView.findViewById(R.id.price);
            return convertView;
        }

        @Override
        public void refreshView(Goods datas) {
            this.info.setText(datas.getGoodsDesc());
            this.price.setText(String.valueOf("￥" + datas.getPrice()));
            String imgUrl = GeneralUtil.GOODSPATH + datas.getImgpath().split("#")[0];
// 加载网络图片
            BitmapHelp.getBitmapUtils().bind(this.img, imgUrl);
        }
    }
}
