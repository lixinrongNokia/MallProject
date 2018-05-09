package iliker.fragment.person.activity;

import android.view.View;
import android.widget.Button;
import cn.iliker.mall.R;
import iliker.fragment.person.OrderDetailActivity;
import iliker.fragment.person.holder.ActionOrderMethod;

import static iliker.fragment.person.holder.ActionOrderMethod.changeOrderIDBuy;

public class PayingOrderDetail extends OrderDetailActivity implements View.OnClickListener {
    private Button cancel;//取消订单按钮
    private Button payingbtn;//支付按钮

    @Override
    protected void loadBbr_Buttons() {
        View view = View.inflate(this, R.layout.forpayment_layout, null);
        payingbtn = (Button) view.findViewById(R.id.payingbtn);
        cancel = (Button) view.findViewById(R.id.cancel);
        bbr_cartitem.addView(view);
        setButtonListener();
    }

    private void setButtonListener() {
        payingbtn.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payingbtn:
                changeOrderIDBuy(this, webOrder,true);
                break;
            case R.id.cancel:
                ActionOrderMethod.cancelledOrder(this, webOrder);
                break;
        }
    }
}
