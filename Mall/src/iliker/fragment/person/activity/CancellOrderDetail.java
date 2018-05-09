package iliker.fragment.person.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.iliker.mall.R;
import iliker.fragment.person.OrderDetailActivity;
import iliker.fragment.type.ShopListActivity;

import static iliker.fragment.person.holder.ActionOrderMethod.delOrder;


public class CancellOrderDetail extends OrderDetailActivity implements OnClickListener {
    private Button del_order;//删除订单按钮
//    private Button againplay;//再次购买按钮

    @Override
    protected void loadBbr_Buttons() {
        View view = View.inflate(this, R.layout.fordone_layout, null);
        del_order = (Button) view.findViewById(R.id.del_order);
//        againplay = (Button) view.findViewById(R.id.againplay);
        bbr_cartitem.addView(view);
        setButtonListener();
    }

    private void setButtonListener() {
        del_order.setOnClickListener(this);
//        againplay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.del_order:
                delOrder(this, webOrder.getId());
                break;
            /*case R.id.againplay:
                Intent intent = new Intent(this, ShopListActivity.class);
                intent.putExtra("serializablelist", orderlist);
                intent.putExtra("isfromCart", false);
                startActivity(intent);
                break;*/
        }
    }
}
