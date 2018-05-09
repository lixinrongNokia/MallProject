package iliker.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import iliker.utils.GeneralUtil;

import static iliker.utils.HttpHelp.getHttpUtils;

public class PostLactionSvc extends IntentService {

    public PostLactionSvc() {
        super("PostLactionSvc");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getBundleExtra("LocationInfo");
        String latitude = bundle.getString("latitude", "");
        String longitude = bundle.getString("longitude", "");
        postLocation(bundle.getString("nickname"), latitude, longitude);
    }

    private void postLocation(String nickname, String latitude, String longitude) {
        RequestParams params = new RequestParams(GeneralUtil.REQUESTLOCATIONSVC);
        params.addBodyParameter("nickname", nickname);
        params.addBodyParameter("latitude", latitude);
        params.addBodyParameter("longitude", longitude);
        getHttpUtils().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                /*if ("1".equals(result)) {
                }*/
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

        });
    }

}
