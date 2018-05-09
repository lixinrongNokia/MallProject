package iliker.fragment.finding.holder;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.fjl.widget.ToastFactory;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.math.BigDecimal;
import java.util.List;

import cn.iliker.mall.R;
import iliker.entity.Praise;
import iliker.entity.UserInfo;
import iliker.fragment.finding.RewardActivity;
import iliker.utils.GeneralUtil;

import static com.iliker.application.CustomApplication.customApplication;
import static com.iliker.application.CustomApplication.getmIMKit;
import static iliker.utils.BitmapHelp.getBitmapUtils;
import static iliker.utils.HttpHelp.getHttpUtils;


public class Goddess_item_Hodler extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final Drawable drawable;
    private final ImageView head_img;
    private final TextView thumbup;
    private final TextView sayhello;
    private final TextView reward;
    private UserInfo userInfo;
    private final View view;

    public Goddess_item_Hodler(View itemView) {
        super(itemView);
        this.view = itemView;
        this.head_img = (ImageView) itemView.findViewById(R.id.head_img);
        this.thumbup = (TextView) itemView.findViewById(R.id.thumbup);
        this.sayhello = (TextView) itemView.findViewById(R.id.sayhello);
        this.reward = (TextView) itemView.findViewById(R.id.reward);
        this.thumbup.setOnClickListener(this);
        this.sayhello.setOnClickListener(this);
        this.reward.setOnClickListener(this);
        drawable = itemView.getResources().getDrawable(R.drawable.fqx);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
    }

    public void setData(UserInfo userInfo, List<Praise> praises) {
        for (Praise praise : praises) {
            if (praise.getuID() == userInfo.getuID()) {
                thumbup.setCompoundDrawables(drawable, null, null, null);
                break;
            }
        }
        thumbup.setText(String.valueOf(userInfo.getPraiseCount()));
        this.userInfo = userInfo;
        if (userInfo.getNickName().equals(customApplication.getUname())) {
            this.thumbup.setVisibility(View.GONE);
            this.sayhello.setVisibility(View.GONE);
            this.reward.setVisibility(View.GONE);
        }
        String[] strings = userInfo.getPhotoAlbum().split("#");
        getBitmapUtils().bind(head_img, GeneralUtil.HEADURL + strings[0]);
    }

    @Override

    public void onClick(View view) {
        if (view == thumbup) {
            praise(customApplication.getUserinfo().getuID(), userInfo.getuID(), "add");
        } else if (view == sayhello) {
            YWIMKit ywimKit = getmIMKit();
            if (ywimKit != null) {
                Intent intent = ywimKit.getChattingActivityIntent(userInfo.getPhone(), view.getContext().getString(R.string.openIMKey));
                view.getContext().startActivity(intent);
            }
        } else {
            getBandle();
        }
    }

    private void praise(int fromUid, int taggetUid, String type) {
        RequestParams params = new RequestParams(GeneralUtil.PRAISESVC);
        params.addBodyParameter("fromUid", fromUid + "");
        params.addBodyParameter("taggetUid", taggetUid + "");
        params.addBodyParameter("type", type);
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseInfo) {
                int backcode = Integer.valueOf(responseInfo);
                switch (backcode) {
                    case -1:
                        break;
                    case 1:
                        String count = thumbup.getText().toString();
                        thumbup.setCompoundDrawables(drawable, null, null, null);
                        if (TextUtils.isEmpty(count)) count = "0";
                        thumbup.setText(String.valueOf(Integer.parseInt(count) + 1));
                        break;
                    case 10:
                        thumbup.setCompoundDrawables(drawable, null, null, null);
                        break;
                    case 100:
                        break;
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

    private void getBandle() {
        RequestParams params = new RequestParams(GeneralUtil.GETMYASSETS);
        params.addBodyParameter("phone", customApplication.getUserinfo().getPhone());
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String responseInfo) {
                double remainingsum = Double.parseDouble(responseInfo);
                BigDecimal data1 = new BigDecimal(remainingsum);
                BigDecimal data2 = new BigDecimal(0);
                if (data1.compareTo(data2) <= 0) {
                    ToastFactory.getMyToast("你当前余额不足").show();
                } else {
                    Intent intent = new Intent(view.getContext(), RewardActivity.class);
                    Bundle bundles = new Bundle();
                    bundles.putDouble("remainingsum", remainingsum);
                    bundles.putSerializable("userinfo", userInfo);
                    intent.putExtra("bundle", bundles);
                    view.getContext().startActivity(intent);
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
