package iliker.fragment.finding;

import android.view.View;
import cn.iliker.mall.R;
import iliker.fragment.mystore.BaseStoreActivity;
import org.xutils.view.annotation.ContentView;

/**
 * Created by LIXINRONG on 2016/7/30.
 */
@ContentView(R.layout.praiselist_layout)
public class PraiseListActivity extends BaseStoreActivity {
    @Override
    protected void initMyViews() {
        title.setText("赞");
        View view= View.inflate(this,R.layout.praiselist_layout,null);
        storeContent.addView(view);
    }
}
