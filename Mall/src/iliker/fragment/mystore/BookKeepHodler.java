package iliker.fragment.mystore;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.iliker.mall.R;
import iliker.entity.BookKeep;
import iliker.holder.BaseHolder;

class BookKeepHodler extends BaseHolder<BookKeep> {
    private TextView incomeTime;
    private TextView incomeAmount;
    private TextView incomeDESC;

    BookKeepHodler(Context context) {
        super(context);
    }

    @Override
    public View initViews() {
        View view = View.inflate(context, R.layout.income_item_layout, null);
        this.incomeTime = (TextView) view.findViewById(R.id.incomeTime);
        this.incomeAmount = (TextView) view.findViewById(R.id.incomeAmount);
        this.incomeDESC = (TextView) view.findViewById(R.id.incomeDESC);
        return view;
    }

    @Override
    public void refreshView(BookKeep datas) {
        this.incomeTime.setText(datas.getSpendTime());
        this.incomeAmount.setText(String.valueOf("-" + datas.getSpendAmount()));
        this.incomeDESC.setText(datas.getSpendDESC());
    }
}
