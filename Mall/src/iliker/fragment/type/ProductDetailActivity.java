package iliker.fragment.type;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fjl.widget.AutoScrollViewPager;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.iliker.mall.R;
import cn.sharesdk.onekeyshare.OnekeyShare;
import iliker.db.DatabaseService;
import iliker.entity.Goods;
import iliker.entity.Order;
import iliker.fragment.faxian.ImageActivity;
import iliker.fragment.mystore.PromoteActivity;
import iliker.mall.CartActivity;
import iliker.mall.XCFlowLayout;
import iliker.utils.GeneralUtil;

import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.DBHelper.getDB;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * 商品展示
 */
public class ProductDetailActivity extends FragmentActivity implements OnClickListener {
    private int index = 0;
    private Button add_shop_cart;// 添加到购物车按钮
    private PopupWindow popupWindow;//选择尺寸颜色菜单
    private TextView cart_icon_num;//购物车内商品数量
    private ImageButton sharebtn, collectionbtn;
    private CustomApplication cap;
    private RelativeLayout cart;
    private DatabaseService ds;
    private View view;
    private String selcor = "";
    private String selsize = "";
    private TextView addpromote;
    private List<TextView> list2 = new ArrayList<>();
    private List<TextView> list3 = new ArrayList<>();
    private Map<Integer, TextView> maps2 = new HashMap<>();
    private Map<Integer, TextView> maps3 = new HashMap<>();
    private final MyListener myListener = new MyListener(maps3);
    private final MyListener myListener2 = new MyListener(maps2);
    private Goods goods;
    private TextView product_price;// 商品价格展示控件
    private TextView product_info;// 商品信息展示控件
    private TextView integral;//积分
    private TextView t_more_pic;
    private AutoScrollViewPager mPosterPager;
    private LinearLayout pointsLayout;
    private List<ImageView> points;
    private List<String> posterImage;
    private String[] imgPath;
    private ImageButton backbtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        ds = getDB();
        cap = (CustomApplication) getApplication();
        goods = getIntent().getParcelableExtra("good");
        findViews();
        initData();
        setListeners();
        initPoints();
        initPoster();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showcart_icon_num();
        if (imgPath.length > 1) {
            mPosterPager.startAutoScroll();
        }
    }

    private class MyListener implements OnClickListener {
        private final Map<Integer, TextView> map;

        MyListener(Map<Integer, TextView> map) {
            this.map = map;
        }

        @Override
        public void onClick(View v) {
            TextView vi = (TextView) v;
            if (map.get(vi.getId()) == null) {
                if (!map.isEmpty()) {
                    Iterator<Map.Entry<Integer, TextView>> it = map
                            .entrySet().iterator();
                    while (it.hasNext()) {
                        TextView t = it.next().getValue();
                        t.setTextColor(getResources().getColor(
                                R.color.myblack));
                        t.setBackgroundResource(R.drawable.sel_textview_bg);
                        it.remove();
                    }

                }
                map.put(vi.getId(), vi);
                vi.setTextColor(getResources().getColor(
                        R.color.defaultWhite));
                vi.setBackgroundResource(R.drawable.no_textview_bg);

            } else {
                map.clear();
                vi.setTextColor(getResources()
                        .getColor(R.color.myblack));
                vi.setBackgroundResource(R.drawable.sel_textview_bg);
            }
        }
    }

    private void initData() {
        product_price.setText(String.valueOf("￥" + goods.getPrice()));
        product_info.setText(Html.fromHtml(goods.getGoodsDesc()));
        integral.setText(String.valueOf((int) (goods.getPrice() * 0.1 + 0.5f)));
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        lp.topMargin = 10;
        lp.bottomMargin = 10;
        view = getLayoutInflater().inflate(
                R.layout.product_attr, null);
        XCFlowLayout colors = (XCFlowLayout) view.findViewById(R.id.colors);
        XCFlowLayout sizes = (XCFlowLayout) view.findViewById(R.id.sizes);
        String[] colorStr = goods.getColors().split("#");
        String[] sizeStr = goods.getSizes().split("#");
        selcor = colorStr[0];
        selsize = sizeStr[0];
        for (int i = 0; i < sizeStr.length; i++) {
            TextView view = new TextView(this);
            list2.add(view);
            view.setId(i + 1);
            view.setText(sizeStr[i]);
            view.setTextColor(getResources().getColor(R.color.myblack));
            view.setBackgroundResource(R.drawable.sel_textview_bg);
            colors.addView(view, lp);
        }
        for (int i = 0; i < colorStr.length; i++) {
            TextView view = new TextView(this);
            list3.add(view);
            view.setId(i + 20);
            view.setText(colorStr[i]);
            view.setTextColor(getResources().getColor(R.color.myblack));
            view.setBackgroundResource(R.drawable.sel_textview_bg);
            sizes.addView(view, lp);
        }
        view.findViewById(R.id.selbtn).setOnClickListener(ProductDetailActivity.this);

        points = new LinkedList<>();
        posterImage = new LinkedList<>();
        imgPath = goods.getImgpath().split("#");
        for (String img : imgPath) {
            posterImage.add(GeneralUtil.GOODSPATH + img);
        }
    }

    private void initPoints() {
        // 设置 ViewPager的高度为屏幕宽度的1/5
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getScreen(this).heightPixels / 2);
        mPosterPager.setLayoutParams(params);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8, 15, 8, 15);

        for (int i = 0; i < imgPath.length; i++) {
            // 添加标记点
            ImageView point = new ImageView(this);

            if (i == index % imgPath.length) {
                point.setBackgroundResource(R.drawable.feature_point_cur);
            } else {
                point.setBackgroundResource(R.drawable.feature_point);
            }
            point.setLayoutParams(lp);

            points.add(point);
            pointsLayout.addView(point);
        }
    }

    /**
     * 获取屏幕宽高
     */
    private DisplayMetrics getScreen(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    private class PosterPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (imgPath.length > 1)
                return Integer.MAX_VALUE;
            return 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(ProductDetailActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);
            getBitmapUtils().bind(imageView, posterImage.get(position % imgPath.length));
            container.addView(imageView);
            imageView.setOnClickListener(new PosterClickListener(position % imgPath.length));
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    private class PosterClickListener implements OnClickListener {

        private final int position;

        PosterClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            mPosterPager.stopAutoScroll();
            Intent intent = new Intent(ProductDetailActivity.this,
                    ImageActivity.class);
            String[] strings = new String[posterImage.size()];
            intent.putExtra("imgpaths", posterImage.toArray(strings));
            intent.putExtra(ImageActivity.CURRENT_INDEX, position);
            startActivity(intent);
        }

    }

    private void initPoster() {
        mPosterPager.setAdapter(new PosterPagerAdapter());
        mPosterPager.setCurrentItem(imgPath.length * 500);
        mPosterPager.setInterval(3000);
        mPosterPager.addOnPageChangeListener(new PosterPageChange());
        mPosterPager
                .setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mPosterPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosterPager.stopAutoScroll();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mPosterPager.startAutoScroll();
                        break;
                    case MotionEvent.ACTION_UP:
                        mPosterPager.startAutoScroll();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPosterPager.stopAutoScroll();
    }

    private class PosterPageChange implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            index = position;
            for (int i = 0; i < imgPath.length; i++) {
                points.get(i).setBackgroundResource(R.drawable.feature_point);
            }
            points.get(position % imgPath.length).setBackgroundResource(
                    R.drawable.feature_point_cur);
        }

    }

    /**
     * 初始化控件
     */
    private void findViews() {
        backbtn = (ImageButton) findViewById(R.id.backbtn);
        mPosterPager = (AutoScrollViewPager) findViewById(R.id.poster_pager);
        add_shop_cart = (Button) findViewById(R.id.add_shop_cart);
        cart_icon_num = (TextView) findViewById(R.id.cart_icon_num);
        sharebtn = (ImageButton) findViewById(R.id.sharebtn);
        collectionbtn = (ImageButton) findViewById(R.id.collectionbtn);
        cart = (RelativeLayout) findViewById(R.id.cart);
        addpromote = (TextView) findViewById(R.id.addpromote);
        product_price = (TextView) findViewById(R.id.product_price);
        product_info = (TextView) findViewById(R.id.product_info);
        integral = (TextView) findViewById(R.id.integral);
        t_more_pic = (TextView) findViewById(R.id.more_pic);
        pointsLayout = (LinearLayout) findViewById(R.id.points);
    }

    private void alertDialog() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(this);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
            // 设置SelectPicPopupWindow弹出窗体的高
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            // 设置SelectPicPopupWindow弹出窗体可点击
            popupWindow.setFocusable(true);
            popupWindow.setFocusable(true);
            popupWindow.setAnimationStyle(R.style.AnimBottom); // 设置动画
            popupWindow.setContentView(view);
        }

        popupWindow.showAtLocation(findViewById(R.id.attrRoot),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);// 需要指定Gravity，默认情况是center.
        /*popupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
               popupWindow.setAnimationStyle(R.style.product_attr_style); // 设置动画
            }
        });*/
    }

    /**
     * 添加购物车
     */
    private void setListeners() {
        add_shop_cart.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        cart.setOnClickListener(this);
        sharebtn.setOnClickListener(this);
        collectionbtn.setOnClickListener(this);
        addpromote.setOnClickListener(this);
        for (TextView view : list2) {
            view.setOnClickListener(myListener2);
        }
        for (TextView view : list3) {
            view.setOnClickListener(myListener);
        }
        t_more_pic.setOnClickListener(this);
    }

    private void showcart_icon_num() {
        int counts = ds.findTotalCount();
        if (counts > 0) {
            cart_icon_num.setVisibility(View.VISIBLE);
            cart_icon_num.setText(String.valueOf(counts));
        } else {
            cart_icon_num.setText("");
            cart_icon_num.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backbtn:
                finish();
                break;
            case R.id.add_shop_cart:// 添加商品到购物车
                alertDialog();
                break;
            case R.id.cart:// 跳转购物车
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                break;
            case R.id.sharebtn:// 分享
                if (cap.getUserinfo() == null) {
                    Toast.makeText(this, "登陆后操作", Toast.LENGTH_SHORT).show();
                    return;
                }
                OnekeyShare oks = new OnekeyShare();
                // 关闭sso授权
                oks.disableSSOWhenAuthorize();
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle(goods.getGoodName());
                // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
                oks.setTitleUrl("http://iliker.cn/wx/product.jsp?goodid=" + goods.getId());
                // text是分享文本，所有平台都需要这个字段
                oks.setText(goods.getGoodsDesc());
                //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
                oks.setImageUrl(GeneralUtil.GOODSPATH + goods.getImgpath().split("#")[0]);
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
                // url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://iliker.cn/wx/product.jsp?goodid=" + goods.getId());
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                oks.setComment("好商品要分享");
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite("iliker");
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl("http://iliker.cn");
                // 启动分享GUI
                oks.show(this);
                break;
            case R.id.collectionbtn:// 收藏
                if (cap.getUserinfo() == null) {
                    Toast.makeText(this, "登陆后操作", Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestParams params = new RequestParams(GeneralUtil.COLLGOODS);
                params.addBodyParameter("nickname", cap.getUname());
                params.addBodyParameter("goodid", goods.getId() + "");
                params.addBodyParameter("color", selcor);
                params.addBodyParameter("size", selsize);
                getHttpUtils().post(params,
                        new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Toast.makeText(ProductDetailActivity.this,
                                        result, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                Toast.makeText(ProductDetailActivity.this, "发生错误",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                break;
            case R.id.selbtn:
                if (maps2.isEmpty() || maps3.isEmpty()) {
                    ToastFactory.getMyToast("选择颜色与尺寸").show();
                    return;
                }
                addCart();
                break;
            case R.id.addpromote:
                Intent intent2 = new Intent(this, PromoteActivity.class);
                intent2.putExtra("goldtwitter", true);
                intent2.putExtra("forwardProduct", true);
                startActivity(intent2);
                break;
            case R.id.more_pic:
                Intent more = new Intent(this, DetailPicActivity.class);
                more.putExtra("goods", goods);
                startActivity(more);
                break;
        }
    }

    private void addCart() {
        Iterator<Map.Entry<Integer, TextView>> iterator = maps2.entrySet().iterator();
        Iterator<Map.Entry<Integer, TextView>> iterator2 = maps3.entrySet().iterator();
        selcor = iterator.next().getValue().getText().toString();
        selsize = iterator2.next().getValue().getText().toString();
        long size;
        Order order = ds.findOrder(goods.getId(), selcor, selsize);
        if (order == null)
            size = ds.addOrder(goods, selcor, selsize);
        else
            size = ds.updateOrder(order.getCid(), order.getCount() + 1);
        if (size > 0) {
            Toast.makeText(ProductDetailActivity.this, "已加入购物车",
                    Toast.LENGTH_SHORT).show();
            showcart_icon_num();
        } else {
            Toast.makeText(ProductDetailActivity.this, "添加失败!",
                    Toast.LENGTH_SHORT).show();
        }
        popupWindow.dismiss();
    }

    @Override
    protected void onDestroy() {
        view = null;
        if (popupWindow != null) {
            popupWindow = null;
        }
        super.onDestroy();
    }

}
