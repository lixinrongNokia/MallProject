package iliker.fragment.type;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import cn.iliker.mall.R;
import iliker.entity.Cartitem;
import iliker.entity.Order;
import iliker.entity.SerializableList;
import iliker.utils.BitmapHelp;
import iliker.utils.GeneralUtil;
import org.xutils.ImageManager;

import java.util.List;

public class ShowGoodsActivity extends Activity {
    private SerializableList serializablelist;
    private ListView goodslv;
    private int windowWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_activity);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowWidth = wm.getDefaultDisplay().getWidth();
        serializablelist = (SerializableList) getIntent().getSerializableExtra("serializablelist");
        findViews();
        goodslv.setAdapter(new MyAdapter(this, serializablelist.getList()));

    }

    private void findViews() {
        int totalcount = 0;
        goodslv = (ListView) findViewById(R.id.goodslv);
        findViewById(R.id.backbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        for (Cartitem item : serializablelist.getList()) {
            totalcount += item.getItemCount();
        }
        ((TextView) findViewById(R.id.totalCount)).setText("共" + String.valueOf(totalcount) + "件");
    }

    public class MyAdapter extends BaseAdapter {
        final List<Cartitem> list;
        final private LayoutInflater inflater;
        // 加载图片用
        private final ImageManager bitmapUtils;
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(windowWidth / 4, LinearLayout.LayoutParams.WRAP_CONTENT);

        public MyAdapter(Context context, List<Cartitem> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
            bitmapUtils = BitmapHelp.getBitmapUtils();
            lp.setMargins(8, 0, 0, 0);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            ViewHolder holder;
            /* 第一次加载 */
            if (null == convertView) {
                holder = new ViewHolder();
                // UI控件实例化
                convertView = inflater.inflate(R.layout.showgoods_item, null);
                holder.info = (TextView) convertView
                        .findViewById(R.id.good_info);
                holder.img = (ImageView) convertView
                        .findViewById(R.id.good_img);
                holder.img.setLayoutParams(lp);
                holder.color = (TextView) convertView.findViewById(R.id.color);
                holder.size = (TextView) convertView.findViewById(R.id.size);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.count = (TextView) convertView
                        .findViewById(R.id.itmecount);
                // 保存条目视图结构
                convertView.setTag(holder);
            } else {/* 非第一次加载 */
                holder = (ViewHolder) convertView.getTag();
            }
            // 统一赋值from --> to
            Cartitem item = list.get(position);
            Order order = item.getOrder();
            holder.info.setText(Html.fromHtml(order.getGoodsDesc()));
            holder.color.setText(String.valueOf("颜色:" + order.getColor()));
            holder.size.setText(String.valueOf("尺寸:" + order.getSize()));
            holder.price.setText(String.valueOf("销售价：￥" + item.getProductPrice()));
            holder.count.setText(String.valueOf("x" + item.getItemCount()));
            String imgUrl = GeneralUtil.GOODSPATH + order.getImgpath();
            bitmapUtils.bind(holder.img, imgUrl);

            return convertView;
        }

        /* 面向对象，做的视图容器类 */
        final class ViewHolder {
            public ImageView img;
            public TextView info;
            public TextView color;
            public TextView size;
            public TextView price;
            public TextView count;
        }
    }

}
