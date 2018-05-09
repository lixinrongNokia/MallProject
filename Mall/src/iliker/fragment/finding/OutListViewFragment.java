package iliker.fragment.finding;


import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import cn.iliker.mall.R;
import iliker.utils.GeneralUtil;

import static iliker.utils.HttpHelp.getHttpUtils;

public class OutListViewFragment extends ListViewFragment {
    private Drawable drawable;
    private String count;
    private int fromUid;//赞者
    private int taggetUid;//被赞者

    @Override
    public void onClick(View v) {
        praise(fromUid, taggetUid, "add");
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tAnimation.setVisibility(View.VISIBLE);
                                tAnimation.startAnimation(animation);
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        tAnimation.setVisibility(View.GONE);
                                        thumbup.setCompoundDrawables(drawable, null, null, null);
                                        count = thumbup.getText().toString();
                                        if (TextUtils.isEmpty(count)) count = "0";
                                        thumbup.setText(String.valueOf(Integer.parseInt(count) + 1));
                                    }
                                }, 1000);

                            }
                        });
                        break;
                    case 10:
                        thumbup.setCompoundDrawables(drawable, null, null, null);
                        break;
                    case 100:
                        ToastFactory.getMyToast("每日一赞明天再来").show();
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

    @Override
    protected void initDate() {
        drawable = getResources().getDrawable(R.drawable.fqx);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        fromUid = CustomApplication.customApplication.getUserinfo().getuID();
        taggetUid = webuser.getuID();
        praise(fromUid, taggetUid, "find");
    }
}
