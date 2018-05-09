package iliker.fragment.finding;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Theme;
import iliker.holder.BaseHolder;

import java.util.List;

 class ThemeAdapter extends DefaultAdapter<Theme> {
    private final Context context;
    private List<Theme> data;

    public ThemeAdapter(Context context, List<Theme> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new MyHodlder(context);
    }

    class MyHodlder extends BaseHolder<Theme> {
        private TextView title;
        private TextView content;
        private TextView starttime;
        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.theme_item, null);
            this.title= (TextView) view.findViewById(R.id.title);
            this.content= (TextView) view.findViewById(R.id.content);
            this.starttime= (TextView) view.findViewById(R.id.starttime);
            return view;
        }

        @Override
        public void refreshView(Theme theme) {
            this.title.setText(theme.getThemeName());
            this.content.setText(theme.getIntroduction());
            this.starttime.setText(theme.getStartTime());
        }

        public MyHodlder(Context context) {
            super(context);
        }
    }
}
