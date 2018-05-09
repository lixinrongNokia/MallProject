package iliker.fragment.finding;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.mobileim.YWIMKit;
import com.fjl.widget.ToastFactory;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.math.BigDecimal;

import cn.iliker.mall.R;
import iliker.utils.GeneralUtil;

import static com.iliker.application.CustomApplication.getmIMKit;
import static iliker.utils.HttpHelp.getHttpUtils;

/**
 * Created by WDHTC on 2016/5/16.
 */
public class OutShowPersonAlity extends ShowPersonality implements View.OnClickListener {

    @Override
    protected void initData() {
        outtbar.setVisibility(View.VISIBLE);
        setListener();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (listViewFragment == null) listViewFragment = new OutListViewFragment();
        listViewFragment.setArguments(bundles);
        ft.replace(R.id.container, listViewFragment).commit();
    }

    private void setListener() {
        reward.setOnClickListener(this);
        send_message.setOnClickListener(this);
    }

    @Override
    protected void editTitle() {
        menuItem.setTitle("更多");
    }

    @Override
    protected void menuOnClick() {
        ToastFactory.getMyToast("暂时没开发").show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reward:

                RequestParams params = new RequestParams(GeneralUtil.GETMYASSETS);
                params.addBodyParameter("phone", cap.getUserinfo().getPhone());
                getHttpUtils().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String responseInfo) {
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(responseInfo);
                        if (jsonObject.getBoolean("success")) {
                            double remainingsum = jsonObject.getDouble("balance");
                            BigDecimal data1 = new BigDecimal(remainingsum);
                            BigDecimal data2 = new BigDecimal(0);
                            if (data1.compareTo(data2) <= 0) {
                                ToastFactory.getMyToast("你当前余额不足").show();
                            } else {
                                Intent intent = new Intent(OutShowPersonAlity.this, RewardActivity.class);
                                bundles.putDouble("remainingsum", remainingsum);
                                intent.putExtra("bundle", bundles);
                                startActivity(intent);
                            }
                        } else {
                            ToastFactory.getMyToast("你当前余额不足").show();
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
                break;
            case R.id.send_message:
                YWIMKit ywimKit = getmIMKit();
                if (ywimKit != null) {
                    Intent intent = ywimKit.getChattingActivityIntent(webuser.getPhone(), getString(R.string.openIMKey));
                    startActivity(intent);
                }
                break;
        }
    }

}
