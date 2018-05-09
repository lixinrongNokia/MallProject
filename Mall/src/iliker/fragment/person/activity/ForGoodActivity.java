package iliker.fragment.person.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import cn.iliker.mall.R;
import iliker.fragment.home.LinkActivity;
import iliker.fragment.person.OrderDetailActivity;
import iliker.fragment.person.holder.ActionOrderMethod;
import iliker.utils.GeneralUtil;
import iliker.utils.QRUtils;

public class ForGoodActivity extends OrderDetailActivity implements View.OnClickListener {

    @Override
    protected void loadBbr_Buttons() {
        View view;
        if ("已发货".equals(webOrder.getOrderstate())) {
            view = View.inflate(this, R.layout.forthegoodsed_layout, null);
            view.findViewById(R.id.query_logistics).setOnClickListener(this);
        } else {
            view = View.inflate(this, R.layout.forgoods_layout, null);
            Button cancelButton= (Button) view.findViewById(R.id.cancel);
            cancelButton.setOnClickListener(this);
            Button createQRCode = (Button) view.findViewById(R.id.createQRCode);
            if ("退款中".equals(webOrder.getOrderstate())){
                cancelButton.setEnabled(false);
                cancelButton.setTextColor(getResources().getColor(R.color.defaultWhite));
            }
            if ("门店自提".equals(webOrder.getPostmethod()) && "等待配货".equals(webOrder.getOrderstate())) {
                createQRCode.setVisibility(View.VISIBLE);
                createQRCode.setOnClickListener(this);
            } else {
                createQRCode.setVisibility(View.GONE);
            }
           /* if (webOrder.isSys_flag() && !"退款中".equals(webOrder.getOrderstate())) {
                Button button = (Button) view.findViewById(R.id.changebtn);
                button.setTextColor(getResources().getColor(R.color.defaultRed));
                button.setEnabled(true);
                button.setOnClickListener(this);
            }*/
        }
        bbr_cartitem.addView(view);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                ActionOrderMethod.cancelledOrder(this, webOrder);
                break;
            case R.id.query_logistics:
                Intent intent = new Intent(this,
                        LinkActivity.class);
                intent.putExtra("openHref",
                        GeneralUtil.HOST
                                + "/querylogistics.do?id="
                                + webOrder.getId());
                startActivity(intent);
                break;
            case R.id.createQRCode:
                Bitmap bitmap = QRUtils.createQRBitmap("{\"ilikerAppOrderID\":" + webOrder.getId() + "}");
                // 显示QRCode
                if (null != bitmap) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    View cardview = View.inflate(this, R.layout.card_layout, null);
                    ImageView imageView = (ImageView) cardview.findViewById(R.id.cardView);
                    imageView.setImageBitmap(bitmap);
                    builder.setView(cardview).show();
                }
                break;
            /*case R.id.changebtn:
                Intent refundIntent = new Intent(this, RefundActivity.class);
                refundIntent.putExtra("toalprice", webOrder.getToalprice());
                refundIntent.putExtra("orderid", webOrder.getId());
                startActivity(refundIntent);
                break;*/
        }
    }
}
