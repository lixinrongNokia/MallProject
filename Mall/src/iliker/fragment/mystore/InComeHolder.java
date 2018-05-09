package iliker.fragment.mystore;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import cn.iliker.mall.R;
import iliker.entity.IncomeInfo;
import iliker.holder.BaseHolder;

public class InComeHolder extends BaseHolder<IncomeInfo> {
    private TextView incomeTime;
    private TextView incomeDESC;
    private TextView incomeAmount;

    public InComeHolder(Context context) {
        super(context);
    }

    @Override
    public View initViews() {
        View view = View.inflate(context, R.layout.income_item_layout, null);
        this.incomeTime = (TextView) view.findViewById(R.id.incomeTime);
        this.incomeDESC = (TextView) view.findViewById(R.id.incomeDESC);
        this.incomeAmount = (TextView) view.findViewById(R.id.incomeAmount);
        return view;
    }

    @Override
    public void refreshView(IncomeInfo datas) {
        this.incomeTime.setText(datas.getIncomeTime());
        this.incomeDESC.setText(datas.getIncomeDESC());
        this.incomeAmount.setText(datas.getIncomeAmount());
    }
}
