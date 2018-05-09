package iliker.fragment.person.holder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.fjl.widget.DialogFactory;
import com.fjl.widget.ToastFactory;
import iliker.entity.WebOrder;
import iliker.fragment.type.CheckStandActivity;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static com.fjl.widget.ToastFactory.getMyToast;
import static iliker.utils.HttpHelp.getHttpUtils;

public final class ActionOrderMethod {
    public static void cancelledOrder(final Context context, final WebOrder order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("取消订单吗？");
        builder.setNegativeButton("NO", null)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                RequestParams params = new RequestParams(GeneralUtil.CANCELORDER);
                                params.addBodyParameter("tOrder.id", String.valueOf(order.getId()));
                                params.addBodyParameter("tOrder.memo", "不想买了");
                                getHttpUtils().post(params, new Callback.CommonCallback<String>() {

                                    @Override
                                    public void onSuccess(String responseString) {
                                        if ("success".equals(responseString)) {
                                            getMyToast("审核后为你取消订单").show();
                                            ((Activity) context).finish();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable throwable, boolean b) {
                                        getMyToast("发生故障").show();
                                    }

                                    @Override
                                    public void onCancelled(CancelledException e) {

                                    }

                                    @Override
                                    public void onFinished() {

                                    }
                                });
                            }

                        }).show();
    }

    public static void delOrder(final Context context, int id) {
        RequestParams params = new RequestParams(GeneralUtil.DELORDERSVC);
        params.addBodyParameter("id", id + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseInfo) {
                int i = Integer.parseInt(responseInfo);
                if (i > 0) {
                    ToastFactory.getMyToast("删除成功！").show();
                    ((Activity) context).finish();
                } else {
                    ToastFactory.getMyToast("删除失败！").show();
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

    public static void changeOrderIDBuy(final Context context, final WebOrder datas, final boolean isFinish) {
        final Dialog dialog = DialogFactory.initDialog(context);
        dialog.show();
        RequestParams params = new RequestParams(GeneralUtil.CHANGEORDERID);
        params.addBodyParameter("orderId", datas.getId() + "");
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                dialog.dismiss();
                if (!"0".equals(s)) {
                    Bundle bundle = new Bundle();
//                    String out_trade_no = datas.getOrderid();
                    bundle.putString("out_trade_no", s);// 订单号
                    bundle.putString("subject", "艾拉奇购物订单");// 商品名
                    bundle.putString("body", "iliker buy");// 商品详情
                    bundle.putString("goods_type", "1");
                    bundle.putDouble("total_fee", datas.getToalprice());// 总金额
                    Intent intent = new Intent(context,
                            CheckStandActivity.class);
                    intent.putExtra("shopdata", bundle);
                    context.startActivity(intent);
                    if (isFinish) {
                        ((Activity) context).finish();
                    }
                } else ToastFactory.getMyToast("获取支付信息失败").show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dialog.dismiss();
                ToastFactory.getMyToast("获取支付信息失败").show();
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
