package iliker.fragment.finding.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

import cn.iliker.mall.R;
import iliker.utils.GeneralUtil;
import ui.HorizontalListView;
import ui.HorizontalListViewAdapter;

public class Person_sond_Holder implements AdapterView.OnItemClickListener {
    private final String[] strings;
    private final Context context;
    private final View view;
    private HorizontalListView horizontalListView;

    public Person_sond_Holder(String[] strings, Context context) {
        this.strings = strings;
        this.context = context;
        this.view = loadView();
        this.view.setTag(this);
    }

    private View loadView() {
        View view = View.inflate(context, R.layout.person_head_layout, null);
        this.horizontalListView = (HorizontalListView) view.findViewById(R.id.horizon_listview);
        this.horizontalListView.setOnItemClickListener(this);
        return view;
    }

    public View getConvertView() {
        return view;
    }

    public void setDate() {
        horizontalListView.setAdapter(new HorizontalListViewAdapter(context, strings));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent it = new Intent(Intent.ACTION_VIEW);
        Uri mUri = Uri.parse(GeneralUtil.HEADURL + strings[position + 1]);
        it.setDataAndType(mUri, "image/*");
        context.startActivity(it);
    }
}
