package cn.iliker.mall.storemodule.holder;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import com.fjl.widget.ToastFactory;
import iliker.entity.StoreInfo;
import iliker.holder.BaseHolder;
import iliker.utils.GeneralUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import static iliker.utils.HttpHelp.getHttpUtils;

public class DetailHolder extends BaseHolder<StoreInfo> {
    private TextView tTell, tContacts, tAddress, tRegTime, tVisible, enableLink;
    private ImageView cover;

    public DetailHolder(Context context) {
        super(context);
    }

    @Override
    public View initViews() {
        View view = View.inflate(context, R.layout.store_detail, null);
        tTell = (TextView) view.findViewById(R.id.tell);
        tContacts = (TextView) view.findViewById(R.id.contacts);
        tAddress = (TextView) view.findViewById(R.id.address);
        tRegTime = (TextView) view.findViewById(R.id.regTime);
        tVisible = (TextView) view.findViewById(R.id.visible);
        cover = (ImageView) view.findViewById(R.id.cover);
        enableLink = (TextView) view.findViewById(R.id.enableLink);
        return view;
    }

    @Override
    public void refreshView(final StoreInfo datas) {
        tTell.setText(String.valueOf("电话:" + datas.getTell()));
        tContacts.setText(String.valueOf("店长:" + datas.getContacts()));
        tAddress.setText(String.valueOf("地址:" + datas.getAddress()));
        tRegTime.setText(String.valueOf("注册时间:" + datas.getRegTime()));
        int visible = datas.getVisible();
        if (visible == 1) {
            tVisible.setText(String.valueOf("激活状态:" + "已激活"));
            enableLink.setVisibility(View.GONE);
        } else {
            tVisible.setText(Html.fromHtml("激活状态:<font color='red'>未激活</font>"));
            enableLink.setVisibility(View.VISIBLE);
            enableLink.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
        String[] storeIcons = datas.getFaceIcon().split("#");
        enableLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableLink.setEnabled(false);
                RequestParams params = new RequestParams(GeneralUtil.SENDENABLECODE + "?id=" + datas.getId() + "");
                getHttpUtils().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if ("ok".equals(result)) {
                            ToastFactory.getMyToast("激活申请已发送").show();
                        }else {
                            ToastFactory.getMyToast("发送失败").show();
                            enableLink.setEnabled(true);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastFactory.getMyToast("发送失败").show();
                        enableLink.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
        bitmapUtils.bind(cover, GeneralUtil.STOREICON + storeIcons[0]);
    }
}
