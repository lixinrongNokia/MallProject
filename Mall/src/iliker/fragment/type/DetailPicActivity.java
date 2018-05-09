package iliker.fragment.type;

import android.annotation.SuppressLint;
import android.webkit.WebResourceRequest;

import iliker.entity.Goods;
import ui.WebViewActivity;

public class DetailPicActivity extends WebViewActivity {
    private WebAppInterface webAppInterface;

    @Override
    protected void setData() {
        webAppInterface.loadProductDetail();
    }

    @Override
    protected boolean onFilter(WebResourceRequest resourceRequest) {
        return false;
    }

    @Override
    protected void setTitle() {
        titlebar.setTitle("商品详情介绍");
    }

    @Override
    protected String setUrl() {
        return "file:///android_asset/product_detail.html";
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @Override
    protected void createWebAppInterface() {
        Goods goods = getIntent().getParcelableExtra("goods");
        String illustrations = goods.getIllustrations();
        webAppInterface = new WebAppInterface(illustrations == null ? "" : illustrations);
        webView.addJavascriptInterface(webAppInterface, null);
    }

    private class WebAppInterface {
        final String imgUrl;

        WebAppInterface(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        void loadProductDetail() {
            webView.loadUrl("javascript:loadProductDetail('" + imgUrl + "')");
        }

    }
}
