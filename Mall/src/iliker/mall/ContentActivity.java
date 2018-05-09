package iliker.mall;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cardsui.example.IndexFragment;

import java.util.ArrayList;

import cn.iliker.mall.R;
import iliker.fragment.BodyCollFragment;
import iliker.fragment.FindFragment;
import iliker.fragment.MyStoreFragment;
import iliker.fragment.PersonFragment;

public class ContentActivity extends AppCompatActivity implements OnTouchListener {
    // bbar导航栏的五个、用于显示图片的控件
    private TextView textView1, textView2, textView3, textView4;
    TextView textView5;
    private ArrayList<TextView> textviews;

    // 五个导航项*/
    private IndexFragment index;
    private BodyCollFragment bodycoll;
    private FindFragment find;
    private MyStoreFragment mystore;
    PersonFragment person;

    private LinearLayout bbar;// bbar导航栏
    private FragmentManager fragmentManager;// 碎片管理
    int selectId = 0;// 记录当前选中下标

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.findViews();
        this.initViews();
        this.setListener();
        this.fragmentManager = getSupportFragmentManager();

    }

    /**
     * 初始化控件
     */
    private void findViews() {
        textView1 = (TextView) findViewById(R.id.hometext);
        textView2 = (TextView) findViewById(R.id.typetext);
        textView3 = (TextView) findViewById(R.id.faxiantext);
        textView4 = (TextView) findViewById(R.id.carttext);
        textView5 = (TextView) findViewById(R.id.persontext);

        bbar = (LinearLayout) findViewById(R.id.bbar);
    }

    /**
     * 初始化数据
     */
    private void initViews() {
        textviews = new ArrayList<>();
        textviews.add(textView1);
        textviews.add(textView2);
        textviews.add(textView3);
        textviews.add(textView4);
        textviews.add(textView5);

    }

    /**
     * 初始化监听事件
     */
    private void setListener() {
        for (TextView item : textviews) {
            item.setOnTouchListener(this);
        }
    }

    /**
     * 屏幕触摸事件
     */
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.hometext:
                if (selectId == 0) {
                    return false;// 预防用户一直点一个键
                }
                selectId = 0;
                break;
            case R.id.typetext:
                if (selectId == 1) {
                    return false;
                }
                selectId = 1;
                break;
            case R.id.faxiantext:
                if (selectId == 2) {
                    return false;
                }
                selectId = 2;
                break;
            case R.id.carttext:
                if (selectId == 3) {
                    return false;
                }
                selectId = 3;
                break;
            case R.id.persontext:
                if (selectId == 4) {
                    return false;
                }
                selectId = 4;
                break;
        }
        this.setSelection(selectId);
        return false;
    }

    /**
     * 根据下标进行碎片管理
     *
     * @param selectid 选中的bbar下标
     */
    void setSelection(int selectid) {
        this.clearSelection();// 清空选中状态
        this.bbar.setVisibility(View.VISIBLE);// 显示底bbar
        FragmentTransaction ft = fragmentManager.beginTransaction();// 开始事务
        switch (selectid) {
            case 0:
                // 将bbar主页图片更换成选中状态的图片
                this.textView1.setBackgroundResource(R.drawable.index_normal);
                // 将bbar主页背景换成有红线并加黑的图片
                if (null == index) {
                    index = new IndexFragment();// 新建一个主页视图、碎片
                    ft.addToBackStack("index");
                }
                ft.replace(R.id.content, index);// 将碎片视图添加到主页面的主体部分、这里可以用replace
                break;
            case 1:
                this.textView2.setBackgroundResource(R.drawable.find_normal);
                if (bodycoll == null) {
                    bodycoll = new BodyCollFragment();
                    ft.addToBackStack("bodycoll");
                }
                ft.replace(R.id.content, bodycoll);
                break;
            case 2:
                this.textView3.setBackgroundResource(R.drawable.aixiu_normal);
                if (null == find) {
                    find = new FindFragment();
                    ft.addToBackStack("find");
                }
                ft.replace(R.id.content, find);
                break;
            case 3:
                this.textView4.setBackgroundResource(R.drawable.classify_normal);
                if (null == mystore) {
                    mystore = new MyStoreFragment();
                    ft.addToBackStack("mystore");
                }
                ft.replace(R.id.content, mystore);
                break;
            case 4:
                this.textView5.setBackgroundResource(R.drawable.penson_normal);
                if (null == person) {
                    person = new PersonFragment();
                    ft.addToBackStack("person");
                }
                ft.replace(R.id.content, person);
                break;
        }
        ft.commitAllowingStateLoss();// 提交事务
    }

    /**
     * 清空选中状态
     */
    private void clearSelection() {
        // 将bbar主页图片更换成没选中状态的图片
        textView1.setBackgroundResource(R.drawable.index_focus);
        textView2.setBackgroundResource(R.drawable.find_focus);
        textView3.setBackgroundResource(R.drawable.aixiu_focus);
        textView4.setBackgroundResource(R.drawable.classify_focus);
        textView5.setBackgroundResource(R.drawable.penson_focus);
    }
}
