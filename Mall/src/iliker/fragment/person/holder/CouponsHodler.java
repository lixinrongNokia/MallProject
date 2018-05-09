package iliker.fragment.person.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.entity.Prepaidcard;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;

public class CouponsHodler extends BaseHolder<Prepaidcard> {
    private ImageView image;
    private TextView denomination;
    private TextView voucher_value;

    public CouponsHodler(Context context) {
        super(context);
    }

    @Override
    public View initViews() {
        View view = View.inflate(context, R.layout.coupons_item, null);
        this.image = (ImageView) view.findViewById(R.id.image);
        this.denomination = (TextView) view.findViewById(R.id.denomination);
        this.voucher_value = (TextView) view.findViewById(R.id.voucher_value);
        return view;
    }

    @Override
    public void refreshView(Prepaidcard datas) {
        this.denomination.setText(String.valueOf(datas.getDenomination()));
        this.voucher_value.setText(String.valueOf(datas.getVoucher_value()));
        bitmapUtils.bind(this.image, GeneralUtil.GETTYPEIMG + datas.getCardUrl());
    }
}
