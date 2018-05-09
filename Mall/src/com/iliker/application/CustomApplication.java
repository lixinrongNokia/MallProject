package com.iliker.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;
import cn.iliker.mall.R;
import com.alibaba.mobileim.*;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.media.MediaService;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.alibaba.wxlib.util.SysUtil;
import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechUtility;
import iliker.entity.StoreInfo;
import iliker.entity.UserInfo;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static iliker.utils.DBHelper.getDB;

/**
 * 全局类，保持用户状态
 *
 * @author Administrator
 */
public class CustomApplication extends Application {
    public static List<Activity> actlist = new ArrayList<>();
    public static File cacheDir;// 全局缓存目录
    public static CustomApplication customApplication;
    private static UserInfo userInfo;
    private static StoreInfo storeInfo;
    private static YWIMKit mIMKit;
    private static long exitTime = 0;
    private String TAG = "aliPush";

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
        SDKInitializer.initialize(this);
        x.Ext.init(this);
        getAd();
        customApplication = this;
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SysUtil.setApplication(this);
        if (SysUtil.isTCMSServiceProcess(this)) {
            return;
        }
        initCloudChannel(this);
        if (SysUtil.isMainProcess()) {
            YWAPI.init(this, getString(R.string.openIMKey));
        }
        AlibabaSDK.asyncInit(this, new InitResultCallback() {
            @Override
            public void onSuccess() {
                AlibabaSDK.getService(MediaService.class);
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    public static void openIMLogin(String phone, String password) {
        mIMKit = YWAPI.getIMKitInstance(phone, customApplication.getString(R.string.openIMKey));
        YWIMCore imCore = mIMKit.getIMCore();
        //开始登录
        IYWLoginService loginService = imCore.getLoginService();
        YWLoginParam Param = YWLoginParam.createLoginParam(phone, password);
        loginService.login(Param, new IWxCallback() {
            @Override
            public void onSuccess(Object... objects) {
                setmIMKit(mIMKit);
            }

            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onProgress(int i) {
            }
        });
    }

    public static void openIMLogOut() {
        if (userInfo != null) {
            mIMKit = YWAPI.getIMKitInstance(userInfo.getPhone(), customApplication.getString(R.string.openIMKey));
            YWIMCore imCore = mIMKit.getIMCore();
            IYWLoginService loginService = imCore.getLoginService();
            loginService.logout(new IWxCallback() {
                @Override
                public void onSuccess(Object... objects) {

                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i) {

                }
            });
        }
    }

    private static void setmIMKit(YWIMKit mIMKit) {
        CustomApplication.mIMKit = mIMKit;
    }

    public static YWIMKit getmIMKit() {
        UserInfo userInfo = customApplication.getUserinfo();
        if (userInfo == null)
            return null;
        if (mIMKit == null) {
            mIMKit = YWAPI.getIMKitInstance(userInfo.getPhone(), customApplication.getString(R.string.openIMKey));
        }
        return mIMKit;
    }

    private void getAd() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "Android/data/" + this.getPackageName() + "/images/");
            if (!cacheDir.exists())
                cacheDir.mkdirs();

        }
        // 缓存目录不存在则创建
        if (cacheDir == null) {
            cacheDir = this.getFilesDir();// 获取系统文件目录
        }
    }

    public UserInfo getUserinfo() {
        if (userInfo == null) {
            userInfo = getDB().findUserInfo();
        }
        return userInfo;
    }

    public StoreInfo getStoreInfo() {
        if (storeInfo == null) {
            storeInfo = getDB().findStoreInfo();
        }
        return storeInfo;
    }


    public void removeUserinfo() {
        getDB().remoreUser();
        userInfo = null;
    }

    public void resetUser() {
        userInfo = null;
    }

    public void removeStoreInfo() {
        getDB().remoreStoreInfo();
        storeInfo = null;
    }

    public String getUname() {
        if (this.getUserinfo() != null) {
            return this.getUserinfo().getNickName();
        }
        return null;
    }

    /**
     * 获取网络是否可用状态
     */
    public boolean networkIsAvailable() {
        ConnectivityManager cManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static void exitSystem() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(customApplication, "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            if (!actlist.isEmpty()) {
                for (Activity act : actlist) {
                    act.finish();
                }
                actlist.clear();
            }
            System.exit(0);
        }
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
//                CloudPushService pushService = PushServiceFactory.getCloudPushService();
                /*String deviceId = pushService.getDeviceId();*/
                Log.e(TAG, response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        // 初始化小米通道，自动判断是否支持小米系统推送，如不支持会跳过注册
        MiPushRegister.register(applicationContext, "2882303761517527541", "5871752792541");
        // 初始化华为通道，自动判断是否支持华为系统推送，如不支持会跳过注册
        //HuaWeiRegister.register(applicationContext);
    }

    public void bindAccount(String accountName) {
        PushServiceFactory.getCloudPushService().bindAccount(accountName, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.e(TAG, response);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, errorMessage);
            }
        });
    }

    public void unBindAccount() {
        PushServiceFactory.getCloudPushService().unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, s);
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.e(TAG, s1);
            }
        });

    }
}
