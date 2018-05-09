package iliker.fragment.mystore;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.iliker.application.CustomApplication;

import org.xutils.x;

import cn.iliker.mall.R;
import iliker.entity.UserInfo;

public abstract class BaseStoreActivity extends FragmentActivity {
    protected TextView title;
    protected FrameLayout storeContent;
    protected String phone = "";
    String superiornum = "";
    protected UserInfo userInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        CustomApplication.actlist.add(this);
        setContentView(R.layout.basestore_layout);
        userInfo = CustomApplication.customApplication.getUserinfo();
        if (userInfo != null) {
            phone = userInfo.getPhone();
            superiornum = userInfo.getSuperiornum();
        }
        findViews();
        initMyViews();
    }

    protected abstract void initMyViews();

    private void findViews() {
        title = (TextView) findViewById(R.id.title);
        storeContent = (FrameLayout) findViewById(R.id.storeContent);
        findViewById(R.id.backbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
