package iliker.fragment.type;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Goods;
import iliker.holder.BaseHolder;

import java.util.List;

 class SearchAdapter extends DefaultAdapter<Goods> {
     final private Context context;

     SearchAdapter(List<Goods> datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new SearchHolder(context);
    }

    private class SearchHolder extends BaseHolder<Goods> {
        TextView textView;

         SearchHolder(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.search_item, null);
            textView = (TextView) view.findViewById(R.id.item_search_tv_content);
            return view;
        }

        @Override
        public void refreshView(Goods datas) {
            textView.setText(Html.fromHtml(datas.getGoodName()));
        }
    }
}
