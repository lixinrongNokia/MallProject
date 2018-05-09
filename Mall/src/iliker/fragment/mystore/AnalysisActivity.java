package iliker.fragment.mystore;

import android.webkit.WebResourceRequest;
import ui.WebViewActivity;
public class AnalysisActivity extends WebViewActivity {
    @Override
    protected void setData() {

    }

    @Override
    protected boolean onFilter(WebResourceRequest resourceRequest) {
        return false;
    }

    @Override
    protected void setTitle() {
        titlebar.setTitle("收益分析");
    }

    @Override
    protected String setUrl() {
        return "file:///android_asset/analysis.html";
    }

    @Override
    protected void createWebAppInterface() {

    }
}
