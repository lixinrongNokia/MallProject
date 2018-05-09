package iliker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import iliker.mall.TestVersionUpdateActivity2;
import iliker.utils.GeneralUtil;
import iliker.utils.ReadStreamUtil;

public class AutoUpdateSvc extends IntentService {
    private JSONObject jo_v = null;
    private int currentV = 0, newV = 0;
    private String content = null;
    private String versionName = null;
    private String appName = null;
    private String url = null;

    public AutoUpdateSvc() {
        super("AutoUpdateSvc");
    }

    private final Handler BroadcastHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Intent intent = new Intent(AutoUpdateSvc.this,
                        TestVersionUpdateActivity2.class);
                // Service 启动activity
                Bundle bundle = new Bundle();
                bundle.putString("versionName", versionName);
                bundle.putString("content", content);
                bundle.putString("appName", appName);
                bundle.putString("url", url);
                intent.putExtra("versiondata", bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent); // 启动功能Activity
                stopSelf();
            }
        }
    };

    private void startCheckUpdate() {
        if (isConnect(this)) {

            new Thread() {
                public void run() {
                    jo_v = getJsonObject(GeneralUtil.VERSIONUPDATE);
                    if (jo_v == null) {
                        return;
                    }
                    try {
                        versionName = jo_v.getString("versionName");
                        content = jo_v.getString("content");
                        appName = jo_v.getString("appName");
                        url = jo_v.getString("url");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    if (jo_v != null)
                        try {
                            newV = Integer.parseInt(jo_v.getString("versionCode"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    if (newV > currentV) {
                        Message msg = BroadcastHandler.obtainMessage();
                        msg.what = 1;
                        BroadcastHandler.sendMessage(msg);
                    }
                }
            }.start();
        }
    }

    private JSONObject getJsonObject(String Url) {
        JSONObject jsonob = null;
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                byte[] data = ReadStreamUtil.readStream(in);
                String str = new String(data);
                jsonob = new JSONObject(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return jsonob;
    }

    private boolean isConnect(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        Toast.makeText(this, "没网络", Toast.LENGTH_SHORT).show();
        return false;
    }

    private int getVerCode(Context _context, String _package) {
        int verCode = -1;
        try {
            verCode = _context.getPackageManager().getPackageInfo(_package, 0).versionCode;
        } catch (NameNotFoundException e) {
        }
        return verCode;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        currentV = getVerCode(this, this.getPackageName());
        startCheckUpdate();
    }

}
