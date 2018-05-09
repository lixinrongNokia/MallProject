package iliker.fragment.finding;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.achep.header2actionbar.FadingActionBarHelper;
import com.iliker.application.CustomApplication;

import cn.iliker.mall.R;
import iliker.entity.UserInfo;

public abstract class ShowPersonality extends Activity {
    private FadingActionBarHelper mFadingActionBarHelper;
    ListViewFragment listViewFragment;
    MenuItem menuItem;
    LinearLayout outtbar;
    Bundle bundles;
    Button reward;
    Button send_message;
    CustomApplication cap;
    UserInfo webuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showperson_layout);
        cap = (CustomApplication) getApplication();
        findViews();
        bundles = getIntent().getBundleExtra("bundle");
        webuser = (UserInfo) bundles.getSerializable("userinfo");
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("返回");
            mFadingActionBarHelper = new FadingActionBarHelper(actionBar,
                    getResources().getDrawable(R.drawable.actionbar_bg));
        }
        initData();
    }

    private void findViews() {
        outtbar = (LinearLayout) findViewById(R.id.outtbar);
        reward = (Button) findViewById(R.id.reward);
        send_message = (Button) findViewById(R.id.send_message);
    }

    protected abstract void initData();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItem = menu.add(0, 1, 0, "更多");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        editTitle();
        return true;
    }

    protected abstract void editTitle();

    protected abstract void menuOnClick();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case 1:
                menuOnClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public FadingActionBarHelper getFadingActionBarHelper() {
        return mFadingActionBarHelper;
    }

}
