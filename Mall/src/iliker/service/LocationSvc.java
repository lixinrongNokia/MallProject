package iliker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import iliker.entity.StoreInfo;
import iliker.utils.GeneralUtil;
import iliker.utils.XmlUtil;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.List;

import static iliker.utils.HttpHelp.getHttpUtils;
import static iliker.utils.XmlUtil.isDecimal;

//定位获取附近门店与自身位置信息
public class LocationSvc extends Service {

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private int COUNT = 0;

    @Override
    public void onCreate() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(3000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            COUNT++;
            if (COUNT == 10) {
                mLocationClient.unRegisterLocationListener(mMyLocationListener);
                mLocationClient.stop();
                stopSelf();
            } else {
                if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                    double latitude = location.getLatitude();//保存经纬度
                    double longitude = location.getLongitude();
                    if (isDecimal(String.valueOf(latitude))) {
                        mLocationClient.unRegisterLocationListener(mMyLocationListener);
                        mLocationClient.stop();
                        getSharedPreferences("locationinfo", Context.MODE_PRIVATE)
                                .edit()
                                .putString("provinces", location.getProvince())
                                .putString("ncity", location.getCity())
                                .putString("naddr", location.getAddrStr())
                                .putString("district", location.getDistrict())
                                .putString("street", location.getStreet())
                                .putString("mylat", String.valueOf(latitude))
                                .putString("mylong", String.valueOf(longitude)).apply();
                        RequestParams params = new RequestParams(GeneralUtil.STORE);
                        params.addBodyParameter("mylat", String.valueOf(latitude));
                        params.addBodyParameter("mylng", String.valueOf(longitude));
                        getHttpUtils().post(params,
                                new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String jsonStr) {
                                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                                        if (jsonObject.getBoolean("success")) {
                                            final List<StoreInfo> data = JSON.parseArray(jsonObject.getJSONArray("stores").toJSONString(), StoreInfo.class);
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    XmlUtil.Object2XML(data);
                                                }
                                            }).start();
                                        }
                                        stopSelf();
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {
                                    }

                                    @Override
                                    public void onCancelled(CancelledException cex) {
                                        stopSelf();
                                    }

                                    @Override
                                    public void onFinished() {
                                        stopSelf();
                                    }
                                });
                    }
                }
            }

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
