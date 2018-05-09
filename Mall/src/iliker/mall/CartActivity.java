package iliker.mall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.iliker.mall.R;
import com.iliker.application.CustomApplication;
import iliker.db.DatabaseService;
import iliker.entity.Cart;
import iliker.entity.Cartitem;
import iliker.entity.Order;
import iliker.entity.SerializableList;
import iliker.fragment.type.ShopListActivity;
import iliker.utils.BitmapHelp;
import iliker.utils.DBHelper;
import iliker.utils.GeneralUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CartActivity extends Activity implements OnClickListener {

    /* 用于从数据库读取订单信息 */
    private List<Map<String, Order>> list;// 用于存储数据库读取的数据集
    private List<Cartitem> webDatas;// 根据数据库订单再到网络取的数据
    private ListView shopcart_listView;// 订单列表主体控件
    private ShopcartAdapter adapter;// 订单信息适配器
    private TextView sumView;// 所有订单总价
    private Button card_btn;// 结算按钮
    private Button cart_edit_btn, totalCbx, del_item;// 编辑按钮，全选按钮，移除按钮
    private boolean isEmpty = true;// 购物车是否为空
    private RelativeLayout bbr_cartitem, bbr_item;
    private List<A> listCheck;// 订单项目勾选状态
    private static Cart cart = null;// 购物车
    private CustomApplication cap;
    private int temp = 0;
    private ProgressDialog progressDialog;// 加载页面的进度条
    private AlertDialog.Builder builder;
    private DatabaseService ds;// 数据库操作对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cap = (CustomApplication) getApplication();
        ds = DBHelper.getDB();
        list = GetDBData();
        if (list == null || list.size() == 0) {
            setContentView(R.layout.cart_empty);// 加载空购物车的页面
        } else {
            if (cart == null) {
                cart = new Cart();
            }
            isEmpty = false;
            setContentView(R.layout.layout_shopcart_full);// 加载有商品的购物车页面
            findViews();
            setListeners();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!isEmpty) {
            if (cart.getMap().size() > 0) {
                cart.getMap().clear();
            }
            if (webDatas != null) {
                webDatas.clear();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        temp = 0;// 当fragment可见时获取数据
        list = GetDBData();
        if (list.isEmpty()) {
            isEmpty = true;
            setContentView(R.layout.cart_empty);
        } else {
            initData();
            if (bbr_item.getVisibility() == View.VISIBLE) {
                bbr_item.setVisibility(View.GONE);
                bbr_cartitem.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initData() {
        /*
         * if (!cap.networkIsAvailable()) { Toast.makeText(this, "没网络",
		 * Toast.LENGTH_SHORT).show(); return; }
		 */
        new ShopAsyncTask().execute();// 异步获得订单数据
    }

    /*
     * 获取本地的sqlite订单总数
     */
    private List<Map<String, Order>> GetDBData() {
        return ds.findAllOrder();
    }

    /**
     * 初始化控件
     */
    private void findViews() {
        shopcart_listView = (ListView) findViewById(R.id.shopcart_listView);
        sumView = (TextView) findViewById(R.id.bbar_card_sumprice);
        card_btn = (Button) findViewById(R.id.bbar_card_btn);
        totalCbx = (Button) findViewById(R.id.totalCbx);
        cart_edit_btn = (Button) findViewById(R.id.cart_edit_btn);
        del_item = (Button) findViewById(R.id.del_item);
        bbr_cartitem = (RelativeLayout) findViewById(R.id.bbr_cartitem);
        bbr_item = (RelativeLayout) findViewById(R.id.bbr_item);
    }

    /**
     * 初始化监听
     */
    private void setListeners() {
        card_btn.setOnClickListener(this);
        totalCbx.setOnClickListener(this);
        cart_edit_btn.setOnClickListener(this);
        del_item.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.bbar_card_btn:
                // 结算
                if (cart.getMap().size() > 0) {
                    if (cap.getUserinfo() != null) {
                        SerializableList serializablelist = new SerializableList();
                        for (Map.Entry<Integer, Cartitem> entry : cart.getMap()
                                .entrySet()) {
                            serializablelist.getList().add(entry.getValue());
                        }
                        Intent intent = new Intent(this, ShopListActivity.class);
                        intent.putExtra("serializablelist", serializablelist);//购物项目
                        intent.putExtra("isfromCart", true);//订单提交来源true为购物车，false为其他
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.totalCbx:
                // 条目全选反选
                temp = temp % 2 + 1;
                switch (temp) {
                    case 1:
                        cart.getMap().clear();
                        for (A click : listCheck) {
                            click.type = A.TYPE_NOCHECKED;
                            if (totalCbx.getText().equals("反选")) {
                                totalCbx.setText("全选");
                            }
                        }
                        break;
                    case 2:
                        for (Cartitem cartitem : webDatas) {
                            Order order = cartitem.getOrder();
                            cart.getMap().put(order.getCid(), cartitem);
                        }
                        for (A unclick : listCheck) {
                            unclick.type = A.TYPE_CHECKED;
                            if (totalCbx.getText().equals("全选")) {
                                totalCbx.setText("反选");
                            }
                        }
                        break;
                }
                adapter.notifyDataSetChanged();
                if (cart.getTotalcount() > 0) {
                    refresh();
                } else {
                    sumView.setText("￥(0)");// 给总价控件赋值
                    card_btn.setText("结算(0)");
                }
                break;
            case R.id.cart_edit_btn:
                // 编辑订单样式
                if (bbr_item.getVisibility() == View.GONE) {
                    bbr_cartitem.setVisibility(View.GONE);
                    bbr_item.setVisibility(View.VISIBLE);
                    cart_edit_btn.setText("完成");
                } else {
                    bbr_item.setVisibility(View.GONE);
                    bbr_cartitem.setVisibility(View.VISIBLE);
                    cart_edit_btn.setText("编辑");
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.del_item:
                // 删除选中的订单条目
                if (cart.getMap().size() == 0) {
                    Toast.makeText(this, "勾选后操作！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (builder == null) {
                    builder = new Builder(this);
                }
                builder.setIcon(R.drawable.logo);
                builder.setTitle("操作提醒");
                builder.setMessage("您确定要移除吗？");
                builder.setPositiveButton("坚决移除",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<Integer, Cartitem> delmap = cart.getMap();
                                Iterator<Integer> iterator = delmap.keySet()
                                        .iterator();
                                while (iterator.hasNext()) {
                                    Integer cid = iterator.next();
                                    // 先更改数据库的商品个数,再更新页面数据
                                    int did = ds.delOrder(cid);
                                    if (did > 0) {
                                        iterator.remove();
                                    }
                                    Iterator<Cartitem> it = webDatas.iterator();
                                    while (it.hasNext()) {
                                        Cartitem cartitem = it.next();
                                        int id = cartitem.getOrder().getCid();
                                        if (id == cid) {
                                            it.remove();
                                        }
                                    }
                                    Iterator<A> ita = listCheck.iterator();
                                    while (ita.hasNext()) {
                                        A a = ita.next();
                                        if (a.type == A.TYPE_CHECKED) {
                                            ita.remove();
                                        }
                                    }
                                }
                                if (webDatas.size() == 0) {
                                    isEmpty = true;
                                    setContentView(R.layout.cart_empty);
                                }
                                adapter.notifyDataSetChanged();
                                refresh();
                            }

                        }).setNegativeButton("保留", null).show();
                break;
        }
    }

    /* 内部类、异步获取订单信息 */
    private class ShopAsyncTask extends AsyncTask<String, String, List<Cartitem>> {
        /* 预处理 */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(CartActivity.this);
            }
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /* 子线程处理 */
        @Override
        protected List<Cartitem> doInBackground(String... params) {
            List<Cartitem> items = new ArrayList<>();
            for (Map<String, Order> map : list) {
                // 先将数据库的商品分个取出
                for (Map.Entry<String, Order> entry : map.entrySet()) {
                    Order order = entry.getValue();
                    Cartitem cartitem = new Cartitem();
                    cartitem.setOrder(order);
                    cartitem.setProductPrice(order.getPrice());
                    cartitem.setItemCount(order.getCount());// 装载商品项，一个商品项是同一个商品多个数量
                    cart.getMap().put(order.getCid(), cartitem);// 装载商品项到购物车
                    items.add(cartitem);// 商品项list集合方便页面适配
                }

            }
            return items;// 返回的列表里面图片只是地址而不是图片文件,图片在适配器里再真正的加载
        }

        /* 主线程处理 */
        @Override
        protected void onPostExecute(List<Cartitem> result) {
            super.onPostExecute(result);
            webDatas = result;// 赋值给页面
            progressDialog.dismiss();// 关闭加载进度条
            if (null != shopcart_listView && null != webDatas) {
                adapter = new ShopcartAdapter(CartActivity.this, webDatas);
                shopcart_listView.setAdapter(adapter);// 设定适配器
                refresh();
            } else {
                loadNotework();
            }
        }
    }

    private void loadNotework() {
        if (builder == null) {
            builder = new Builder(this);
        }
        builder.setMessage("连接服务器失败");
        builder.setPositiveButton("刷新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                initData();
            }

        }).show();
    }

    /**
     * 购物车视图适配器
     */
    class ShopcartAdapter extends BaseAdapter {
        private final LayoutInflater inflater;// 加载布局用的,视图容器
        private final List<Cartitem> datas;// 数据资源
        A a;

        /* 构造函数 */
        ShopcartAdapter(Context context, List<Cartitem> datas) {
            this.inflater = LayoutInflater.from(context);
            this.datas = datas;
            listCheck = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                a = new A(A.TYPE_CHECKED);
                listCheck.add(a);
            }
        }

        public int getCount() {
            return datas.size();// 返回数据列表长度
        }

        public Object getItem(int position) {
            return datas.get(position);
        }

        public long getItemId(int position) {
            return position;// 返回下标
        }

        /* 重写视图 */
        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent) {
            final int index = position;
            ViewHoler viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHoler();
                convertView = inflater.inflate(R.layout.shopcart_item, null);
                viewHolder.cartItemCbx = (CheckBox) convertView
                        .findViewById(R.id.cartItemCbx);
                viewHolder.img = (ImageView) convertView
                        .findViewById(R.id.shopcart_item_img);
                viewHolder.title = (TextView) convertView
                        .findViewById(R.id.shopcart_item_title);
                viewHolder.color = (TextView) convertView
                        .findViewById(R.id.productcolor);
                viewHolder.size = (TextView) convertView
                        .findViewById(R.id.productsize);
                viewHolder.cut = (Button) convertView
                        .findViewById(R.id.shopcart_cut);
                viewHolder.count = (EditText) convertView
                        .findViewById(R.id.shopcart_item_count);
                viewHolder.add = (Button) convertView
                        .findViewById(R.id.shopcart_add);
                viewHolder.price = (TextView) convertView
                        .findViewById(R.id.shopcart_item_price);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHoler) convertView.getTag();
            }
            final Cartitem cartitem = datas.get(index);
            final Order order = cartitem.getOrder();
            viewHolder.title.setText(order.getGoodName());
            viewHolder.color.setText(String.valueOf("颜色:" + order.getColor()));
            viewHolder.size.setText(String.valueOf("尺寸:" + order.getSize()));
            viewHolder.count.setText(String.valueOf(cartitem.getItemCount()));
            viewHolder.price.setText(String.valueOf("￥" + order.getPrice()));
            if (cartitem.getItemCount() == 1) {// 单个条目商品数量为1时减号按钮不可用
                viewHolder.cut.setEnabled(false);
            } else if (cartitem.getItemCount() > 1) {
                viewHolder.cut.setEnabled(true);
            }
            viewHolder.cartItemCbx// 单个订单条目选中事件
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            List<A> list = new ArrayList<>();
                            if (isChecked) {
                                listCheck.get(index).type = A.TYPE_CHECKED;
                                cart.getMap().put(order.getCid(), cartitem);
                            } else {
                                listCheck.get(index).type = A.TYPE_NOCHECKED;
                                cart.getMap().remove(order.getCid());
                            }
                            ShopcartAdapter.this.notifyDataSetChanged();// 更新列表
                            if (cart.getTotalcount() > 0) {
                                refresh();
                            } else {
                                sumView.setText("￥(0)");// 给总价控件赋值
                                card_btn.setText("结算(0)");
                            }

                            for (A check : listCheck) {
                                if (check.type == A.TYPE_CHECKED) {
                                    list.add(check);
                                }
                            }
                            if (list.size() == listCheck.size()) {
                                totalCbx.setText("反选");
                                temp = 0;
                            } else {
                                totalCbx.setText("全选");
                                temp = 1;
                            }
                        }
                    });

            if (listCheck.get(index).type == A.TYPE_CHECKED) {
                viewHolder.cartItemCbx.setChecked(true);

            } else {
                viewHolder.cartItemCbx.setChecked(false);
            }

            viewHolder.cut.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {// 减号按钮点击事件
                    int cid = order.getCid();// 获得点击减号的当前商品的订单id
                    int newcount = cartitem.getItemCount();
                    // 先更改数据库的商品个数,再更新页面数据
                    int uid = ds.updateOrder(cid, newcount - 1);
                    if (uid > 0) {
                        cartitem.setItemCount(newcount - 1);
                    }
                    ShopcartAdapter.this.notifyDataSetChanged();// 更新列表
                    // 更新总价钱和总商品个数
                    refresh();
                }
            });
            viewHolder.add.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {// 加号按钮点击事件
                    int cid = order.getCid();// 获得点击加号的当前商品的订单id
                    int newCount = cartitem.getItemCount();
                    // 先更改数据库的商品个数,再更新页面数据
                    int uid = ds.updateOrder(cid, newCount + 1);
                    if (uid > 0) {
                        cartitem.setItemCount(newCount + 1);
                    }
                    ShopcartAdapter.this.notifyDataSetChanged();// 更新列表
                    // 更新总价钱和总商品个数
                    refresh();
                }

            });
            String imgUrl = GeneralUtil.GOODSPATH + order.getImgpath();// 图片地址
            BitmapHelp.getBitmapUtils().bind(viewHolder.img, imgUrl);
            return convertView;
        }
    }

    /* 适配器内部类、用于封装视图组件 */
    final class ViewHoler {
        ImageView img;
        TextView title, color, size, price;
        Button cut, add;
        EditText count;
        CheckBox cartItemCbx;
    }

    class A {// 内部类标识checkBox状态

        static final int TYPE_CHECKED = 1;
        static final int TYPE_NOCHECKED = 0;

        int type;

        A(int type) {
            this.type = type;
        }
    }

    private void refresh() {
        BigDecimal bigDecimal = new BigDecimal(cart.getTotalprice());
        sumView.setText(String.valueOf("￥" + bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue()));// 给总价控件赋值
        card_btn.setText("结算("
                + String.valueOf(cart.getTotalcount()) + ")");
    }

}
