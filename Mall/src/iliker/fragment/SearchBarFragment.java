package iliker.fragment;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cn.iliker.mall.R;
import iliker.fragment.type.SearchActivity;

public class SearchBarFragment extends BaseFragment {

    @Override
    protected View initSubclassView() {
        if (view == null) {
            view = View.inflate(context, R.layout.search_bar, null);
            view.findViewById(R.id.searchView).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(context, SearchActivity.class);
                    context.startActivity(intent);
                }

            });
        }
        return view;
    }
}
