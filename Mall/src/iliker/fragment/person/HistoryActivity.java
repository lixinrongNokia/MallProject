package iliker.fragment.person;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.entity.Goods;
import iliker.fragment.mystore.BaseStoreActivity;
import iliker.fragment.type.ProductDetailActivity;

import java.util.List;

import static iliker.utils.DBHelper.getDB;

public class HistoryActivity extends BaseStoreActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView hisotrylv;
    private HistoryAdapter historyAdapter;
    private TextView emptytext;
    private List<Goods> list;
    private final String[] content = new String[]{"删除", "清空"};

    @Override
    protected void initMyViews() {
        title.setText("浏览历史");
        View view = View.inflate(this, R.layout.history_layout, null);
        hisotrylv = (ListView) view.findViewById(R.id.hisotrylv);
        emptytext = (TextView) view.findViewById(R.id.empty);
        list = getDB().findHistory();
        if (list.isEmpty()) {
            hisotrylv.setVisibility(View.GONE);
            emptytext.setVisibility(View.VISIBLE);
        } else {
            emptytext.setVisibility(View.GONE);
            hisotrylv.setVisibility(View.VISIBLE);
            historyAdapter = new HistoryAdapter(this, list);
            hisotrylv.setAdapter(historyAdapter);
            hisotrylv.setOnItemClickListener(this);
            hisotrylv.setOnItemLongClickListener(this);
        }
        storeContent.addView(view);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Goods good = list.get(position);
        Intent intent = new Intent(this, ProductDetailActivity.class);// 跳到商品详情页面
        intent.putExtra("good", good);
        startActivity(intent);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(content, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("删除".equals(content[which])) {
                    getDB().delHistoryById(list.get(position).getId());
                    list.remove(position);
                }
                if ("清空".equals(content[which])) {
                    getDB().delHistory();
                    list.clear();
                }
                if (list.isEmpty()) {
                    hisotrylv.setVisibility(View.GONE);
                    emptytext.setVisibility(View.VISIBLE);
                } else {
                    historyAdapter.notifyDataSetChanged();
                }
            }
        }).show();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (list != null) {
            list.clear();
            list = null;
        }
    }
}
