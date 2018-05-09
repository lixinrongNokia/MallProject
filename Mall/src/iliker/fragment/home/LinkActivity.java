package iliker.fragment.home;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.webkit.WebResourceRequest;

import ui.WebViewActivity;

public class LinkActivity extends WebViewActivity {

    @Override
    protected void onResume() {
        webSettings.setJavaScriptEnabled(false);
        super.onResume();
    }

    @Override
    protected void setData() {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onFilter(WebResourceRequest resourceRequest) {
        return "https://iliker888.taobao.com/".equals(resourceRequest.getUrl().toString());
    }

    @Override
    public void setTitle() {
        layout_titlebar.setVisibility(View.GONE);
    }

    @Override
    protected String setUrl() {
        return getIntent().getStringExtra("openHref");
    }

    @Override
    protected void createWebAppInterface() {

    }
}
