package iliker.fragment.type;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.cardsui.example.Mall_Activity;
import iliker.adapter.DefaultAdapter;
import iliker.holder.BaseHolder;

import java.util.List;
import java.util.Map;

public class ListViewAdapter extends DefaultAdapter<Map<String, Object>> {
    private List<Map<String, Object>> mList;
    private final Context mContext;

    public ListViewAdapter(List<Map<String, Object>> datas, Context mContext) {
        super(datas);
        this.mContext = mContext;
    }

    @Override
    public BaseHolder getHolder() {
        return new ViewHolder(mContext);
    }

    class ViewHolder extends BaseHolder<Map<String, Object>> {
        public ViewHolder(Context context) {
            super(context);
        }

        TextView tv_namelabel;
        GridView gridView;

        @Override
        public View initViews() {
            View convertView = View.inflate(mContext, R.layout.listview_type_item, null);
            this.tv_namelabel = (TextView) convertView
                    .findViewById(R.id.tv_namelabel);
            this.gridView = (GridView) convertView
                    .findViewById(R.id.listview_item_gridview);
            return convertView;
        }

        @Override
        public void refreshView(Map<String, Object> datas) {
            final List<Map<String, String>> clolist = (List<Map<String, String>>) datas
                    .get("clothes");
            this.tv_namelabel.setText(String
                    .valueOf(datas.get("crowdName")));
            if (this.gridView != null) {
                GridViewAdapter gridViewAdapter = new GridViewAdapter(mContext,
                        datas);
                this.gridView.setAdapter(gridViewAdapter);
            }
            this.gridView
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int arg2, long arg3) {
                            if (clolist != null) {
                                Map<String, String> clo = clolist.get(arg2);
                                if (clo != null) {
                                    Intent intent = new Intent(mContext,
                                            Mall_Activity.class);
                                    intent.putExtra("clothestypeid",
                                            clo.get("id"));
                                    mContext.startActivity(intent);
                                }
                            }
                        }

                    });
        }
    }

}
