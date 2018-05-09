package cn.iliker.mall.storemodule;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import cn.iliker.mall.MessagePanelAtc;
import cn.iliker.mall.PushMsgObserver;
import cn.iliker.mall.R;
import cn.iliker.mall.storemodule.fragments.FragmentFactory;
import com.fjl.widget.ToastFactory;
import com.iliker.application.CustomApplication;
import iliker.entity.StoreInfo;
import iliker.mall.MainActivity;
import iliker.mall.StartActivity;
import org.json.JSONException;
import org.json.JSONObject;
import org.xing.qr_code.scan.MipcaActivityCapture;

import java.lang.ref.WeakReference;

import static com.fjl.widget.ToastFactory.getMyToast;
import static com.iliker.application.CustomApplication.exitSystem;
import static iliker.utils.DBHelper.getDB;

public class Store_MainActivity extends AppCompatActivity {

    private static final int SCANNIN_GREQUEST_CODE = 1;
    private ViewPager mViewPager;
    private PagerTabStrip pager_tab_strip;
    private String[] tab_names = new String[]{"门店信息"};  // 标签的名字
    private PushMsgObserver pushMsgObserver;
    private static int messageCode = 211;
    private MesHandler mesHandler = new MesHandler(this);
    private StoreInfo storeInfo;
    private BadgeActionProvider mPicBadgeActionProvider;
    private static final int PIC_WHAT = 0X02;

    private class MesHandler extends Handler {
        private final WeakReference<Context> reference;

        public MesHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            Store_MainActivity activity = (Store_MainActivity) reference.get();
            if (activity != null) {
                if (msg.what == messageCode) {
                    mPicBadgeActionProvider.setBadge(msg.arg1);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_activity_main);
        ActionBar actionBar = getSupportActionBar();
        CustomApplication.customApplication.actlist.add(this);
        storeInfo = CustomApplication.customApplication.getStoreInfo();
        if (actionBar != null) {
            actionBar.setTitle("门店");
        }
        pushMsgObserver = new PushMsgObserver(mesHandler, storeInfo.getLoginEmail(), messageCode);
        findViews();
        initViews();
        getContentResolver().registerContentObserver(Uri.parse("content://cn.iliker.mall.aliPushMsg/add/"), false, pushMsgObserver);
    }

    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        pager_tab_strip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
    }

    private void initViews() {
        pager_tab_strip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        //  设置标签下划线的颜色
        pager_tab_strip.setTabIndicatorColor(getResources().getColor(R.color.indicatorcolor));

        mViewPager.setAdapter(new Store_MainAdpater(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                /*BaseFragment createFragment = FragmentFactory.createFragment(position);
                createFragment.show();//  当切换界面的时候 重新请求服务器*/
            }

        });
    }

    private class Store_MainAdpater extends FragmentStatePagerAdapter {
        public Store_MainAdpater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.createFragment(position);
        }

        @Override
        public int getCount() {
            return tab_names.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab_names[position];
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.msg, menu);
        MenuItem msgItem = menu.findItem(R.id.viewMessage);
        mPicBadgeActionProvider = (BadgeActionProvider) MenuItemCompat.getActionProvider(msgItem);
        mPicBadgeActionProvider.setOnClickListener(PIC_WHAT, onClickListener);
        return true;
    }

    private BadgeActionProvider.OnClickListener onClickListener = new BadgeActionProvider.OnClickListener() {
        @Override
        public void onClick(int what) {
            if (what == PIC_WHAT) {
                Intent intent = new Intent(Store_MainActivity.this, MessagePanelAtc.class);
                intent.putExtra("receiver", storeInfo.getLoginEmail());
                startActivity(intent);
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.switchGUI:
                getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 1).apply();
                Intent switchIntent = new Intent(this, StartActivity.class);
                startActivity(switchIntent);
                finish();
                break;
            case R.id.qrCode:
                int asPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
                if (asPermission == PackageManager.PERMISSION_GRANTED) {
                    Intent intent1 = new Intent();
                    intent1.setClass(this, MipcaActivityCapture.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent1, SCANNIN_GREQUEST_CODE);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                    }
                }
                break;
            case R.id.storeLogout:
                getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).edit().putInt("switchID", 1).apply();
                CustomApplication.customApplication.removeStoreInfo();
                startActivity(new Intent(this, StartActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent1 = new Intent();
            intent1.setClass(this, MipcaActivityCapture.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent1, SCANNIN_GREQUEST_CODE);
        } else ToastFactory.getMyToast("没有获得相机权限").show();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case SCANNIN_GREQUEST_CODE:
                    String result = data.getStringExtra("result");
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (!jsonObject.isNull("ilikerAppOrderID")) {
                            int orderID = jsonObject.getInt("ilikerAppOrderID");
                            Intent intent = new Intent(Store_MainActivity.this, UpInStoresActivity.class);
                            intent.putExtra("orderID", orderID);
                            startActivity(intent);
                        } else getMyToast("找不到订单信息").show();
                    } catch (JSONException e) {
                        getMyToast("不正确数据").show();
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitSystem();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(pushMsgObserver);
        CustomApplication.customApplication.unBindAccount();
        mesHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mPicBadgeActionProvider.setIcon(android.R.drawable.sym_action_email);
        int um = getDB().getUnReadCount(new String[]{storeInfo.getLoginEmail()});
        if (um > 0) {
            mPicBadgeActionProvider.setBadge(um);
        } else {
            mPicBadgeActionProvider.hiddenBadge();
        }
    }
}
