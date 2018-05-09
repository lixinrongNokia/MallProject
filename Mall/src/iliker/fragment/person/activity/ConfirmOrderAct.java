package iliker.fragment.person.activity;

import android.view.View;
import android.widget.Button;
import cn.iliker.mall.R;
import com.fjl.widget.ToastFactory;
import iliker.fragment.person.OrderDetailActivity;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.HttpManagerImpl;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import static iliker.utils.HttpHelp.getHttpUtils;

public class ConfirmOrderAct extends OrderDetailActivity {
    private Button recev;//取消订单按钮
    private Button conrirmBtn;//支付按钮

    @Override
    protected void loadBbr_Buttons() {
        View view = View.inflate(this, R.layout.forpayment_layout, null);
        conrirmBtn = (Button) view.findViewById(R.id.payingbtn);
        conrirmBtn.setText("确认收货");
        conrirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqConfirm(webOrder.getId());
            }
        });
        recev = (Button) view.findViewById(R.id.cancel);
        recev.setText(webOrder.getOrderstate());
        bbr_cartitem.addView(view);
    }

    private void reqConfirm(int orderId) {
        RequestParams params = new RequestParams(GeneralUtil.CONFIRMORDER);
        params.addBodyParameter("orderId",orderId+"");
        getHttpUtils().post(params, new Callback.CommonCallback<Integer>() {
            @Override
            public void onSuccess(Integer s) {
                if (s==1){
                    ToastFactory.getMyToast("操作成功").show();
                    finish();
                }else {
                    ToastFactory.getMyToast("操作").show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
