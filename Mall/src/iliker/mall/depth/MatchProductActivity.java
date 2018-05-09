package iliker.mall.depth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cardsui.example.goods.ClassificationActivity;
import com.fjl.widget.DialogFactory;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import cn.iliker.mall.R;
import iliker.mall.MainActivity;

import static iliker.utils.HttpHelp.getHttpUtils;
public class MatchProductActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matchproduct_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        ((GridView) findViewById(R.id.gridView)).setOnItemClickListener(this);
        dialog = DialogFactory.initDialog(this);
        initData();
    }

    private void initData() {
        dialog.show();
        RequestParams params = new RequestParams("");
        getHttpUtils().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                dialog.dismiss();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem indexitem = menu.add(0, 1, 0, "首页");
        indexitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        indexitem.setIcon(R.drawable.ic_menu_home);

        MenuItem moreitem = menu.add(0, 2, 0, "更多");
        moreitem.setIcon(android.R.drawable.ic_menu_more);
        moreitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 1:
                Intent homeIntent = new Intent(this, MainActivity.class);
                startActivity(homeIntent);
                this.finish();
                break;
            case 2:
                Intent intent = new Intent(this, ClassificationActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
