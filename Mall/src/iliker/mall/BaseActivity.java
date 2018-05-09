package iliker.mall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import com.iliker.application.CustomApplication;
import org.xutils.x;

public abstract class BaseActivity extends FragmentActivity {
    protected CustomApplication cap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        CustomApplication.actlist.add(this);
        cap = (CustomApplication) this.getApplication();
        subClassInit();
    }

    protected abstract void subClassInit();
}
