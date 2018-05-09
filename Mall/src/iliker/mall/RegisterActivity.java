package iliker.mall;

import android.content.Intent;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import cn.iliker.mall.R;
import iliker.fragment.PersonRegisterFeagment;
import iliker.fragment.StoreRegisterFragment;
import iliker.fragment.mystore.BaseStoreActivity;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * 用户注册
 */
public class RegisterActivity extends BaseStoreActivity {
    private View view;
    private FragmentTabHost tabhost;// 选项卡主控件
    private StoreRegisterFragment storeRegisterFragment;

    @Override
    protected void initMyViews() {
        title.setText("用户注册");
        view = View.inflate(this, R.layout.follwers_layout, null);
        storeContent.addView(view);
        findChildViews();
        storeRegisterFragment = new StoreRegisterFragment();
        initdata();
    }

    private void findChildViews() {
        // 获取TabHost对象
        tabhost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE) {
            storeRegisterFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initdata() {
        tabhost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("个人注册"), PersonRegisterFeagment.class, null);
        tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("门店注册"), StoreRegisterFragment.class, null);
    }
}
