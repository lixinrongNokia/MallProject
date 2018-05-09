package iliker.fragment.mystore;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Partners;
import iliker.holder.BaseHolder;
import iliker.utils.BitmapHelp;
import iliker.utils.GeneralUtil;

/**
 * Created by WDHTC on 2016/4/18.
 */
public class PartnersAdapter extends DefaultAdapter<Partners> {
    private Context context;

    public PartnersAdapter() {
    }

    public PartnersAdapter(Context context, List<Partners> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new MyHoder(context);
    }

    class MyHoder extends BaseHolder<Partners> {
        private ImageView headimg;
        private TextView phone;
        private TextView registered;
        private TextView mylevel;

        public MyHoder(Context context) {
            super(context);
        }

        @Override
        public View initViews() {
            View view = View.inflate(context, R.layout.partners_item_layout, null);
            this.headimg = (ImageView) view.findViewById(R.id.headimg);
            this.phone = (TextView) view.findViewById(R.id.phone);
            this.registered = (TextView) view.findViewById(R.id.registered);
            this.mylevel = (TextView) view.findViewById(R.id.mylevel);
            return view;
        }

        @Override
        public void refreshView(Partners datas) {
            this.phone.setText(datas.getPhone());
            this.registered.setText(datas.getRegistered());
            bitmapUtils.bind(this.headimg, GeneralUtil.SHAREPATH + datas.getHeadimg(), BitmapHelp.getImageOptions());
            this.mylevel.setText(String.valueOf(datas.getLevel()));
        }
    }
}
