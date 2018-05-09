package iliker.fragment.mystore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import com.cardsui.example.goods.ClassificationActivity;
import ui.WebViewActivity;

public class Question_Center extends WebViewActivity {
    @Override
    protected void setData() {

    }

    @Override
    protected boolean onFilter(WebResourceRequest resourceRequest) {
        return false;
    }

    @Override
    protected void setTitle() {
        titlebar.setTitle("答疑中心");
    }

    @Override
    protected String setUrl() {
        return "file:///android_asset/index.html";
    }

    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void createWebAppInterface() {
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
    }

    public class WebAppInterface {
        final Context mContext;

        /*Instantiate the interface and set the context*/
        WebAppInterface(Context context) {
            mContext = context;
        }

       /*js调用android*/
        @JavascriptInterface
        public void entrance() {
            Intent classintent = new Intent(mContext, ClassificationActivity.class);
            startActivity(classintent);
        }

    }
}
