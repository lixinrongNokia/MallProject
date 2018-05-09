package iliker.fragment.finding;

import android.content.Intent;
import android.view.View;

/**
 * Created by WDHTC on 2016/5/16.
 */
public class InListViewFragment extends ListViewFragment {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, PraiseListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void initDate() {

    }

}
