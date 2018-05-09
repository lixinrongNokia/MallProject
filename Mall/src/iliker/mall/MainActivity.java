package iliker.mall;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import cn.iliker.mall.R;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.IYWConversationUnreadChangeListener;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import com.jikexueyuan.tulingdemo.OnServcieActivity;
import com.readystatesoftware.viewbadger.BadgeView;
import iliker.service.AutoUpdateSvc;
import iliker.service.LocationSvc;
import iliker.service.PostLactionSvc;
import iliker.utils.DataCleanManager;

import java.util.ArrayList;

import static com.iliker.application.CustomApplication.*;

/**
 * app主入口
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends ContentActivity {
    private DrawerLayout drawer_layout;
    private NavigationView left;
    private BadgeView badgeView;
    private IYWConversationService mConversationService;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public static int unreadmsg = 0;
    private final String[] MUTIPERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_CODE_ASK_MUTI_PERMISSIONS = 222;//请求多个权限

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        badgeView = new BadgeView(this, textView5);
        if (customApplication.getUserinfo() != null) {
            SharedPreferences sf = getSharedPreferences("locationinfo", Context.MODE_PRIVATE);
            Intent intent = new Intent(this, PostLactionSvc.class);
            Bundle bundle = new Bundle();
            bundle.putString("nickname", customApplication.getUname());
            bundle.putString("latitude", sf.getString("mylat", ""));
            bundle.putString("longitude", sf.getString("mylong", ""));
            intent.putExtra("LocationInfo", bundle);
            startService(intent);
        }
        if (customApplication.networkIsAvailable()) {
            requestMutiPermission();
        }
        SharedPreferences sf = getSharedPreferences("applogin", Context.MODE_PRIVATE);
        boolean isperson = sf.getBoolean("isperson", false);
        if (isperson) {
            selectId = 4;
            sf.edit().clear().apply();
        }
        setSelection(selectId);
        drawer_layout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
//        StatusBarUtil.setColorForDrawerLayout(this, drawer_layout, 25);
        left = (NavigationView) this.findViewById(R.id.nav_view);
        drawer_layout.addDrawerListener(new DrawerListener() {

            @Override
            public void onDrawerClosed(View arg0) {
            }

            @Override
            public void onDrawerOpened(View arg0) {

            }

            @Override
            public void onDrawerSlide(View arg0, float arg1) {

            }

            @Override
            public void onDrawerStateChanged(int arg0) {

            }

        });
        HeaderHolder headerHolder = new HeaderHolder(this);
        left.addHeaderView(headerHolder.getConvertView());
        left.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.download_layout:
                        Intent i = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(i);
                        break;
                    case R.id.service_layout:
                        Intent onserviceintent = new Intent(MainActivity.this, OnServcieActivity.class);
                        startActivity(onserviceintent);
                        break;
                    case R.id.update_layout:
                        Intent checkintent = new Intent(MainActivity.this, TestVersionUpdateActivity.class);
                        startActivity(checkintent);
                        break;
                    case R.id.clear_layout:
                        DataCleanManager.clearAllCache(customApplication);
                        ToastFactory.getMyToast("已经清理干净了!").show();
                        break;
                    case R.id.switchUI:
                        getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 0).apply();
                        startActivity(new Intent(MainActivity.this, StartActivity.class));
                        for (Activity activity : CustomApplication.actlist) {
                            activity.finish();
                        }
                        CustomApplication.actlist.clear();
                        finish();
                        break;
                    case R.id.logout_layout:
                        new AlertDialog.Builder(MainActivity.this).setMessage("确定注销登录?")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        openIMLogOut();
                                        customApplication.removeUserinfo();
                                        customApplication.unBindAccount();
                                        left.getMenu().setGroupVisible(R.id.exitSystemID, false);
                                        MainActivity.this.onResumeLoad2();
                                    }
                                }).setNegativeButton("取消", null).show();

                        break;
                }
                return false;
            }
        });
        YWIMKit mIMKit = getmIMKit();
        if (mIMKit != null) {
            mConversationService = mIMKit.getConversationService();
        }
    }
    private void initNavi() {
        String mSDCardPath = null;
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            mSDCardPath = Environment.getExternalStorageDirectory().toString();
        }
        BaiduNaviManager.getInstance().init(this, mSDCardPath, getPackageName(),
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            Log.d("log","key校验成功!");
                        } else {
                            Log.d("log","key校验失败!");
                        }
                    }

                    public void initSuccess() {
                        Log.d("log","百度导航引擎初始化成功!");
                    }
                    public void initStart() {
                    }

                    public void initFailed() {
                    }
                }, null /*mTTSCallback*/);
    }
    private void requestMutiPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            //需要请求授权的权限
            ArrayList<String> needRequest = new ArrayList<>();

            //遍历 过滤已授权的权限，防止重复申请
            for (String permission : MUTIPERMISSIONS) {
                int check = checkSelfPermission(permission);
                if (check != PackageManager.PERMISSION_GRANTED) {
                    needRequest.add(permission);
                }
            }
            //如果没有全部授权
            if (needRequest.size() > 0) {
                //请求权限，此方法异步执行，会弹出权限请求对话框，让用户授权，并回调 onRequestPermissionsResult 来告知授权结果
                requestPermissions(needRequest.toArray(new String[needRequest.size()]), REQUEST_CODE_ASK_MUTI_PERMISSIONS);

            } else {//已经全部授权过
                //做一些你想做的事情，即原来不需要动态授权时做的操作
                startService(new Intent(this, LocationSvc.class));
                startService(new Intent(this, AutoUpdateSvc.class));
                initNavi();
            }
        } else {
            startService(new Intent(this, LocationSvc.class));
            startService(new Intent(this, AutoUpdateSvc.class));
            initNavi();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MUTI_PERMISSIONS://请求多个权限
                if (grantResults.length > 0) {
                    //被拒绝的权限列表
                    ArrayList<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permissions[i]);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {//已全部授权
                        startService(new Intent(this, LocationSvc.class));
                        startService(new Intent(this, AutoUpdateSvc.class));
                        initNavi();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (CustomApplication.customApplication.getStoreInfo() == null)
            left.getMenu().setGroupVisible(R.id.switchID, false);
        else left.getMenu().setGroupVisible(R.id.switchID, true);*/

        if (CustomApplication.customApplication.getUserinfo() == null)
            left.getMenu().setGroupVisible(R.id.exitSystemID, false);
        else
            left.getMenu().setGroupVisible(R.id.exitSystemID, true);

        if (mConversationService != null) {
            mConversationService.addTotalUnreadChangeListener(mConversationUnreadChangeListener);
            mConversationUnreadChangeListener.onUnreadChange();
        }
    }

    // 账号完成页面的回调方法
    private void onResumeLoad2() {
        if (selectId == 4) {
            getSupportFragmentManager().beginTransaction().remove(person).commit();
        }
        setSelection(selectId);// 设置选中页面
    }

    private final IYWConversationUnreadChangeListener mConversationUnreadChangeListener = new IYWConversationUnreadChangeListener() {
        //当未读数发生变化时会回调该方法，开发者可以在该方法中更新未读数
        @Override
        public void onUnreadChange() {
            mHandler.post(runnable);
        }
    };
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //获取当前登录用户的所有未读数
            if (mConversationService != null) {
                int unreadNum = mConversationService.getAllUnreadCount();
                unreadmsg = unreadNum;
                if (unreadNum > 0) {
                    if (unreadNum > 100) {
                        badgeView.setText(String.valueOf("99+"));
                    } else {
                        badgeView.setText(String.valueOf(unreadNum));
                    }
                    badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);// 设置徽章位置为右上角
                    badgeView.show();
                } else {
                    badgeView.hide();// 将徽章隐藏
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mConversationService != null)
            mConversationService.removeTotalUnreadChangeListener(mConversationUnreadChangeListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer_layout.isDrawerOpen(left)) {
                drawer_layout.closeDrawer(left);
                return true;
            }
            if (selectId != 0) {
                selectId = 0;
                setSelection(selectId);// 设置选中页面
                return true;
            }
            int i = getSupportFragmentManager().getBackStackEntryCount();
            android.support.v4.app.FragmentManager.BackStackEntry entry = getSupportFragmentManager()
                    .getBackStackEntryAt(i - 1);
            if ("index".equals(entry.getName()) || "bodycoll".equals(entry.getName()) || "find".equals(entry.getName())
                    || "mystore".equals(entry.getName()) || "person".equals(entry.getName())) {
                exitSystem();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        openIMLogOut();
        CustomApplication.customApplication.unBindAccount();
        super.onDestroy();
    }
}
