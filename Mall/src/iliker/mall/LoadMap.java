package iliker.mall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.iliker.mall.R;
import cn.iliker.mall.storemodule.StoreDetail;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.fjl.widget.ToastFactory;
import iliker.entity.StoreInfo;
import iliker.utils.XmlUtil;

import java.util.ArrayList;
import java.util.List;

import static iliker.utils.XmlUtil.isDecimal;

/**
 * 加载百度地图
 *
 * @author lixinrong
 */
@SuppressLint("InflateParams")
public class LoadMap extends FragmentActivity implements OnMarkerClickListener,
        OnClickListener {
    /**
     * MapView 是地图主控件
     */
    private LocationClient mLocationClient;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationMode mCurrentMode;
    private Button requestLocButton;
    private String[] items;
    private List<StoreInfo> stores;
    private String mobilestr;
    private boolean isFirstLoc = true;// 是否首次定位
    private final MyLocationListener myListener = new MyLocationListener();

    // 初始化全局 bitmap 信息，不用时及时 recycle
    private final BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    private int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stores = XmlUtil.xml2Object();
        setContentView(R.layout.activity_location);
        mCurrentMode = LocationMode.NORMAL;
        findViews();
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setScanSpan(3000);
        option.setCoorType("bd09ll"); // 设置坐标类型
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        if (stores != null) {
            initOverlay();
        }
        setListener();
    }

    private void findViews() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        requestLocButton = (Button) findViewById(R.id.button1);
        requestLocButton.setText("普通");
    }

    private void setListener() {
        mBaiduMap.setOnMarkerClickListener(this);
        requestLocButton.setOnClickListener(this);
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            double latitude = location.getLatitude();//保存经纬度
            double longitude = location.getLongitude();
            if (isDecimal(String.valueOf(latitude))) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
                if (isFirstLoc) {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(latitude, longitude);
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                    mBaiduMap.animateMapStatus(u);
                }
            } else count++;
            if (count == 5) {
                ToastFactory.getMyToast("不能访问位置信息").show();
                mLocationClient.stop();
            }
        }
    }

    private void initOverlay() {
        List<LatLng> latlngs = new ArrayList<>();
        for (StoreInfo store : stores) {
            LatLng ll = new LatLng(store.getLatitude(), store.getLongitude());
            latlngs.add(ll);
            OverlayOptions oo = new MarkerOptions().position(ll).icon(bd)
                    .zIndex(9);

            Marker mMarker = (Marker) (mBaiduMap.addOverlay(oo));
            mMarker.setTitle(store.getId() + "");
        }
        Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latlngs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);
    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        if (mMapView != null) {

            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        if (mLocationClient != null) {
            mLocationClient.start();
        }
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
        mMapView.onDestroy();
        // 回收 bitmap 资源
        if (bd != null) {
            bd.recycle();
        }
        super.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        View view = getLayoutInflater().inflate(R.layout.custom_info_window,
                null);
        ImageView img = (ImageView) view.findViewById(R.id.badge);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        TextView details = (TextView) view.findViewById(R.id.details);
        BitmapDescriptor infowindow = null;
        details.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        img.setImageResource(R.drawable.logo);
        LatLng llInfo = marker.getPosition();
        OnInfoWindowClickListener listener = null;
        for (final StoreInfo store : stores) {
            if (marker.getTitle().equals(store.getId() + "")) {
                String address = store.getAddress();
                String substr = address;
                if (address.length() > 10) {
                    String start = address.substring(0, 10);
                    String end = address.substring(10, address.length());
                    substr = start + "\n" + end;
                }
                title.setText(store.getStoreName());
                snippet.setText(substr);
                details.setText("详情");
                infowindow = BitmapDescriptorFactory.fromView(view);
                listener = new OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
                        /*AlertDialog.Builder builder = new AlertDialog.Builder(
                                LoadMap.this);
                        items = new String[]{"店铺名称:" + store.getStoreName(),
                                "店铺地址:" + store.getAddress(),
                                "店铺负责人:" + store.getContacts(),
                                "电话:" + store.getTell()};
                        builder.setItems(items, null);
                        builder.show();
                        mBaiduMap.hideInfoWindow();*/
                        Intent storeFace = new Intent(LoadMap.this, StoreDetail.class);
                        storeFace.putExtra("store", store);
                        LoadMap.this.startActivity(storeFace);
                        mBaiduMap.hideInfoWindow();
                    }

                };
                break;
            }
        }
        if (infowindow != null) {
            InfoWindow mInfoWindow = new InfoWindow(infowindow, llInfo, 0, listener);
            mBaiduMap.showInfoWindow(mInfoWindow);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (mCurrentMode) {
            case NORMAL:
                requestLocButton.setText("跟随");
                mCurrentMode = LocationMode.FOLLOWING;
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, null));
                break;
            case COMPASS:
                requestLocButton.setText("普通");
                mCurrentMode = LocationMode.NORMAL;
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, null));
                break;
            case FOLLOWING:
                requestLocButton.setText("罗盘");
                mCurrentMode = LocationMode.COMPASS;
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, null));
                break;
        }
    }

}
