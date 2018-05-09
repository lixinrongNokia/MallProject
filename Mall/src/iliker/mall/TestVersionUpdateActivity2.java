package iliker.mall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import cn.iliker.mall.R;

/**
 * 版本更新检查，下载安装
 *
 * @author Administrator
 */
@SuppressLint("InflateParams")
public class TestVersionUpdateActivity2 extends Activity {

    private String content = null;
    private String versionName = null;
    private String appName = null;
    private String url = null;
    private ProgressBar pb;
    private TextView tv;
    private static int loading_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_version);
        Bundle bundle = getIntent().getBundleExtra("versiondata");
        versionName = bundle.getString("versionName");
        content = bundle.getString("content");
        appName = bundle.getString("appName");
        url = bundle.getString("url");
        loading_process = 0;
        initdata();
    }

    private void initdata() {
        Dialog dialog = new AlertDialog.Builder(TestVersionUpdateActivity2.this)
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
    }

    private void Beginning() {
        LinearLayout ll = (LinearLayout) LayoutInflater.from(
                TestVersionUpdateActivity2.this).inflate(
                R.layout.layout_loadapk, null);
        pb = (ProgressBar) ll.findViewById(R.id.down_pb);
        tv = (TextView) ll.findViewById(R.id.tv);
        AlertDialog.Builder builder = new Builder(TestVersionUpdateActivity2.this);
        builder.setView(ll);
        builder.setTitle("版本更新进度提示").setCancelable(false);
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
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+getPackageName(),appName);
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
                        TestVersionUpdateActivity2.this.finish();
                        break;
                    case -1:
                        String error = msg.getData().getString("error");
                        Toast.makeText(TestVersionUpdateActivity2.this, error,
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };
}
