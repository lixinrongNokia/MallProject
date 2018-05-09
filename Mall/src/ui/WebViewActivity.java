package ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.iliker.mall.R;
import com.kawa.widget.ProgressWebView;
import com.kawa.widget.TitleBar;

public abstract class WebViewActivity extends Activity {
    protected LinearLayout layout_titlebar;
    protected TitleBar titlebar;
    protected ProgressWebView webView;
    private String url;
    protected WebSettings webSettings;
    protected Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basewebview);
        intent=getIntent();
        url = setUrl();
        findViews();
        setTitle();
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onResume() {
        super.onResume();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        webSettings.setJavaScriptEnabled(false);
    }

    private void findViews() {
        webView = (ProgressWebView) findViewById(R.id.webview);
        layout_titlebar = (LinearLayout) findViewById(R.id.layout_titlebar);
        titlebar = new TitleBar(this, true, true);
        layout_titlebar.addView(titlebar.getView());
    }

    private void init() {
        webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        createWebAppInterface();
        // 处理webview中的回退事件
        webView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { // 表示按返回键时的操作
                        webView.goBack(); // 后退
                        return true; // 已处理
                    }
                }
                return false;
            }

        });
        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) { // Handle the
                // error
                Toast.makeText(WebViewActivity.this, error.getDescription(),
                        Toast.LENGTH_SHORT).show();
            }

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest resourceRequest) {
                return onFilter(resourceRequest);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setData();
            }
        });
        webView.loadUrl(url);
        titlebar.setOnRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(webView.getUrl());
            }
        });

        titlebar.setOnleftListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (webView.canGoBack()) {
            webView.loadUrl(url);
        }

    }

    /*设置网页资源*/
    protected abstract void setData();

    /*过滤网址*/
    protected abstract boolean onFilter(WebResourceRequest resourceRequest);

    /*设置标题*/
    protected abstract void setTitle();

    /*设置加载资源*/
    protected abstract String setUrl();

    /*与js交互*/
    protected abstract void createWebAppInterface();
}
