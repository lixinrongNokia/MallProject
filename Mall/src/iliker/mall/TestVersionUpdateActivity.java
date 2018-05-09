package iliker.mall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.iliker.mall.R;
import iliker.service.VersionService;
import iliker.utils.GeneralUtil;
import iliker.utils.ReadStreamUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.fjl.widget.DialogFactory.initDialog;

/**
 * 版本更新检查，下载安装
 *
 * @author Administrator
 */
@SuppressLint("InflateParams")
public class TestVersionUpdateActivity extends Activity {

    private int currentV = 0, newV = 0;
    private JSONObject jo_v;
    private String content = null;
    private String versionName = null;
    private String appName = null;
    private String url = null;
    private ProgressBar pb;
    private TextView tv;
    public static int loading_process;
    private Dialog mydialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_version);
        mydialog = initDialog(this);
        mydialog.show();
        loading_process = 0;
        if (isConnect(TestVersionUpdateActivity.this)) {
            currentV = getVerCode(this, this.getPackageName());

            new Thread() {
                public void run() {
                    jo_v = getJsonObject(GeneralUtil.VERSIONUPDATE);
                    if (jo_v != null)
                        try {
                            newV = Integer.valueOf(jo_v
                                    .getString("versionCode"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    mydialog.dismiss();
                    if (newV > currentV) {
                        Message msg = BroadcastHandler.obtainMessage();
                        msg.what = 1;
                        BroadcastHandler.sendMessage(msg);
                    } else {
                        Message msg = BroadcastHandler.obtainMessage();
                        msg.what = 2;
                        BroadcastHandler.sendMessage(msg);

                    }
                }
            }.start();
        } else mydialog.dismiss();
    }

    private final Handler BroadcastHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        versionName = jo_v.getString("versionName");
                        content = jo_v.getString("content");
                        appName = jo_v.getString("appName");
                        url = jo_v.getString("url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Dialog dialog = new AlertDialog.Builder(
                            TestVersionUpdateActivity.this)
                            .setTitle("新版本更新内容" + versionName)
                            .setMessage(content)
                            .setCancelable(false)
                            .setPositiveButton("马上更新",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Beginning();
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton("以后再说",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            finish();
                                            dialog.dismiss();
                                        }
                                    }).create();
                    dialog.show();

                    break;
                case 2:
                    Builder dialog2 = new AlertDialog.Builder(
                            TestVersionUpdateActivity.this);
                    dialog2.setMessage("已是最新版本了");
                    dialog2.setCancelable(false);
                    dialog2.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
            }
        }
    };

    private void Beginning() {
        LinearLayout ll = (LinearLayout) LayoutInflater.from(
                TestVersionUpdateActivity.this).inflate(
                R.layout.layout_loadapk, null);
        pb = (ProgressBar) ll.findViewById(R.id.down_pb);
        tv = (TextView) ll.findViewById(R.id.tv);
        Builder builder = new Builder(TestVersionUpdateActivity.this);
        builder.setView(ll);
        builder.setTitle("版本更新进度提示").setCancelable(false);
        builder.setNegativeButton("后台下载",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                TestVersionUpdateActivity.this,
                                VersionService.class);
                        startService(intent);
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.show();
        new Thread() {
            public void run() {
                loadFile(url);
            }
        }.start();
    }

    @SuppressWarnings("deprecation")
    private void loadFile(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse response;
        try {
            response = client.execute(get);

            HttpEntity entity = response.getEntity();
            float length = entity.getContentLength();

            InputStream is = entity.getContent();
            FileOutputStream fileOutputStream;
            if (is != null) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName(), appName);
                fileOutputStream = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int ch;
                float count = 0;
                while ((ch = is.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, ch);
                    count += ch;
                    sendMsg(1, (int) (count * 100 / length));
                }
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            sendMsg(2, 0);
        } catch (Exception e) {
            sendMsg(-1, 0);
        }
    }

    private void sendMsg(int flag, int c) {
        Message msg = new Message();
        msg.what = flag;
        msg.arg1 = c;
        handler.sendMessage(msg);
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 1:
                        pb.setProgress(msg.arg1);
                        loading_process = msg.arg1;
                        tv.setText("已为您加载了：" + loading_process + "%");
                        break;
                    case 2:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName(), appName)),
                                "application/vnd.android.package-archive");
                        startActivity(intent);
                        TestVersionUpdateActivity.this.finish();
                        break;
                    case -1:
                        String error = msg.getData().getString("error");
                        Toast.makeText(TestVersionUpdateActivity.this, error,
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

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
}
