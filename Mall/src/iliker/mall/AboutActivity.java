package iliker.mall;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.iliker.mall.R;
import cn.sharesdk.onekeyshare.OnekeyShare;
import iliker.utils.QRUtils;

//
public class AboutActivity extends Activity implements OnClickListener {
    private ImageView back, shakeup, downloader;
    private TextView codeName;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        findViews();
        setListener();
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), PackageInfo.INSTALL_LOCATION_AUTO);
            codeName.setText(String.valueOf("v" + packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = QRUtils.createQRBitmap("http://iliker.cn/download.html");
        // 显示QRCode
        if (null != bitmap) {
            downloader.setImageBitmap(bitmap);
            downloader.setScaleType(ScaleType.FIT_CENTER);
        }
    }

    private void findViews() {
        back = (ImageView) findViewById(R.id.backbtn);
        shakeup = (ImageView) findViewById(R.id.shareapp);
        downloader = (ImageView) findViewById(R.id.downloadcode);
        codeName = (TextView) findViewById(R.id.codeName);
    }

    private void setListener() {
        back.setOnClickListener(this);
        shakeup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backbtn:
                this.finish();
                break;
            case R.id.shareapp:
                OnekeyShare oks = new OnekeyShare();
                // 关闭sso授权
                oks.disableSSOWhenAuthorize();
                oks.setTitle("爱内秀客户端");
                oks.setTitleUrl("http://iliker.cn/");
                oks.setText("扫描二维码");
                oks.setImageUrl("http://iliker.cn/img/download.png");
                oks.setUrl("http://iliker.cn");
                // 启动分享GUI
                oks.show(this);
                break;
        }
    }
}
