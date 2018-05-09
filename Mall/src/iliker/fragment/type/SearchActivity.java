package iliker.fragment.type;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.iliker.mall.R;
import iliker.entity.Goods;
import iliker.mall.XCFlowLayout;
import iliker.utils.GeneralUtil;

import static iliker.utils.DBHelper.getDB;
import static iliker.utils.ParsJsonUtil.parseJSON;

public class SearchActivity extends Activity implements OnQueryTextListener,
        OnItemClickListener, OnClickListener {
    private SearchView sv;
    private XCFlowLayout flowlayout;
    private ListView lv;
    private ImageView backbtn;
    private LinearLayout localsearch;
    private List<String> records;
    private final List<TextView> list = new ArrayList<>();

    private List<Goods> webdata;
    private String currentSearchTip;

    private final MyHandler handler = new MyHandler();

    private final ScheduledExecutorService scheduledExecutor = Executors
            .newScheduledThreadPool(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        findViews();
        initChildViews();
        setListener();
    }

    private void findViews() {
        sv = (SearchView) findViewById(R.id.search_view);
        sv.onActionViewExpanded();
        flowlayout = (XCFlowLayout) findViewById(R.id.flowlayout);
        lv = (ListView) findViewById(R.id.lv);
        localsearch = (LinearLayout) findViewById(R.id.localsearch);
        backbtn = (ImageView) findViewById(R.id.backbtn);
    }

    private void initChildViews() {
        try {
            records = readerText(openFileInput("searchRecord"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        MarginLayoutParams lp = new MarginLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        lp.topMargin = 10;
        lp.bottomMargin = 10;
        if (records != null && !records.isEmpty()) {
            for (int i = 0; i < records.size(); i++) {
                TextView view = new TextView(this);
                list.add(view);
                view.setId(i + 1);
                view.setText(records.get(i));
                view.setTextColor(getResources().getColor(R.color.myblack));
                view.setBackgroundResource(R.drawable.sel_textview_bg);
                view.setOnClickListener(this);
                flowlayout.addView(view, lp);
            }
        }
    }

    private void setListener() {
        sv.setOnQueryTextListener(this);
        lv.setOnItemClickListener(this);
        backbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }

        });
        for (TextView view : list) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        currentSearchTip = filterString(newText);
        if (currentSearchTip != null) {
            showSearchTip(currentSearchTip);
        } else {
            if (localsearch.getVisibility() == View.GONE) {
                localsearch.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
            }
        }
        return true;
    }

    private static String filterString(String newText) {
        if (newText != null && newText.trim().length() > 0) {
            return newText.trim();
        }
        return null;
    }

    private void showSearchTip(String newText) {
        schedule(new SearchTipThread(newText), 1000);
    }

    class SearchTipThread implements Runnable {

        final String newText;

        SearchTipThread(String newText) {
            this.newText = newText;
        }

        @SuppressWarnings("deprecation")
        public void run() {
            if (newText.equals(currentSearchTip)) {
                try {
                    List<NameValuePair> pairList = new ArrayList<>();
                    NameValuePair keyword = new BasicNameValuePair("keyword", currentSearchTip);
                    NameValuePair fristResult = new BasicNameValuePair("firstResult", 0 + "");
                    NameValuePair maxResult = new BasicNameValuePair("maxResult", 5 + "");
                    pairList.add(keyword);
                    pairList.add(fristResult);
                    pairList.add(maxResult);
                    HttpPost post = new HttpPost(GeneralUtil.SEARCHSVC);
                    HttpEntity entity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
                    post.setEntity(entity);
                    HttpClient client = new DefaultHttpClient();
                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
                    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
                    org.apache.http.HttpResponse response = client.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                        if (!"".equals(result)) {
                            //保存有效搜索
                            appendText(openFileOutput("searchRecord", Context.MODE_APPEND), newText);
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("reval", result);
                            msg.setData(bundle);
                            msg.what = 11;
                            handler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = 10;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    /*删除文本第一行*/
    private static void delTopLine() {

    }

    /*读取保存的记录*/
    private static List<String> readerText(FileInputStream fileInputStream) {
        List<String> list = new ArrayList<>();
        try {
            if (fileInputStream != null) {
                InputStreamReader inputreader = new InputStreamReader(fileInputStream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                while ((line = buffreader.readLine()) != null) {
                    list.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /*得到保存记录的行数*/
    /*private static int getLineNumber() {
        File file = new File("D:/b.txt");
        long fileLength = file.length();
        LineNumberReader rf = null;
        try {
            rf = new LineNumberReader(new FileReader(file));
            rf.skip(fileLength);
            return rf.getLineNumber();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (rf != null) {
                try {
                    rf.close();
                } catch (IOException ee) {
                }
            }
        }
        return 0;
    }*/

    /*把有效搜索保存到本地*/
    private static void appendText(FileOutputStream outputStream, String content) {
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(content.getBytes("UTF-8"));
            bufferedOutputStream.write("\n".getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedOutputStream != null)
                    bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*解析JSON*/
    private void schedule(Runnable command, long delayTimeMills) {
        scheduledExecutor.schedule(command, delayTimeMills,
                TimeUnit.MILLISECONDS);
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 11:
                    String reval = msg.getData().getString("reval", "");
                    if (webdata != null) {
                        webdata.clear();
                    }
                    webdata = parseJSON(reval);
                    if (lv.getVisibility() == View.GONE) {
                        localsearch.setVisibility(View.GONE);
                        lv.setVisibility(View.VISIBLE);
                    }
                    lv.setAdapter(new SearchAdapter(webdata, SearchActivity.this));
                    break;
                case 10:
                    if (localsearch.getVisibility() == View.GONE) {
                        localsearch.setVisibility(View.VISIBLE);
                        lv.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Goods good = webdata.get(position);
        Intent intent = new Intent(this, ProductDetailActivity.class);// 跳到商品详情页面
        getDB().addHistory(good, sdf.format(new Date()));
        intent.putExtra("good", good);
        startActivity(intent);// 执行
    }

    @Override
    public void onClick(View v) {
        TextView view = (TextView) v;
        Intent intent = new Intent(this, SearchResult_Activity.class);
        intent.putExtra("searchText", view.getText().toString());
        startActivity(intent);
    }

}
