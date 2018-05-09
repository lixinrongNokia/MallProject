package iliker.fragment.finding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;

import com.fjl.widget.ToastFactory;

import iliker.entity.UserInfo;
import iliker.utils.GeneralUtil;
import ui.WebViewActivity;

import static com.iliker.application.CustomApplication.customApplication;

public class LoadThemeActivity extends WebViewActivity {
    private WebAppInterface anInterface;
    private int userId;

    @Override
    protected void setData() {
        anInterface.initUserInfo();
    }

    @Override
    protected boolean onFilter(WebResourceRequest resourceRequest) {
        return false;
    }

    @Override
    protected void setTitle() {
        titlebar.setTitle("活动介绍");
    }

    @Override
    protected String setUrl() {
//        return intent.getStringExtra("webUrl");
        return intent.getStringExtra("webUrl");
    }

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void createWebAppInterface() {
        UserInfo userInfo = customApplication.getUserinfo();
        if (userInfo == null) {
            ToastFactory.getMyToast("你没登陆!").show();
            finish();
        }
        anInterface = new WebAppInterface(this, userInfo.getuID());
        webView.addJavascriptInterface(anInterface, "Android");
    }

    public class WebAppInterface {
        final Context mContext;
        final int userId;

        WebAppInterface(Context context, int userId) {
            mContext = context;
            this.userId = userId;
        }

        /*页面点击事件*/
        @JavascriptInterface
        public void onWebClick() {
            /*if (customApplication.getUserinfo() == null) {
                ToastFactory.getMyToast("你没登陆!").show();
                return;
            }
            Intent intent = new Intent(LoadThemeActivity.this, BatchTailorImgActivity.class);
            intent.putExtra("themeID", themeID);
            startActivity(intent);
            finish();*/
        }

        void initUserInfo() {
            webView.loadUrl("javascript:setUserId('" + userId + "')");
        }

    }
}
